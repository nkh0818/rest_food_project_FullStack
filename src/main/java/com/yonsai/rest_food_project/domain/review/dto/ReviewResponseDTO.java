package com.yonsai.rest_food_project.domain.review.dto;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import com.yonsai.rest_food_project.domain.review.entity.Review;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReviewResponseDTO {

    // 사용자 정보
    private Long reviewId;
    private Long userId; // 유저 ID (DB의 1, 2, 3)
    private String nickname; // 유저의 닉네임

    // 휴게소, 음식 정보
    private String restAreaId; // DB내부 관리용
    private String restAreaName;
    private String restAreaCode; // 휴게소코드
    private Long foodId;
    private String foodName;
    private Double latitude;
    private Double longitude;

    // 리뷰 데이터
    private String content;
    private Integer rating;
    private String tag;
    private String imageUrl;
    private List<String> tags;
    private Boolean gpsVerified;

    // 통계 데이터
    private Integer likeCount; // 좋아요 수
    private Integer reportCount;

    // 시간 정보
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String timeAgo;

    // 좋아요 정보
    private boolean liked;

    public static String calculateTime(LocalDateTime createdAt) {
        if (createdAt == null)
            return "";
        Duration duration = Duration.between(createdAt, LocalDateTime.now());
        long seconds = duration.getSeconds();

        if (seconds < 60)
            return "방금 전";
        if (seconds < 3600)
            return (seconds / 60) + "분 전";
        if (seconds < 86400)
            return (seconds / 3600) + "시간 전";
        return (seconds / 86400) + "일 전";
    }

    public static ReviewResponseDTO from(Review review) {
        return ReviewResponseDTO.builder()
                .reviewId(review.getId())
                .userId(review.getUser() != null ? review.getUser().getId() : null)
                .nickname(review.getUser() != null ? review.getUser().getNickname() : "알 수 없음")
                .restAreaId(review.getRestArea() != null ? String.valueOf(review.getRestArea().getId()) : null)
                .restAreaName(review.getRestArea() != null ? review.getRestArea().getName() : null)
                .restAreaCode(review.getRestArea() != null ? review.getRestArea().getStdRestCd() : null)
                .foodId(review.getFood() != null ? review.getFood().getId() : null)
                .foodName(review.getFood() != null ? review.getFood().getFoodName() : null)
                .content(review.getContent())
                .rating(review.getRating())
                .tags(review.getTag() == null ? List.of() : List.of(review.getTag().split(",")))
                .imageUrl(review.getImageUrl())
                .likeCount(review.getLikeCount())
                .reportCount(review.getReportCount())
                .createdAt(review.getCreatedAt())
                .updatedAt(review.getUpdatedAt())
                .timeAgo(calculateTime(review.getCreatedAt()))
                .liked(false)
                .latitude(review.getRestArea() != null ? review.getRestArea().getLatitude() : null)
                .longitude(review.getRestArea() != null ? review.getRestArea().getLongitude() : null)
                .gpsVerified(review.getGpsVerified())
                .build();
    }

    public static ReviewResponseDTO from(Review review, boolean liked) {
        return ReviewResponseDTO.builder()
                .reviewId(review.getId())
                .userId(review.getUser() != null ? review.getUser().getId() : null)
                .nickname(review.getUser() != null ? review.getUser().getNickname() : "알 수 없음")
                .restAreaId(review.getRestArea() != null ? String.valueOf(review.getRestArea().getId()) : null)
                .restAreaName(review.getRestArea() != null ? review.getRestArea().getName() : null)
                .restAreaCode(review.getRestArea() != null ? review.getRestArea().getStdRestCd() : null)
                .foodId(review.getFood() != null ? review.getFood().getId() : null)
                .foodName(review.getFood() != null ? review.getFood().getFoodName() : null)
                .content(review.getContent())
                .rating(review.getRating())
                .tags(review.getTag() == null ? List.of() : List.of(review.getTag().split(",")))
                .imageUrl(review.getImageUrl())
                .likeCount(review.getLikeCount())
                .reportCount(review.getReportCount())
                .createdAt(review.getCreatedAt())
                .updatedAt(review.getUpdatedAt())
                .timeAgo(calculateTime(review.getCreatedAt()))
                .liked(liked)
                .latitude(review.getRestArea() != null ? review.getRestArea().getLatitude() : null)
                .longitude(review.getRestArea() != null ? review.getRestArea().getLongitude() : null)
                .gpsVerified(review.getGpsVerified())
                .build();
    }
}