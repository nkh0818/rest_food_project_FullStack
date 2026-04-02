package com.yonsai.rest_food_project.domain.restArea.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.yonsai.rest_food_project.domain.restArea.entity.Food;

public interface FoodRepository extends JpaRepository<Food, Long> {
    List<Food> findByRestAreaId(Long restAreaId);
}