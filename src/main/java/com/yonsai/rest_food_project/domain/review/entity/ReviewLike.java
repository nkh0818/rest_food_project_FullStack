package com.yonsai.rest_food_project.domain.review.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

import com.yonsai.rest_food_project.domain.user.entity.User;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(
    name = "review_likes",
    uniqueConstraints = { // 하나의 review_id와 user_id만 매칭하겠다
        @UniqueConstraint(
            name = "uk_review_user",
            columnNames = {"review_id", "user_id"}
        )
    }
)
public class ReviewLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 어떤 리뷰?
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id", nullable = false)
    private Review review;

    // 누가?
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 언제?
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}