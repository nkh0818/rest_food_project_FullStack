package com.yonsai.rest_food_project.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rest_area_id") // 휴게소 전체 리뷰를 위해 추가
    private RestArea restArea;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "food_id")
    private Food food;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content; // 리뷰 내용

    @Column(nullable = false) 
    private int rating; // 평점 (1~5)
    private String tag; // 맛있다, 별로다 등 태그

    @Builder.Default // 기본값 설정
    private int likeCount = 0;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    private String imageUrl;
    private LocalDateTime createdAt;


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
}