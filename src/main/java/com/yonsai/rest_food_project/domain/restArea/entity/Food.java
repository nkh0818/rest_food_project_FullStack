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
    private Long id;

    @Column(nullable = false)
    private String foodName;

    private int price;
    private String category; // 한식, 양식 등

    @Builder.Default
    private int isBest = 0; // 베스트 유무 (0,1)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rest_area_id")
    private RestArea restArea;

    @Builder.Default
    @OneToMany(mappedBy = "food", cascade = CascadeType.ALL)
    private List<Review> reviews = new ArrayList<>();
}