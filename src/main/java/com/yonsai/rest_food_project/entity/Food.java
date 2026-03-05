package com.yonsai.rest_food_project.entity;

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
    private String foodName;

    private int price;
    private String category; // 한식, 양식 등
    private boolean isBest; // 베스트 유무

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rest_area_id")
    private RestArea restArea;
}