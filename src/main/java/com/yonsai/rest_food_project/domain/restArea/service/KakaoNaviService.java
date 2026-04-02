package com.yonsai.rest_food_project.domain.restArea.service;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.*;

@Service
@Slf4j
public class KakaoNaviService {

    // (주의) $ 안에 들어갈 이름은 본인의 application.properties에 적힌 카카오 키 이름과 같아야 합니다.
    @Value("${kakaoNaviKey}")
    private String kakaoNaviKey;

    private final String NAVI_URL = "https://apis-navi.kakaomobility.com/v1/directions";
    private final String LOCAL_URL = "https://dapi.kakao.com/v2/local/search/keyword.json"; // 장소 검색 API 추가

    // ⭐ 1. 한글 장소명(예: "서울역")을 좌표("127.123,37.123")로 바꿔주는 로직 추가
    private String getCoordinates(String keyword) {
        // 만약 '내 위치' 버튼을 눌러서 이미 좌표(숫자)로 들어왔다면 변환 없이 바로 통과시킴
        if (keyword.matches("^[0-9]+\\.[0-9]+,[0-9]+\\.[0-9]+$")) {
            return keyword;
        }

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "KakaoAK " + kakaoNaviKey);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        // 카카오 로컬 API로 장소 검색
        String url = UriComponentsBuilder.fromUriString(LOCAL_URL)
                .queryParam("query", keyword)
                .build().toUriString();

        try {
            ResponseEntity<JsonNode> response = restTemplate.exchange(url, HttpMethod.GET, entity, JsonNode.class);
            JsonNode documents = response.getBody().path("documents");

            // 검색 결과가 있으면 가장 첫 번째 장소의 X, Y 좌표를 뽑아옵니다.
            if (documents.isArray() && documents.size() > 0) {
                String x = documents.get(0).path("x").asText();
                String y = documents.get(0).path("y").asText();
                return x + "," + y; // "127.xxxx,37.xxxx" 형태로 반환
            }
        } catch (Exception e) {
            log.error("장소 검색 API 에러: {}", e.getMessage());
        }
        return null;
    }

    // 2. 기존 경로 탐색 메서드
    public Map<String, Object> getRouteWithRestAreas(String originKeyword, String destinationKeyword) {

        // ⭐ 입력받은 글자를 여기서 좌표로 싹 바꿉니다.
        String originCoords = getCoordinates(originKeyword);
        String destCoords = getCoordinates(destinationKeyword);

        if (originCoords == null || destCoords == null) {
            log.warn("⚠️ 출발지 또는 목적지를 찾을 수 없습니다: 출발({}), 도착({})", originKeyword, destinationKeyword);
            return null;
        }

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "KakaoAK " + kakaoNaviKey);
        headers.set("Content-Type", "application/json");

        HttpEntity<String> entity = new HttpEntity<>(headers);

        // 네비 API 호출 (이제 한글 이름 대신 변환된 좌표가 들어갑니다)
        String url = UriComponentsBuilder.fromHttpUrl(NAVI_URL)
                .queryParam("origin", originCoords)
                .queryParam("destination", destCoords)
                .queryParam("priority", "RECOMMEND")
                .build().toUriString();

        try {
            ResponseEntity<JsonNode> response = restTemplate.exchange(url, HttpMethod.GET, entity, JsonNode.class);
            JsonNode root = response.getBody();

            if (root == null || root.path("routes").isEmpty()) {
                log.warn("⚠️ 경로 검색 결과가 없습니다.");
                return null;
            }

            JsonNode sections = root.path("routes").get(0).path("sections");
            List<Map<String, String>> restAreas = new ArrayList<>();

            for (int i = 0; i < sections.size(); i++) {
                JsonNode guides = sections.get(i).path("guides");
                for (JsonNode guide : guides) {
                    String name = guide.path("name").asText();
                    int type = guide.path("type").asInt();

                    if (type == 301 || name.contains("휴게소")) {
                        Map<String, String> area = new HashMap<>();
                        area.put("name", name);
                        area.put("x", guide.path("x").asText());
                        area.put("y", guide.path("y").asText());
                        restAreas.add(area);
                    }
                }
            }

            Map<String, Object> finalResult = new HashMap<>();
            finalResult.put("fullRoute", root);
            finalResult.put("restAreas", restAreas);

            return finalResult;

        } catch (Exception e) {
            log.error("❌ 카카오 네비 API 호출 에러: {}", e.getMessage());
            return null;
        }
    }
}