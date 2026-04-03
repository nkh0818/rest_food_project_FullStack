package com.yonsai.rest_food_project.domain.user.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.yonsai.rest_food_project.domain.review.entity.Review;
import com.yonsai.rest_food_project.domain.review.entity.ReviewLike;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = true)
    private String provider; // 어디에서 온 로그인 정보인지?

    @Column(unique = true, nullable = true)
    private String providerId; // 해당 서비스에서 보내온 번호

    @Column(unique = true)
    private String email; // 위에가 해당 안 되면 이메일

    @Column(unique = true, nullable = false)
    private String nickname; // 랜덤 생성 닉네임

    @Column(nullable = false)
    private String password;

    @Builder.Default
    private int xp = 0; // 경험치 (기본값 0)

    @Builder.Default
    private int level = 1; // 레벨 (기본값 1)

    // 칭호, 랭킹용 점수
    @Builder.Default
    @Column(nullable = false)
    private int activityScore = 0; // 칭호, 랭킹용

    @Column(nullable = false)
    private int rewardPoint = 0; // 보상포인트

    @Builder.Default
    @Column(nullable = false)
    private boolean reviewExpert = false;

    @Builder.Default
    @Column(nullable = false)
    private boolean foodExpert = false;

    @Builder.Default
    @Column(nullable = false)
    private boolean photoExpert = false;

    @Builder.Default
    @Column(nullable = false)
    private boolean restAreaExpert = false;

    // 현재 대표 칭호
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "current_title_id")
    private Title currentTitle;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role = UserRole.USER;

    // security 권한 체크용
    public String getRoleKey() {
        return this.role.getKey();
    }

    // 관리자 여부 확인
    public boolean isAdmin() {
        return this.role == UserRole.ADMIN;
    }

    // 사용자가 누른 추천 목록 조회
    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReviewLike> reviewLikes = new ArrayList<>();
    // 사용자가 획득한 칭호 목록
    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserTitle> userTitles = new ArrayList<>();

    // 사용자가 작성한 리뷰 목록
    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews = new ArrayList<>();

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    public void addActivityScore(int score) {
        this.activityScore += score;
        if (this.activityScore < 0) {
            this.activityScore = 0;
        }
    }

    public void addRewardPoint(int point) {
        this.rewardPoint += point;
    }

    public void useRewardPoint(int point) {
        if (this.rewardPoint < point) {
            throw new IllegalArgumentException("포인트가 부족합니다.");
        }
        this.rewardPoint -= point;
    }
}