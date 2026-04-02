package com.yonsai.rest_food_project.domain.user.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserRole {
    USER("ROLE_USER","유저"),
    ADMIN("ROLE_ADMIN", "관리자");

    private final String key;
    private final String title;
}
