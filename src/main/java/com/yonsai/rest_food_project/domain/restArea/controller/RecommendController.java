package com.yonsai.rest_food_project.domain.restArea.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.yonsai.rest_food_project.domain.restArea.dto.RestAreaResponseDto;
import com.yonsai.rest_food_project.domain.restArea.service.RecommendService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/recommend")
@RequiredArgsConstructor
public class RecommendController {

    private final RecommendService recommendService;

    @GetMapping
    public ResponseEntity<List<RestAreaResponseDto>> recommend(
            @RequestParam String companion,
            @RequestParam String priority) {
                
        List<RestAreaResponseDto> result = recommendService.getAiRecommendations(companion, priority);
        return ResponseEntity.ok(result);
    }
}
