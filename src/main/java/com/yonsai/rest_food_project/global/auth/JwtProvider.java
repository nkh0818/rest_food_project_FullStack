package com.yonsai.rest_food_project.global.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.yonsai.rest_food_project.domain.user.repository.UserRepository;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtProvider {

    @Value("${jwt.secret}")
    private String secretKeyBasic;
    private SecretKey key;

    private final long TOKEN_VALID_TIME = 24 * 60 * 60 * 1000L;
    private final UserRepository userRepository;

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(secretKeyBasic.getBytes(StandardCharsets.UTF_8));
        log.info("JWT Provider 초기화 완료 - 암호화 키 생성됨");
    }

    public String createToken(String email, String nickname) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + TOKEN_VALID_TIME);

        return Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setSubject(email)
                .claim("nickname", nickname)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String getEmailFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key) // 보통 클래스 상단에 선언된 SecretKey
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject(); // 토큰 생성 시 이메일을 Subject에 넣었다고 가정
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            log.error("유효하지 않은 JWT 토큰입니다: {}", e.getMessage());
            return false;
        }
    }

    public Authentication getAuthentication(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        String email = claims.getSubject();

        // DB에서 조회하고 principalDetails에 저장한다
        com.yonsai.rest_food_project.domain.user.entity.User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("해당 유저를 찾을 수 없습니다: " + email));

        PrincipalDetails principalDetails = new PrincipalDetails(user);

        return new UsernamePasswordAuthenticationToken(
                principalDetails,
                token,
                principalDetails.getAuthorities());
    }
}