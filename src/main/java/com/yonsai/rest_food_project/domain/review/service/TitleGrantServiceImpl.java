package com.yonsai.rest_food_project.domain.review.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yonsai.rest_food_project.domain.review.repository.ReviewRepository;
import com.yonsai.rest_food_project.domain.user.entity.Title;
import com.yonsai.rest_food_project.domain.user.entity.User;
import com.yonsai.rest_food_project.domain.user.entity.UserTitle;
import com.yonsai.rest_food_project.domain.user.repository.TitleRepository;
import com.yonsai.rest_food_project.domain.user.repository.UserRepository;
import com.yonsai.rest_food_project.domain.user.repository.UserTitleRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TitleGrantServiceImpl implements TitleGrantService {

    private final ReviewRepository reviewRepository;
    private final TitleRepository titleRepository;
    private final UserTitleRepository userTitleRepository;
    private final UserRepository userRepository;

    @Transactional
    @Override
    public void checkAndGrantTitles(User user) {
        long reviewCount = reviewRepository.countByUserId(user.getId());
        long photoReviewCount = reviewRepository.countByUserIdAndImageUrlIsNotNull(user.getId());
        long distinctRestAreaCount = reviewRepository.countDistinctRestAreaByUserId(user.getId());
        Long likeCountValue = reviewRepository.sumLikeCountByUserId(user.getId());
        int likeCount = likeCountValue != null ? likeCountValue.intValue() : 0;
        Double avg = reviewRepository.findAverageRatingByUserId(user.getId());
        double avgRating = avg != null ? avg : 0.0;
        int score = user.getActivityScore();

        // 점수 기반
        grantIfEligible(user, "초보 탐험가", score >= 50);
        grantIfEligible(user, "리뷰 탐험가", score >= 120);
        grantIfEligible(user, "휴게소 기록자", score >= 250);
        grantIfEligible(user, "휴게소 전문가", score >= 450);
        grantIfEligible(user, "로드마스터", score >= 700);
        grantIfEligible(user, "전설의 여행자", score >= 1200);

        // 리뷰 수 기반
        grantIfEligible(user, "첫 방문자", reviewCount >= 1);
        grantIfEligible(user, "리뷰 탐험가", reviewCount >= 5);
        grantIfEligible(user, "길 위의 기록자", reviewCount >= 10);
        grantIfEligible(user, "휴게소 전문가", reviewCount >= 20);
        grantIfEligible(user, "리뷰 마스터", reviewCount >= 50);

        // 사진 기반
        grantIfEligible(user, "첫 인증자", photoReviewCount >= 1);
        grantIfEligible(user, "여행 사진가", photoReviewCount >= 5);
        grantIfEligible(user, "휴게소 포토그래퍼", photoReviewCount >= 15);
        grantIfEligible(user, "현장 기록 마스터", photoReviewCount >= 30);

        // 좋아요 기반
        grantIfEligible(user, "공감받는 리뷰어", likeCount >= 10);
        grantIfEligible(user, "인기 리뷰어", likeCount >= 50);
        grantIfEligible(user, "믿고 보는 리뷰어", likeCount >= 100);
        grantIfEligible(user, "커뮤니티 스타", likeCount >= 130);

        // 휴게소 개수 기반
        grantIfEligible(user, "초보 정복자", distinctRestAreaCount >= 3);
        grantIfEligible(user, "길 위의 탐험가", distinctRestAreaCount >= 10);
        grantIfEligible(user, "전국 휴게소 도전자", distinctRestAreaCount >= 20);
        grantIfEligible(user, "로드 정복자", distinctRestAreaCount >= 40);

        // 노선 기반
        long gyeongbuCount = reviewRepository.countDistinctRestAreaByUserIdAndRouteNames(
                user.getId(), List.of("경부고속도로"));
        long seohaeanCount = reviewRepository.countDistinctRestAreaByUserIdAndRouteNames(
                user.getId(), List.of("서해안고속도로"));
        long yeongdongCount = reviewRepository.countDistinctRestAreaByUserIdAndRouteNames(
                user.getId(), List.of("영동고속도로"));
        long namhaeCount = reviewRepository.countDistinctRestAreaByUserIdAndRouteNames(
                user.getId(), List.of("남해고속도로"));

        grantIfEligible(user, "경부 길잡이", gyeongbuCount >= 5);
        grantIfEligible(user, "서해안 길잡이", seohaeanCount >= 5);
        grantIfEligible(user, "영동 길잡이", yeongdongCount >= 5);
        grantIfEligible(user, "남해 길잡이", namhaeCount >= 5);

        // 음식 카테고리 기반
        long porkCutletCount = reviewRepository.countFoodCategoryReviews(user.getId(), "PORK_CUTLET");
        long spicyPorkCount = reviewRepository.countFoodCategoryReviews(user.getId(), "SPICY_PORK");
        long noodleCount = reviewRepository.countFoodCategoryReviews(user.getId(), "NOODLE");
        long soupStewCount = reviewRepository.countFoodCategoryReviews(user.getId(), "SOUP_STEW");
        long riceMealCount = reviewRepository.countFoodCategoryReviews(user.getId(), "RICE_MEAL");
        long snackCount = reviewRepository.countFoodCategoryReviews(user.getId(), "SNACK");

        grantIfEligible(user, "돈까스 입문자", porkCutletCount >= 3);
        grantIfEligible(user, "돈까스 탐험가", porkCutletCount >= 7);
        grantIfEligible(user, "돈까스 헌터", porkCutletCount >= 15);

        grantIfEligible(user, "제육 입문자", spicyPorkCount >= 3);
        grantIfEligible(user, "제육 탐험가", spicyPorkCount >= 7);
        grantIfEligible(user, "제육 헌터", spicyPorkCount >= 15);

        grantIfEligible(user, "면 요리 애호가", noodleCount >= 3);
        grantIfEligible(user, "면 요리 탐험가", noodleCount >= 7);
        grantIfEligible(user, "면 요리 마스터", noodleCount >= 15);

        grantIfEligible(user, "뜨끈한 한 끼 입문자", soupStewCount >= 3);
        grantIfEligible(user, "국물 탐험가", soupStewCount >= 7);
        grantIfEligible(user, "국밥 장인", soupStewCount >= 15);

        grantIfEligible(user, "한 끼 해결사", riceMealCount >= 3);
        grantIfEligible(user, "든든한 식사 탐험가", riceMealCount >= 7);
        grantIfEligible(user, "정식 마스터", riceMealCount >= 15);

        grantIfEligible(user, "간식 수집가", snackCount >= 3);
        grantIfEligible(user, "간식 탐험가", snackCount >= 7);
        grantIfEligible(user, "휴게소 간식 헌터", snackCount >= 15);

        // 전문가 플래그
        user.setReviewExpert(
                reviewCount >= 10 && avgRating >= 3.5 && likeCount >= 30 && getSafeReportCount(user.getId()) == 0);
        user.setFoodExpert(reviewRepository.countByUserIdAndFoodIsNotNull(user.getId()) >= 15 && likeCount >= 20);
        user.setPhotoExpert(photoReviewCount >= 10);
        user.setRestAreaExpert(distinctRestAreaCount >= 15);

        updateCurrentTitle(user);
        userRepository.save(user);
    }

    private int getSafeReportCount(Long userId) {
        return reviewRepository.findByUserId(userId)
                .stream()
                .mapToInt(r -> r.getReportCount())
                .sum();
    }

    private void grantIfEligible(User user, String titleName, boolean condition) {
        if (!condition) {
            return;
        }

        Title title = titleRepository.findByTitleName(titleName).orElse(null);
        if (title == null) {
            return;
        }

        boolean alreadyOwned = userTitleRepository.existsByUserIdAndTitleTitleId(user.getId(), title.getTitleId());
        if (alreadyOwned) {
            return;
        }

        userTitleRepository.save(UserTitle.builder()
                .user(user)
                .title(title)
                .build());
    }

    private void updateCurrentTitle(User user) {
        List<UserTitle> titles = userTitleRepository.findByUserId(user.getId());
        Title bestTitle = user.getCurrentTitle();

        for (UserTitle userTitle : titles) {
            Title title = userTitle.getTitle();
            if (title == null) {
                continue;
            }
            if (bestTitle == null || getSafePriority(title) > getSafePriority(bestTitle)) {
                bestTitle = title;
            }
        }

        user.setCurrentTitle(bestTitle);
    }

    private int getSafePriority(Title title) {
        return title.getPriority() == null ? 0 : title.getPriority();
    }
}
