package com.yonsai.rest_food_project.domain.favorite.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yonsai.rest_food_project.domain.favorite.entity.Favorite;
import com.yonsai.rest_food_project.domain.favorite.repository.FavoriteRepository;
import com.yonsai.rest_food_project.domain.restArea.dto.RestAreaResponseDto;
import com.yonsai.rest_food_project.domain.restArea.entity.RestArea;
import com.yonsai.rest_food_project.domain.restArea.repository.RestAreaRepository;
import com.yonsai.rest_food_project.domain.user.entity.User;
import com.yonsai.rest_food_project.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class FavoriteServiceImpl implements FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final RestAreaRepository restAreaRepository;
    private final UserRepository userRepository;

    @Override
    public boolean toggleFavorite(String userEmail, String stdRestCd) {

        User user = userRepository.findByEmail(userEmail).orElseThrow();
        RestArea restArea = restAreaRepository.findByStdRestCd(stdRestCd).orElseThrow();

        Optional<Favorite> favorite = favoriteRepository.findByUserAndRestArea(user, restArea);

        if (favorite.isPresent()) {
            favoriteRepository.delete(favorite.get());
            return false;
        } else {
            favoriteRepository.save(
                    Favorite.builder()
                            .user(user)
                            .restArea(restArea)
                            .build());
            return true;
        }
    }

    @Override
public List<RestAreaResponseDto> findAllByUserEmailWithRestArea(String userEmail) {
    List<Favorite> favorites = favoriteRepository.findAllByUserEmailWithRestArea(userEmail);

    return favorites.stream()
            .map(favorite -> RestAreaResponseDto.fromEntity(favorite.getRestArea()))
            .filter(dto -> dto.getGasolinePrice() != null && dto.getGasolinePrice() > 0)
            .collect(Collectors.toList());
}

}
