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
                // [음식 3종 수집에만 집중]
                fetchAndSaveAllFoods(area);
                log.info(">> {} 음식 수집 성공", area.getName());

                // API 서버 부하 방지 (0.1초 대기)
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
                        .stdRestCd(stdRestCd)
                        .gasolinePrice(0).diselPrice(0).lpgPrice(0) // 유가는 일단 0으로 초기화
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
                        .category(category)
                        .isBest(isBest)
                        .restArea(area)
                        .build();

                foodRepository.save(food);
                // 4. 실제로 DB에 저장되는지 확인
                log.info(">>>> [저장 성공] {} - {} ({}원)", area.getName(), name, price);
            }
        } catch (Exception e) {
            log.error(">>>> [API 에러] {} 수집 중 문제 발생: {}", area.getName(), e.getMessage());
        }
    }

    @Transactional // 이게 있어야 메서드 종료 시 DB에 반영됩니다!
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

                // 1. DB에서 해당 휴게소 찾기
                restAreaRepository.findByStdRestCd(apiRestCode).ifPresent(area -> {
                    // [중요 로그] API에서 넘어온 원본 문자열을 직접 찍어봅니다.
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

    /**
     * 가격 문자열("1,650" 등)을 Integer로 안전하게 변환
     */
    private Integer parsePriceInt(String priceStr) {
        if (priceStr == null || priceStr.trim().isEmpty() || priceStr.equals("-") || priceStr.equals("null")) {
            return 0;
        }
        try {
            // 1. 숫자가 아닌 모든 문자(콤마, '원', 공백 등)를 빈 값("")으로 치환합니다.
            String cleanPrice = priceStr.replaceAll("[^0-9]", "");

            // 2. 만약 치환 후 빈 문자열이 되면 0 반환
            if (cleanPrice.isEmpty())
                return 0;

            // 3. 이제 순수한 숫자만 남았으니 Integer로 변환!
            return Integer.parseInt(cleanPrice);
        } catch (Exception e) {
            log.error("가격 파싱 에러: {} -> {}", priceStr, e.getMessage());
            return 0;
        }
    }
}