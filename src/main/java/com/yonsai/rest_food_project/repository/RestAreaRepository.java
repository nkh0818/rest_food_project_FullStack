package com.yonsai.rest_food_project.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.yonsai.rest_food_project.entity.RestArea;

public interface RestAreaRepository extends JpaRepository<RestArea, Long> {
    boolean existsByStdRestCd(String stdRestCd);
}