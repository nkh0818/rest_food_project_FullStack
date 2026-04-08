package com.yonsai.rest_food_project.domain.review.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.yonsai.rest_food_project.domain.review.dto.*;
import com.yonsai.rest_food_project.domain.review.service.BlockService;
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
    private final BlockService blockService;

    // 새로운 리뷰 등록
    @PostMapping
    public ResponseEntity<ReviewResponseDTO> createReview(
            @AuthenticationPrincipal PrincipalDetails principalDetails, // 토큰에서 유저 추출
            @Valid @RequestBody ReviewRequestDTO dto) {

        // principalDetails가 null이면 로그인이 안 된 상태
        Long userId = principalDetails.getUser().getId();
        return ResponseEntity.ok(reviewService.createReview(userId, dto));
    }

    // 휴게소 별 리뷰 조회
    @GetMapping("/rest-area/{restAreaId}")
    public ResponseEntity<List<ReviewResponseDTO>> getReviewsByRestArea(
            @PathVariable Long restAreaId,
            @AuthenticationPrincipal PrincipalDetails principalDetails) {

        // 로그인 안 했으면 좋아요 여부 체크를 위해 null 전달, 했으면 ID 전달
        Long userId = (principalDetails != null) ? principalDetails.getUser().getId() : null;

        return ResponseEntity.ok(reviewService.getReviewsByRestArea(restAreaId, userId));
    }

    // 음식별 리뷰 조회
    @GetMapping("/food/{foodId}")
    public ResponseEntity<List<ReviewResponseDTO>> getReviewsByFood(
            @PathVariable Long foodId) {

        return ResponseEntity.ok(reviewService.getReviewsByFood(foodId));
    }

    @GetMapping("/me")
    public ResponseEntity<List<ReviewResponseDTO>> getMyReviews(
            @AuthenticationPrincipal PrincipalDetails principalDetails) {

        // 토큰에서 현재 로그인한 유저 ID 추출
        Long userId = principalDetails.getUser().getId();

        // 서비스에서 내가 쓴 리뷰 목록 가져오기
        List<ReviewResponseDTO> myReviews = reviewService.getMyReviews(userId);

        return ResponseEntity.ok(myReviews);
    }

    // 내 리뷰 조회
    // 로그인 사용자 기준 조회
    @GetMapping("/{reviewId}")
    public ResponseEntity<ReviewResponseDTO> getReview(@PathVariable Long reviewId) {
        return ResponseEntity.ok(reviewService.getReview(reviewId));
    }

    // 커뮤니티 페이지용 리뷰 조회 0405 나다희 추가
    @GetMapping("/community")
    public ResponseEntity<PagedModel<ReviewResponseDTO>> getCommunityReviews(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        // 1. 로그인 여부에 따라 userId 추출 (비로그인이면 null)
        Long userId = (principalDetails != null) ? principalDetails.getUser().getId() : null;

        // 2. 서비스 호출 (PagedModel 반환)
        PagedModel<ReviewResponseDTO> reviews = blockService.getCommunityReviews(userId, pageable);

        return ResponseEntity.ok(reviews);
    }

    // 리뷰 수정
    @PutMapping("/{reviewId}")
    public ResponseEntity<Void> updateReview(
            @PathVariable Long reviewId,
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @RequestBody ReviewUpdateRequestDTO dto) {

        Long userId = principalDetails.getUser().getId();
        reviewService.updateReview(reviewId, userId, dto);
        return ResponseEntity.ok().build();
    }

    // 리뷰 삭제
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Void> deleteReview(
            @PathVariable Long reviewId,
            @AuthenticationPrincipal PrincipalDetails principalDetails) {
        reviewService.deleteReview(reviewId, principalDetails.getUser());
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

    // // 좋아요 추가
    // @PostMapping("/{reviewId}/like")
    // public ResponseEntity<Void> likeReview(
    //         @PathVariable Long reviewId,
    //         @AuthenticationPrincipal PrincipalDetails principalDetails) {

    //     reviewService.likeReview(reviewId, principalDetails.getUser().getId());
    //     return ResponseEntity.ok().build();
    // }

    // // 좋아요 취소
    // @DeleteMapping("/{reviewId}/like")
    // public ResponseEntity<Void> unlikeReview(
    //         @PathVariable Long reviewId,
    //         @AuthenticationPrincipal PrincipalDetails principalDetails) {

    //     reviewService.unlikeReview(reviewId, principalDetails.getUser().getId());
    //     return ResponseEntity.ok().build();
    // }
}