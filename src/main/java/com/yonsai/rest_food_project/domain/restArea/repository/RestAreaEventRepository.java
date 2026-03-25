package com.yonsai.rest_food_project.domain.restArea.repository;

import com.yonsai.rest_food_project.domain.restArea.entity.RestAreaEvent;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RestAreaEventRepository extends JpaRepository<RestAreaEvent, Long> {

    boolean existsByEventSeq(String eventSeq);

    List<RestAreaEvent> findByStdRestCd(String stdRestCd);

    List<RestAreaEvent> findByStdRestNmContaining(String restName);
}