package com.yonsai.rest_food_project.domain.review.dto;

import java.util.List;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor // JSON 데이터를 변환하기 위해 사용합니다
public class ReviewRequestDTO {

    @NotNull(message = "평점을 입력해 주세요.")
    @Min(value = 1, message = "1점부터 입력할 수 있습니다.")
    @Max(value = 5, message = "최대 5점까지 가능합니다.")
    private Integer rating;

    @NotBlank(message = "리뷰 내용을 입력해 주세요.")
    private String content;

    @NotBlank(message = "휴게소 정보가 존재하지 않습니다.")
    private String restAreaId;

    private Long foodId;
    private String imageUrl;
    private String tag;
    private List<String> tags; // 기존 유지하고 추가

    // 거리 계산을 위해 리액트에서 넘겨받아야 하는 사용자의 현재 위치
    @NotNull(message = "사용자의 현재 위도 정보가 필요합니다.")
    private Double userLat;

    @NotNull(message = "사용자의 현재 경도 정보가 필요합니다.")
    private Double userLon;
}
