package com.yonsai.rest_food_project.repository;

import com.yonsai.rest_food_project.entity.User; // User 엔티티 위치
import org.springframework.data.jpa.repository.JpaRepository; // JPA 인터페이스
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}