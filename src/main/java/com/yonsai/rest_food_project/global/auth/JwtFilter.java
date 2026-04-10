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


    // [메인 로직] 모든 HTTP 요청마다 실행되며, Header에서 JWT를 추출해 유효성을 검사하고 인증 정보를 설정
    @Override
protected void doFilterInternal(HttpServletRequest request,
                                HttpServletResponse response,
                                FilterChain filterChain) throws ServletException, IOException {
    
    String token = resolveToken(request);

    try {
        if (StringUtils.hasText(token)) {
            // 토큰이 유효하면 사용자 인증 정보를 SecurityContext에 저장하여 서버가 유저를 인식하게 함
            if (jwtProvider.validateToken(token)) {
                Authentication auth = jwtProvider.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }
    } catch (ExpiredJwtException e) {
        setErrorResponse(response, "토큰이 만료되었습니다.");
        return;
    } catch (Exception e) {
        log.error("JWT 검증 에러: {}", e.getMessage());
    }

    filterChain.doFilter(request, response);
}

    // [예외 처리] JWT 검증 중 에러(만료 등) 발생 시, 클라이언트에게 401(Unauthorized) 상태와 에러 메시지를 JSON 형태로 반환
    private void setErrorResponse(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"error\": \"" + message + "\"}");
    }

    // [토큰 추출] HTTP 요청 Header의 'Authorization' 키에서 'Bearer ' 접두사를 제외한 실제 JWT 문자열만 뽑아냅니다.
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // "Bearer " 이후의 토큰 값만 추출
        }
        return null;
    }
}