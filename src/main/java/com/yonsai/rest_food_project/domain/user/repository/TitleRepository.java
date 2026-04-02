package com.yonsai.rest_food_project.domain.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.yonsai.rest_food_project.domain.user.entity.Title;

public interface TitleRepository extends JpaRepository<Title, Long> {
    Optional<Title> findByTitleName(String titleName);
}
