package com.yonsai.rest_food_project.domain.restArea.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yonsai.rest_food_project.domain.restArea.entity.YoutubePlaylist;
import com.yonsai.rest_food_project.domain.restArea.repository.YoutubeRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class YoutubeService {

    private final YoutubeRepository repository;

    @Value("${YoutubeKey}")
    private String YoutubeKey;

    public List<YoutubePlaylist> getMusicPlaylists(String keyword) {
        // 1. DB 먼저 확인
        List<YoutubePlaylist> saved = repository.findBySearchKey(keyword);
        if (!saved.isEmpty()) {
            return saved;
        }

        // 2. API 호출을 위한 URL 생성 (공백 인코딩 포함)
        RestTemplate restTemplate = new RestTemplate();
        String url = UriComponentsBuilder.fromHttpUrl("https://www.googleapis.com/youtube/v3/search")
                .queryParam("part", "snippet")
                .queryParam("q", keyword + " 노래모음")
                .queryParam("type", "playlist")
                .queryParam("maxResults", 15)
                .queryParam("key", YoutubeKey)
                .build()
                // .encode() // 한글과 공백을 URL용 코드로 변환
                .toUriString();

        System.out.println("최종 생성 URL: " + url);

        List<YoutubePlaylist> parsedList = new ArrayList<>();

        try {
            String response = restTemplate.getForObject(url, String.class);
            System.out.println("📬 API 응답 원본: " + response); // 1. 응답 데이터가 진짜 오는지 확인
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response);
            JsonNode items = root.path("items");
            System.out.println("📦 가져온 데이터 개수: " + items.size()); // 2. 이게 0이면 경로 문제
            System.out.println("=== API 응답 수신 성공 ===");

            for (JsonNode item : items) {

                String plId = item.path("id").path("playlistId").asText();
                String title = item.path("snippet").path("title").asText();
                System.out.println("🔍 API가 준 제목: [" + title + "]"); // 이 로그가 찍히는지 확인!
                // 제목이 너무 짧거나 비어있으면 패스
                if (title.length() < 2)
                    continue;

                // 이상한 특수문자나 외계어 제목 차단
                if (isStrangeTitle(title)) {
                    System.out.println("❌ 필터링됨 (이상한 제목): " + title);
                    continue;
                }

                // 중복 체크
                if (plId.isEmpty() || repository.existsByPlaylistId(plId))
                    continue;

                // 객체 생성
                YoutubePlaylist playlist = YoutubePlaylist.builder()
                        .playlistId(plId)
                        .title(title)
                        .searchKey(keyword)
                        .build();

                parsedList.add(playlist);
                System.out.println("✅ 추출 성공: " + title);
            }

        } catch (Exception e) {
            System.out.println("❌ API 호출 중 에러 발생: " + e.getMessage());
        }

        if (parsedList.isEmpty()) {
            System.out.println("⚠️ 저장할 데이터가 없습니다.");
            return new ArrayList<>();
        }

        return repository.saveAll(parsedList);
    }

    // 제목에 이상한 특수문자나 외계어가 너무 많은지 체크하는 메서드
    private boolean isStrangeTitle(String title) {
        if (title == null || title.isEmpty())
            return true;

        // 정상적인 글자(한글, 영어, 숫자, 공백, 기본 기호)를 다 지워봄
        String strangeChars = title.replaceAll("[가-힣a-zA-Z0-9\\s\\.,!?\\(\\)\\[\\]&~#@*🔥🎧]", "");

        // 이상한 글자의 비중이 전체 제목의 30%를 넘으면 "이상하다"고 판단
        return (double) strangeChars.length() / title.length() > 0.6;
    }
}