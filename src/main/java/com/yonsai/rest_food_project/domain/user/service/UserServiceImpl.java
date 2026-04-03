package com.yonsai.rest_food_project.domain.user.service;

import java.util.stream.Stream;

import org.springframework.stereotype.Service;

import com.yonsai.rest_food_project.domain.user.dto.UserResponseDTO;
import com.yonsai.rest_food_project.domain.user.entity.User;
import com.yonsai.rest_food_project.domain.user.entity.UserRole;
import com.yonsai.rest_food_project.domain.user.repository.UserRepository;
import com.yonsai.rest_food_project.global.common.NicknameGenerator;
import com.yonsai.rest_food_project.global.common.RedisService;
import com.yonsai.rest_food_project.global.exception.RoadQuestException;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 실제로 repo에 접근해서 데이터를 다루는 클래스로만 사용합니다
 * 로직에서 repo에 접근해 조회 결과가 있어야지만 다음 로직을 실행할 수 있는 경우에는
 * 무조건 .orElseThrow를 통해 exception을 잡아 주세요!
 */

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepo;
    private final NicknameGenerator nicknameGenerator;
    private final RedisService redisService;

    @Override
    @Transactional
    public User saveOrUpdate(String email, String nickname, String provider, String providerId) {
        return userRepo.findByProviderAndProviderId(provider, providerId)
                .map(user -> {
                    user.setEmail(email);
                    return user;
                })
                .orElseGet(() -> {
                    // 소셜 로그인 시 닉네임이 없으면 유니크 닉네임 생성
                    String finalNickname = (nickname == null || nickname.trim().isEmpty())
                            ? generateUniqueNickname()
                            : nickname;

                    User newUser = User.builder()
                            .email(email)
                            .nickname(finalNickname)
                            .provider(provider)
                            .providerId(providerId)
                            .role(UserRole.USER)
                            .password("SOCIAL_DUMMY")
                            .build();
                    return userRepo.save(newUser);
                });
    }

    @Override
    @Transactional
    public User createLocalUser(String email, String encodedPassword, String nickname) {
        String finalNickname = (nickname == null || nickname.trim().isEmpty())
                ? generateUniqueNickname()
                : nickname;

        log.info(">>>> [로컬 가입] 최종 닉네임: {}", finalNickname);

        User user = User.builder()
                .email(email)
                .password(encodedPassword)
                .nickname(finalNickname)
                .provider("local")
                .providerId(email)
                .role(UserRole.USER)
                .build();
        return userRepo.save(user);
    }

    @Override
    @Transactional
    public User nicknameUpdate(Long userId, String newNickname) {

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RoadQuestException("사용자를 찾을 수 없습니다. ID: " + userId));

        String oldNickname = user.getNickname();

        // 1. 중복 확인
        if (userRepo.existsByNickname(newNickname) || redisService.isNicknameExists(newNickname)) {
            throw new RoadQuestException("이미 사용중인 닉네임입니다!");
        }

        user.setNickname(newNickname);

        redisService.deleteNickname(oldNickname);
        redisService.saveNickname(newNickname);

        log.info("닉네임 변경 완료: {} -> {}", oldNickname, newNickname);

        // 4. 수정된 객체 반환 (AuthServiceImpl에서 사용하기 위함)
        return user;
    }

    // --- 이하 단순 조회 로직 ---

    @Override
    @Transactional(readOnly = true)
    public User findByEmail(String email) {
        return userRepo.findByEmail(email)
                .orElseThrow(() -> new RoadQuestException("사용자를 찾을 수 없습니다. email: " + email));
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return userRepo.existsByEmail(email);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDTO getMyInfo(Long userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RoadQuestException("사용자를 찾을 수 없습니다. ID: " + userId));

        int reviewCount = user.getReviews().size();
        log.info(">>>> [내 정보 조회] 유저: {}, 리뷰 수: {}", user.getNickname(), user.getReviews().size());

        return UserResponseDTO.from(user);
    }

    private String generateUniqueNickname() {
        log.info(">>>> 유니크 닉네임 생성 프로세스 시작");
        return Stream.generate(nicknameGenerator::generate)
                .filter(name -> !userRepo.existsByNickname(name))
                .findFirst()
                .orElseThrow(() -> new RoadQuestException("닉네임 생성에 실패했습니다."));
    }
}
