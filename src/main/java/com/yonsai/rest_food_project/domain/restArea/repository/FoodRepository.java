package com.yonsai.rest_food_project.domain.restArea.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.yonsai.rest_food_project.domain.restArea.entity.Food;

public interface FoodRepository extends JpaRepository<Food, Long> {

    List<Food> findByRestAreaId(Long restAreaId);

    List<Food> findByCategoryCode(String categoryCode);

    @Query(value = "SELECT * FROM food WHERE is_best = 1 ORDER BY RAND() LIMIT 3", nativeQuery = true)
    List<Food> findRandomBestFoods();
}