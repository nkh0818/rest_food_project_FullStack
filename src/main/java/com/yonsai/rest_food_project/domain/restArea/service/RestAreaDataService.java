package com.yonsai.rest_food_project.domain.restArea.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yonsai.rest_food_project.domain.restArea.dto.RestAreaResponseDto;
import com.yonsai.rest_food_project.domain.restArea.entity.Food;
import com.yonsai.rest_food_project.domain.restArea.entity.RestArea;
import com.yonsai.rest_food_project.domain.restArea.repository.FoodRepository;
import com.yonsai.rest_food_project.domain.restArea.repository.RestAreaRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.geo.Point;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class RestAreaDataService {

    private final RestAreaRepository restAreaRepository;
    private final FoodRepository foodRepository;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper mapper = new ObjectMapper();

    // 메모리 검색/매칭용 데이터 저장소
    private List<RestAreaResponseDto> originalData = new ArrayList<>();
    private List<RestAreaResponseDto> currentSearchResult = new ArrayList<>();

    @Value("${road-service-key}")
    private String serviceKey;

    /**
     * 초기 데이터 주입 (DataInitializer용)
     */
    public void initData(List<RestAreaResponseDto> dataList) {
        this.originalData = dataList;
        this.currentSearchResult = new ArrayList<>(dataList);
        log.info(" 메모리 데이터 로드 완료: {}건", dataList.size());
    }

    // ================= [검색/필터/매칭 기능] =================

    public List<RestAreaResponseDto> search(String keyword) {
        this.currentSearchResult = originalData.stream()
                .filter(d -> d.getDbName().contains(keyword))
                .collect(Collectors.toList());
        return this.currentSearchResult;
    }

    public List<RestAreaResponseDto> filter(String type) {
        return this.currentSearchResult.stream()
                .filter(d -> d.getType().equals(type))
                .collect(Collectors.toList());
    }

    public List<RestAreaResponseDto> reset() {
        this.currentSearchResult = new ArrayList<>(originalData);
        return this.currentSearchResult;
    }

    public RestAreaResponseDto findBestMatch(String kakaoName, double kX, double kY) {
        // 공백 제거 , 4글자이상 까지! 4개이하일경우 X
        String cleanKa = kakaoName.replace(" ", "").substring(0, Math.min(kakaoName.length(), 4));
        return originalData.stream().filter(db -> {
            // 특수기호 없에기 하나씩 가저올때
            String cleanDb = db.getDbName().replaceAll("\\(.*?\\)", "").replace(" ", "")
                    .substring(0, Math.min(db.getDbName().length(), 4));
            double dist = calculateDistance(kY, kX, db.getY(), db.getX());
            // 거리로 매칭 혹은 (카카오이름에 디비 ) , 디비에 카카오 , 거리 1.5이하
            return cleanKa.contains(cleanDb) || cleanDb.contains(cleanKa) || dist < 1.5;
        }).findFirst().orElse(null);
    }

    // ================= [공공데이터 수집 기능] =================

    public void fetchAndSaveAllData() {
        fetchRestAreaBasics();
        List<RestArea> allAreas = restAreaRepository.findAll();
        for (RestArea area : allAreas) {
            try {
                fetchAndSaveAllFoods(area);
                Thread.sleep(100); // 공공 데이터 api 폭탄 막기
            } catch (Exception e) {
                log.error("!! {} 수집 중 에러: {}", area.getName(), e.getMessage());
            }
        }
    }

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
                        .gasolinePrice(0).diselPrice(0).lpgPrice(0).build();
                restAreaRepository.save(area);
            }
        } catch (Exception e) {
            log.error("기본목록 에러: {}", e.getMessage());
        }
    }

    public void fetchAndSaveAllFoods(RestArea area) throws Exception {
        String url1 = "https://data.ex.co.kr/openapi/restinfo/restBestfoodList?key=" + serviceKey
                + "&type=json&stdRestCd=" + area.getStdRestCd();
        processFoodApi(url1, area, "일반", "foodNm", "foodCost", "bestfoodyn");
        String url2 = "https://data.ex.co.kr/openapi/restinfo/restVentureList?key=" + serviceKey
                + "&type=json&stdRestCd=" + area.getStdRestCd();
        processFoodApi(url2, area, "청년창업", "bzNm", null, null);
        String url3 = "https://data.ex.co.kr/openapi/restinfo/restBrandList?key=" + serviceKey + "&type=json&stdRestCd="
                + area.getStdRestCd();
        processFoodApi(url3, area, "브랜드", "brdName", null, null);
    }

    @Transactional
    public void processFoodApi(String url, RestArea area, String category, String nameField, String priceField,
            String bestField) throws Exception {
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            JsonNode list = mapper.readTree(response.getBody()).path("list");
            if (list.isMissingNode() || !list.isArray() || list.size() == 0)
                return;
            for (JsonNode node : list) {
                String name = node.path(nameField).asText();
                if (name == null || name.trim().isEmpty() || name.equals("null"))
                    continue;
                int price = (priceField != null) ? node.path(priceField).asInt(0) : 0;
                int isBest = (bestField != null && node.path(bestField).asText().equals("Y")) ? 1 : 0;
                Food food = Food.builder().foodName(name).price(price).category(category).isBest(isBest).restArea(area)
                        .build();
                foodRepository.save(food);
            }
        } catch (Exception e) {
            log.error("API 에러: {}", e.getMessage());
        }
    }

    @Transactional
    public void updateOilPricesOnly() {
        log.info("==== 실시간 유가 수집 시작 ====");
        String url = "https://data.ex.co.kr/openapi/business/curStateStation?key=" + serviceKey
                + "&type=json&numOfRows=99&pageNo=1";
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            JsonNode list = mapper.readTree(response.getBody()).path("list");
            for (JsonNode node : list) {
                String apiRestCode = node.path("serviceAreaCode2").asText();
                restAreaRepository.findByStdRestCd(apiRestCode).ifPresent(area -> {
                    area.setGasolinePrice(parsePriceInt(node.path("gasolinePrice").asText()));
                    area.setDiselPrice(parsePriceInt(node.path("diselPrice").asText()));
                    area.setLpgPrice(parsePriceInt(node.path("lpgPrice").asText()));
                    area.setOilCompany(node.path("oilCompany").asText());
                    area.setTelNo(node.path("telNo").asText());
                    restAreaRepository.save(area);
                });
            }
        } catch (Exception e) {
            log.error("유가 수집 에러: {}", e.getMessage());
        }
    }

    private Integer parsePriceInt(String priceStr) {
        if (priceStr == null || priceStr.trim().isEmpty() || priceStr.equals("-") || priceStr.equals("null"))
            return 0;
        try {
            String cleanPrice = priceStr.replaceAll("[^0-9]", "");
            return cleanPrice.isEmpty() ? 0 : Integer.parseInt(cleanPrice);
        } catch (Exception e) {
            return 0;
        }
    }

    // 거리 찾기 기능임 ,, Ai
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(Math.toRadians(lat1)) * Math.sin(Math.toRadians(lat2)) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.cos(Math.toRadians(theta));
        dist = Math.acos(dist);
        dist = Math.toDegrees(dist);
        return dist * 60 * 1.1515 * 1.609344;
    }

    // 경로 상의 휴게소 필터링 메서드
    public List<RestAreaResponseDto> getRestAreasOnPath(List<Point> routePoints, double radiusKm) {
        return originalData.stream()
                .filter(area -> routePoints.stream()
                        .anyMatch(point -> calculateDistance(point.getY(), point.getX(), area.getY(),
                                area.getX()) <= radiusKm))
                .collect(Collectors.toList());
    }

    public List<RestAreaResponseDto> getSortedGasStations(List<RestAreaResponseDto> list, String fuelType,
            boolean ascending) {
        return list.stream()
                .filter(area -> area.getType().contains("주유소")) // 주유소만 필터링
                .sorted((a, b) -> {
                    Double priceA = a.getPriceByFuelType(fuelType); // 유종에 따른 가격 가져오기
                    Double priceB = b.getPriceByFuelType(fuelType);

                    // 가격 정보가 없는 경우(0 또는 null) 맨 뒤로 보냄
                    if (priceA == null || priceA <= 0)
                        return 1;
                    if (priceB == null || priceB <= 0)
                        return -1;

                    return ascending ? Double.compare(priceA, priceB) : Double.compare(priceB, priceA);
                })
                .collect(Collectors.toList());
    }
}