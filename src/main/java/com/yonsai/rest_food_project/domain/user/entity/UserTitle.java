package com.yonsai.rest_food_project.domain.user.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_title", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "user_id", "title_id" })
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserTitle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_title_id")
    private Long userTitleId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "title_id", nullable = false)
    private Title title;

    @Builder.Default
    @Column(name = "is_representative")
    private Boolean isRepresentative = false;

    @Column(name = "earned_at")
    private LocalDateTime earnedAt;

    @PrePersist
    public void prePersist() {
        this.earnedAt = LocalDateTime.now();
    }
}