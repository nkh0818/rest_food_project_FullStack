package com.yonsai.rest_food_project.domain.review.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReviewLikeResponseDTO {
    private Long reviewId;
    private int likeCount;
    private boolean liked;
}