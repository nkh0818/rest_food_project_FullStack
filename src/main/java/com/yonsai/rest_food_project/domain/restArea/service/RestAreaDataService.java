package com.yonsai.rest_food_project.domain.restArea.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yonsai.rest_food_project.domain.restArea.entity.Food;
import com.yonsai.rest_food_project.domain.restArea.entity.RestArea;
import com.yonsai.rest_food_project.domain.restArea.repository.FoodRepository;
import com.yonsai.rest_food_project.domain.restArea.repository.RestAreaRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class RestAreaDataService {

    private final RestAreaRepository restAreaRepository;
    private final FoodRepository foodRepository;
    private final FoodNormalizationService foodNormalizationService;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper mapper = new ObjectMapper();

    @Value("${road-service-key}")
    private String serviceKey;

    /**
     * 전체 데이터 수집 실행 (유가는 제외하고 음식에만 집중)
     */
    public void fetchAndSaveAllData() {
        // 1. 휴게소 기본 목록부터 수집하여 DB에 커밋
        fetchRestAreaBasics();

        // 2. DB에 저장된 휴게소를 다시 읽어옴 (S 접두어 포함된 상태)
        List<RestArea> allAreas = restAreaRepository.findAll();
        log.info(">>>> 총 {}개 휴게소 대상 음식 수집 시작", allAreas.size());

        for (RestArea area : allAreas) {
            try {
                fetchAndSaveAllFoods(area);
                log.info(">> {} 음식 수집 성공", area.getName());
                Thread.sleep(100);
            } catch (Exception e) {
                log.error("!! {} 수집 중 에러: {}", area.getName(), e.getMessage());
            }
        }
        log.info("==== [성공] 모든 음식 데이터 수집 완료 ====");
    }

    /**
     * 휴게소 기본 정보 수집 및 저장
     */
    @Transactional
    public void fetchRestAreaBasics() {
        String url = "https://data.ex.co.kr/openapi/restinfo/hiwaySvarInfoList?key=" + serviceKey + "&type=json";
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            JsonNode list = mapper.readTree(response.getBody()).path("list");

            for (JsonNode node : list) {
                String stdRestCd = node.path("svarCd").asText();
                if (restAreaRepository.findByStdRestCd(stdRestCd).isPresent())
                    continue;

                RestArea area = RestArea.builder()
                        .name(node.path("svarNm").asText())
                        .routeName(node.path("routeNm").asText())
                        .direction(node.path("gudClssCdNm").asText(null))
                        .location(node.path("svarAddr").asText(null))
                        .stdRestCd(stdRestCd)
                        .gasolinePrice(0)
                        .diselPrice(0)
                        .lpgPrice(0)
                        .build();

                restAreaRepository.save(area);
            }

            log.info(">> 휴게소 기본 목록 저장 완료");
        } catch (Exception e) {
            log.error("기본목록 에러: {}", e.getMessage());
        }
    }

    /**
     * 특정 휴게소의 모든 음식 수집 (일반/청년/브랜드)
     */
    public void fetchAndSaveAllFoods(RestArea area) throws Exception {
        // (1) 일반 메뉴
        String url1 = "https://data.ex.co.kr/openapi/restinfo/restBestfoodList?key=" + serviceKey
                + "&type=json&stdRestCd=" + area.getStdRestCd();
        processFoodApi(url1, area, "일반", "foodNm", "foodCost", "bestfoodyn");

        // (2) 청년창업
        String url2 = "https://data.ex.co.kr/openapi/restinfo/restVentureList?key=" + serviceKey
                + "&type=json&stdRestCd=" + area.getStdRestCd();
        processFoodApi(url2, area, "청년창업", "bzNm", null, null);

        // (3) 브랜드 매장
        String url3 = "https://data.ex.co.kr/openapi/restinfo/restBrandList?key=" + serviceKey
                + "&type=json&stdRestCd=" + area.getStdRestCd();
        processFoodApi(url3, area, "브랜드", "brdName", null, null);
    }

    /**
     * API 응답 가공 및 DB 저장 (공통 로직)
     */
    @Transactional
    public void processFoodApi(String url, RestArea area, String category, String nameField, String priceField,
            String bestField) throws Exception {
        // 1. 요청하는 URL을 로그로 출력 (가장 중요)
        log.info(">>>> [요청 시작] {} - 카테고리: {}, URL: {}", area.getName(), category, url);

        try {
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            String body = response.getBody();

            // 2. API가 응답한 실제 JSON 데이터를 통째로 로그에 찍음
            log.info(">>>> [API 응답 내용]: {}", body);

            JsonNode root = mapper.readTree(body);
            JsonNode list = root.path("list");

            // 3. 데이터가 비어있는지 확인
            if (list.isMissingNode() || !list.isArray() || list.size() == 0) {
                log.warn(">>>> [결과 없음] {} 휴게소의 {} 데이터가 비어있습니다.", area.getName(), category);
                return;
            }

            for (JsonNode node : list) {
                String name = node.path(nameField).asText();
                if (name == null || name.trim().isEmpty() || name.equals("null"))
                    continue;

                int price = (priceField != null) ? node.path(priceField).asInt(0) : 0;
                int isBest = (bestField != null && node.path(bestField).asText().equals("Y")) ? 1 : 0;

                Food food = Food.builder()
                        .foodName(name)
                        .price(price)
                        .sourceType(category)
                        .isBest(isBest)
                        .isSignature(isBest == 1)
                        .storeName(node.path("storgCdNm").asText(null))
                        .nutritionInfo(node.path("etc").asText(null))
                        .restArea(area)
                        .build();

                foodNormalizationService.normalize(food);
                foodRepository.save(food);

                log.info(">>>> [저장 성공] {} - {} ({}원)", area.getName(), name, price);
            }
        } catch (Exception e) {
            log.error(">>>> [API 에러] {} 수집 중 문제 발생: {}", area.getName(), e.getMessage());
        }
    }

    @Transactional
    public void updateOilPricesOnly() {
        log.info("==== 유가 수집 시작 ====");
        String url = "https://data.ex.co.kr/openapi/business/curStateStation?key="
                + serviceKey + "&type=json&numOfRows=99&pageNo=2";

        try {
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            JsonNode root = mapper.readTree(response.getBody());
            JsonNode list = root.path("list");

            for (JsonNode node : list) {
                String apiRestCode = node.path("serviceAreaCode2").asText();

                restAreaRepository.findByStdRestCd(apiRestCode).ifPresent(area -> {
                    String rawGasoline = node.path("gasolinePrice").asText();
                    log.info(">>>> API 원본 데이터 - 휴게소: {}, 휘발유: '{}'", area.getName(), rawGasoline);

                    area.setGasolinePrice(parsePriceInt(rawGasoline));
                    area.setDiselPrice(parsePriceInt(node.path("diselPrice").asText()));
                    area.setLpgPrice(parsePriceInt(node.path("lpgPrice").asText()));
                    area.setOilCompany(node.path("oilCompany").asText());
                    area.setTelNo(node.path("telNo").asText());

                    restAreaRepository.save(area);
                    log.info(">>>> [저장된 값] {} - 휘발유: {}", area.getName(), area.getGasolinePrice());
                });
            }
        } catch (Exception e) {
            log.error("에러 발생: {}", e.getMessage());
        }
    }

    private Integer parsePriceInt(String priceStr) {
        if (priceStr == null || priceStr.trim().isEmpty() || "-".equals(priceStr) || "null".equals(priceStr)) {
            return 0;
        }

        try {
            String cleanPrice = priceStr.replaceAll("[^0-9]", "");
            if (cleanPrice.isEmpty()) {
                return 0;
            }
            return Integer.parseInt(cleanPrice);
        } catch (Exception e) {
            log.error("가격 파싱 에러: {} -> {}", priceStr, e.getMessage());
            return 0;
        }
    }
}