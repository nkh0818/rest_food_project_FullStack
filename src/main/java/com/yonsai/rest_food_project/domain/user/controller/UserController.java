package com.yonsai.rest_food_project.domain.user.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.yonsai.rest_food_project.domain.user.dto.UserResponseDTO;
import com.yonsai.rest_food_project.domain.user.entity.User;
import com.yonsai.rest_food_project.domain.user.service.UserService;
import com.yonsai.rest_food_project.global.auth.PrincipalDetails;
import com.yonsai.rest_food_project.global.common.RedisService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final RedisService redisService;
    private final UserService userService;

    @Transactional(readOnly = true)
    @GetMapping("/my")
    public ResponseEntity<UserResponseDTO> getMyInfo(
            @AuthenticationPrincipal PrincipalDetails principalDetails) {
        log.info(">>>> GET /api/user/my 요청 들어옴!");
        UserResponseDTO response = userService.getMyInfo(principalDetails.getUser().getId());
        return ResponseEntity.ok(response);
    }

    // --- 닉네임 중복확인 --- 0403 나다희 추가

    @GetMapping("/check-nickname")
    public ResponseEntity<Boolean> checkNickname(@RequestParam String nickname) {
        if (nickname == null || nickname.trim().isEmpty()) {
            log.warn("⚠️ 빈 닉네임 중복 체크 요청됨");
            return ResponseEntity.ok(false);
        }

        try {
            // 2. Redis 조회 실행
            boolean isDuplicate = redisService.isNicknameExists(nickname);
            return ResponseEntity.ok(isDuplicate);
        } catch (Exception e) {
            // 3. 만약 Redis 연결 오류 등이 나면 서버가 터지는(500) 대신 로그를 찍고 false 반환
            log.error("❌ Redis 중복 체크 중 에러 발생: {}", e.getMessage());
            return ResponseEntity.ok(false);
        }
    }

}
