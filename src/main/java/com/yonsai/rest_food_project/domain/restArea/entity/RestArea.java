package com.yonsai.rest_food_project.domain.restArea.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

import com.yonsai.rest_food_project.domain.review.entity.Review;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RestArea {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name; // 휴게소 명칭

    private String routeName; // 노선명 (예: 경부선)

    private String location; // 주소

    private String direction; // 상행,하행

    @Column(unique = true)
    private String stdRestCd; // 휴게소 코드 (API 매칭용)

    @Column(name = "x_value")
    private Double longitude; // 경도

    @Column(name = "y_value")
    private Double latitude; // 위도

    // --- 주유소 정보 관련 추가 ---

    @Column(name = "gasoline_price")
    @Builder.Default
    private Integer gasolinePrice = 0; // 휘발유

    @Column(name = "disel_price")
    @Builder.Default
    private Integer diselPrice = 0; // 경유

    @Column(name = "lpg_price")
    @Builder.Default
    private Integer lpgPrice = 0; // LPG

    private String oilCompany; // 정유사 (예: SK, GS, 알뜰주유소 등)
    private String telNo; // 주유소 전화번호

    // --- 관계 설정 ---

    // 음식 조회
    @Builder.Default
    @OneToMany(mappedBy = "restArea", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Food> foods = new ArrayList<>();

    // 휴게소 자체 리뷰 조회
    @Builder.Default
    @OneToMany(mappedBy = "restArea", cascade = CascadeType.ALL)
    private List<Review> reviews = new ArrayList<>();
}