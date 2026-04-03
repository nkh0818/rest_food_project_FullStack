package com.yonsai.rest_food_project.domain.review.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yonsai.rest_food_project.domain.review.dto.ReviewLikeResponseDTO;
import com.yonsai.rest_food_project.domain.review.entity.Review;
import com.yonsai.rest_food_project.domain.review.entity.ReviewLike;
import com.yonsai.rest_food_project.domain.review.repository.ReviewLikeRepository;
import com.yonsai.rest_food_project.domain.review.repository.ReviewRepository;
import com.yonsai.rest_food_project.domain.user.entity.User;
import com.yonsai.rest_food_project.domain.user.repository.UserRepository;
import com.yonsai.rest_food_project.global.exception.RoadQuestException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReviewLikeServiceImpl implements ReviewLikeService {

    private final ReviewRepository reviewRepository;
    private final ReviewLikeRepository reviewLikeRepository;
    private final UserRepository userRepository;
    private final ActivityScoreService activityScoreService;
    private final TitleGrantService titleGrantService;

    @Transactional
    @Override
    public ReviewLikeResponseDTO likeReview(Long userId, Long reviewId) {

        User loginUser = userRepository.findById(userId)
                .orElseThrow(() -> new RoadQuestException("사용자 정보가 없습니다."));

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RoadQuestException("리뷰를 찾을 수 없습니다."));

        if (review.getUser().getId().equals(userId)) {
            throw new RoadQuestException("본인 리뷰에는 좋아요를 누를 수 없습니다.");
        }

        if (reviewLikeRepository.existsByReviewIdAndUserId(reviewId, userId)) {
            throw new RoadQuestException("이미 좋아요를 누른 리뷰입니다.");
        }

        reviewLikeRepository.save(ReviewLike.builder()
                .review(review)
                .user(loginUser)
                .build());

        review.addLike();

        activityScoreService.addScoreForLikeGiven(loginUser);
        activityScoreService.addScoreForLikeReceived(review.getUser());
        titleGrantService.checkAndGrantTitles(review.getUser());
        titleGrantService.checkAndGrantTitles(loginUser);

        return new ReviewLikeResponseDTO(review.getId(), review.getLikeCount(), true);
    }

    @Transactional
    @Override
    public ReviewLikeResponseDTO unlikeReview(Long userId, Long reviewId) {

        ReviewLike reviewLike = reviewLikeRepository.findByReviewIdAndUserId(reviewId, userId)
                .orElseThrow(() -> new RoadQuestException("좋아요를 누른 기록이 없습니다."));

        Review review = reviewLike.getReview();
        review.removeLike();

        reviewLikeRepository.delete(reviewLike);

        titleGrantService.checkAndGrantTitles(review.getUser());

        return new ReviewLikeResponseDTO(review.getId(), review.getLikeCount(), false);
    }
}
