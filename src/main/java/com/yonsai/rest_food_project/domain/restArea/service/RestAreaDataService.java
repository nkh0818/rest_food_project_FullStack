package com.yonsai.rest_food_project.domain.restArea.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yonsai.rest_food_project.domain.restArea.entity.Food;
import com.yonsai.rest_food_project.domain.restArea.entity.RestArea;
import com.yonsai.rest_food_project.domain.restArea.repository.FoodRepository;
import com.yonsai.rest_food_project.domain.restArea.repository.RestAreaRepository;
import com.yonsai.rest_food_project.global.common.LocationUtils;
import com.yonsai.rest_food_project.global.common.RedisService;
import com.yonsai.rest_food_project.domain.restArea.dto.RestAreaResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

    private final KakaoNaviService kakaoNaviService;
    private final RestAreaRepository restAreaRepository;
    private final FoodRepository foodRepository;
    private final LocationUtils locationUtils;
    private final RedisService redisService;

    private RestTemplate restTemplate = new RestTemplate();
    private ObjectMapper mapper = new ObjectMapper();

    private List<RestAreaResponseDto> originalData = new ArrayList<>();
    private List<RestAreaResponseDto> currentSearchResult = new ArrayList<>();

    @Value("${road-service-key}")
    private String serviceKey;

    public void initData(List<RestAreaResponseDto> dataList) {
        this.originalData = dataList;
        this.currentSearchResult = new ArrayList<>(dataList);
        log.info("✅ 메모리 데이터 로드 완료: {}건", dataList.size());
    }

    // ================= [검색/위치/필터 기능] =================

    public List<RestAreaResponseDto> getNearby(Double userLat, Double userLng) {
        List<RestArea> entities = restAreaRepository.findAll();
        return entities.stream()
                .map(entity -> {
                    double distInMeter = locationUtils.getDistance(userLat, userLng, entity.getLatitude(),
                            entity.getLongitude());
                    double distInKm = Math.round((distInMeter / 1000.0) * 10) / 10.0;
                    return RestAreaResponseDto.fromEntity(entity, distInKm);
                })
                .sorted(Comparator.comparing(RestAreaResponseDto::getDistance))
                .limit(10)
                .collect(Collectors.toList());
    }

    public List<RestAreaResponseDto> search(String keyword) {
        List<RestArea> results = restAreaRepository.findByNameContaining(keyword);
        return results.stream()
                .map(entity -> RestAreaResponseDto.fromEntity(entity, 0.0))
                .collect(Collectors.toList());
    }

    public List<RestAreaResponseDto> filter(String type) {
        return currentSearchResult.stream()
                .filter(dto -> type.equals("GAS_STATION")
                        ? (dto.getGasolinePrice() != null && dto.getGasolinePrice() > 0)
                        : true)
                .collect(Collectors.toList());
    }

    public List<RestAreaResponseDto> reset() {
        this.currentSearchResult = new ArrayList<>(originalData);
        return currentSearchResult;
    }

    public RestAreaResponseDto findBestMatch(String kakaoName, double kX, double kY) {
        if (kakaoName == null || kakaoName.isEmpty())
            return null;
        String cleanKa = kakaoName.replaceAll("\\(.*?\\)", "")
                .replace("휴게소", "").replace("주유소", "")
                .replaceAll("\\s+", "").trim();

        List<RestArea> allAreas = restAreaRepository.findAll();

        return allAreas.stream()
                .filter(entity -> {
                    String dbRawName = entity.getName() != null ? entity.getName() : "";
                    String cleanDb = dbRawName.replaceAll("\\(.*?\\)", "")
                            .replace("휴게소", "").replace("주유소", "")
                            .replaceAll("\\s+", "").trim();

                    if (!cleanDb.isEmpty() && !cleanKa.isEmpty() &&
                            (cleanDb.contains(cleanKa) || cleanKa.contains(cleanDb))) {
                        return true;
                    }

                    if (entity.getLatitude() != null && entity.getLongitude() != null) {
                        double dist = locationUtils.getDistance(kY, kX, entity.getLatitude(), entity.getLongitude())
                                / 1000.0;
                        return dist < 2.0;
                    }
                    return false;
                })
                .min(Comparator.comparingDouble(entity -> {
                    if (entity.getLatitude() == null)
                        return 9999.0;
                    return locationUtils.getDistance(kY, kX, entity.getLatitude(), entity.getLongitude());
                }))
                .map(entity -> {
                    double dist = 0.0;
                    if (entity.getLatitude() != null) {
                        dist = locationUtils.getDistance(kY, kX, entity.getLatitude(), entity.getLongitude()) / 1000.0;
                    }
                    log.info("✅ 매칭 성공! 카카오: [{}] <-> DB: [{}]", kakaoName, entity.getName());
                    return RestAreaResponseDto.fromEntity(entity, Math.round(dist * 10) / 10.0);
                })
                .orElseGet(() -> {
                    log.warn("매칭 실패: {}", kakaoName);
                    return null;
                });
    }

    // ================= [페이징 지원 검색/랜덤] =================

    public List<RestAreaResponseDto> getRandomAreas(int size) {
        Pageable pageable = PageRequest.of(0, size);
        return restAreaRepository.findRandomAreas(pageable).stream()
                .map(entity -> RestAreaResponseDto.fromEntity(entity, 0.0))
                .collect(Collectors.toList());
    }

    public Page<RestAreaResponseDto> searchAreas(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return restAreaRepository.findByNameContaining(keyword, pageable)
                .map(entity -> RestAreaResponseDto.fromEntity(entity, 0.0));
    }

    @Transactional
    public void updateOilPricesAndMerge() {
        log.info("==== 실시간 유가 수집 및 데이터 머지 시작 ====");
        int pageNo = 1;

        while (true) {
            String url = "https://data.ex.co.kr/openapi/business/curStateStation?key=" + serviceKey
                    + "&type=json&numOfRows=99&pageNo=" + pageNo;

            try {
                ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
                JsonNode list = mapper.readTree(response.getBody()).path("list");

                if (list.isMissingNode() || list.isEmpty())
                    break;

                for (JsonNode node : list) {
                    String apiRestCode = node.path("serviceAreaCode2").asText();
                    String stationName = node.path("serviceAreaName").asText();

                    restAreaRepository.findByStdRestCd(apiRestCode).ifPresent(area -> {
                        applyOilPricesOnly(area, node);
                    });

                    if (stationName.contains("주유소")) {
                        String pureName = stationName.replace("주유소", "").trim();
                        String searchName = pureName + "휴게소";

                        List<RestArea> matchingAreas = restAreaRepository.findByNameContaining(searchName);
                        for (RestArea restArea : matchingAreas) {
                            applyOilPricesOnly(restArea, node);
                        }
                    }
                }
                pageNo++;
            } catch (Exception e) {
                log.error("❌ 유가 수집/머지 에러 (Page {}): {}", pageNo, e.getMessage());
                break;
            }
        }
        log.info("==== ✅ 모든 유가 데이터 머지 완료 ====");
    }

    private void applyOilPricesOnly(RestArea area, JsonNode node) {
        area.setGasolinePrice(parsePriceInt(node.path("gasolinePrice").asText()));
        area.setDiselPrice(parsePriceInt(node.path("diselPrice").asText()));
        area.setLpgPrice(parsePriceInt(node.path("lpgPrice").asText()));
        area.setOilCompany(node.path("oilCompany").asText());
        area.setTelNo(node.path("telNo").asText());
    }

    private Integer parsePriceInt(String priceStr) {
        if (priceStr == null || priceStr.trim().isEmpty() || priceStr.equals("-") || priceStr.equals("null"))
            return 0;
        try {
            return Integer.parseInt(priceStr.replaceAll("[^0-9]", ""));
        } catch (Exception e) {
            return 0;
        }
    }

    // ================= [데이터 수집 공통] =================

    @Transactional
    public void fetchAndSaveAllData() {

        fetchRestAreaBasics();
        List<RestArea> allAreas = restAreaRepository.findAll();

        for (RestArea area : allAreas) {
            try {
                fetchAndSaveAllFoods(area);
            } catch (Exception e) {
                log.error("!! {} 음식 수집 중 에러: {}", area.getName(), e.getMessage());
            }
        }
    }

    // ================= [1. 휴게소 기본 정보 및 좌표 수집] =================
    @Transactional
    public void fetchRestAreaBasics() {
        String url = "https://data.ex.co.kr/openapi/restinfo/hiwaySvarInfoList?key=" + serviceKey + "&type=json";
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            JsonNode list = mapper.readTree(response.getBody()).path("list");

            for (JsonNode node : list) {
                String stdRestCd = node.path("svarCd").asText();
                String apiAddr = node.path("svarAddr").asText();
                String name = node.path("svarNm").asText();

                double initialLng = node.path("gudX").asDouble(0.0);
                double initialLat = node.path("gudY").asDouble(0.0);

                double finalLng = initialLng;
                double finalLat = initialLat;

                if (finalLat == 0.0 && apiAddr != null && !apiAddr.isEmpty()) {
                    String coords = kakaoNaviService.getCoordinates(apiAddr);
                    if (coords != null && coords.contains(",")) {
                        String[] split = coords.split(",");
                        finalLng = Double.parseDouble(split[0]);
                        finalLat = Double.parseDouble(split[1]);
                    }
                }

                double effectiveLng = finalLng;
                double effectiveLat = finalLat;

                restAreaRepository.findByStdRestCd(stdRestCd).ifPresentOrElse(
                        area -> {
                            area.setLatitude(effectiveLat);
                            area.setLongitude(effectiveLng);
                            area.setLocation(apiAddr);
                        },
                        () -> {
                            RestArea area = RestArea.builder()
                                    .name(name)
                                    .stdRestCd(stdRestCd)
                                    .location(apiAddr)
                                    .latitude(effectiveLat)
                                    .longitude(effectiveLng)
                                    .gasolinePrice(0).diselPrice(0).lpgPrice(0)
                                    .build();
                            restAreaRepository.save(area);
                        });
            }
        } catch (Exception e) {
            log.error("❌ 수집 에러: {}", e.getMessage());
        }
    }

    public void fetchAndSaveAllFoods(RestArea area) throws Exception {
        String baseUrl = "https://data.ex.co.kr/openapi/restinfo/";
        processFoodApi(baseUrl + "restBestfoodList?key=" + serviceKey + "&type=json&stdRestCd=" + area.getStdRestCd(),
                area, "일반", "foodNm", "foodCost", "bestfoodyn");
        processFoodApi(baseUrl + "restVentureList?key=" + serviceKey + "&type=json&stdRestCd=" + area.getStdRestCd(),
                area, "청년창업", "bzNm", null, null);
        processFoodApi(baseUrl + "restBrandList?key=" + serviceKey + "&type=json&stdRestCd=" + area.getStdRestCd(),
                area, "브랜드", "brdName", null, null);
    }

    @Transactional
    public void processFoodApi(String url, RestArea area, String category, String nameField, String priceField,
            String bestField) {
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            if (response.getBody() == null)
                return;

            JsonNode root = mapper.readTree(response.getBody());
            JsonNode list = root.path("list");

            if (list.isMissingNode() || !list.isArray() || list.isEmpty()) {
                return;
            }

            for (JsonNode node : list) {
                String name = node.path(nameField).asText();

                if (name == null || name.trim().isEmpty() || name.equalsIgnoreCase("null")) {
                    continue;
                }

                int price = (priceField != null) ? node.path(priceField).asInt(0) : 0;
                int isBest = (bestField != null && "Y".equals(node.path(bestField).asText())) ? 1 : 0;

                Food food = Food.builder()
                        .foodName(name)
                        .price(price)
                        .category(category)
                        .isBest(isBest)
                        .restArea(area)
                        .build();

                foodRepository.save(food);
            }
        } catch (Exception e) {
            log.error("❌ [API 에러] URL: {}, 사유: {}", url, e.getMessage());
        }
    }
}