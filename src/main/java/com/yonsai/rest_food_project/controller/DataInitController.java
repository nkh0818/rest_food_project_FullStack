package com.yonsai.rest_food_project.controller;

import com.yonsai.rest_food_project.service.RestAreaDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class DataInitController {

    private final RestAreaDataService restAreaDataService;

    @GetMapping("/init-data")
    public String initData() {
        restAreaDataService.fetchAndSaveAll();
        return "데이터 수집 프로세스 실행됨.!";
    }
}