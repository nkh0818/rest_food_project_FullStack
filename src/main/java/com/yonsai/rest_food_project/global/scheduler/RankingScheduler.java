package com.yonsai.rest_food_project.global.scheduler;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class RankingScheduler {

    private final RedisTemplate<String, String> redisTemplate;

    // 초 분 시 일 월 요일
    @Scheduled(cron = "0 0 0 * * *")
    public void resetDailyRanking() {
        redisTemplate.delete("ranking:search:daily");
        log.info("🧹 [Scheduler 실행] 어제 날짜 인기 검색어 데이터를 초기화했습니다.");
    }
}