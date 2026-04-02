package com.yonsai.rest_food_project.domain.restArea.entity;

import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
    private Long id;

    @Column(nullable = false)
    private String foodName; // 음식명

    @Column(length = 100)
    private String storeName; // 매장명 (API 추가분)

    @Column(length = 100)
    private String sourceType; // 일반 / 청년창업 / 브랜드 (API 추가분)

    @Column(length = 150)
    private String normalizedName; // 정규화 음식명 (API 추가분)

    @Column(length = 50)
    private String categoryCode; // 음식 분류 코드 (API 추가분)

    private String category; // 기존 카테고리 (한식, 양식 등)

    private int price;

    @Column(length = 1000)
    private String nutritionInfo; // 영양정보 (API 추가분)

    @Builder.Default
    private Integer isBest = 0; // 베스트 유무 (기존 및 API 유지)

    @Builder.Default
    private Boolean isSignature = false; // 대표 메뉴 여부 (API 추가분)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rest_area_id")
    @JsonIgnore // 순환 참조 방지용 (기존 유지)
    private RestArea restArea;

    @Builder.Default
    @OneToMany(mappedBy = "food", cascade = CascadeType.ALL)
    private List<Review> reviews = new ArrayList<>();
}