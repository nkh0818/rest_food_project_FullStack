package com.yonsai.rest_food_project.domain.review.service;

import java.util.List;

import com.yonsai.rest_food_project.domain.review.dto.ReviewLikeResponseDTO;
import com.yonsai.rest_food_project.domain.review.dto.ReviewRequestDTO;
import com.yonsai.rest_food_project.domain.review.dto.ReviewResponseDTO;
import com.yonsai.rest_food_project.domain.review.dto.ReviewUpdateRequestDTO;

public interface ReviewService {

    ReviewResponseDTO createReview(Long userId, ReviewRequestDTO dto);

    List<ReviewResponseDTO> getReviewsByRestArea(Long restAreaId, Long userId);

    List<ReviewResponseDTO> getReviewsByFood(Long foodId);

    ReviewResponseDTO getReview(Long reviewId);

    ReviewLikeResponseDTO likeReview(Long userId, Long reviewId);

    ReviewLikeResponseDTO unlikeReview(Long userId, Long reviewId);

    void updateReview(Long userId, Long reviewId, ReviewUpdateRequestDTO dto);

    void deleteReview(Long userId, Long reviewId);

    Double getAverageRating(Long restAreaId);

    Long getReviewCount(Long restAreaId);

    boolean isLiked(Long reviewId, Long userId);
}
