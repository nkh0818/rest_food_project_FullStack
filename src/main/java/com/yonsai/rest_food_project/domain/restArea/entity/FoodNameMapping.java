package com.yonsai.rest_food_project.domain.restArea.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "food_name_mapping")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FoodNameMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mapping_id")
    private Long id;

    @Column(nullable = false, length = 100)
    private String keyword; // 예: 돈까스, 제육, 우동

    @Column(nullable = false, length = 100)
    private String normalizedName; // 예: 돈까스

    @Column(nullable = false, length = 50)
    private String categoryCode; // 예: PORK_CUTLET

    @Column(length = 255)
    private String description;
}