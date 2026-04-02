package com.yonsai.rest_food_project.domain.user.entity;

import java.util.ArrayList;
import java.util.List;

import com.yonsai.rest_food_project.domain.review.entity.ReviewLike;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name="users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String provider; // 어디에서 온 로그인 정보인지? (구글인지, 카카오인지)

    @Column(unique = true, nullable = false)
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

    // 현재 대표 칭호
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "current_title_id")
    private Title currentTitle;

    @Enumerated(EnumType.STRING)
    private UserRole role; // 권한 (USER, ADMIN)

    // 사용자가 누른 추천 목록 조회
    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReviewLike> reviewLikes = new ArrayList<>();
    
    // 사용자가 획득한 칭호 목록
    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserTitle> userTitles = new ArrayList<>();


}