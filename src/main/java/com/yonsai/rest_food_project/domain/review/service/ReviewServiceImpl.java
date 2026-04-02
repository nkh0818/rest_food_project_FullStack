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
    private final ImageService imageService;
    
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
        
        String imageUrl = null;
        if (dto.getImage() != null && !dto.getImage().isEmpty()) {
            imageUrl = imageService.upload(dto.getImage());
        }

        Review review = Review.builder()
                .user(user)
                .restArea(restArea)
                .food(food)
                .rating(dto.getRating())
                .content(dto.getContent())
                .tag(tagString)
                .imageUrl(imageUrl)
                .build();

        return ReviewResponseDTO.from(reviewRepository.save(review));
    }

    @Override
    public List<ReviewResponseDTO> getReviewsByRestArea(Long restAreaId, Long userId) {

        // 1. 리뷰 먼저 조회
        List<Review> reviews = reviewRepository.findByRestAreaId(restAreaId);

        // 2. 좋아요를 한 번에 조회
        List<Long> likedReviewIds = (userId != null)
                ? reviewLikeRepository.findReviewIdsByUserId(userId)
                : List.of();

        // 3. 반복문 안에서 DB 조회 제거
        return reviews.stream()
                .map(review -> {

                    // 4. contains로 체크 (메모리에서 처리)
                    boolean liked = likedReviewIds.contains(review.getId());

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
    public void updateReview(Long userId, Long reviewId, ReviewUpdateRequestDTO dto) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RoadQuestException("리뷰를 찾을 수 없습니다. ID: " + reviewId));
        
        if (!review.getUser().getId().equals(userId)) {
            throw new RoadQuestException("수정 권한이 없습니다.");
        }
        
        review.update(dto.getContent(), dto.getRating(), dto.getTag());
    }

    @Transactional
    @Override
    public void deleteReview(Long userId, Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RoadQuestException("삭제할 리뷰가 존재하지 않습니다."));
        
        if (!review.getUser().getId().equals(userId)) {
            throw new RoadQuestException("삭제 권한이 없습니다.");
        }
        
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
    public ReviewLikeResponseDTO likeReview(Long userId, Long reviewId) {

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RoadQuestException("해당 리뷰를 찾을 수 없습니다."));

        if (review.getUser().getId().equals(userId)) {
            throw new RoadQuestException("자신의 리뷰에는 좋아요를 누를 수 없습니다.");
        }

        if (reviewLikeRepository.existsByReviewIdAndUserId(reviewId, userId)) {
            throw new RoadQuestException("이미 좋아요를 누른 리뷰입니다.");
        }

        User user = userRepository.findById(userId)
        	    .orElseThrow(() -> new RoadQuestException("사용자 없음"));

        ReviewLike reviewLike = ReviewLike.builder()
                .review(review)
                .user(user)
                .build();

        reviewLikeRepository.save(reviewLike);

        long likeCount = reviewLikeRepository.countByReviewId(review.getId());

        return new ReviewLikeResponseDTO(
                review.getId(),
                (int) likeCount,
                true
        );
    }

    @Transactional
    @Override
    public ReviewLikeResponseDTO unlikeReview(Long userId, Long reviewId) {

        // 1. 좋아요 조회
        ReviewLike like = reviewLikeRepository.findByReviewIdAndUserId(reviewId, userId)
                .orElseThrow(() -> new RoadQuestException("좋아요 기록을 찾을 수 없습니다."));

        // 2. 삭제
        reviewLikeRepository.delete(like);

        // 3. review 객체 가져오기
        Review review = like.getReview();

        // 4. 반환
        long likeCount = reviewLikeRepository.countByReviewId(review.getId());

        return new ReviewLikeResponseDTO(
                review.getId(),
                (int) likeCount,
                false
        );
    }

    @Override
    public boolean isLiked(Long reviewId, Long userId) {
        return reviewLikeRepository.existsByReviewIdAndUserId(reviewId, userId);
    }
}