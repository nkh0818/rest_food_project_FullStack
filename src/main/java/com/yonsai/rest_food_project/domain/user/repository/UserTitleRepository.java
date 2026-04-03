package com.yonsai.rest_food_project.domain.user.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.yonsai.rest_food_project.domain.user.entity.UserTitle;

public interface UserTitleRepository extends JpaRepository<UserTitle, Long> {

    boolean existsByUserIdAndTitleTitleId(Long userId, Long titleId);

    List<UserTitle> findByUserId(Long userId);
}