package com.yonsai.rest_food_project.domain.ranking.Controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.yonsai.rest_food_project.global.common.RedisService;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Set;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/api/ranking")
@RequiredArgsConstructor
public class RankingController {
    private final RedisService redisService;

    // 상위 10개 키워드 가져오기
    @GetMapping("/search")
    public ResponseEntity<Set<String>> getSearchRanking() {
        return ResponseEntity.ok(redisService.getTopSearchKeywords(10));
    }

    // 데일리 랭킹 가져오기
    @GetMapping("/search/daily")
    public ResponseEntity<List<String>> getDailyRanking() {
        return ResponseEntity.ok(redisService.getDailyRanking());
    }

    // 전체 랭킹 가져오기
    @GetMapping("/search/all")
    public ResponseEntity<List<String>> getAllTimeRanking() {
        return ResponseEntity.ok(redisService.getAllRanking());
    }

}
