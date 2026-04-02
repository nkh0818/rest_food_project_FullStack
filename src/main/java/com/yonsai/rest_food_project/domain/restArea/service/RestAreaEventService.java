package com.yonsai.rest_food_project.domain.restArea.service;

import com.yonsai.rest_food_project.domain.restArea.dto.RestAreaEventDto;
import com.yonsai.rest_food_project.domain.restArea.dto.RestAreaEventResponse;
import com.yonsai.rest_food_project.domain.restArea.entity.RestAreaEvent;
import com.yonsai.rest_food_project.domain.restArea.repository.RestAreaEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor // final이 붙은 eventRepository 생성자로
public class RestAreaEventService {

    private final RestAreaEventRepository eventRepository;

    // 빈 주입 에러 방지를 위해 직접 생성 (final 제거)
    private final RestTemplate restTemplate = createRestTemplate();

    @Value("${road-service-key}")
    private String serviceKey;

    // RestTemplate 설정 (기존 생성자에 있던 컨버터 설정 포함)
    private RestTemplate createRestTemplate() {
        RestTemplate rt = new RestTemplate();
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setSupportedMediaTypes(Collections.singletonList(MediaType.ALL));
        rt.getMessageConverters().add(0, converter);
        return rt;
    }

    // 이벤트 가져오기
    public List<RestAreaEvent> getRelatedEvents(String stdRestCd, String restAreaName) {
        List<RestAreaEvent> allEvents = new ArrayList<>();
        allEvents.addAll(eventRepository.findByStdRestCd(stdRestCd));
        if (allEvents.isEmpty()) {
            String shortName = restAreaName.length() > 2 ? restAreaName.substring(0, 2) : restAreaName;
            allEvents.addAll(eventRepository.findByStdRestNmContaining(shortName));
        }
        return allEvents;
    }

    @Transactional
    public void fetchAndSaveAllEvents() {
        int pageNo = 1;
        int totalPages = 1;
        try {
            while (pageNo <= totalPages) {
                String fullUrl = "https://data.ex.co.kr/openapi/restinfo/restEventList?key=" + serviceKey
                        + "&type=json&numOfRows=99&pageNo=" + pageNo;
                URI uri = URI.create(fullUrl);
                ResponseEntity<RestAreaEventResponse> responseEntity = restTemplate.getForEntity(uri,
                        RestAreaEventResponse.class);
                RestAreaEventResponse response = responseEntity.getBody();
                if (response != null && response.getList() != null) {
                    if (pageNo == 1 && response.getPageSize() != null) {
                        totalPages = Integer.parseInt(response.getPageSize());
                    }
                    for (RestAreaEventDto dto : response.getList()) {
                        if (!eventRepository.existsByEventSeq(dto.getEventSeq())) {
                            eventRepository.save(dto.toEntity());
                        }
                    }
                    pageNo++;
                } else {
                    break;
                }
            }
        } catch (Exception e) {
            log.error("❌ 에러: {}", e.getMessage());
        }
    }

    public List<RestAreaEvent> getAllEvents() {
        return eventRepository.findAll();
    }

    public RestAreaEvent getEventDetail(Long id) {
        return eventRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("이벤트 없음: " + id));
    }
}