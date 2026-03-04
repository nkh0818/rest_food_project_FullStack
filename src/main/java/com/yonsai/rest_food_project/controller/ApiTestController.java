package com.yonsai.rest_food_project.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class ApiTestController {

    @Value("${road-service-key}")
    private String roadApiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    @GetMapping("/api/test/road")
    public String testRoadApi() {
        // 명세서의 HTTPS 주소와 변수 조합 (JSON 타입 권장)
        String url = "https://data.ex.co.kr/openapi/restinfo/restBestfoodList?key="
                + roadApiKey + "&type=json&numOfRows=10&pageNo=1";

        try {
            System.out.println("호출 시도 URL: " + url);
            return restTemplate.getForObject(url, String.class);
        } catch (Exception e) {
            return "API 호출 중 진짜 에러 발생: " + e.getMessage();
        }
    }
}