package com.yonsai.rest_food_project.domain.restArea.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.yonsai.rest_food_project.domain.restArea.entity.FoodCategory;

public interface FoodCategoryRepository extends JpaRepository<FoodCategory, String> {
}