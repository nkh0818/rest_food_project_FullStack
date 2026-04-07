package com.yonsai.rest_food_project.domain.ranking.dto;

import java.util.List;

import com.yonsai.rest_food_project.domain.restArea.dto.RestAreaResponseDto;
import com.yonsai.rest_food_project.domain.review.dto.ReviewResponseDTO;
import com.yonsai.rest_food_project.domain.user.dto.UserResponseDTO;

// 랭킹 페이지에서 사용하기 위한 토탈 DTO

public record TotalRankingResponseDTO(
    List<String> hotSearchKeywords,             // 실시간 검색
    List<ReviewResponseDTO> bestReviews,        // 좋아요 많은 리뷰
    List<RestAreaResponseDto> trendingRestAreaNames,         // 리뷰 급상승 휴게소
    List<UserResponseDTO> topExplorers,         // 이달의 리뷰어
    List<RestAreaResponseDto> lowestGasPrices   // 최저가 주유소
) {}
