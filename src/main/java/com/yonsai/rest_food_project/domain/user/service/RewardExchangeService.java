package com.yonsai.rest_food_project.domain.user.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yonsai.rest_food_project.domain.user.entity.Reward;
import com.yonsai.rest_food_project.domain.user.entity.RewardStatus;
import com.yonsai.rest_food_project.domain.user.entity.User;
import com.yonsai.rest_food_project.domain.user.entity.UserReward;
import com.yonsai.rest_food_project.domain.user.repository.RewardRepository;
import com.yonsai.rest_food_project.domain.user.repository.UserRepository;
import com.yonsai.rest_food_project.domain.user.repository.UserRewardRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class RewardExchangeService {

    private final UserRepository userRepository;
    private final RewardRepository rewardRepository;
    private final UserRewardRepository userRewardRepository;

    public List<Reward> getAvailableRewards() {
        return rewardRepository.findByActiveTrueOrderByPointCostAsc();
    }

    public List<UserReward> getUserRewards(Long userId) {
        return userRewardRepository.findByUserIdOrderByIssuedAtDesc(userId);
    }

    public UserReward exchangeReward(Long userId, Long rewardId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Reward reward = rewardRepository.findById(rewardId)
                .orElseThrow(() -> new IllegalArgumentException("보상을 찾을 수 없습니다."));

        if (!Boolean.TRUE.equals(reward.getActive())) {
            throw new IllegalStateException("현재 교환할 수 없는 보상입니다.");
        }

        if (user.getRewardPoint() < reward.getPointCost()) {
            throw new IllegalStateException("포인트가 부족합니다.");
        }

        user.useRewardPoint(reward.getPointCost());

        UserReward userReward = UserReward.builder()
                .user(user)
                .reward(reward)
                .status(RewardStatus.AVAILABLE)
                .rewardCode(createRewardCode(reward))
                .issuedAt(LocalDateTime.now())
                .expiredAt(LocalDateTime.now().plusDays(30))
                .build();

        userRepository.save(user);
        return userRewardRepository.save(userReward);
    }

    public void useReward(Long userRewardId) {
        UserReward userReward = userRewardRepository.findById(userRewardId)
                .orElseThrow(() -> new IllegalArgumentException("보상 내역을 찾을 수 없습니다."));

        userReward.use();
        userRewardRepository.save(userReward);
    }

    private String createRewardCode(Reward reward) {
        String prefix = reward.getCouponCodePrefix() != null && !reward.getCouponCodePrefix().isBlank()
                ? reward.getCouponCodePrefix()
                : "RQ";

        return prefix + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}