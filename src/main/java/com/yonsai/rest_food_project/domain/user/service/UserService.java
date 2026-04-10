package com.yonsai.rest_food_project.domain.user.service;

import com.yonsai.rest_food_project.domain.user.dto.UserResponseDTO;
import com.yonsai.rest_food_project.domain.user.entity.User;

public interface UserService {

    UserResponseDTO getMyInfo(Long userId);

    // OAuth 회원가입/업데이트 (카카오 등)
    User saveOrUpdate(String email, String nickname, String provider, String providerId);

    // 일반 회원가입 (로컬)
    User createLocalUser(String email, String encodedPassword, String nickname);

    // email로 유저 조회 (없으면 예외)
    User findByEmail(String email);

    // email 중복 여부 확인
    boolean existsByEmail(String email);

    // 정보조회
    UserResponseDTO getMyInfo(Long userId);

    // 닉네임 변경
    // User nicknameUpdate(Long userId, String newNickname);

    // 프사 변경
    User nicknameUpdate(Long userId, String newNickname, String profileImage);

    // // 회원 탈퇴
    // void deleteUser(Long userId);

    // // 비밀번호 변경
    // void updatePassword(Long userId, String newPassword);

    // // 관리자 권한 부여
    // void assignRole(Long userId, UserRole role);

}
