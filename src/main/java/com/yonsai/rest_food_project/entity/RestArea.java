package com.yonsai.rest_food_project.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

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
    private String location; // 주소 또는 좌표

    @Column(unique = true)
    private String stdRestCd; // 휴게소 코드 (API 매칭용)

    private Double xValue; // 경도
    private Double yValue; // 위도

    // --- 주유소 정보 관련 추가 ---

    @Column(name = "gasoline_price")
    private Integer gasolinePrice = 0; // 휘발유

    @Column(name = "disel_price")
    private Integer diselPrice = 0; // 경유

    @Column(name = "lpg_price")
    private Integer lpgPrice = 0; // LPG

    private String oilCompany; // 정유사 (예: SK, GS, 알뜰주유소 등)
    private String telNo; // 주유소 전화번호

    // --- 관계 설정 ---

    @OneToMany(mappedBy = "restArea", cascade = CascadeType.ALL)
    private List<Food> foods;
}