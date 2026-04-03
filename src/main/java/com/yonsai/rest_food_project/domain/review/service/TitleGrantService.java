package com.yonsai.rest_food_project.domain.review.service;

import com.yonsai.rest_food_project.domain.user.entity.User;

public interface TitleGrantService {

    void checkAndGrantTitles(User user);
}
