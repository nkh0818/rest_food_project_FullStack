package com.yonsai.rest_food_project.domain.review.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yonsai.rest_food_project.domain.restArea.entity.*;
import com.yonsai.rest_food_project.domain.restArea.repository.*;
import com.yonsai.rest_food_project.domain.review.dto.*;
import com.yonsai.rest_food_project.domain.review.entity.Review;
import com.yonsai.rest_food_project.domain.review.entity.ReviewLike;
import com.yonsai.rest_food_project.domain.review.repository.ReviewLikeRepository;
import com.yonsai.rest_food_project.domain.review.repository.ReviewRepository;
import com.yonsai.rest_food_project.domain.user.entity.User;
import com.yonsai.rest_food_project.domain.user.repository.UserRepository;
import com.yonsai.rest_food_project.domain.user.service.UserTitleService;
import com.yonsai.rest_food_project.global.common.LocationUtils;
import com.yonsai.rest_food_project.global.exception.RoadQuestException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final UserTitleService titleService;
    private final RestAreaRepository restAreaRepository;
    private final FoodRepository foodRepository;
    private final ReviewLikeRepository reviewLikeRepository;

    private final AsyncAiReviewService asyncAiReviewService;

    private final LocationUtils locationUtils;

    @Transactional
    @Override
    public ReviewResponseDTO createReview(Long userId, ReviewRequestDTO dto) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RoadQuestException("사용자를 찾을 수 없습니다. ID: " + userId));

        RestArea restArea = restAreaRepository.findByStdRestCd(dto.getRestAreaId())
                .orElseThrow(() -> new RoadQuestException("휴게소를 찾을 수 없습니다. ID: " + dto.getRestAreaId()));

        Food food = null;
        if (dto.getFoodId() != null) {
            food = foodRepository.findById(dto.getFoodId()).orElse(null);
        }

        String tagString = (dto.getTags() == null || dto.getTags().isEmpty())
                ? null
                : String.join(",", dto.getTags());

        boolean isVerified = false;

        if (restArea.getLatitude() != null && restArea.getLongitude() != null &&
                restArea.getLatitude() != 0.0 && restArea.getLongitude() != 0.0 &&
                dto.getUserLat() != null && dto.getUserLon() != null) {

            double distance = locationUtils.getDistance(
                    dto.getUserLat(), dto.getUserLon(),
                    restArea.getLatitude(), restArea.getLongitude());

            // 1000m(1km) 이내면 인증 성공
            isVerified = distance <= 1000;
            log.info("GPS 인증 결과: {} (거리: {}m)", isVerified, (int) distance);
        } else {
            log.warn("좌표 데이터 부족으로 GPS 인증을 스킵합니다. (휴게소 코드: {})", restArea.getStdRestCd());
            // 좌표가 없으면 기본값 false 유지
        }

        Review review = Review.builder()
                .user(user)
                .restArea(restArea)
                .food(food)
                .rating(dto.getRating())
                .content(dto.getContent())
                .tag(tagString)
                .imageUrl(dto.getImageUrl())
                .gpsVerified(isVerified)
                .build();

        Review savedReview = reviewRepository.save(review);

        user.addActivityScore(30);
        user.setRewardPoint(user.getRewardPoint() + 100);

        int newXp = user.getXp() + 30;
        if (newXp >= 100) {
            user.setLevel(user.getLevel() + 1);
            user.setXp(newXp % 100);
        } else {
            user.setXp(newXp);
        }

        userRepository.saveAndFlush(user);
        titleService.checkAndGrantTitles(user);

        // Ai 로직 추가 - 비동기 처리
        long reviewCount = reviewRepository.countByRestArea(restArea);
        if (reviewCount >= 5 && reviewCount % 5 == 0) {
            List<Review> recentReviews = reviewRepository.findTop10ByRestAreaOrderByCreatedAtDesc(restArea);

            String combinedContent = recentReviews.stream()
                    .map(Review::getContent)
                    .collect(Collectors.joining("\n"));

            asyncAiReviewService.analyzeAndUpdate(restArea.getStdRestCd(), combinedContent);
        }

        userRepository.saveAndFlush(user);

        return ReviewResponseDTO.from(savedReview);
    }

    // 커뮤니티 전체 피드 조회
    @Transactional(readOnly = true)
    @Override
    public Page<ReviewResponseDTO> getCommunityReviews(Pageable pageable) {
        Page<Review> reviews = reviewRepository.findAll(pageable);
        return reviews.map(ReviewResponseDTO::from);
    }

    @Override
    public List<ReviewResponseDTO> getReviewsByRestArea(String restAreaId, Long userId) {
        return reviewRepository.findByRestAreaStdRestCd(restAreaId)
                .stream()
                .map(review -> {
                    boolean liked = false;
                    if (userId != null) {
                        liked = reviewLikeRepository.existsByReviewIdAndUserId(review.getId(), userId);
                    }
                    return ReviewResponseDTO.from(review, liked);
                })
                .toList();
    }

    @Override
    public List<ReviewResponseDTO> getReviewsByFood(Long foodId) {
        return reviewRepository.findByFoodId(foodId)
                .stream()
                .map(ReviewResponseDTO::from)
                .toList();
    }

    @Override
    public List<ReviewResponseDTO> getMyReviews(Long userId) {
        return reviewRepository.findByUserId(userId)
                .stream()
                .map(review -> {
                    return ReviewResponseDTO.from(review);
                })
                .toList();
    }

    @Override
    public ReviewResponseDTO getReview(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RoadQuestException("리뷰를 찾을 수 없습니다. ID: " + reviewId));
        return ReviewResponseDTO.from(review);
    }

    @Transactional
    @Override
    public void updateReview(Long reviewId, Long userId, ReviewUpdateRequestDTO dto) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RoadQuestException("리뷰를 찾을 수 없습니다. ID: " + reviewId));

        if (!review.getUser().getId().equals(userId)) {
            throw new RoadQuestException("해당 리뷰를 수정할 권한이 없습니다.");
        }

        review.update(dto.getContent(), dto.getRating(), dto.getTag());
    }

    @Transactional
    @Override
    public void deleteReview(Long reviewId, User currentUser) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("해당 리뷰가 없습니다."));

        if (review.getUser().getId().equals(currentUser.getId()) || currentUser.isAdmin()) {
            reviewRepository.delete(review);
        } else {
            throw new AccessDeniedException("삭제 권한이 없습니다.");
        }
    }

    @Override
    public Double getAverageRating(String restAreaId) {
        return reviewRepository.getAverageRating(restAreaId);
    }

    @Override
    public Long getReviewCount(String restAreaId) {
        return reviewRepository.countByRestAreaStdRestCd(restAreaId);
    }

    @Transactional
    @Override
    public void likeReview(Long reviewId, Long userId) {
        // 1. 리뷰 조회
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RoadQuestException("해당 리뷰를 찾을 수 없습니다."));

        // 2. 본인 리뷰 체크
        if (review.getUser().getId().equals(userId)) {
            throw new RoadQuestException("자신의 리뷰에는 좋아요를 누를 수 없습니다.");
        }

        // 3. 중복 좋아요 체크
        if (reviewLikeRepository.existsByReviewIdAndUserId(reviewId, userId)) {
            throw new RoadQuestException("이미 좋아요를 누른 리뷰입니다.");
        }

        // 4. 좋아요 저장
        User user = userRepository.getReferenceById(userId);
        ReviewLike reviewLike = ReviewLike.builder()
                .review(review)
                .user(user)
                .build();

        reviewLikeRepository.save(reviewLike);

        // 5. 좋아요 수 증가
        review.addLike();
    }

    @Transactional
    @Override
    public void unlikeReview(Long reviewId, Long userId) {

        ReviewLike like = reviewLikeRepository.findByReviewIdAndUserId(reviewId, userId)
                .orElseThrow(() -> new RoadQuestException("좋아요 기록을 찾을 수 없습니다."));

        reviewLikeRepository.delete(like);

        like.getReview().removeLike();
    }

    @Override
    public boolean isLiked(Long reviewId, Long userId) {
        return reviewLikeRepository.existsByReviewIdAndUserId(reviewId, userId);
    }
}