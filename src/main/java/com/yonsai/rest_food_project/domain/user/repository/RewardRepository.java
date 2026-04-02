package com.yonsai.rest_food_project.domain.user.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.yonsai.rest_food_project.domain.user.entity.Reward;

public interface RewardRepository extends JpaRepository<Reward, Long> {
    Optional<Reward> findByRewardName(String rewardName);

    List<Reward> findByActiveTrueOrderByPointCostAsc();
}