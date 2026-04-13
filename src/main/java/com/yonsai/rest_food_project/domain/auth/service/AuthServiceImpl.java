package com.yonsai.rest_food_project.domain.auth.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.yonsai.rest_food_project.domain.auth.dto.AuthResponseDTO;
import com.yonsai.rest_food_project.domain.auth.dto.OAuthRequestDTO;
import com.yonsai.rest_food_project.domain.auth.dto.SignUpRequestDTO;
import com.yonsai.rest_food_project.domain.user.entity.User;
import com.yonsai.rest_food_project.domain.user.service.UserService;
import com.yonsai.rest_food_project.global.auth.JwtProvider;
import com.yonsai.rest_food_project.global.auth.KakaoApiModule;
import com.yonsai.rest_food_project.global.common.RedisService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

// [인증 서비스] 회원가입, 다양한 로그인 방식(카카오/일반), 내 정보 관리 등 인증 전반의 로직을 수행


@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserService userService;

    private final KakaoApiModule kakaoApiModule;
    private final JwtProvider jwtProvider;

    private final RedisService redisService;
    private final PasswordEncoder passwordEncoder;

    //[카카오 로그인] 카카오 토큰으로 사용자 정보를 가져와 DB에 저장(또는 업데이트)하고 JWT를 발급
    @Override
    @Transactional
    public AuthResponseDTO loginKakao(String code) {

        log.info("---카카오 로그인 프로세스 시작---(인가코드)");

        String kakaoAccessToken = kakaoApiModule.getAccessToken(code);

        // 1. module 호출해서 dto를 가져옴
        OAuthRequestDTO dto = kakaoApiModule.getUserInfo(kakaoAccessToken);

        String providerId = dto.getProviderId();
        String email = dto.getKakaoAccount().getEmail();
        String nickname = dto.getKakaoAccount().getProfile().getNickname();
        String provider = "kakao";

        // 2. DB 확인 및 회원가입/업데이트 (saveOrUpdate가 upsert 처리)
        log.info("유저 저장/업데이트 진행: email={}, providerId={}", email, providerId);
        User user = userService.saveOrUpdate(email, nickname, provider, providerId);

        // 3. 내용을 토대로 토큰 생성
        String accessToken = jwtProvider.createToken(user.getEmail(), user.getNickname());

        log.info("로그인 성공: 사용자={}, 토큰 발행 완료", user.getEmail());

        // 4. 최종 응답 DTO 반환
        return convertToAuthResponse(user, accessToken);
    }

    //[일반 회원가입] 중복 체크 후 비밀번호를 암호화하여 저장하고, Redis에 닉네임을 등록한 뒤 토큰을 발행
    @Override
    @Transactional
    public AuthResponseDTO signUp(SignUpRequestDTO dto) {

        // 1. 중복 가입 체크
        if (userService.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("이미 사용 중인 이메일입니다.");
        }

        // 2. 비밀번호 암호화 및 유저 저장
        String encodedPassword = passwordEncoder.encode(dto.getPassword());

        // 3. 유저 생성을 UserService에 위임
        User user = userService.createLocalUser(
                dto.getEmail(),
                encodedPassword,
                dto.getNickname());

        redisService.saveNickname(user.getNickname());

        // 토큰 생성 및 반환
        String accessToken = jwtProvider.createToken(user.getEmail(), user.getNickname());

        log.info("회원가입 및 토큰 발행 완료: email={}", user.getEmail());

        return convertToAuthResponse(user, accessToken);
    }

    // [정보 조회] 현재 요청한 사용자의 토큰을 파싱하여 DB에서 최신 유저 정보를 가져옵니다.
    @Override
    @Transactional
    public AuthResponseDTO getMe(String accessToken) {

        // 1. 토큰에서 이메일 추출
        String jwtToken = accessToken.startsWith("Bearer ") ? accessToken.substring(7) : accessToken;
        String email = jwtProvider.getEmailFromToken(jwtToken);

        // 2. UserService 호출해서 정보를 얻어옴
        User user = userService.findByEmail(email);

        return convertToAuthResponse(user, jwtToken);
    }

    // [닉네임 수정] 유저의 닉네임을 변경하고 정보가 바뀌었으므로 새로운 정보를 담은 JWT를 재발급
    @Override
    @Transactional
    public AuthResponseDTO updateNickname(String accessToken, String newNickname, String profileImage) {

        // 1. 토큰 정제 및 이메일 추출
        String jwtToken = accessToken.startsWith("Bearer ") ? accessToken.substring(7) : accessToken;
        String email = jwtProvider.getEmailFromToken(jwtToken);

        // 2. 유저 조회
        User user = userService.findByEmail(email);

        User updatedUser = userService.nicknameUpdate(user.getId(), newNickname, profileImage);

        String newAccessToken = jwtProvider.createToken(updatedUser.getEmail(), updatedUser.getNickname());

        log.info("닉네임 변경 완료: email={}, newNickname={}", email, updatedUser.getNickname());

        return convertToAuthResponse(updatedUser, newAccessToken);
    }

    // [일반 로그인] 이메일 존재 여부와 암호화된 비밀번호 일치 여부를 확인하여 인증 토큰을 발급
    @Override
    @Transactional
    public AuthResponseDTO loginLocal(String email, String password) {

        User user = userService.findByEmail(email);

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }

        String accessToken = jwtProvider.createToken(user.getEmail(), user.getNickname());

        log.info("일반 로그인 성공: email={}", email);

        return convertToAuthResponse(user, accessToken);
    }

    // [응답 변환] 유저 엔티티와 토큰을 결합하여 프론트엔드가 필요한 통합 사용자 정보를 DTO로 변환
    private AuthResponseDTO convertToAuthResponse(User user, String accessToken) {
        return AuthResponseDTO.builder()
                .accessToken(accessToken)
                .nickname(user.getNickname())
                .email(user.getEmail())
                .profileImage(user.getProfileImage())
                .level(user.getLevel())
                .xp(user.getXp())
                .provider(user.getProvider())
                .currentTitle(user.getCurrentTitle() != null ? 
                                user.getCurrentTitle().getTitleName() : "신규 탐험가")
                .rewardPoint(user.getRewardPoint())
                .reviewCount(user.getReviews().size())
                .build();
    }

}
