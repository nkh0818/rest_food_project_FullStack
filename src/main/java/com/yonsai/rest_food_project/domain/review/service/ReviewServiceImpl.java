package com.yonsai.rest_food_project.domain.review.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yonsai.rest_food_project.domain.restArea.entity.*;
import com.yonsai.rest_food_project.domain.restArea.repository.*;
import com.yonsai.rest_food_project.domain.review.dto.*;
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
@Transactional(readOnly = true)
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final RestAreaRepository restAreaRepository;
    private final FoodRepository foodRepository;
    private final ReviewLikeRepository reviewLikeRepository;

    @Transactional
    @Override
    public ReviewResponseDTO createReview(Long userId, ReviewRequestDTO dto) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RoadQuestException("사용자를 찾을 수 없습니다. ID: " + userId));

        RestArea restArea = restAreaRepository.findById(dto.getRestAreaId())
                .orElseThrow(() -> new RoadQuestException("휴게소를 찾을 수 없습니다. ID: " + dto.getRestAreaId()));

        Food food = null;
        if (dto.getFoodId() != null) {
            food = foodRepository.findById(dto.getFoodId()).orElse(null);
        }

        String tagString = (dto.getTags() == null || dto.getTags().isEmpty())
                ? null
                : String.join(",", dto.getTags());

        Review review = Review.builder()
                .user(user)
                .restArea(restArea)
                .food(food)
                .rating(dto.getRating())
                .content(dto.getContent())
                .tag(tagString)
                .imageUrl(dto.getImageUrl())
                .build();

        return ReviewResponseDTO.from(reviewRepository.save(review));
    }

    @Override
    public List<ReviewResponseDTO> getReviewsByRestArea(Long restAreaId, Long userId) {
        return reviewRepository.findByRestAreaId(restAreaId)
                .stream()
                .map(review -> {
                    boolean liked = false;
                    if (userId != null) {
                        liked = reviewLikeRepository.existsByReviewIdAndUserId(review.getId(), userId);
                    }
                    return ReviewResponseDTO.from(review, liked);
                })
                .toList();
    }

    @Override
    public List<ReviewResponseDTO> getReviewsByFood(Long foodId) {
        return reviewRepository.findByFoodId(foodId)
                .stream()
                .map(ReviewResponseDTO::from)
                .toList();
    }

    @Override
    public ReviewResponseDTO getReview(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RoadQuestException("리뷰를 찾을 수 없습니다. ID: " + reviewId));
        return ReviewResponseDTO.from(review);
    }

    @Transactional
    @Override
    public void updateReview(Long reviewId, ReviewUpdateRequestDTO dto) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RoadQuestException("리뷰를 찾을 수 없습니다. ID: " + reviewId));
        review.update(dto.getContent(), dto.getRating(), dto.getTag());
    }

    @Transactional
    @Override
    public void deleteReview(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RoadQuestException("삭제할 리뷰가 존재하지 않습니다."));
        reviewRepository.delete(review);
    }

    @Override
    public Double getAverageRating(Long restAreaId) {
        return reviewRepository.getAverageRating(restAreaId);
    }

    @Override
    public Long getReviewCount(Long restAreaId) {
        return reviewRepository.countByRestAreaId(restAreaId);
    }

    @Transactional
    @Override
    public void likeReview(Long reviewId, Long userId) {
        // 1. 리뷰 조회
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RoadQuestException("해당 리뷰를 찾을 수 없습니다."));

        // 2. 본인 리뷰 체크
        if (review.getUser().getId().equals(userId)) {
            throw new RoadQuestException("자신의 리뷰에는 좋아요를 누를 수 없습니다.");
        }

        // 3. 중복 좋아요 체크
        if (reviewLikeRepository.existsByReviewIdAndUserId(reviewId, userId)) {
            throw new RoadQuestException("이미 좋아요를 누른 리뷰입니다.");
        }

        // 4. 좋아요 저장
        User user = userRepository.getReferenceById(userId);
        ReviewLike reviewLike = ReviewLike.builder()
                .review(review)
                .user(user)
                .build();

        reviewLikeRepository.save(reviewLike);

        // 5. 좋아요 수 증가
        review.addLike();
    }

    @Transactional
    @Override
    public void unlikeReview(Long reviewId, Long userId) {

        ReviewLike like = reviewLikeRepository.findByReviewIdAndUserId(reviewId, userId)
                .orElseThrow(() -> new RoadQuestException("좋아요 기록을 찾을 수 없습니다."));

        reviewLikeRepository.delete(like);

        like.getReview().removeLike();
    }

    @Override
    public boolean isLiked(Long reviewId, Long userId) {
        return reviewLikeRepository.existsByReviewIdAndUserId(reviewId, userId);
    }
}


/*
N+1 문제 해결: 지금은 리뷰 개수가 적어 괜찮지만, 나중에 리뷰가 많아지면 getReviewsByRestArea의 루프 안에서 실행되는 existsBy... 쿼리가 성능 저하를 일으킬 수 있습니다.

이미지 처리: 현재 imageUrl을 문자열로 받고 있는데, 실제로 이미지를 업로드하고 S3 같은 저장소의 URL을 받아오는 로직이 추가되면 ReviewService의 복잡도가 올라갈 수 있습니다.

데이터 무결성: addLike()와 removeLike() 메서드 내부에 동시성 이슈를 방지하기 위한 로직을 고려해 보는 것도 좋은 공부가 될 것입니다.
*/