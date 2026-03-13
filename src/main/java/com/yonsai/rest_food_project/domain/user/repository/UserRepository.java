package com.yonsai.rest_food_project.domain.user.repository;

import org.springframework.data.jpa.repository.JpaRepository; // JPA 인터페이스

import com.yonsai.rest_food_project.domain.user.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // 특정 플랫폼의 고유번호 검색
    Optional<User> findByProviderAndProviderId(String provider, String providerId);

    // email로 검색
    Optional<User> findByEmail(String email);

    Optional<User> findById(Long userId);

    boolean existsByNickname(String nickname); // 존재의 여부만 true, false로 반환
    /**
     * -- existsByNickname(String nickname) 호출 시
        SELECT count(*) > 0 
        FROM users 
        WHERE nickname = '요청한닉네임' 
        LIMIT 1;
     */



}