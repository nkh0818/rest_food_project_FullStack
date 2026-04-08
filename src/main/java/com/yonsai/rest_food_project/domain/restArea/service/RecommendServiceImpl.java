package com.yonsai.rest_food_project.domain.restArea.service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yonsai.rest_food_project.domain.restArea.dto.RestAreaResponseDto;
import com.yonsai.rest_food_project.domain.restArea.entity.RestArea;
import com.yonsai.rest_food_project.domain.restArea.repository.RestAreaRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class RecommendServiceImpl implements RecommendService {

    private final RestAreaRepository restAreaRepository;

    @Override
    public List<RestAreaResponseDto> getAiRecommendations(String companion, String priority) {
        // 1. 후보군 가져오기 (전체 가져오기보다는 적절히 상위권 위주로 1차 필터링 권장)
        List<RestArea> allAreas = restAreaRepository.findAll();

        // 2. 점수 계산 로직
        return allAreas.stream()
            .map(area -> {
                double score = 0;

                // [A] 기본 점수 (별점) : 5점 만점 기준 -> 10점 만점으로 환산
                score += (area.getRating() != null ? area.getRating() : 0) * 2;

                // [B] AI 감성 분석 점수 반영 (POSITIVE면 가산점) 🚩
                if ("POSITIVE".equalsIgnoreCase(area.getAiScore())) {
                    score += 25.0; // 긍정 리뷰가 많은 곳은 우선순위 대폭 상승
                } else if ("NEGATIVE".equalsIgnoreCase(area.getAiScore())) {
                    score -= 10.0; // 부정 리뷰가 많으면 감점
                }

                Set<String> tags = (area.getAiTags() != null) ? area.getAiTags() : Set.of();
                
                if ("food".equals(priority)) {
                    if (tags.contains("맛집") || tags.contains("맛있는")) score += 40;
                    if ("POSITIVE".equalsIgnoreCase(area.getAiScore())) score += 10; // 맛집인데 긍정이면 금상첨화
                } 
                else if ("scenery".equals(priority)) {
                    if (tags.contains("경치") || tags.contains("전망") || tags.contains("뷰")) score += 40;
                } 
                else if ("event".equals(priority)) {
                    if (tags.contains("테마") || tags.contains("공원") || tags.contains("체험")) score += 40;
                }

                // [D] 동행자(Companion) 가중치
                if ("group".equals(companion)) {
                    // 여럿이 갈 때는 리뷰 수가 많은 '검증된' 큰 곳 선호
                    if (area.getReviewCount() != null && area.getReviewCount() > 50) score += 15;
                } else if ("solo".equals(companion)) {
                    // 혼자일 때는 가산점보다는 무난한 곳 위주 (필요시 로직 추가 가능)
                    score += 5;
                }

                return new RecommendTemp(area, score);
            })
            // 3. 점수 정렬
            .sorted((a, b) -> Double.compare(b.getScore(), a.getScore())) 
            // 4. 결과 반환 (상위 3개)
            .limit(3)
            .map(temp -> RestAreaResponseDto.fromEntity(temp.getArea()))
            .collect(Collectors.toList());
    }

    // 내부 계산용 Helper Class
    private static class RecommendTemp {
        private final RestArea area;
        private final double score;

        public RecommendTemp(RestArea area, double score) {
            this.area = area;
            this.score = score;
        }

        public double getScore() { return score; }
        public RestArea getArea() { return area; }
    }
}
