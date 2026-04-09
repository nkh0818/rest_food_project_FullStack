package com.yonsai.rest_food_project.domain.restArea.service;

import java.util.List;
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

    @Transactional
    @Override
public List<RestAreaResponseDto> getAiRecommendations(String companion, String priority) {
    List<RestArea> allAreas = restAreaRepository.findAll();

    return allAreas.stream()
        .map(area -> {
            double score = 0;

            // 기본 점수: 별점이 없으면 기본 3.0점이라도 부여해서 순위를 만듦
            double rating = (area.getRating() != null) ? area.getRating() : 3.0;
            score += rating * 10;

            String tags = (area.getAiTags() != null) ? area.getAiTags() : "";
            if ("food".equals(priority)) {
                if (tags.contains("맛") || tags.contains("식사") || tags.contains("맛집")) score += 40;
            } else if ("scenery".equals(priority)) {
                if (tags.contains("경치") || tags.contains("뷰") || tags.contains("전망")) score += 40;
            } else if ("event".equals(priority)) {
                if (tags.contains("테마") || tags.contains("공원") || tags.contains("체험")) score += 40;
            }
            score += Math.random(); 

            return new RecommendTemp(area, score);
        })
        .sorted((a, b) -> Double.compare(b.getScore(), a.getScore()))
        .limit(10)
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
