package com.yonsai.rest_food_project.domain.user.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "title")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Title {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "title_id")
    private Long titleId;

    @Column(name = "title_name", nullable = false, unique = true, length = 100)
    private String titleName;

    @Column(name = "description", length = 255)
    private String description;

    @Column(name = "title_type", length = 50)
    private String titleType;
    // SCORE / REVIEW / PHOTO / LIKE / ROUTE / REST_AREA / FOOD / EXPERT

    @Column(name = "condition_type", length = 50)
    private String conditionType;
    // REVIEW_COUNT / SCORE / FOOD_CATEGORY_COUNT / ROUTE_REVIEW_COUNT ...

    @Column(name = "condition_value")
    private Integer conditionValue;

    @Column(name = "category_code", length = 50)
    private String categoryCode; // FOOD 칭호용

    @Column(name = "route_name", length = 100)
    private String routeName; // ROUTE 칭호용

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "priority")
    private Integer priority;

    @Builder.Default
    @Column(name = "is_active")
    private Boolean isActive = true;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}