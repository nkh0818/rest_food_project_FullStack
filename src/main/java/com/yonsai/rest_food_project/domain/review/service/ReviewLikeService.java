package com.yonsai.rest_food_project.domain.review.service;

import com.yonsai.rest_food_project.domain.review.dto.ReviewLikeResponseDTO;

public interface ReviewLikeService {

    ReviewLikeResponseDTO likeReview(Long userId, Long reviewId);

    ReviewLikeResponseDTO unlikeReview(Long userId, Long reviewId);
}
