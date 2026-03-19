package com.yonsai.rest_food_project.domain.restArea.entity;

import jakarta.persistence.*; // Column, Entity, Id 등을 위해 필요
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor // 이 어노테이션이 에러의 주원인이었습니다.
public class YoutubePlaylist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true) // Column 어노테이션을 위해 jakarta.persistence.* 임포트 필요
    private String playlistId;

    private String title;

    private String searchKey;
    @Column(name = "item_count", nullable = false)
    private Integer itemCount = 0; // 기본값을 0으로 설정
}