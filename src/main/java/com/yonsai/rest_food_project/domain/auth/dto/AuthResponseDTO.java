package com.yonsai.rest_food_project.domain.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class AuthResponseDTO {
    private String accessToken;
    private String nickname;
    private String email;
    private String password;
    private int level;
}
