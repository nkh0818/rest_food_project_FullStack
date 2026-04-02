package com.yonsai.rest_food_project.domain.review.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yonsai.rest_food_project.domain.review.entity.Review;
import com.yonsai.rest_food_project.domain.review.repository.ReviewRepository;
import com.yonsai.rest_food_project.domain.user.entity.User;
import com.yonsai.rest_food_project.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ActivityScoreServiceImpl implements ActivityScoreService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;

    @Transactional
    @Override
    public int addScoreForCreatedReview(User user, Review review) {
        int score = 0;

        // 휴게소 리뷰 작성
        score += 10;

        // 음식 리뷰 작성
        if (review.getFood() != null) {
            score += 10;
        }

        // 사진 포함 리뷰
        if (review.getImageUrl() != null && !review.getImageUrl().isBlank()) {
            score += 10;
        }

        // 같은 휴게소 방문 횟수 기반 점수 (현재 리뷰가 저장된 뒤 호출된다고 가정)
        long reviewCountAtRestArea = reviewRepository.countByUserIdAndRestAreaId(
                user.getId(), review.getRestArea().getId());

        if (reviewCountAtRestArea == 1) {
            score += 5; // 첫 방문 휴게소 리뷰
        } else if (reviewCountAtRestArea > 1) {
            score += 3; // 재방문 리뷰
        }

        user.addActivityScore(score);
        user.addRewardPoint(score);

        userRepository.save(user);
        return score;
    }

    @Transactional
    @Override
    public void addScoreForLikeReceived(User user) {
        user.addActivityScore(5);
        userRepository.save(user);
    }

    @Transactional
    @Override
    public void addScoreForLikeGiven(User user) {
        user.addActivityScore(3);
        userRepository.save(user);
    }

    @Transactional
    @Override
    public void subtractScoreForConfirmedReport(User user) {
        user.addActivityScore(-10);
        userRepository.save(user);
    }

    @Override
    public Double getAverageRating(Long restAreaId) {
        return reviewRepository.findAverageRatingByRestAreaId(restAreaId);
    }

    @Override
    public Long getReviewCount(Long restAreaId) {
        return reviewRepository.countByRestAreaId(restAreaId);
    }
}
