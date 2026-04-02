package com.yonsai.rest_food_project.domain.review.dto;

import lombok.Getter;

@Getter
public class RequestDTO {

    private Long restAreaId;
    private Long foodId;
    private String content;
    private Integer rating;
    private String tag;

}
