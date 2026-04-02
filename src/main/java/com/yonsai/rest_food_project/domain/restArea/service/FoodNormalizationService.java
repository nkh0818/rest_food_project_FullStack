package com.yonsai.rest_food_project.domain.restArea.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.yonsai.rest_food_project.domain.restArea.entity.Food;
import com.yonsai.rest_food_project.domain.restArea.entity.FoodNameMapping;
import com.yonsai.rest_food_project.domain.restArea.repository.FoodNameMappingRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FoodNormalizationService {

    private final FoodNameMappingRepository foodNameMappingRepository;

    public void normalize(Food food) {
        if (food == null || food.getFoodName() == null) {
            return;
        }

        String foodName = food.getFoodName().trim();
        List<FoodNameMapping> mappings = foodNameMappingRepository.findAllByOrderByKeywordAsc();

        for (FoodNameMapping mapping : mappings) {
            if (foodName.contains(mapping.getKeyword())) {
                food.setNormalizedName(mapping.getNormalizedName());
                food.setCategoryCode(mapping.getCategoryCode());
                return;
            }
        }

        // 매핑 실패 시 기본값
        food.setNormalizedName(foodName);
        food.setCategoryCode("ETC");
    }
}