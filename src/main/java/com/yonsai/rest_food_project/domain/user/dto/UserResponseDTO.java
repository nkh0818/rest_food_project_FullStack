package com.yonsai.rest_food_project.domain.user.dto;

import com.yonsai.rest_food_project.domain.user.entity.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponseDTO {

    /** FE로 보내주기 위해 필요한 정보만 적기 */

    private Long id;
    private String email;
    private String nickname;
    private int xp;
    private int level;
    private String currentTitle; // 칭호 객체가 아니라 이름만 전달

    public static UserResponseDTO from(User user){
        return UserResponseDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .xp(user.getXp())
                .level(user.getLevel())
                .currentTitle(user.getCurrentTitle() != null ?
                                user.getCurrentTitle().getTitleName() : "칭호 없음")
                .build();
    }
}
