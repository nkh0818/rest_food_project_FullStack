package com.yonsai.rest_food_project.domain.review.service;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yonsai.rest_food_project.domain.ai.entity.ReviewResult;
import com.yonsai.rest_food_project.domain.ai.service.ReviewSummarizer;
import com.yonsai.rest_food_project.domain.restArea.entity.RestArea;
import com.yonsai.rest_food_project.domain.restArea.repository.RestAreaRepository;
import com.yonsai.rest_food_project.global.exception.RoadQuestException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AsyncAiReviewService {

    private static final Set<String> TAG_BLACKLIST = Set.of(
        "재방문", "만족", "불만족", "추천", "비추", "별로",
        "좋음", "나쁨", "기대이하", "좋아요", "싫어요", "실망"
    );

    private final ReviewSummarizer reviewSummarizer;
    private final RestAreaRepository restAreaRepository;

    @Async
    @Transactional
    public void analyzeAndUpdate(String stdRestCd, String combinedContent) {
        try {
            RestArea restArea = restAreaRepository.findByStdRestCd(stdRestCd)
                    .orElseThrow(() -> new RoadQuestException("휴게소를 찾을 수 없습니다: " + stdRestCd));

            ReviewResult aiResult = reviewSummarizer.analyze(combinedContent);

            Set<String> filteredTags = aiResult.tags().stream()
                    .filter(tag -> !TAG_BLACKLIST.contains(tag))
                    .collect(Collectors.toSet());

            restArea.setAiSummary(aiResult.summary());
            restArea.setAiTags(filteredTags);
            restArea.setAiScore(aiResult.score());

            restAreaRepository.save(restArea);

            log.info("[AI분석 완료] restArea={}, summary={}, score={}", stdRestCd, aiResult.summary(), aiResult.score());

        } catch (Exception e) {
            log.error("[AI분석 오류] restArea={}, error={}", stdRestCd, e.getMessage());
        }
    }
}
