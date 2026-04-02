package com.yonsai.rest_food_project.domain.restArea.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.yonsai.rest_food_project.domain.restArea.entity.FoodNameMapping;

public interface FoodNameMappingRepository extends JpaRepository<FoodNameMapping, Long> {

    List<FoodNameMapping> findAllByOrderByKeywordAsc();

    Optional<FoodNameMapping> findFirstByKeyword(String keyword);

    boolean existsByKeyword(String keyword); // 키워드가 존재하는지만 체크 0402나다희 추가
}