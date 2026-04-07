package com.yonsai.rest_food_project.domain.user.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository; // JPA 인터페이스

import com.yonsai.rest_food_project.domain.user.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

   // 특정 플랫폼의 고유번호 검색
   Optional<User> findByProviderAndProviderId(String provider, String providerId);

   // XP(경험치) 순으로 정렬해서 가져오기
   List<User> findAllByOrderByXpDesc(Pageable pageable);

   // email로 유저 찾기 (로그인)
   Optional<User> findByEmail(String email);

   // ID로 유저 찾기
   Optional<User> findById(Long userId);

   // 닉네임 중복 체크 용도
   boolean existsByNickname(String nickname); // 존재의 여부만 true, false로 반환

   /**
    * -- existsByNickname(String nickname) 호출 시
    * SELECT count(*) > 0
    * FROM users
    * WHERE nickname = '요청한닉네임'
    * LIMIT 1;
    */

   // 이메일 중복 체크 (방금 추가할 것!)
   boolean existsByEmail(String email);

}