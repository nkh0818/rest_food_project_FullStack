package com.yonsai.rest_food_project.domain.ai.entity;
import java.util.Set;

import jakarta.persistence.Column;

public record ReviewResult(

    @Column(name = "ai_summary")
    String summary, // 한줄(30자)요약

    @Column(name = "ai_tags")
    Set<String> tags, // 메뉴 혹은 태그

    @Column(name = "ai_score")
    String score      // POSITIVE(10), NEGATIVE(-5), NEUTRAL(5)


) {}
