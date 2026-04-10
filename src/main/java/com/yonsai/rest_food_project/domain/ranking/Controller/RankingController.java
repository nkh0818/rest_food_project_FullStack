package com.yonsai.rest_food_project.domain.ranking.Controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.yonsai.rest_food_project.domain.ranking.dto.SearchRankingRequestDTO;
import com.yonsai.rest_food_project.domain.ranking.dto.TotalRankingResponseDTO;
import com.yonsai.rest_food_project.domain.ranking.service.RankingService;
import com.yonsai.rest_food_project.global.common.RedisService;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Set;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@RestController
@RequestMapping("/api/ranking")
@RequiredArgsConstructor
public class RankingController {

    private final RedisService redisService;
    private final RankingService rankingService;

    @PostMapping("/record")
    public ResponseEntity<Void> recordSearchKeyword(@Valid @RequestBody SearchRankingRequestDTO request) {
        redisService.incrementSearchCount(request.getKeyword());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/totalrank")
    public ResponseEntity<TotalRankingResponseDTO> getRankings() {
        return ResponseEntity.ok(rankingService.getTotalRankings());
    }

    // -----------조회------------

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
