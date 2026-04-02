package com.yonsai.rest_food_project.domain.review.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.yonsai.rest_food_project.domain.review.dto.*;
import com.yonsai.rest_food_project.domain.review.service.ReviewService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
@Validated
@CrossOrigin(origins = "*") /* security에 CrossOrigin 설정이 되어 있어서 필요 없는 어노테이션입니다 */
public class ReviewController {

    /*
    1. X-USER-ID 헤더와 보안
현재 userId를 헤더에서 직접 받고 있는데 사용자가 브라우저 개발자 도구에서 헤더 값만 2, 3으로 바꾸면 다른 사람의 이름으로 리뷰를 쓰거나 좋아요를 누를 수 있습니다.

**accessToken**을 받아 서버에서 직접 유저 정보를 꺼내야 합니다.

2. 리뷰 수정/삭제의 권한 체크 누락
지금 코드는 reviewId만 알면 누구나 리뷰를 수정하거나 삭제할 수 있는 상태입니다. updateReview와 deleteReview에도 userId를 파라미터로 넘겨서, **"이 리뷰를 쓴 사람과 지금 삭제하려는 사람이 같은가?"**를 서비스 레이어에서 체크해야 합니다.
    */

    private final ReviewService reviewService;

    // 새로운 리뷰 등록
    @PostMapping
    public ResponseEntity<ReviewResponseDTO> createReview(
            @RequestHeader(value = "X-USER-ID", required = false) Long userId,
            @Valid @RequestBody ReviewRequestDTO dto) {

        // 테스트용 기본 유저 설정
        if (userId == null)
            userId = 1L;

        return ResponseEntity.ok(reviewService.createReview(userId, dto));
    }

    // 휴게소 별 리뷰 조회
    @GetMapping("/rest-area/{restAreaId}")
    public ResponseEntity<List<ReviewResponseDTO>> getReviewsByRestArea(
            @PathVariable Long restAreaId,
            @RequestHeader(value = "X-USER-ID", required = false) Long userId) {

        if (userId == null) userId = 1L;

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

    // 내 리뷰 조회
    // 로그인 사용자 기준 조회
    @GetMapping("/{reviewId}")
    public ResponseEntity<ReviewResponseDTO> getReview(@PathVariable Long reviewId) {
        return ResponseEntity.ok(reviewService.getReview(reviewId));
    }

    // 리뷰 수정
    @PutMapping("/{reviewId}")
    public ResponseEntity<Void> updateReview(
            @PathVariable Long reviewId,
            @RequestBody ReviewUpdateRequestDTO dto) {

        reviewService.updateReview(reviewId, dto);
        return ResponseEntity.ok().build();
    }

    // 리뷰 삭제
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long reviewId) {
        reviewService.deleteReview(reviewId);
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
    public ResponseEntity<Void> likeReview(
            @PathVariable Long reviewId,
            @RequestHeader(value = "X-USER-ID", required = false) Long userId) {

        if (userId == null) userId = 1L; // 테스트용

        reviewService.likeReview(reviewId, userId);

        return ResponseEntity.ok().build();
    }

    // 좋아요 취소
    @DeleteMapping("/{reviewId}/like")
    public ResponseEntity<Void> unlikeReview(
            @PathVariable Long reviewId,
            @RequestHeader(value = "X-USER-ID", required = false) Long userId) {

        if (userId == null) userId = 1L;

        reviewService.unlikeReview(reviewId, userId);

        return ResponseEntity.ok().build();
    }

    /* 해당 컨트롤러는 RestController인데 String으로 된 이 컨트롤러는 필요없지 않나 하는 생각이 듭니다... */
    
    @GetMapping("/review/edit")
    public String reviewEditPage() {
        return "review_edit";
    }
    
}