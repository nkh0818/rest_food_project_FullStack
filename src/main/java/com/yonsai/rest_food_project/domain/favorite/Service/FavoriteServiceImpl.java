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

// [즐겨찾기 서비스] 사용자가 선호하는 휴게소(RestArea)를 찜하거나 목록을 조회하는 기능을 담당

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class FavoriteServiceImpl implements FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final RestAreaRepository restAreaRepository;
    private final UserRepository userRepository;

    // [찜하기/취소] 이미 즐겨찾기가 되어 있으면 삭제하고, 없으면 새로 추가하는 토글 기능을 수행
    @Override
    public boolean toggleFavorite(String userEmail, String stdRestCd) {

        User user = userRepository.findByEmail(userEmail).orElseThrow();
        RestArea restArea = restAreaRepository.findByStdRestCd(stdRestCd).orElseThrow();

        Optional<Favorite> favorite = favoriteRepository.findByUserAndRestArea(user, restArea);

        if (favorite.isPresent()) {
            favoriteRepository.delete(favorite.get()); // 이미 존재 시 삭제 (즐겨찾기 해제)
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

    // [목록 조회] 사용자가 찜한 휴게소 리스트를 가져와 DTO로 변환하며, 유효한 유가 정보가 있는 곳만 필터링하여 반환
    @Override
    public List<RestAreaResponseDto> findAllByUserEmailWithRestArea(String userEmail) {
        // Fetch Join 등을 활용해 Favorite과 연관된 RestArea를 한꺼번에 조회
        List<Favorite> favorites = favoriteRepository.findAllByUserEmailWithRestArea(userEmail);

        return favorites.stream()
                .map(favorite -> RestAreaResponseDto.fromEntity(favorite.getRestArea()))
                // 비즈니스 규칙: 유가 정보(gasolinePrice)가 없는 데이터는 목록에서 제외
                .filter(dto -> dto.getGasolinePrice() != null && dto.getGasolinePrice() > 0)
                .collect(Collectors.toList());
    }

}
