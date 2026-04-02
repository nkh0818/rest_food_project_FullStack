package com.yonsai.rest_food_project.domain.auth.service;

import com.yonsai.rest_food_project.domain.auth.dto.AuthResponseDTO;
import com.yonsai.rest_food_project.domain.auth.dto.SignUpRequestDTO;

public interface AuthService {

    // 카카오 로그인
    AuthResponseDTO loginKakao(String kakaoAccessToken);

    // 일반 회원가입
    AuthResponseDTO signUp(SignUpRequestDTO dto);

    // 일반 로그인
    AuthResponseDTO loginLocal(String email, String password);

    // 내 정보 조회
    AuthResponseDTO getMe(String token);

    // 닉네임 업데이트
    AuthResponseDTO updateNickname(String accessToken, String newNickname);

}
