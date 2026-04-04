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

    private final RestAreaDataService dataService;
    private final RestAreaEventService eventService;

    /**
     * 휴게소 기초 정보 업데이트 (매달 1일 새벽 2시)
     * 자주 바뀌지 않는 노선명, 휴게소명 등을 최신화
     */
    @Scheduled(cron = "0 0 2 1 * *")
    public void scheduledBasicInit() {
        log.info("[스케줄러] 휴게소 기초 목록 업데이트 시작");
        dataService.fetchRestAreaBasics();
    }

    /**
     * 음식 데이터 전체 수집 (매주 월요일 새벽 3시)
     * 메뉴나 가격 변동을 반영하기 위해 일주일에 한 번 전체 갱신
     */
    @Scheduled(cron = "0 0 3 * * 1")
    public void scheduledFoodInit() {
        log.info("[스케줄러] 음식 데이터 전체 수집 시작");
        dataService.fetchAndSaveAllData();
    }

    /**
     * 유가 및 이벤트 정보 업데이트 (매일 새벽 5시)
     * 유가는 매일 변하므로 매일 새벽에 업데이트와 머지
     */
    @Scheduled(cron = "0 0 5 * * *")
    public void scheduledDailyUpdate() {
        log.info("[스케줄러] 실시간 유가 정보 및 휴게소 머지 시작");
        dataService.updateOilPricesAndMerge();

        log.info("[스케줄러] 이벤트 정보 수집 시작");
        eventService.fetchAndSaveAllEvents();
    }
}
