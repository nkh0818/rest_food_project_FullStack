package com.yonsai.rest_food_project.domain.restArea.controller;

import com.yonsai.rest_food_project.domain.restArea.dto.RestAreaResponseDto;
import com.yonsai.rest_food_project.domain.restArea.service.RestAreaDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/rest-areas")
@RequiredArgsConstructor
public class RestAreaTestController {

    private final RestAreaDataService restAreaService;

    // 모든 휴게소 데이터 가져오기
    // @GetMapping
    // public List<RestAreaResponseDto> getAllRestAreas() {
    // return restAreaService.getOriginalData(); // 서비스에 getter가 있어야 함
    // }
}