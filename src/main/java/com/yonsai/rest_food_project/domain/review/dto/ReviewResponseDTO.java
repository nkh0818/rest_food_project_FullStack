package com.yonsai.rest_food_project.domain.review.dto;

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
    private Long userId;
    private String nickname;

    // 휴게소, 음식 정보
    private Long restAreaId;
    private String restAreaName;
    private Long foodId;
    private String foodName;

    // 리뷰 데이터
    private String content;
    private Integer rating;
    private String tag;
    private String imageUrl;
    private List<String> tags;

    // 통계 데이터
    private Integer likeCount;
    private Integer reportCount;
    
    // 시간 정보
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // 좋아요 정보
    private boolean liked;
    
    // 칭호 정보
    private String currentTitle;
    
    // 리뷰를 DTO로 변환 -> 로그인 X
    public static ReviewResponseDTO from(Review review) {
        return ReviewResponseDTO.builder()
                .reviewId(review.getId())
                .userId(review.getUser() != null ? review.getUser().getId() : null)
                .nickname(review.getUser() != null ? review.getUser().getNickname() : "알 수 없음")
                .restAreaId(review.getRestArea() != null ? review.getRestArea().getId() : null)
                .restAreaName(review.getRestArea() != null ? review.getRestArea().getName() : null)
                .foodId(review.getFood() != null ? review.getFood().getId() : null)
                .foodName(review.getFood() != null ? review.getFood().getFoodName() : null)
                .content(review.getContent())
                .rating(review.getRating())
                .tag(review.getTag())
                .tags(review.getTag() == null ? List.of() : List.of(review.getTag().split(",")))
                .imageUrl(review.getImageUrl())
                .likeCount(review.getLikeCount())
                .reportCount(review.getReportCount())
                .createdAt(review.getCreatedAt())
                .updatedAt(review.getUpdatedAt())
                .liked(false)
                .build();
    }
    
    // 사용자 상태(좋아요 여부)까지 포함해서 DTO 반환 -> 로그인 사용자 상태 포함
    public static ReviewResponseDTO from(Review review, boolean liked) {
        return ReviewResponseDTO.builder()
                .reviewId(review.getId())
                .userId(review.getUser() != null ? review.getUser().getId() : null)
                .nickname(review.getUser() != null ? review.getUser().getNickname() : "알 수 없음")
                .restAreaId(review.getRestArea() != null ? review.getRestArea().getId() : null)
                .restAreaName(review.getRestArea() != null ? review.getRestArea().getName() : null)
                .foodId(review.getFood() != null ? review.getFood().getId() : null)
                .foodName(review.getFood() != null ? review.getFood().getFoodName() : null)
                .content(review.getContent())
                .rating(review.getRating())
                .tag(review.getTag())
                .tags(review.getTag() == null ? List.of() : List.of(review.getTag().split(",")))
                .imageUrl(review.getImageUrl())
                .likeCount(review.getLikeCount())
                .reportCount(review.getReportCount())
                .createdAt(review.getCreatedAt())
                .updatedAt(review.getUpdatedAt())
                .liked(liked)
                .currentTitle(
                	    review.getUser() != null && review.getUser().getCurrentTitle() != null
                	        ? review.getUser().getCurrentTitle().getTitleName()
                	        : null
                	)
                .build();
    }
}