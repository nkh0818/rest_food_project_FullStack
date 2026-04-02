package com.yonsai.rest_food_project.domain.review.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.yonsai.rest_food_project.domain.review.dto.*;
import com.yonsai.rest_food_project.domain.review.service.ReviewService;
import com.yonsai.rest_food_project.global.auth.PrincipalDetails;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
@Validated
public class ReviewController {

    private final ReviewService reviewService;

    // 새로운 리뷰 등록
    @PostMapping
    public ResponseEntity<ReviewResponseDTO> createReview(
            @AuthenticationPrincipal PrincipalDetails userDetails,
            @ModelAttribute ReviewRequestDTO dto) {

        Long userId = userDetails.getUser().getId();

        return ResponseEntity.ok(reviewService.createReview(userId, dto));
    }

    // 휴게소 별 리뷰 조회
    @GetMapping("/rest-area/{restAreaId}")
    public ResponseEntity<List<ReviewResponseDTO>> getReviewsByRestArea(
            @PathVariable Long restAreaId,
            @AuthenticationPrincipal PrincipalDetails userDetails) {

        Long userId = userDetails.getUser().getId();

        // 특정 휴게소의 리뷰 목록 조회
        return ResponseEntity.ok(
                reviewService.getReviewsByRestArea(restAreaId, userId)
        );
    }

    // 음식별 리뷰 조회
    @GetMapping("/food/{foodId}")
    public ResponseEntity<List<ReviewResponseDTO>> getReviewsByFood(
            @PathVariable Long foodId) {

        return ResponseEntity.ok(reviewService.getReviewsByFood(foodId));
    }

    // 리뷰 조회
    @GetMapping("/{reviewId}")
    public ResponseEntity<ReviewResponseDTO> getReview(@PathVariable Long reviewId) {
        return ResponseEntity.ok(reviewService.getReview(reviewId));
    }

    // 리뷰 수정
    @PutMapping("/{reviewId}")
    public ResponseEntity<Void> updateReview(
            @PathVariable Long reviewId,
            @AuthenticationPrincipal PrincipalDetails userDetails,
            @RequestBody ReviewUpdateRequestDTO dto) {

        Long userId = userDetails.getUser().getId();

        reviewService.updateReview(userId, reviewId, dto);
        return ResponseEntity.ok().build();
    }

    // 리뷰 삭제
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Void> deleteReview(
    		@PathVariable Long reviewId,
    		@AuthenticationPrincipal PrincipalDetails userDetails) {

        Long userId = userDetails.getUser().getId();

        reviewService.deleteReview(userId, reviewId);
        return ResponseEntity.ok().build();
    }

    // 평균 평점 조회
    @GetMapping("/rest-area/{restAreaId}/average")
    public ResponseEntity<Double> getAverage(@PathVariable Long restAreaId) {
        return ResponseEntity.ok(reviewService.getAverageRating(restAreaId));
    }

    // 리뷰 개수 조회
    @GetMapping("/rest-area/{restAreaId}/count")
    public ResponseEntity<Long> getCount(@PathVariable Long restAreaId) {
        return ResponseEntity.ok(reviewService.getReviewCount(restAreaId));
    }
    
    // 좋아요 추가
    @PostMapping("/{reviewId}/like")
    public ResponseEntity<ReviewLikeResponseDTO> likeReview(
            @PathVariable Long reviewId,
            @AuthenticationPrincipal PrincipalDetails userDetails) {

        Long userId = userDetails.getUser().getId();

        ReviewLikeResponseDTO response = reviewService.likeReview(userId, reviewId);

        return ResponseEntity.ok(response);
    }

    // 좋아요 취소
    @DeleteMapping("/{reviewId}/like")
    public ResponseEntity<ReviewLikeResponseDTO> unlikeReview(
            @PathVariable Long reviewId,
            @AuthenticationPrincipal PrincipalDetails userDetails) {
        
    	Long userId = userDetails.getUser().getId();

        ReviewLikeResponseDTO response = reviewService.unlikeReview(userId, reviewId);

        return ResponseEntity.ok(response);
    } 
}