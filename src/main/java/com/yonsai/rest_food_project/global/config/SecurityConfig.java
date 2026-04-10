package com.yonsai.rest_food_project.global.config;

import com.yonsai.rest_food_project.global.auth.JwtFilter;
import com.yonsai.rest_food_project.global.auth.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

// [보안 메인 설정] Spring Security를 통해 웹 보안, CORS, JWT 필터링 및 API 접근 권한을 총괄 설정

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtProvider jwtProvider;

    // [비밀번호 암호화] 사용자 비밀번호를 안전하게 해시화하여 DB에 저장하기 위한 BCrypt 인코더
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())

                // [세션 정책] JWT를 사용하므로 서버에 세션을 저장하지 않는 STATELESS 모드 설정
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // [보안 필터 체인] HTTP 요청에 대한 보안 규칙(인증, 인가, 세션 관리, 필터 순서)을 설정

                        // 단순 조회 및 공통 API: 누구나 접근 가능(permitAll)
                        .requestMatchers(HttpMethod.GET, "/api/reviews/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/ranking/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/search/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/trends/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/restareas/**").permitAll()
                        .requestMatchers("/api/user/check-nickname/**").permitAll()
                        .requestMatchers("/api/main/best-food/**").permitAll()
                        .requestMatchers("/init-**").permitAll()
                        .requestMatchers("/api/recommend/**").permitAll()
                        

                        // 인증/회원가입 관련
                        .requestMatchers("/api/auth/**").permitAll()

                        // 로그인이 필요한 요청
                        .requestMatchers("/api/blocks/**").authenticated()
                        .requestMatchers("/api/reviews/me").authenticated()
                        .requestMatchers("/api/favorites/**").authenticated()
                        .requestMatchers("/init-**").permitAll()

                        // 나머지 요청 POST(등록), PUT(수정), DELETE(삭제)
                        .anyRequest().authenticated())

                .addFilterBefore(new JwtFilter(jwtProvider), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // [CORS] 세부 설정 Bean
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:5173", "http://localhost:5174", "http://43.200.160.144",
                "http://43.200.160.144:80"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true); // 쿠키나 인증 헤더 허용 시 true
        config.setExposedHeaders(List.of("Authorization")); // 프론트에서 토큰을 읽을 수 있게 허용

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}