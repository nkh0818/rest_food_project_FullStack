package com.yonsai.rest_food_project.domain.review.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.yonsai.rest_food_project.domain.review.dto.ReviewRequestDTO;
import com.yonsai.rest_food_project.domain.review.dto.ReviewResponseDTO;
import com.yonsai.rest_food_project.domain.review.dto.ReviewUpdateRequestDTO;
import com.yonsai.rest_food_project.domain.user.entity.User;

public interface ReviewService {

    ReviewResponseDTO createReview(Long userId, ReviewRequestDTO dto);

    List<ReviewResponseDTO> getReviewsByRestArea(String restAreaId, Long userId);

    List<ReviewResponseDTO> getReviewsByFood(Long foodId);

    List<ReviewResponseDTO> getMyReviews(Long userId);

    ReviewResponseDTO getReview(Long reviewId);

    Page<ReviewResponseDTO> getCommunityReviews(Pageable pageable);

    void updateReview(Long reviewId, Long userId, ReviewUpdateRequestDTO dto);

    void deleteReview(Long reviewId, User currentUser);

    Double getAverageRating(String restAreaId);

    Long getReviewCount(String restAreaId);

    void likeReview(Long reviewId, Long userId);

    void unlikeReview(Long reviewId, Long userId);

    boolean isLiked(Long reviewId, Long userId);
}
