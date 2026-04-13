package com.yonsai.rest_food_project.domain.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponseDTO {
    private String accessToken;
    private String nickname;
    private String email;
    private String password;
    private int level;
    private int xp;
    private int rewardPoint;
    private long reviewCount;
    private String currentTitle;
    private String profileImage;
    private String provider;
}
