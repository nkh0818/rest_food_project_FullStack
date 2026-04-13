package com.yonsai.rest_food_project.global.auth;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.yonsai.rest_food_project.domain.auth.dto.OAuthRequestDTO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class KakaoApiModule {

    private final RestTemplate restTemplate;

    @Value("${kakao.client-id}")
    private String clientId;

    

    public OAuthRequestDTO getUserInfo(String kakaoAccessToken) {
        String url = "https://kapi.kakao.com/v2/user/me";

        // 1. 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + kakaoAccessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // 2. 요청 객체 생성 (헤더만 보내는 GET 방식)
        HttpEntity<String> entity = new HttpEntity<>(headers);

        log.info("카카오 API 요청 - AccessToken 존재 여부: {}", (kakaoAccessToken != null));

        try {
            // 3. url로 GET 요청을 보내고, 응답은 OAuthRequestDTO 클래스 형태로
            ResponseEntity<OAuthRequestDTO> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    OAuthRequestDTO.class);

            // null 대비용
            if (response.getBody() == null) {
                throw new RuntimeException("데이터가 비어있습니다.");
            }

            log.info("카카오 사용자 정보 조회 성공: {}", response.getBody().getProviderId());
            return response.getBody();

        } catch (Exception e) {
            log.error("카카오 API 호출 중 에러 발생: {}", e.getMessage());
            throw new RuntimeException("카카오 서버와 통신에 실패했습니다.");
        }
    }

    public String getAccessToken(String code) {
        String url = "https://kauth.kakao.com/oauth/token";

        // 1. 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // 2. 바디 설정
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", clientId);
        params.add("redirect_uri", "http://localhost:5173/kakao/callback");
        params.add("code", code);

        // 3. 요청 생성
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(params, headers);

        try {
            // 4. POST 요청
            ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);

            if (response.getBody() != null && response.getBody().containsKey("access_token")) {
                return response.getBody().get("access_token").toString();
            }
            throw new RuntimeException("카카오 토큰을 받지 못했습니다.");
        } catch (Exception e) {
            log.error("카카오 토큰 요청 중 에러: {}", e.getMessage());
            throw new RuntimeException("카카오 서버와 통신 실패 (토큰 요청)");
        }
    }
}
