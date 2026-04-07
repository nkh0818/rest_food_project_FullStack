package com.yonsai.rest_food_project.domain.review.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BlockResponseDTO {
    private Long userId;     // 차단된 유저의 ID
    private String nickname; // 차단된 유저의 닉네임
    private String createdAt; // 차단한 날짜
}
