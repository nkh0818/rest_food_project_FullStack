package com.yonsai.rest_food_project.domain.user.service;

import com.yonsai.rest_food_project.domain.user.dto.UserResponseDTO;
import com.yonsai.rest_food_project.domain.user.entity.User;

public interface UserService {

    // 회원가입
    User saveOrUpdate(String email, String nickname, String provider, String providerId);

    // 정보조회
    UserResponseDTO getMyInfo(Long userId);

    // 닉네임 변경
    void nicknameUpdate(Long userId, String newNickname);


}
