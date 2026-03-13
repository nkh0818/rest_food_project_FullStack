package com.yonsai.rest_food_project.domain.auth.service;

import com.yonsai.rest_food_project.domain.auth.dto.AuthResponseDTO;
import com.yonsai.rest_food_project.domain.auth.dto.OAuthRequestDTO;

public interface AuthService {

    AuthResponseDTO login(OAuthRequestDTO dto);

}
