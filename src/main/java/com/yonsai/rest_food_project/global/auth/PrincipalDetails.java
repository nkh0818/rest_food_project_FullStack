package com.yonsai.rest_food_project.global.auth;

import com.yonsai.rest_food_project.domain.user.entity.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

// User 엔티티를 Spring Security가 인식할 수 있는 'UserDetails' 타입으로 변환해주는 어댑터 클래스

@Getter
public class PrincipalDetails implements UserDetails {

    private final User user;

    public PrincipalDetails(User user) {
        this.user = user;
    }

    public Long getUserId() { return user.getId(); }
    public String getNickname() { return user.getNickname(); }

    // [권한 설정] 유저가 가진 역할(Role, 예: ROLE_USER)을 시큐리티가 읽을 수 있는 형태로 변환
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority(user.getRoleKey()));
    }

    @Override
    public String getPassword() { return user.getPassword(); }

    @Override
    public String getUsername() { return user.getEmail(); }


    // --- 아래 메서드들은 계정의 상태 관리 로직으로, 현재는 모두 '활성(true)' 상태로 설정함 ---

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }
}