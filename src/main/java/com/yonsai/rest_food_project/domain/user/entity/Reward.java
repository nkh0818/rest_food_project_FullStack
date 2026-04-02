package com.yonsai.rest_food_project.domain.user.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "reward")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reward {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reward_id")
    private Long rewardId;

    @Column(name = "reward_name", nullable = false, unique = true, length = 100)
    private String rewardName;

    @Column(name = "description", length = 255)
    private String description;

    @Column(name = "reward_type", nullable = false, length = 50)
    private String rewardType;

    @Column(name = "point_cost", nullable = false)
    private Integer pointCost;

    @Column(name = "coupon_code_prefix", length = 30)
    private String couponCodePrefix;

    @Builder.Default
    @Column(name = "is_active", nullable = false)
    private Boolean active = true;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}