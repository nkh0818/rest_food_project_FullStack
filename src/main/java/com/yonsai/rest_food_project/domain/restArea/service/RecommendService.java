package com.yonsai.rest_food_project.domain.restArea.service;

import java.util.List;
import com.yonsai.rest_food_project.domain.restArea.dto.RestAreaResponseDto;

public interface RecommendService {
    List<RestAreaResponseDto> getAiRecommendations(String companion, String priority);
}