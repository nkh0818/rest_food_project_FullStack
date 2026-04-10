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

import jakarta.persistence.EntityNotFoundException;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

//[유저 서비스] 회원 가입(소셜/로컬), 정보 수정, 유니크 닉네임 생성 등 사용자 프로필 관리를 총괄

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepo;
    private final NicknameGenerator nicknameGenerator;
    private final RedisService redisService;

    //[소셜 로그인 처리] 기존 회원은 정보를 업데이트하고, 신규 회원은 유니크 닉네임을 생성하여 자동 가입(Upsert)
    @Override
    public User saveOrUpdate(String email, String nickname, String provider, String providerId) {
        return userRepo.findByProviderAndProviderId(provider, providerId)
                .map(user -> {
                    user.setEmail(email);
                    return user;
                })
                .orElseGet(() -> {
                    // 신규 유저일 경우 닉네임이 없으면 랜덤 생성
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

    //[로컬 회원가입] 이메일과 비밀번호를 사용하여 유저를 등록하며, 닉네임 미입력 시 자동 생성
    @Override
    public User createLocalUser(String email, String encodedPassword, String nickname) {
        String finalNickname = (nickname == null || nickname.trim().isEmpty())
                ? generateUniqueNickname()
                : nickname;
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
    
    //[프로필 수정] 닉네임 중복 검사(DB+Redis) 및 프로필 이미지 경로를 업데이트
    @Override
    public User nicknameUpdate(Long userId, String newNickname, String profileImage) {
        User user = findUserById(userId);
        applyNicknameChange(user, newNickname);

        if (profileImage != null && !profileImage.isEmpty()) {
            user.setProfileImage(profileImage);
        }

        log.info("DB 저장 직전 유저 객체 상태: {}", user.getProfileImage());
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
                .orElseThrow(() -> new EntityNotFoundException("유저를 찾을 수 없습니다."));
        return UserResponseDTO.from(user);
    }

    // --- 이하 private 헬퍼 메서드 ---

    private User findUserById(Long userId) {
        return userRepo.findById(userId)
                .orElseThrow(() -> new RoadQuestException("사용자를 찾을 수 없습니다. ID: " + userId));
    }

    // [중복 체크] DB와 Redis 캐시를 모두 확인하여 현재 사용 중인 닉네임인지 검사
    private boolean isNicknameTaken(String nickname) {
        return userRepo.existsByNickname(nickname) || redisService.isNicknameExists(nickname);
    }

    // [닉네임 동기화] 변경된 닉네임을 Redis에 반영하고 기존 닉네임 캐시는 삭제하여 정합성을 유지
    private void applyNicknameChange(User user, String newNickname) {
        String oldNickname = user.getNickname();
        if (oldNickname.equals(newNickname)) return;
        if (isNicknameTaken(newNickname)) {
            throw new RoadQuestException("이미 사용중인 닉네임입니다!");
        }
        user.setNickname(newNickname);
        redisService.deleteNickname(oldNickname);
        redisService.saveNickname(newNickname);
    }

    // [자동 생성] NicknameGenerator를 사용하여 DB에 중복되지 않는 유니크한 닉네임을 무한 스트림으로 탐색하여 생성
    private String generateUniqueNickname() {
        log.info(">>>> 유니크 닉네임 생성 프로세스 시작");
        return Stream.generate(nicknameGenerator::generate)
                .filter(name -> !userRepo.existsByNickname(name)) // 마음에 드는 중복 아닌 닉네임이 나올 때까지 생성
                .findFirst() // 찾으면 멈춤
                .orElseThrow(() -> new RoadQuestException("닉네임 생성에 실패했습니다."));
    }
}
