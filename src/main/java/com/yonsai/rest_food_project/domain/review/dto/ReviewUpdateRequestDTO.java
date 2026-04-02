package com.yonsai.rest_food_project.domain.review.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviewUpdateRequestDTO {

    private String content;
    private Integer rating;
    private String tag;
}