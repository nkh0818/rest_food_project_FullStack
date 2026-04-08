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

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/restareas")
@RequiredArgsConstructor
public class RestAreaTestController {

    private final KakaoNaviService kakaoNaviService;
    private final RestAreaDataService restAreaDataService;
    private final RestAreaRepository restAreaRepository;
    private final FoodRepository foodRepository;
    private final RestAreaEventRepository eventRepository;

    /**
     * 경로 검색 (카카오 내비 기반 + DB 매칭)
     */
    @GetMapping("/search")
    public List<RestAreaResponseDto> search(@RequestParam String start, @RequestParam String end) {
        Map<String, Object> routeData = kakaoNaviService.getRouteWithRestAreas(start, end);
        if (routeData == null) return new ArrayList<>();

        List<Map<String, String>> kakaoRestAreas = (List<Map<String, String>>) routeData.get("restAreas");
        List<RestAreaResponseDto> combinedResult = new ArrayList<>();

        for (Map<String, String> kakao : kakaoRestAreas) {
            String name = kakao.get("name");
            double x = Double.parseDouble(kakao.get("x"));
            double y = Double.parseDouble(kakao.get("y"));

            RestAreaResponseDto dbInfo = restAreaDataService.findBestMatch(name, x, y);

            if (dbInfo != null) {
                combinedResult.add(dbInfo);
            }
        }
        return combinedResult;
    }

    /**
     * 상세페이지 (음식, 이벤트, 머지된 유가 정보 포함)
     */
    @GetMapping("/detail/{stdRestCd}")
    public ResponseEntity<Map<String, Object>> getRestAreaDetail(@PathVariable String stdRestCd) {
        // 휴게소 정보 가져오기
        RestArea restArea = restAreaRepository.findByStdRestCd(stdRestCd)
                .orElseThrow(() -> new RuntimeException("휴게소를 찾을 수 없습니다."));

        // 음식과 이벤트 찾기
        List<Food> foods = foodRepository.findByRestAreaId(restArea.getId());
        List<RestAreaEvent> events = eventRepository.findByStdRestCd(stdRestCd);

        Map<String, Object> result = new HashMap<>();
        result.put("info", restArea);
        result.put("food", foods);
        result.put("events", events);

        return ResponseEntity.ok(result);
    }

    /** 랜덤 휴게소 리스트*/
    @GetMapping("/random")
    public ResponseEntity<List<RestAreaResponseDto>> getRandomRestAreas(
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(restAreaDataService.getRandomAreas(size));
    }

    /** 이름 검색 (일반 검색창용)*/
    @GetMapping("/search-name")
    public ResponseEntity<Page<RestAreaResponseDto>> searchByRestAreaName(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("휴게소 이름 검색 시작: keyword={}, page={}", keyword, page);
        return ResponseEntity.ok(restAreaDataService.searchAreas(keyword, page, size));
    }
}