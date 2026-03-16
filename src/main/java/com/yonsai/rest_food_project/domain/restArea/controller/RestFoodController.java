package com.yonsai.rest_food_project.domain.restArea.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.yonsai.rest_food_project.domain.restArea.entity.Food;
import com.yonsai.rest_food_project.domain.restArea.entity.RestArea;
import com.yonsai.rest_food_project.domain.restArea.repository.FoodRepository;
import com.yonsai.rest_food_project.domain.restArea.repository.RestAreaRepository;
import com.yonsai.rest_food_project.domain.restArea.service.KakaoNaviService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/food") // 모든 경로는 /food로 시작 (예: /food/list, /food/1)
public class RestFoodController {

    private final RestAreaRepository restAreaRepository;
    private final FoodRepository foodRepository;
    private final KakaoNaviService kakaoNaviService; // 카카오 네비 서비스 추가됨

    // 1. 리스트 출력 (검색, 경로, 주유소 정렬 포함)
    @GetMapping("/list")
    public String list(
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "start", required = false) String start,
            @RequestParam(value = "end", required = false) String end,
            @RequestParam(value = "fuelType", defaultValue = "gasoline") String fuelType,
            @RequestParam(value = "sortOrder", defaultValue = "asc") String sortOrder,
            Model model) {

        List<RestArea> allList = new ArrayList<>();
        String finalSearch = (search != null) ? search.trim() : "";

        // 1-1. 경로 기반 카카오 네비 검색 (출발지, 목적지 좌표가 있을 때)
        if (start != null && !start.isEmpty() && end != null && !end.isEmpty()) {
            Map<String, Object> naviResult = kakaoNaviService.getRouteWithRestAreas(start, end);

            if (naviResult != null && naviResult.get("restAreas") != null) {
                // 경고 방지를 위해 명시적 캐스팅 처리
                @SuppressWarnings("unchecked")
                List<Map<String, String>> extractedAreas = (List<Map<String, String>>) naviResult.get("restAreas");
                // aa
                for (Map<String, String> areaData : extractedAreas) {
                    String areaName = areaData.get("name");

                    // 카카오가 넘겨준 해당 휴게소의 실제 좌표
                    double kakaoX = Double.parseDouble(areaData.get("x"));
                    double kakaoY = Double.parseDouble(areaData.get("y"));

                    String coreName = areaName.replaceAll("휴게소|주유소|충전소|의광장", "").trim();
                    List<RestArea> found = restAreaRepository.findByNameContaining(coreName);

                    if (found.isEmpty() && coreName.length() >= 2) {
                        found = restAreaRepository.findByNameContaining(coreName.substring(0, 2));
                    }

                    if (!found.isEmpty()) {
                        // ⭐ 1. 카카오 좌표와 가장 가까운 진짜 휴게소(내 진행 방향)를 찾습니다.
                        RestArea closestArea = null;
                        double minDistance = Double.MAX_VALUE;

                        for (RestArea a : found) {
                            double dist = calculateDistance(kakaoY, kakaoX, a.getLatitude(), a.getLongitude());
                            if (dist < minDistance) {
                                minDistance = dist;
                                closestArea = a;
                            }
                        }

                        // ⭐ 2. 진짜 내 방향의 휴게소 이름에서 괄호 (방향) 글자를 뽑아냅니다. (예: "기흥(부산)휴게소" -> "(부산)")
                        String targetDirection = "";
                        if (closestArea != null && closestArea.getName().contains("(")) {
                            int startIdx = closestArea.getName().indexOf("(");
                            int endIdx = closestArea.getName().indexOf(")");
                            if (startIdx != -1 && endIdx != -1) {
                                targetDirection = closestArea.getName().substring(startIdx, endIdx + 1);
                            }
                        }

                        // ⭐ 3. 뽑아낸 방향과 똑같은 녀석들만 리스트에 넣습니다. (반대편 상행선은 걸러짐!)
                        for (RestArea matchedArea : found) {
                            // 방향 괄호가 있고, 뽑아낸 방향과 이름이 다르면 가차 없이 스킵!
                            if (!targetDirection.isEmpty() && !matchedArea.getName().contains(targetDirection)) {
                                continue;
                            }

                            boolean isDuplicate = allList.stream()
                                    .anyMatch(a -> a.getId().equals(matchedArea.getId()));

                            if (!isDuplicate) {
                                allList.add(matchedArea);
                                System.out
                                        .println("🎯 찐 매칭: 카카오[" + areaName + "] -> DB[" + matchedArea.getName() + "]");
                            }
                        }
                    }
                }

            }
            // vhf
        }
        // 1-2. 단일 키워드 검색 (경로 없이 검색어만 쳤을 때)
        else if (!finalSearch.isEmpty()) {
            allList = restAreaRepository.findByNameContainingOrRouteNameContaining(finalSearch, finalSearch);
        }
        // 1-3. 아무 조건도 없으면 전체 다 가져오기
        else {
            allList = restAreaRepository.findAll();
        }

        // 맛집 휴게소 리스트 생성 (주유소, 충전소, 쉼터 제외)
        List<RestArea> restAreas = allList.stream()
                .filter(area -> !area.getName().contains("주유소") &&
                        !area.getName().contains("충전소") &&
                        !area.getName().contains("쉼터"))
                .toList();

        // 최저가 주유소 리스트 생성 및 정렬 기능 적용
        List<RestArea> gasStations = allList.stream()
                .filter(area -> area.getName().contains("주유소") ||
                        area.getName().contains("충전소") ||
                        (area.getGasolinePrice() != null && area.getGasolinePrice() > 0))
                .sorted((a, b) -> {
                    // 유종에 맞는 가격 가져오기
                    Integer priceA = getPriceByFuelType(a, fuelType);
                    Integer priceB = getPriceByFuelType(b, fuelType);

                    // 0원이거나 null이면 무조건 맨 밑으로 보내기 (1 반환)
                    if (priceA == null || priceA == 0)
                        return 1;
                    if (priceB == null || priceB == 0)
                        return -1;

                    // 오름차순(최저가순)과 내림차순(최고가순) 분기
                    if ("asc".equals(sortOrder)) {
                        return priceA.compareTo(priceB);
                    } else {
                        return priceB.compareTo(priceA);
                    }
                })
                .toList();

        // 화면(HTML)으로 변수 넘기기
        model.addAttribute("restAreas", restAreas);
        model.addAttribute("gasStations", gasStations);
        model.addAttribute("search", finalSearch);
        model.addAttribute("fuelType", fuelType);
        model.addAttribute("sortOrder", sortOrder);

        return "food/list";
    }

    // [중요!] 여기가 비어있어서 에러가 났던 곳입니다. /{} 로 채웠습니다.
    // 2. 특정 휴게소 클릭 시 상세 메뉴판 보여주기
    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        RestArea area = restAreaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 휴게소가 없습니다."));

        // 만약 현재 클릭한 곳이 주유소가 아니라면, 같은 이름을 가진 주유소 정보를 DB에서 찾아봅니다.
        if (area.getGasolinePrice() == null || area.getGasolinePrice() == 0) {
            String shortName = area.getName().length() > 2 ? area.getName().substring(0, 2) : area.getName();
            restAreaRepository.findAll().stream()
                    .filter(a -> a.getName().contains(shortName) && a.getName().contains("주유소"))
                    .findFirst()
                    .ifPresent(gasStation -> {
                        // 주유소에서 찾은 기름값을 현재 area 객체에 잠시 빌려옵니다.
                        area.setGasolinePrice(gasStation.getGasolinePrice());
                        area.setDiselPrice(gasStation.getDiselPrice());
                        area.setLpgPrice(gasStation.getLpgPrice());
                        area.setOilCompany(gasStation.getOilCompany());
                        area.setTelNo(gasStation.getTelNo());
                    });
        }

        // 해당 휴게소의 음식 리스트 조회
        List<Food> foods = foodRepository.findByRestAreaId(id);

        model.addAttribute("area", area);
        model.addAttribute("foods", foods);

        return "food/detail";
    }

    // 주유소 정렬 시 유종(휘발유, 경유, LPG)에 따라 가격을 추출하는 내부 도우미 메서드
    private Integer getPriceByFuelType(RestArea area, String fuelType) {
        if ("diesel".equals(fuelType))
            return area.getDiselPrice();
        if ("lpg".equals(fuelType))
            return area.getLpgPrice();
        return area.getGasolinePrice(); // 기본값은 휘발유
    } // 두 좌표 간의 거리를 계산하는 도우미 메서드 (단위: km)

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(Math.toRadians(lat1)) * Math.sin(Math.toRadians(lat2)) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.cos(Math.toRadians(theta));
        dist = Math.acos(dist);
        dist = Math.toDegrees(dist);
        return dist * 60 * 1.1515 * 1.609344;
    }
}