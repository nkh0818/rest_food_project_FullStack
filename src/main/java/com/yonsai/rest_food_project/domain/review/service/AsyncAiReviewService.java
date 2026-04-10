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

// [비동기 AI 분석 서비스] 리뷰 내용을 바탕으로 AI가 휴게소 요약, 태그 추출, 점수 산출을 비동기적으로 수행

@Service
@RequiredArgsConstructor
@Slf4j
public class AsyncAiReviewService {

    // [필터링] AI가 생성한 태그 중 의미가 모호하거나 불필요한 단어들을 걸러내기 위한 블랙리스트
    private static final Set<String> TAG_BLACKLIST = Set.of(
            "재방문", "만족", "불만족", "추천", "비추", "별로",
            "좋음", "나쁨", "기대이하", "좋아요", "싫어요", "실망");

    private final ReviewSummarizer reviewSummarizer;
    private final RestAreaRepository restAreaRepository;

    // [AI 분석 및 업데이트] 리뷰 텍스트를 분석하여 요약문과 태그를 생성하고 휴게소 정보에 반영
    // @Async를 통해 사용자가 리뷰 등록 완료를 기다리지 않도록 백그라운드에서 실행
    @Async
    @Transactional
    public void analyzeAndUpdate(String stdRestCd, String combinedContent) {
        try {
            RestArea restArea = restAreaRepository.findByStdRestCd(stdRestCd)
                    .orElseThrow(() -> new RoadQuestException("휴게소를 찾을 수 없습니다: " + stdRestCd));

            ReviewResult aiResult = reviewSummarizer.analyze(combinedContent);

            // 블랙리스트를 활용하여 무의미한 태그 필터링
            Set<String> filteredTags = aiResult.tags().stream()
                    .filter(tag -> !TAG_BLACKLIST.contains(tag))
                    .collect(Collectors.toSet());

            String tagsString = filteredTags.stream()
                    .collect(Collectors.joining(", ", "[", "]"));

            // AI 분석 결과를 휴게소 엔티티에 업데이트
            restArea.setAiSummary(aiResult.summary());
            restArea.setAiTags(tagsString);
            restArea.setAiScore(aiResult.score());

            restAreaRepository.save(restArea);

            log.info("[AI분석 완료] restArea={}, summary={}, score={}", stdRestCd, aiResult.summary(), aiResult.score());

        } catch (Exception e) {
            // 비동기 로직이므로 메인 흐름에 영향을 주지 않도록 예외를 로깅으로 처리
            log.error("[AI분석 오류] restArea={}, error={}", stdRestCd, e.getMessage());
        }
    }
}
