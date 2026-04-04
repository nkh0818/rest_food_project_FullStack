package com.yonsai.rest_food_project.domain.favorite.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.yonsai.rest_food_project.domain.favorite.entity.Favorite;
import com.yonsai.rest_food_project.domain.restArea.entity.RestArea;
import com.yonsai.rest_food_project.domain.user.entity.User;

import io.lettuce.core.dynamic.annotation.Param;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    Optional<Favorite> findByUserAndRestArea(User user, RestArea restArea);

    List<Favorite> findByUser(User user);

    @Query("SELECT f FROM Favorite f " +
            "JOIN FETCH f.restArea " +
            "WHERE f.user.email = :userEmail")
    List<Favorite> findAllByUserEmailWithRestArea(@Param("userEmail") String userEmail);
}
