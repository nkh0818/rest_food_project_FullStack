package com.yonsai.rest_food_project.domain.review.service;

import java.util.List;

import com.yonsai.rest_food_project.domain.review.dto.ReviewRequestDTO;
import com.yonsai.rest_food_project.domain.review.dto.ReviewResponseDTO;
import com.yonsai.rest_food_project.domain.review.dto.ReviewUpdateRequestDTO;
import com.yonsai.rest_food_project.domain.user.entity.User;

public interface ReviewService {

    ReviewResponseDTO createReview(Long userId, ReviewRequestDTO dto);

    List<ReviewResponseDTO> getReviewsByRestArea(Long restAreaId, Long userId);

    List<ReviewResponseDTO> getReviewsByFood(Long foodId);

    ReviewResponseDTO getReview(Long reviewId);

    void updateReview(Long reviewId, Long userId, ReviewUpdateRequestDTO dto);

    void deleteReview(Long reviewId, User currentUser);

    Double getAverageRating(Long restAreaId);

    Long getReviewCount(Long restAreaId);

    void likeReview(Long reviewId, Long userId);

    void unlikeReview(Long reviewId, Long userId);

    boolean isLiked(Long reviewId, Long userId);
}
