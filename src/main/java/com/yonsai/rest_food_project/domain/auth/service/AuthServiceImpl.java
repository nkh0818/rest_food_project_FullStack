package com.yonsai.rest_food_project.domain.auth.service;

import org.springframework.stereotype.Service;

import com.yonsai.rest_food_project.domain.auth.dto.AuthResponseDTO;
import com.yonsai.rest_food_project.domain.auth.dto.OAuthRequestDTO;
import com.yonsai.rest_food_project.domain.user.entity.User;
import com.yonsai.rest_food_project.domain.user.repository.UserRepository;
import com.yonsai.rest_food_project.domain.user.service.UserService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService{

    private final UserRepository userRepository;
    private final UserService userService;


    @Override
    @Transactional
    public AuthResponseDTO login(OAuthRequestDTO dto){

        String providerId = dto.getProviderId();
        String email = dto.getKakaoAccount().getEmail();
        String nickname = dto.getKakaoAccount().getProfile().getNickname();
        String provider = "kakao";

        // 유저가 DB에 있는지 확인하고 가입
        User user = userRepository.findByProviderAndProviderId(provider, providerId)
            .orElseGet(()->{
                log.info("신규 유저 가입 진행 : email={}, providerId={}", email, providerId);
                return userService.saveOrUpdate(email, nickname, provider, providerId);
            });
        // 토큰 발행
        String accessToken = "JWT_TOKEN_" + java.util.UUID.randomUUID();
        return AuthResponseDTO.builder()
            .accessToken(accessToken)
            .nickname(user.getNickname())
            .email(user.getEmail())
            .level(user.getLevel())
            .build();
    };

}
