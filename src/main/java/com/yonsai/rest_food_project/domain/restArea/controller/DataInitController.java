package com.yonsai.rest_food_project.domain.restArea.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.yonsai.rest_food_project.domain.restArea.service.RestAreaDataService;
import com.yonsai.rest_food_project.domain.restArea.service.RestAreaEventService;

@RestController
@RequiredArgsConstructor
public class DataInitController {

    private final RestAreaDataService restAreaDataService;
    private final RestAreaEventService eventService;

    // 1. 기존: 휴게소 목록만 (A단계)
    @GetMapping("/init-data-reg")
    public String initBasic() {
        // restAreaDataService.fetchAndSaveAll(); // 휴게소 리스트만 저장 혹시몰라서 주석
        return "기초 정보 수집 완료";
    }

    // 2. 신규: 휴게소 + 음식 + 유가 싹 다 유가아님
    @GetMapping("/init-foods")
    public String initFoods() {
        restAreaDataService.fetchAndSaveAllData();
        return "음식 데이터 수집 시작 (로그를 확인하세요)";
    }

    // 2. 유가 정보만 수집
    @GetMapping("/init-oil")
    public String initOil() {
        restAreaDataService.updateOilPricesOnly();
        return "실시간 유가 정보 업데이트 완료!";
    }

    @GetMapping("/init-events")
    public String initEvents() {
        eventService.fetchAndSaveAllEvents();
        return "이벤트 데이터 수집 완료!";
    }
    // http://localhost:8080/init-data-reg
    // http://localhost:8080/init-foods
    // http://localhost:8080/init-oil
    // http://localhost:8080/init-events 휴게소 이벤트들
}