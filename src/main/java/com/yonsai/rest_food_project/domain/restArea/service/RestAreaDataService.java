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
import java.util.Comparator;
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

    private List<RestAreaResponseDto> originalData = new ArrayList<>();
    private List<RestAreaResponseDto> currentSearchResult = new ArrayList<>();

    @Value("${road-service-key}")
    private String serviceKey;

    public void initData(List<RestAreaResponseDto> dataList) {
        this.originalData = dataList;
        this.currentSearchResult = new ArrayList<>(dataList);
        log.info("✅ 메모리 데이터 로드 완료: {}건", dataList.size());
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

    /**
     * 개선된 findBestMatch: 에러 방어 및 거리순 정렬 추가
     */
    public RestAreaResponseDto findBestMatch(String kakaoName, double kX, double kY) {
        if (kakaoName == null || originalData.isEmpty())
            return null;

        // 1. 카카오 이름에서 '휴게소'와 공백 제거 (예: "가평휴게소" -> "가평")
        String cleanKa = kakaoName.replace("휴게소", "").replace(" ", "").trim();

        return originalData.stream()
                .filter(db -> {
                    String dbName = db.getDbName() != null ? db.getDbName() : "";

                    // 2. DB 이름 전처리: 괄호와 그 안의 내용 제거, 공백/휴게소 제거
                    // 예: "가평(춘천)휴게소" -> "가평"
                    String cleanDb = dbName.replaceAll("\\(.*?\\)", "")
                            .replace("휴게소", "")
                            .replace(" ", "")
                            .trim();

                    // 3. 거리 계산
                    double dist = calculateDistance(kY, kX, db.getY(), db.getX());

                    // 4. [핵심] search 메서드와 로직 100% 통일
                    // 가평이든 죽전이든 어느 한 쪽이 포함만 되어 있으면 무조건 통과
                    boolean isNameMatch = (!cleanDb.isEmpty() && !cleanKa.isEmpty()) &&
                            (cleanDb.contains(cleanKa) || cleanKa.contains(cleanDb));

                    // 이름이 매칭되거나, 혹은 이름이 달라도 거리가 5km 이내면 후보군에 포함
                    // (가평휴게소처럼 부지가 넓은 곳을 위해 거리 제한을 5km로 대폭 상향)
                    return isNameMatch || dist < 5.0;
                })
                // 5. 후보들 중 가장 가까운 놈으로 최종 선택
                .min(Comparator.comparingDouble(db -> calculateDistance(kY, kX, db.getY(), db.getX())))
                .orElse(null);
    }

    // ================= [공공데이터 수집 기능] =================

    public void fetchAndSaveAllData() {
        fetchRestAreaBasics();
        List<RestArea> allAreas = restAreaRepository.findAll();
        for (RestArea area : allAreas) {
            try {
                fetchAndSaveAllFoods(area);
                Thread.sleep(100);
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

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(Math.toRadians(lat1)) * Math.sin(Math.toRadians(lat2)) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.cos(Math.toRadians(theta));
        dist = Math.acos(dist);
        dist = Math.toDegrees(dist);
        return dist * 60 * 1.1515 * 1.609344;
    }

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
                .filter(area -> area.getType() != null && area.getType().contains("주유소"))
                .sorted((a, b) -> {
                    Double priceA = a.getPriceByFuelType(fuelType);
                    Double priceB = b.getPriceByFuelType(fuelType);

                    if (priceA == null || priceA <= 0)
                        return 1;
                    if (priceB == null || priceB <= 0)
                        return -1;

                    return ascending ? Double.compare(priceA, priceB) : Double.compare(priceB, priceA);
                })
                .collect(Collectors.toList());
    }
}