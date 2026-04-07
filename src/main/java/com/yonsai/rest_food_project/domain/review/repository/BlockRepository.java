package com.yonsai.rest_food_project.domain.review.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.yonsai.rest_food_project.domain.review.entity.Block;
import com.yonsai.rest_food_project.domain.user.entity.User;

import io.lettuce.core.dynamic.annotation.Param;

public interface BlockRepository extends JpaRepository<Block, Long> {

    boolean existsByBlockerAndBlocked(User blocker, User blocked);

    //차단목록조회
    @Query("SELECT b FROM Block b JOIN FETCH b.blocked WHERE b.blocker.id = :blockerId")
    List<Block> findAllByBlockerId(@Param("blockerId") Long blockerId);

    //차단해제
    void deleteByBlockerAndBlockedId(User blocker, Long blockedUserId);

}
