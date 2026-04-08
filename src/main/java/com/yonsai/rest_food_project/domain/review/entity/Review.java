package com.yonsai.rest_food_project.domain.review.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.yonsai.rest_food_project.domain.restArea.entity.Food;
import com.yonsai.rest_food_project.domain.restArea.entity.RestArea;
import com.yonsai.rest_food_project.domain.user.entity.User;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "review")
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rest_area_id", nullable = false) // 휴게소 전체 리뷰를 위해 추가
    @JsonIgnore
    private RestArea restArea;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "food_id")
    private Food food; // null이면 휴게소 전체 리뷰

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content; // 리뷰 내용

    @Column(nullable = false)
    private int rating; // 평점 (1~5)
    private String tag; // 맛있다, 별로다 등 태그

    @Column(name = "like_count")
    @Builder.Default
    private int likeCount = 0;

    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private java.util.List<ReviewLike> likes = new java.util.ArrayList<>();

    @Column(name = "report_count")
    @Builder.Default
    private int reportCount = 0;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // 인증 0408나다희 추가
    @Column(name = "gps_verified")
    @Builder.Default
    private Boolean gpsVerified = false;

    // 자동으로 시간 설정
    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // 추천 수 증가
    public void addLike() {
        this.likeCount++;
    }

    // 추천 수 감소
    public void removeLike() {
        if (this.likeCount > 0) {
            this.likeCount--;
        }
    }

    public void update(String content, Integer rating, String tag) {

        // null 방지
        if (content != null)
            this.content = content;
        if (rating != null)
            this.rating = rating;
        if (tag != null)
            this.tag = tag;
        if (gpsVerified != null)
            this.gpsVerified = gpsVerified;
    }
}