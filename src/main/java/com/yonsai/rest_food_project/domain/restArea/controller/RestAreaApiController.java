package com.yonsai.rest_food_project.domain.restArea.controller;

import com.yonsai.rest_food_project.domain.restArea.dto.RestAreaResponseDto;
import com.yonsai.rest_food_project.domain.restArea.service.RestAreaDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/rest-area")
public class RestAreaApiController {

    private final RestAreaDataService restAreaDataService;

    /**
     * 1. 휴게소 이름으로 검색 (검색어 포함된 결과 반환)
     */
    @GetMapping("/search")
    public List<RestAreaResponseDto> search(@RequestParam String keyword) {
        return restAreaDataService.search(keyword);
    }

    /**
     * 2. 현재 검색 결과 내에서 필터링 (REST_AREA 또는 GAS_STATION)
     */
    @GetMapping("/filter")
    public List<RestAreaResponseDto> filter(@RequestParam String type) {
        return restAreaDataService.filter(type);
    }

    /**
     * 3. 모든 검색/필터 초기화 후 전체 리스트 반환
     */
    @GetMapping("/reset")
    public List<RestAreaResponseDto> reset() {
        return restAreaDataService.reset();
    }
}