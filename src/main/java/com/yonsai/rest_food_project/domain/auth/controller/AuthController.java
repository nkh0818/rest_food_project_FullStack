package com.yonsai.rest_food_project.domain.auth.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.yonsai.rest_food_project.domain.auth.dto.AuthResponseDTO;
import com.yonsai.rest_food_project.domain.auth.dto.KakaoLoginRequestDTO;
import com.yonsai.rest_food_project.domain.auth.dto.LoginRequestDTO;
import com.yonsai.rest_food_project.domain.auth.dto.SignUpRequestDTO;
import com.yonsai.rest_food_project.domain.auth.service.AuthService;
import com.yonsai.rest_food_project.global.common.NicknameGenerator;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;
    private final NicknameGenerator nicknameGenerator;

    // 카카오 로그인
    @PostMapping("/kakao")
    public ResponseEntity<AuthResponseDTO> kakaoLogin(@RequestBody KakaoLoginRequestDTO request) {

        AuthResponseDTO response = authService.loginKakao(request.getAccessToken());
        return ResponseEntity.ok(response);
    }

    // 일반 로그인
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody LoginRequestDTO request) {
        AuthResponseDTO response = authService.loginLocal(request.getEmail(), request.getPassword());
        return ResponseEntity.ok(response);
    }

    // 랜덤 닉네임 생성
    @GetMapping("/nickname")
    public ResponseEntity<String> getRandomNickname() {
        log.info("----닉네임 랜덤 생성기 시작");
        return ResponseEntity.ok(nicknameGenerator.generate());
    }

    @PostMapping("/signup")
    public ResponseEntity<AuthResponseDTO> signUp(@Valid @RequestBody SignUpRequestDTO dto) {
        return ResponseEntity.ok(authService.signUp(dto));
    }

    @GetMapping("/me")
    public ResponseEntity<AuthResponseDTO> getCurrentUser(
            @RequestHeader(value = "Authorization", required = false) String token) {
        // 서비스에서 토큰을 검증 후 유저 정보를 가져옴
        AuthResponseDTO response = authService.getMe(token);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/nickname")
    public ResponseEntity<AuthResponseDTO> updateNickname(
            @RequestHeader("Authorization") String token,
            @RequestBody Map<String, String> body) {
        String newNickname = body.get("nickname"); // 프론트에서 보낸 key값으로 꺼내기

        if (newNickname == null || newNickname.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        AuthResponseDTO response = authService.updateNickname(token, body.get("nickname"));
        return ResponseEntity.ok(response);
    }

}
