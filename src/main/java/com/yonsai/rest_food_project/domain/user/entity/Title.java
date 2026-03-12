package com.yonsai.rest_food_project.domain.user.entity;

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

    @Column(name = "title_name", nullable = false, length = 100)
    private String titleName;

    @Column(name = "title_condition", length = 255)
    private String titleCondition;

    @Column(name = "title_desc", length = 255)
    private String titleDesc;
}