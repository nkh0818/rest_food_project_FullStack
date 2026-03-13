package com.yonsai.rest_food_project.domain.restArea.service;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.*;

@Service
@Slf4j
public class KakaoNaviService {

    // 사용자님의 REST API 키를 입력하세요
    private final String REST_API_KEY = "키";
    private final String KAKAO_URL = "https://apis-navi.kakaomobility.com/v1/directions";

    public Map<String, Object> getRouteWithRestAreas(String origin, String destination) {
        RestTemplate restTemplate = new RestTemplate();

        // 1. 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "KakaoAK " + REST_API_KEY);
        headers.set("Content-Type", "application/json");

        HttpEntity<String> entity = new HttpEntity<>(headers);

        // 2. URL 빌드
        String url = UriComponentsBuilder.fromHttpUrl(KAKAO_URL)
                .queryParam("origin", origin)
                .queryParam("destination", destination)
                .queryParam("priority", "RECOMMEND")
                .build().toUriString();

        try {
            // 3. 카카오 API 호출
            ResponseEntity<JsonNode> response = restTemplate.exchange(url, HttpMethod.GET, entity, JsonNode.class);
            JsonNode root = response.getBody();

            if (root == null || root.path("routes").isEmpty()) {
                log.warn("⚠️ 검색 결과가 없습니다.");
                return null;
            }

            // 4. [디버깅] 콘솔에 모든 가이드 정보 출력하기
            JsonNode sections = root.path("routes").get(0).path("sections");
            System.out.println("\n===== [카카오 네비 가이드 디버깅 시작] =====");

            List<Map<String, String>> restAreas = new ArrayList<>();

            for (int i = 0; i < sections.size(); i++) {
                JsonNode guides = sections.get(i).path("guides");
                for (JsonNode guide : guides) {
                    String name = guide.path("name").asText();
                    int type = guide.path("type").asInt();

                    // 1. 타입이 301이거나, 2. 이름에 '휴게소'가 들어있으면 수집!
                    if (type == 301 || name.contains("휴게소")) {
                        System.out.println("   ✨ 휴게소 추출 성공: " + name + " (타입:" + type + ")");

                        Map<String, String> area = new HashMap<>();
                        area.put("name", name);
                        area.put("x", guide.path("x").asText());
                        area.put("y", guide.path("y").asText());
                        restAreas.add(area);
                    }
                }
            }
            System.out.println("===== [카카오 네비 가이드 디버깅 종료] =====\n");

            // 5. 결과 조립 (전체 경로 + 추출된 휴게소 리스트)
            Map<String, Object> finalResult = new HashMap<>();
            finalResult.put("fullRoute", root); // 지도에 선 그릴 때 사용
            finalResult.put("restAreas", restAreas); // 지도에 마커 찍을 때 사용

            return finalResult;

        } catch (Exception e) {
            log.error("❌ 카카오 API 호출 에러: {}", e.getMessage());
            return null;
        }
    }
}