package com.yonsai.rest_food_project.domain.restArea.controller;

import com.yonsai.rest_food_project.domain.restArea.dto.RestAreaResponseDto;
import com.yonsai.rest_food_project.domain.restArea.entity.Food;
import com.yonsai.rest_food_project.domain.restArea.entity.RestArea;
import com.yonsai.rest_food_project.domain.restArea.entity.RestAreaEvent;
import com.yonsai.rest_food_project.domain.restArea.repository.FoodRepository;
import com.yonsai.rest_food_project.domain.restArea.repository.RestAreaEventRepository;
import com.yonsai.rest_food_project.domain.restArea.repository.RestAreaRepository;
import com.yonsai.rest_food_project.domain.restArea.service.KakaoNaviService;
import com.yonsai.rest_food_project.domain.restArea.service.RestAreaDataService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/restareas")
@RequiredArgsConstructor
public class RestAreaTestController {

    private final KakaoNaviService kakaoNaviService;
    private final RestAreaDataService restAreaDataService;
    private final RestAreaRepository restAreaRepository;
    private final FoodRepository foodRepository;
    private final RestAreaEventRepository eventRepository;

    @GetMapping("/search")
    public List<RestAreaResponseDto> search(@RequestParam String start, @RequestParam String end) {
        Map<String, Object> routeData = kakaoNaviService.getRouteWithRestAreas(start, end);
        if (routeData == null)
            return new ArrayList<>();

        List<Map<String, String>> kakaoRestAreas = (List<Map<String, String>>) routeData.get("restAreas");
        List<RestAreaResponseDto> combinedResult = new ArrayList<>();

        for (Map<String, String> kakao : kakaoRestAreas) {
            String name = kakao.get("name");
            double x = Double.parseDouble(kakao.get("x"));
            double y = Double.parseDouble(kakao.get("y"));

            RestAreaResponseDto dbInfo = restAreaDataService.findBestMatch(name, x, y);

            if (dbInfo != null) {
                String coreName = dbInfo.getDbName() != null ? dbInfo.getDbName() : name;
                coreName = coreName.replaceAll("휴게소", "").replaceAll("주유소", "").replaceAll(" ", "").trim();

                List<RestArea> matchingStations = restAreaRepository.findByNameContaining(coreName);
                for (RestArea entity : matchingStations) {
                    // 1. 휴게소 본체인 경우 -> 진짜 휴게소 코드(stdRestCd)를 세팅함
                    if (!entity.getName().contains("주유소") && !entity.getName().contains("충전소")) {
                        dbInfo.setStdRestCd(entity.getStdRestCd());
                    }
                    // 2. 주유소인 경우 -> 유가 정보를 덮어씀
                    if (entity.getName().contains("주유소") || entity.getName().contains("충전소")) {
                        if (entity.getGasolinePrice() != null && entity.getGasolinePrice() > 0) {
                            dbInfo.setGasolinePrice(Double.valueOf(entity.getGasolinePrice()));
                            dbInfo.setDieselPrice(Double.valueOf(entity.getDiselPrice()));
                            dbInfo.setLpgPrice(Double.valueOf(entity.getLpgPrice()));
                            if (entity.getOilCompany() != null && !entity.getOilCompany().isEmpty()) {
                                dbInfo.setType(entity.getOilCompany());
                            }
                        }
                    }
                }
                combinedResult.add(dbInfo);
            }
        }
        return combinedResult;
    }

    @GetMapping("/detail/{stdRestCd}")
    public ResponseEntity<Map<String, Object>> getRestAreaDetail(@PathVariable String stdRestCd) {
        Optional<RestArea> optionalArea = restAreaRepository.findByStdRestCd(stdRestCd);
        if (!optionalArea.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        // DB에서 휴게소 정보 꺼내기
        RestArea restArea = optionalArea.get();
        String coreName = restArea.getName().replaceAll("휴게소", "").replaceAll("주유소", "").replaceAll(" ", "").trim();
        List<RestArea> matchingStations = restAreaRepository.findByNameContaining(coreName);

        for (RestArea station : matchingStations) {
            if (station.getName().contains("주유소") || station.getName().contains("충전소")) {
                // 주유소의 가격 정보를 휴게소 객체에 덮어쓰기
                restArea.setGasolinePrice(station.getGasolinePrice());
                restArea.setDiselPrice(station.getDiselPrice());
                restArea.setLpgPrice(station.getLpgPrice());
                restArea.setOilCompany(station.getOilCompany());
                break;
            }
        }

        // 3. 음식과 이벤트 찾기
        List<Food> foods = foodRepository.findByRestAreaId(restArea.getId());
        List<RestAreaEvent> events = eventRepository.findByStdRestCd(stdRestCd);

        Map<String, Object> result = new HashMap<>();
        result.put("info", restArea);
        result.put("food", foods);
        result.put("events", events);

        return ResponseEntity.ok(result);
    }
}