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
    private String gasPrice; // 기름값 정보 (문자열 또는 별도 필드)
    // 추가
    private String stdRestCd; // 공공데이터와 매칭할 '휴게소 코드' (중요!)
    private Double xValue; // 길찾기용 경도
    private Double yValue; // 길찾기용 위도
    @OneToMany(mappedBy = "restArea", cascade = CascadeType.ALL)
    private List<Food> foods; // 휴게소에 딸린 음식들
}