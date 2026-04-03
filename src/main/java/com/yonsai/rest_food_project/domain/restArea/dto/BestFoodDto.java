package com.yonsai.rest_food_project.domain.restArea.dto;

import lombok.*;

@Getter
@Setter // 추가 (데이터 바인딩 시 안전하게)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BestFoodDto {
    private Long id;
    private String restAreaId; // 000 제거된 ID (예: "615")
    private String name; // 음식명 (foodName)
    private String restArea; // 휴게소명 (restArea.name)
    private String type; // "FOOD" 고정
    private int price;
    private double rating;
    private int reviews;
    private String image;
}