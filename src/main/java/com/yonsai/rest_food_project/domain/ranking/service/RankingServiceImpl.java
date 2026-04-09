package com.yonsai.rest_food_project.domain.ranking.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yonsai.rest_food_project.domain.ranking.dto.TotalRankingResponseDTO;
import com.yonsai.rest_food_project.domain.restArea.dto.RestAreaResponseDto;
import com.yonsai.rest_food_project.domain.restArea.repository.RestAreaRepository;
import com.yonsai.rest_food_project.domain.review.dto.ReviewResponseDTO;
import com.yonsai.rest_food_project.domain.review.repository.ReviewRepository;
import com.yonsai.rest_food_project.domain.user.dto.UserResponseDTO;
import com.yonsai.rest_food_project.domain.user.repository.UserRepository;
import com.yonsai.rest_food_project.global.common.RedisService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RankingServiceImpl implements RankingService {

    private final RedisService redisService;
    private final ReviewRepository reviewRepository;
    private final RestAreaRepository restAreaRepository;
    private final UserRepository userRepository;

    @Override
    public TotalRankingResponseDTO getTotalRankings() {
        // 1. 실시간 검색어 (Redis 상위 10개)
        List<String> hotKeywords = redisService.getDailyRanking();

        // 2. 실시간 리뷰 (TOP 5)
        List<ReviewResponseDTO> bestReviews = reviewRepository.findTop5ByOrderByIdDesc()
            .stream()
            .map(ReviewResponseDTO::from)
            .collect(Collectors.toList());

        // 3. 리뷰 급상승 휴게소
        List<RestAreaResponseDto> trendingRestAreas = reviewRepository.findTrendingRestAreas(PageRequest.of(0, 10))
                .stream()
                .map(RestAreaResponseDto::fromEntity)
                .collect(Collectors.toList());
        
        java.util.Collections.shuffle(trendingRestAreas);

        // 4. 이달의 탐험가 (XP 순 TOP 5)
        List<UserResponseDTO> topExplorers = userRepository.findAllByOrderByXpDesc(PageRequest.of(0, 5))
                .stream()
                .map(UserResponseDTO::from)
                .collect(Collectors.toList());

        // 5. 전국 최저가 주유소 (가격 낮은 순 TOP 10)
        List<RestAreaResponseDto> lowestGasPrices = restAreaRepository
                .findTop5ByGasolinePriceGreaterThanOrderByGasolinePriceAsc(0.0)
                .stream()
                .map(RestAreaResponseDto::fromEntity)
                .collect(Collectors.toList());

        return new TotalRankingResponseDTO(
                hotKeywords,
                bestReviews,
                trendingRestAreas,
                topExplorers,
                lowestGasPrices);
    }
}