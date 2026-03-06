package com.yonsai.rest_food_project.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.yonsai.rest_food_project.entity.Food;

public interface FoodRepository extends JpaRepository<Food, Long> {
}