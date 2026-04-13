package com.yonsai.rest_food_project.domain.ranking.Controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.yonsai.rest_food_project.domain.ranking.dto.SearchRankingRequestDTO;
import com.yonsai.rest_food_project.domain.ranking.dto.TotalRankingResponseDTO;
import com.yonsai.rest_food_project.domain.ranking.service.RankingService;
import com.yonsai.rest_food_project.global.common.RedisService;

import org.springframework.web.bind.annotation.RequestBody;
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

    /**
     * 1. 검색어 기록 저장 (POST)
     * 프론트에서 검색할 때 호출
     */
    @PostMapping("/record")
    public ResponseEntity<Void> recordSearchKeyword(@Valid @RequestBody SearchRankingRequestDTO request) {
        redisService.incrementSearchCount(request.getKeyword());
        return ResponseEntity.ok().build();
    }

    /**
     * 2. 종합 랭킹 조회 (GET)
     * 실시간 검색어(Daily), 베스트 리뷰, 급상승 휴게소 등을 한 번에 가져옴
     * 프론트 메인이나 검색 메인에서 사용
     */
    @GetMapping("/totalrank")
    public ResponseEntity<TotalRankingResponseDTO> getRankings() {
        return ResponseEntity.ok(rankingService.getTotalRankings());
    }

    // ----------- 개별 조회 API (필요한 경우만 사용) ------------

    /**
     * 데일리 실시간 검색어 랭킹 (상위 10개)
     */
    @GetMapping("/search/daily")
    public ResponseEntity<List<String>> getDailyRanking() {
        return ResponseEntity.ok(redisService.getDailyRanking());
    }

    /**
     * 전체 누적 검색어 랭킹
     */
    @GetMapping("/search/all")
    public ResponseEntity<List<String>> getAllTimeRanking() {
        return ResponseEntity.ok(redisService.getAllRanking());
    }
    
    @GetMapping("/search")
    public ResponseEntity<Set<String>> getSearchRanking() {
        return ResponseEntity.ok(redisService.getTopSearchKeywords(10));
    }
}