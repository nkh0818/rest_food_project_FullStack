package com.yonsai.rest_food_project.domain.restArea.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.yonsai.rest_food_project.domain.restArea.entity.Food;
import com.yonsai.rest_food_project.domain.restArea.entity.RestArea;
import com.yonsai.rest_food_project.domain.restArea.repository.FoodRepository;
import com.yonsai.rest_food_project.domain.restArea.repository.RestAreaRepository;
import com.yonsai.rest_food_project.domain.restArea.service.KakaoNaviService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/food")
@CrossOrigin(origins = "http://localhost:5173") // 리액트 기본 포트 허용
public class RestFoodController {

    private final RestAreaRepository restAreaRepository;
    private final FoodRepository foodRepository;
    private final KakaoNaviService kakaoNaviService;

    // 리액트에서 데이터를 가져갈 "진짜" API 엔드포인트
    @GetMapping("/api/list")
    @ResponseBody // HTML 이동 없이 JSON 데이터만 반환
    public ResponseEntity<Map<String, Object>> getApiList(
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "start", required = false) String start,
            @RequestParam(value = "end", required = false) String end,
            @RequestParam(value = "fuelType", defaultValue = "gasoline") String fuelType,
            @RequestParam(value = "sortOrder", defaultValue = "asc") String sortOrder) {

        // 1. 데이터 필터링 및 로직 실행 (사용자님의 기존 로직과 동일)
        List<RestArea> allList = processSearch(search, start, end);

        // 2. 맛집 휴게소와 주유소 분리
        List<RestArea> restAreas = allList.stream()
                .filter(area -> !area.getName().contains("주유소") &&
                        !area.getName().contains("충전소") &&
                        !area.getName().contains("쉼터"))
                .collect(Collectors.toList());

        List<RestArea> gasStations = allList.stream()
                .filter(area -> area.getName().contains("주유소") || area.getName().contains("충전소"))
                .collect(Collectors.toList());

        // 3. 결과 맵 구성
        Map<String, Object> result = new HashMap<>();
        result.put("restAreas", restAreas);
        result.put("gasStations", gasStations);

        return ResponseEntity.ok(result);
    }

    // 기존 로직을 별도 메서드로 분리 (중복 방지)
    private List<RestArea> processSearch(String search, String start, String end) {
        List<RestArea> allList = new ArrayList<>();
        String finalSearch = (search != null) ? search.trim() : "";

        if (start != null && !start.isEmpty() && end != null && !end.isEmpty()) {
            Map<String, Object> naviResult = kakaoNaviService.getRouteWithRestAreas(start, end);
            if (naviResult != null && naviResult.get("restAreas") != null) {
                @SuppressWarnings("unchecked")
                List<Map<String, String>> extractedAreas = (List<Map<String, String>>) naviResult.get("restAreas");

                for (Map<String, String> areaData : extractedAreas) {
                    String areaName = areaData.get("name");
                    double kakaoX = Double.parseDouble(areaData.get("x"));
                    double kakaoY = Double.parseDouble(areaData.get("y"));

                    String coreName = areaName.replaceAll("휴게소|주유소|충전소|의광장", "").trim();
                    List<RestArea> found = restAreaRepository.findByNameContaining(coreName);

                    if (!found.isEmpty()) {
                        RestArea closestArea = null;
                        double minDistance = Double.MAX_VALUE;
                        for (RestArea a : found) {
                            double dist = calculateDistance(kakaoY, kakaoX, a.getLatitude(), a.getLongitude());
                            if (dist < minDistance) {
                                minDistance = dist;
                                closestArea = a;
                            }
                        }

                        String targetDirection = "";
                        if (closestArea != null && closestArea.getName().contains("(")) {
                            int startIdx = closestArea.getName().indexOf("(");
                            int endIdx = closestArea.getName().indexOf(")");
                            targetDirection = closestArea.getName().substring(startIdx, endIdx + 1);
                        }

                        for (RestArea matchedArea : found) {
                            if (!targetDirection.isEmpty() && !matchedArea.getName().contains(targetDirection))
                                continue;
                            if (allList.stream().noneMatch(a -> a.getId().equals(matchedArea.getId()))) {
                                allList.add(matchedArea);
                            }
                        }
                    }
                }
            }
        } else if (!finalSearch.isEmpty()) {
            allList = restAreaRepository.findByNameContainingOrRouteNameContaining(finalSearch, finalSearch);
        } else {
            allList = restAreaRepository.findAll();
        }
        return allList;
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(Math.toRadians(lat1)) * Math.sin(Math.toRadians(lat2)) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.cos(Math.toRadians(theta));
        dist = Math.acos(dist);
        dist = Math.toDegrees(dist);
        return dist * 60 * 1.1515 * 1.609344;
    }

}
