package com.yonsai.rest_food_project.domain.user.history.service;

import java.util.List;

import com.yonsai.rest_food_project.domain.user.entity.User;
import com.yonsai.rest_food_project.domain.user.history.dto.HistoryResponseDTO;

public interface HistoryService {
    List<HistoryResponseDTO> getUserHistory(User user);
}
