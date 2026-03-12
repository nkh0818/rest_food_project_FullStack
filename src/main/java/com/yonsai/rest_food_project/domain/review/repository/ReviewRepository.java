package com.yonsai.rest_food_project.domain.review.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.yonsai.rest_food_project.domain.review.entity.Review;

public interface ReviewRepository extends JpaRepository<Review, Long> {
}