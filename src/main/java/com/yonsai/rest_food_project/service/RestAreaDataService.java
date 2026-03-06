package com.yonsai.rest_food_project.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yonsai.rest_food_project.entity.RestArea;
import com.yonsai.rest_food_project.repository.RestAreaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class RestAreaDataService {

    private final RestAreaRepository restAreaRepository;

    @Value("${road-service-key}")
    private String roadServiceKey;

    @Transactional
    public void fetchAndSaveAll() {
        // 1. 사용자님이 찾은 정확한 URL (hiwaySvarInfoList)
        String url = "https://data.ex.co.kr/openapi/restinfo/hiwaySvarInfoList?key="
                + roadServiceKey + "&type=json";

        RestTemplate restTemplate = new RestTemplate();

        try {
            // 2. 응답 확인용 로그 (데이터가 제대로 오는지 체크)
            String jsonString = restTemplate.getForObject(url, String.class);
            log.info("하이쉼마루 API 응답: {}", jsonString);

            if (jsonString == null || jsonString.contains("<html")) {
                log.error("API 응답이 올바르지 않습니다 (HTML 수신). 키 설정을 확인하세요.");
                return;
            }

            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> responseMap = objectMapper.readValue(jsonString, Map.class);
            List<Map<String, Object>> list = (List<Map<String, Object>>) responseMap.get("list");

            if (list != null) {
                for (Map<String, Object> item : list) {
                    // 명세에 적힌 변수명으로 매칭 (svarCd, svarNm 등)
                    String unitCode = (String) item.get("svarCd");

                    if (restAreaRepository.existsByStdRestCd(unitCode))
                        continue;

                    RestArea restArea = RestArea.builder()
                            .stdRestCd(unitCode)
                            .name((String) item.get("svarNm")) // 휴게소명
                            .routeName((String) item.get("routeNm")) // 노선명
                            .location((String) item.get("svarAddr")) // 휴게소주소
                            // 이 API는 좌표(x,y)를 주지 않으므로 일단 0.0 처리
                            .xValue(0.0)
                            .yValue(0.0)
                            .build();

                    restAreaRepository.save(restArea);
                }
                log.info("하이쉼마루 데이터 {}건 저장 완료!", list.size());
            }
        } catch (Exception e) {
            log.error("데이터 수집 에러: {}", e.getMessage());
        }
    }
}