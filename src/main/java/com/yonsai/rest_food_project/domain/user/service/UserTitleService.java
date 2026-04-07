package com.yonsai.rest_food_project.domain.user.service;

import com.yonsai.rest_food_project.domain.user.entity.User;

public interface UserTitleService {

    void checkAndGrantTitles(User user);

}
