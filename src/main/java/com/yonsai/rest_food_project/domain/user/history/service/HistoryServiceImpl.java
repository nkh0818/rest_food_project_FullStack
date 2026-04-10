package com.yonsai.rest_food_project.domain.user.history.service;

import com.yonsai.rest_food_project.domain.review.entity.Review;
import com.yonsai.rest_food_project.domain.review.repository.ReviewRepository;
import com.yonsai.rest_food_project.domain.user.entity.User;
import com.yonsai.rest_food_project.domain.user.history.dto.HistoryResponseDTO;
import com.yonsai.rest_food_project.domain.restArea.entity.RestArea;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HistoryServiceImpl implements HistoryService {

    private final ReviewRepository reviewRepository;

    @Transactional(readOnly = true)
    @Override
    public List<HistoryResponseDTO> getUserHistory(User user) {
        // 1. 해당 유저가 작성한 모든 리뷰를 최신순으로 가져옴
        List<Review> userReviews = reviewRepository.findByUserOrderByCreatedAtDesc(user);

        return userReviews.stream().map((Review review) -> {
            RestArea restArea = review.getRestArea();

            String routeName = restArea.getRouteName() != null ? restArea.getRouteName() : "고속도로";
            String restName = restArea.getName() != null ? restArea.getName() : "이름 없는 휴게소";

            // 2. 방문지 상세 정보
            HistoryResponseDTO.StopInfo stopInfo = HistoryResponseDTO.StopInfo.builder()
                    .stdRestCd(restArea.getStdRestCd())
                    .name(restName) // 안전하게 변수 사용
                    .tags(restArea.getAiTags())
                    .score(restArea.getAiScore() != null ? restArea.getAiScore() : "없음")
                    .theme(determineTheme(restArea.getRouteName()))
                    .build();

            // 3. 전체 DTO 조립
            return HistoryResponseDTO.builder()
                    .id(review.getId())
                    .date(review.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy.MM.dd")))
                    .routeName(routeName)
                    // 제목을 "경부고속도로 여정" 혹은 "안성휴게소 탐방" 식으로 동적으로!
                    .title(restArea.getRouteName() != null ? routeName + " 여정" : restName + " 탐험")
                    .aiSummary(restArea.getAiSummary() != null ? restArea.getAiSummary() : "맛있는 추억을 쌓았어요!")
                    .stops(List.of(stopInfo))
                    .build();
        }).collect(Collectors.toList());
    }

    // 노선명에 따라 프론트엔드 UI 테마를 결정하는 로직
    private String determineTheme(String routeName) {
        if (routeName == null)
            return "emerald";
        if (routeName.contains("경부"))
            return "orange";
        if (routeName.contains("영동"))
            return "blue";
        if (routeName.contains("서해안"))
            return "indigo";
        return "emerald"; // 기본값
    }
}