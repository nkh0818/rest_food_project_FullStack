package com.yonsai.rest_food_project.domain.user.dto;

import java.util.List;

import com.yonsai.rest_food_project.domain.user.entity.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponseDTO {

        // 0403 나다희 수정

        private Long id; // 아이디
        private String email; // 이메일
        private String nickname; // 닉네임
        private int xp; // 경험치
        private int level; // 레벨
        private int rewardPoint; // 포인트
        private int reviewCount; // 리뷰 수
        private String currentTitle;// 타이틀
        private String profileImage; // 프사이미지
        private List<Long> reviewLikes;
        private List<String> userTitles;

        public static UserResponseDTO from(User user) {
                return UserResponseDTO.builder()
                                .id(user.getId())
                                .email(user.getEmail())
                                .nickname(user.getNickname())
                                .xp(user.getXp())
                                .level(user.getLevel())
                                .rewardPoint(user.getRewardPoint())
                                .currentTitle(user.getCurrentTitle() != null ? user.getCurrentTitle().getTitleName()
                                                : "칭호 없음")
                                .reviewLikes(user.getReviewLikes().stream()
                                                .map(like -> like.getReview().getId()).toList())
                                .userTitles(user.getUserTitles().stream()
                                                .map(ut -> ut.getTitle().getTitleName()).toList())
                                .profileImage(user.getProfileImage())
                                .reviewCount(user.getReviews().size())
                                .build();
        }
}
