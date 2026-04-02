package com.yonsai.rest_food_project;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import com.yonsai.rest_food_project.domain.auth.dto.AuthResponseDTO;
import com.yonsai.rest_food_project.domain.auth.dto.OAuthRequestDTO;
import com.yonsai.rest_food_project.domain.auth.service.AuthService;
import com.yonsai.rest_food_project.domain.user.repository.UserRepository;
import com.yonsai.rest_food_project.global.common.NicknameProperties;

import jakarta.transaction.Transactional;

@SpringBootTest // 스프링 컨텍스트를 불러와서 빈(Bean)들을 테스트할 때 사용
@Transactional   // 테스트가 끝나면 DB를 자동으로 롤백해줘서 깔끔함!
@EnableConfigurationProperties(NicknameProperties.class)
@TestPropertySource(locations = "classpath:nickname.yml")
class AuthServiceTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NicknameProperties nicknameProperties;

    @BeforeEach
    void setUp() {
        // 테스트 실행 전, null인 리스트에 가짜 데이터를 강제로 넣습니다.
        if (nicknameProperties.getAdjectives() == null) {
            nicknameProperties.setAdjectives(List.of("행복한", "즐거운"));
        }
        if (nicknameProperties.getNouns() == null) {
            nicknameProperties.setNouns(List.of("사자", "호랑이"));
        }
    }

    @Test
    @DisplayName("카카오 로그인 시 신규 유저라면 DB에 저장되어야 한다")
    void kakao_login_success_test() {
        // 1. Given
        OAuthRequestDTO.KakaoAccount.Profile profile = new OAuthRequestDTO.KakaoAccount.Profile(null, "http://image.url");
        OAuthRequestDTO.KakaoAccount account = new OAuthRequestDTO.KakaoAccount("test@test.com", profile);
        OAuthRequestDTO dto = new OAuthRequestDTO(12345L, account);

        // 2. When (실행: 우리가 만든 로그인 로직 호출)
        AuthResponseDTO response = authService.login(dto);

        // 3. Then (검증: 결과가 내 예상과 맞는지 확인)
        assertNotNull(response.getAccessToken());
        assertEquals("test@test.com", response.getEmail());

        // 실제로 DB에도 저장되었는지?
        assertTrue(userRepository.findByProviderAndProviderId("kakao", "12345").isPresent());

        System.out.println("생성된 유저 이메일: " + response.getEmail());
        System.out.println("생성된 유저 닉네임: " + response.getNickname());

        assertNotNull(response.getAccessToken());
    }
}
