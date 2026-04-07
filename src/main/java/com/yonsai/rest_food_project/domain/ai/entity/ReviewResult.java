package com.yonsai.rest_food_project.domain.ai.entity;
import java.util.Set;

public record ReviewResult(

    String summary, // 한줄(30자)요약
    Set<String> tags, // 메뉴 혹은 태그
    String score      // POSITIVE(10), NEGATIVE(-5), NEUTRAL(5)


) {}
