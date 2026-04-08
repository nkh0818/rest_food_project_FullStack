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

    /**
     * 1. 휴게소 기본 정보 + 음식 데이터 수집
     * (기본 목록을 먼저 만들고, 각 휴게소별 음식을 긁어옵니다.)
     */
    @GetMapping("/init-foods")
    public String initFoods() {
        restAreaDataService.fetchAndSaveAllData();
        return "휴게소 기본 정보 및 음식 데이터 수집 시작! (서버 로그를 확인하세요)";
    }

    /**
     * 2. 실시간 유가 정보 수집 + 휴게소 머지 (중요!)
     * 서비스에서 이름을 변경한 updateOilPricesAndMerge를 호출합니다.
     */
    @GetMapping("/init-oil")
    public String initOil() {
        // 기존 updateOilPricesOnly() 대신 통합된 메서드 호출
        restAreaDataService.updateOilPricesAndMerge();
        return "실시간 유가 업데이트 및 휴게소 데이터 머지 완료!";
    }

    /**
     * 3. 휴게소 이벤트 정보 수집
     */
    @GetMapping("/init-events")
    public String initEvents() {
        eventService.fetchAndSaveAllEvents();
        return "이벤트 데이터 수집 완료!";
    }

    // 1. 기존: 휴게소 목록만 (A단계)
    // @GetMapping("/init-data-reg")
    // public String initBasic() {
    // restAreaDataService.fetchAndSaveAllData(); // 휴게소 리스트만 저장 혹시몰라서 주석
    // return "기초 정보 수집 완료";
    // }

    // // 휴게소 + 음식
    // @GetMapping("/init-foods")
    // public String initFoods() {
    // restAreaDataService.fetchAndSaveAllData();
    // return "음식 데이터 수집 시작 (로그를 확인하세요)";
    // }

    // // 유가 정보만 수집
    // @GetMapping("/init-oil")
    // public String initOil() {
    // restAreaDataService.updateOilPricesOnly();
    // return "실시간 유가 정보 업데이트 완료!";
    // }

    // @GetMapping("/init-events")
    // public String initEvents() {
    // eventService.fetchAndSaveAllEvents();
    // return "이벤트 데이터 수집 완료!";
    // }
    // http://localhost:8080/init-foods
    // http://localhost:8080/init-data-reg
    // http://localhost:8080/init-oil
    // http://localhost:8080/init-events 휴게소 이벤트들
}