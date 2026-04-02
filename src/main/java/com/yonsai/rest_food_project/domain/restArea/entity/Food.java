package com.yonsai.rest_food_project.domain.restArea.entity;

import java.util.ArrayList;
import java.util.List;

import com.yonsai.rest_food_project.domain.review.entity.Review;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Food {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "food_id")
    private Long id;

    @Column(nullable = false)
    private String foodName; // API 원본 음식명

    @Column(length = 100)
    private String storeName; // API의 매장명

    @Column(length = 100)
    private String sourceType; // 일반 / 청년창업 / 브랜드

    @Column(length = 150)
    private String normalizedName; // 정규화 음식명

    @Column(length = 50)
    private String categoryCode; // 음식 분류 코드

    private int price;

    @Column(length = 1000)
    private String nutritionInfo; // 영양정보

    @Builder.Default
    private Integer isBest = 0; // 기존 API 대표메뉴 표시값 유지

    @Builder.Default
    private Boolean isSignature = false; // 대표 메뉴

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rest_area_id")
    private RestArea restArea;

    @Builder.Default
    @OneToMany(mappedBy = "food", cascade = CascadeType.ALL)
    private List<Review> reviews = new ArrayList<>();
}