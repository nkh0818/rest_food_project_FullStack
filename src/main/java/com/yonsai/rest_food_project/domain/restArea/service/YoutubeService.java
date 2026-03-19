package com.yonsai.rest_food_project.domain.restArea.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yonsai.rest_food_project.domain.restArea.entity.YoutubePlaylist;
import com.yonsai.rest_food_project.domain.restArea.repository.YoutubeRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class YoutubeService {
    @Value("${YoutubeKey}")
    private String YoutubeKey;

    private final YoutubeRepository repository;

    public List<YoutubePlaylist> getMusicPlaylists(String keyword) {
        List<YoutubePlaylist> saved = repository.findBySearchKey(keyword);
        if (!saved.isEmpty())
            return saved;

        RestTemplate restTemplate = new RestTemplate();
        String url = UriComponentsBuilder.fromHttpUrl("https://www.googleapis.com/youtube/v3/search")
                .queryParam("part", "snippet")
                .queryParam("q", keyword + " music playlist")
                .queryParam("type", "playlist")
                // .queryParam("topicId", "/m/04rlf")
                .queryParam("maxResults", 10)
                .queryParam("key", YoutubeKey)
                .toUriString();

        List<YoutubePlaylist> parsedList = new ArrayList<>();
        try {
            String response = restTemplate.getForObject(url, String.class);
            System.out.println("=== API 응답 수신 성공 ==="); // 진단 로그 1

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response);
            JsonNode items = root.path("items");

            System.out.println("검색된 아이템 개수: " + items.size()); // 진단 로그 2

            for (JsonNode item : items) {
                String plId = item.path("id").path("playlistId").asText();
                String title = item.path("snippet").path("title").asText();

                System.out.println("추출된 ID: " + plId + " | 제목: " + title);

                if (plId.isEmpty() || repository.existsByPlaylistId(plId))
                    continue;

                YoutubePlaylist pl = new YoutubePlaylist();
                pl.setPlaylistId(plId);
                pl.setTitle(title);
                pl.setSearchKey(keyword);

                pl.setItemCount(0);

                parsedList.add(pl);
            }

            if (parsedList.isEmpty()) {
                System.out.println("!!! 저장할 데이터가 없습니다 (중복이거나 추출 실패) !!!");
            }

            return repository.saveAll(parsedList);
        } catch (Exception e) {
            System.err.println("!!! 에러 발생: " + e.getMessage()); // 진단 로그 4
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}