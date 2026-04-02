package com.yonsai.rest_food_project.domain.user.service;

import java.util.stream.Stream;

import org.springframework.stereotype.Service;

import com.yonsai.rest_food_project.domain.user.dto.UserResponseDTO;
import com.yonsai.rest_food_project.domain.user.entity.User;
import com.yonsai.rest_food_project.domain.user.entity.UserRole;
import com.yonsai.rest_food_project.domain.user.repository.UserRepository;
import com.yonsai.rest_food_project.global.common.NicknameGenerator;
import com.yonsai.rest_food_project.global.exception.RoadQuestException;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

/**
 * 실제로 repo에 접근해서 데이터를 다루는 클래스로만 사용합니다
 * 로직에서 repo에 접근해 조회 결과가 있어야지만 다음 로직을 실행할 수 있는 경우에는
 * 무조건 .orElseThrow를 통해 exception을 잡아 주세요!
 */

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepo;
    private final NicknameGenerator nicknameGenerator;

    @Override
    @Transactional
    public User saveOrUpdate(String email, String nickname, String provider, String providerId) {
        return userRepo.findByProviderAndProviderId(provider, providerId)
                .map(user -> {
                    // 정보를 최신화 (이메일 등 변경 대응)
                    user.setEmail(email);
                    return user;
                })
                .orElseGet(() -> {
                    // 신규 가입 (Social)
                    User newUser = User.builder()
                            .email(email)
                            .nickname(generateUniqueNickname())
                            .provider(provider)
                            .providerId(providerId)
                            .role(UserRole.USER)
                            .password("1234") // 소셜 로그인은 패스워드 검증을 거치지 않으므로 더미값 사용
                            .build();
                    return userRepo.save(newUser);
                });
    }

    @Override
    @Transactional
    public User createLocalUser(String email, String encodedPassword, String nickname) {
        User user = User.builder()
                .email(email)
                .password(encodedPassword)
                .nickname(nickname)
                .provider("local")
                .role(UserRole.USER)
                .build();
        return userRepo.save(user);
    }

    @Override
    @Transactional
    public User nicknameUpdate(Long userId, String newNickname) {
        // 1. 중복 확인
        if (userRepo.existsByNickname(newNickname)) {
            throw new RoadQuestException("이미 사용중인 닉네임입니다!");
        }

        // 2. 유저 조회
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RoadQuestException("사용자를 찾을 수 없습니다. ID: " + userId));

        // 3. 닉네임 변경 (더티 체킹 발생)
        user.setNickname(newNickname);

        // 4. 수정된 객체 반환 (AuthServiceImpl에서 사용하기 위함)
        return user;
    }

    // --- 이하 단순 조회 로직 (ReadOnly 최적화 가능) ---

    @Override
    public User findByEmail(String email) {
        return userRepo.findByEmail(email)
                .orElseThrow(() -> new RoadQuestException("사용자를 찾을 수 없습니다. email: " + email));
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepo.existsByEmail(email);
    }

    @Override
    public UserResponseDTO getMyInfo(Long userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RoadQuestException("사용자를 찾을 수 없습니다. ID: " + userId));
        return UserResponseDTO.from(user);
    }

    private String generateUniqueNickname() {
        return Stream.generate(nicknameGenerator::generate)
                .filter(name -> !userRepo.existsByNickname(name))
                .findFirst()
                .orElseThrow(() -> new RoadQuestException("닉네임 생성에 실패했습니다."));
    }
}
