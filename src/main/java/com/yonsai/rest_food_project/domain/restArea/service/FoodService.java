package com.yonsai.rest_food_project.domain.restArea.service;

import com.yonsai.rest_food_project.domain.restArea.dto.BestFoodDto;
import com.yonsai.rest_food_project.domain.restArea.entity.Food;
import com.yonsai.rest_food_project.domain.restArea.repository.FoodRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FoodService {

    private final FoodRepository foodRepository;

    @Transactional(readOnly = true)
    public List<BestFoodDto> getBestFoodForMain() {
        List<Food> foods = foodRepository.findRandomBestFoods();

        return foods.stream().map(f -> {
            // 1. 문자열 그대로 가져오기 ("000208")
            String rawCode = f.getRestArea().getStdRestCd();

            // 만약 rawCode가 혹시라도 숫자형태로 저장되어 있다면 아래처럼 6자리를 강제합니다.
            // String cleanId = String.format("%06d", Integer.parseInt(rawCode));

            // 하지만 DB에 이미 "000208"로 들어있다면 그냥 rawCode를 쓰면 됩니다.
            String cleanId = rawCode;

            return BestFoodDto.builder()
                    .id(f.getId())
                    .restAreaId(cleanId) // 이제 "000208"이 그대로 들어갑니다.
                    .name(f.getFoodName())
                    .restArea(f.getRestArea().getName())
                    .type("FOOD")
                    .price(f.getPrice())
                    .rating(4.8)
                    .reviews(f.getReviews() != null ? f.getReviews().size() : 0)
                    .build();
        }).collect(Collectors.toList());
    }
}