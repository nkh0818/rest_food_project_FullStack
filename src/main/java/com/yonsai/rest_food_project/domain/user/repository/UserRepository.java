package com.yonsai.rest_food_project.domain.user.repository;

import org.springframework.data.jpa.repository.JpaRepository; // JPA 인터페이스

import com.yonsai.rest_food_project.domain.user.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}