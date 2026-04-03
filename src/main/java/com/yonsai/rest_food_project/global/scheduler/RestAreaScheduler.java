package com.yonsai.rest_food_project.global.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.yonsai.rest_food_project.domain.restArea.service.RestAreaDataService;
import com.yonsai.rest_food_project.domain.restArea.service.RestAreaEventService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class RestAreaScheduler {

    private RestAreaDataService dataService;
    private RestAreaEventService eventService;

       // 1. 휴게소 기초 정보 (매달 1일 새벽 2시 - 자주 안 바뀌므로)
    @Scheduled(cron = "0 0 2 1 * *")
    public void scheduledBasicInit() {
        log.info("⏰ [스케줄러] 휴게소 기초 목록 업데이트 시작");
        dataService.fetchRestAreaBasics();
    }

    // 매주 월요일(1) 새벽 3시에 실행
    @Scheduled(cron = "0 0 3 * * 1")
    public void scheduledFoodInit() {
        log.info("⏰ [스케줄러] 음식 데이터 전체 수집 시작");
        dataService.fetchAndSaveAllData();
    }

    // 유가, 이벤트 정보 (매일 새벽 5시)
    @Scheduled(cron = "0 0 5 * * *")
    public void scheduledOilInit() {
        log.info("⏰ [스케줄러] 유가 정보 업데이트 시작");
        dataService.updateOilPricesOnly();

        log.info("⏰ [스케줄러] 이벤트 정보 수집 시작");
        eventService.fetchAndSaveAllEvents();
    }
    
}
