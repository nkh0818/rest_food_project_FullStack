package com.yonsai.rest_food_project.domain.user.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.yonsai.rest_food_project.domain.user.entity.RewardStatus;
import com.yonsai.rest_food_project.domain.user.entity.UserReward;

public interface UserRewardRepository extends JpaRepository<UserReward, Long> {
    List<UserReward> findByUserIdOrderByIssuedAtDesc(Long userId);

    List<UserReward> findByUserIdAndStatusOrderByIssuedAtDesc(Long userId, RewardStatus status);
}