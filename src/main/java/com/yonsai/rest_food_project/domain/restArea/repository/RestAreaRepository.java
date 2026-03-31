package com.yonsai.rest_food_project.domain.restArea.repository;

import java.util.List;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.yonsai.rest_food_project.domain.restArea.entity.RestArea;

public interface RestAreaRepository extends JpaRepository<RestArea, Long> {

    boolean existsByStdRestCd(String stdRestCd);

    List<RestArea> findByNameContaining(String keyword);

    List<RestArea> findByNameContainingOrRouteNameContaining(String name, String routeName);

    List<RestArea> findByRouteNameContaining(String routeName);

    Optional<RestArea> findByStdRestCd(String stdRestCd);

}