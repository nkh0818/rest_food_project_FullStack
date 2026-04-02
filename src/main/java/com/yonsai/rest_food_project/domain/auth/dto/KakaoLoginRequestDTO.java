package com.yonsai.rest_food_project.domain.auth.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class KakaoLoginRequestDTO {
    private String accessToken;
}
