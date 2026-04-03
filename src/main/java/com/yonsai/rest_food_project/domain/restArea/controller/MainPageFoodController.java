package com.yonsai.rest_food_project.domain.restArea.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.yonsai.rest_food_project.domain.restArea.dto.BestFoodDto;
import com.yonsai.rest_food_project.domain.restArea.service.FoodService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/main")
@RequiredArgsConstructor
public class MainPageFoodController {

    private final FoodService foodService;

    @GetMapping("/best-food")
    public List<BestFoodDto> getBestFoods() {
        return foodService.getBestFoodForMain();
    }
}