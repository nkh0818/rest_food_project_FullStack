package com.yonsai.rest_food_project.domain.user.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_reward")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserReward {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_reward_id")
    private Long userRewardId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reward_id", nullable = false)
    private Reward reward;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private RewardStatus status;

    @Column(name = "reward_code", nullable = false, unique = true, length = 100)
    private String rewardCode;

    @Column(name = "issued_at")
    private LocalDateTime issuedAt;

    @Column(name = "expired_at")
    private LocalDateTime expiredAt;

    @Column(name = "used_at")
    private LocalDateTime usedAt;

    @PrePersist
    public void prePersist() {
        if (this.issuedAt == null) {
            this.issuedAt = LocalDateTime.now();
        }
    }

    public void use() {
        if (this.status != RewardStatus.AVAILABLE) {
            throw new IllegalStateException("사용 가능한 보상이 아닙니다.");
        }
        this.status = RewardStatus.USED;
        this.usedAt = LocalDateTime.now();
    }

    public void expire() {
        this.status = RewardStatus.EXPIRED;
    }
}