package com.yonsai.rest_food_project.global.auth;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;

    @Override
protected void doFilterInternal(HttpServletRequest request,
                                HttpServletResponse response,
                                FilterChain filterChain) throws ServletException, IOException {
    
    String token = resolveToken(request);

    try {
        // 토큰이 있을 때만 검증 진행
        if (StringUtils.hasText(token)) {
            if (jwtProvider.validateToken(token)) {
                Authentication auth = jwtProvider.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
            // 토큰이 있는데 유효하지 않은 경우, 여기서 throw를 던지지 말고 
            // 그냥 다음 필터로 넘기면 시큐리티가 permitAll 여부에 따라 처리해줍니다.
        }
    } catch (ExpiredJwtException e) {
        setErrorResponse(response, "토큰이 만료되었습니다.");
        return; // 응답을 보냈으니 종료
    } catch (Exception e) {
        // 기타 토큰 에러는 로그만 찍고 넘어갑니다. (가입 같은 permitAll 경로를 위해)
        log.error("JWT 검증 에러: {}", e.getMessage());
    }

    // ★★★ 이 코드가 반드시 실행되어야 합니다!
    filterChain.doFilter(request, response);
}

    // JSON 응답을 직접 만들어서 프론트로 보냄
    private void setErrorResponse(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"error\": \"" + message + "\"}");
    }

    // 헤더에서 토큰을 꺼내오는 보조 메서드
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // "Bearer " 이후의 토큰 값만 추출
        }
        return null;
    }
}