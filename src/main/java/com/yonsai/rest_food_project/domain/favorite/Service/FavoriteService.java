package com.yonsai.rest_food_project.domain.favorite.Service;

import java.util.List;

import com.yonsai.rest_food_project.domain.restArea.dto.RestAreaResponseDto;

public interface FavoriteService {

    boolean toggleFavorite(String userEmail, String stdRestCd);

    List<RestAreaResponseDto> findAllByUserEmailWithRestArea(String userEmail);

}
