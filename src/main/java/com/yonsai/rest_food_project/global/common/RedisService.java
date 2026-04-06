package com.yonsai.rest_food_project.global.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class RedisService {

    private final RedisTemplate<String, String> redisTemplate;

    // --- [닉네임 중복 방지 저장/중복확인(b)/삭제] ---
    public void saveNickname(String nickname) {
        redisTemplate.opsForSet().add("user:nicknames", nickname);
    }

    public boolean isNicknameExists(String nickname) {
        return Boolean.TRUE.equals(redisTemplate.opsForSet().isMember("user:nicknames", nickname));
    }

    public void deleteNickname(String nickname) {
        redisTemplate.opsForSet().remove("users:nicknames", nickname);
    }

    // --- 핫 트랜드 검색어 (검색어+1점) ---
    public void incrementSearchCount(String keyword) {
        // 전체누적
        redisTemplate.opsForZSet().incrementScore("ranking:search:all", keyword, 1);

        // 하루누적
        redisTemplate.opsForZSet().incrementScore("ranking:search:daily", keyword, 1);
    }

    public List<String> getDailyRanking() {
        // 데일리 조회
        Set<String> ranking = redisTemplate.opsForZSet().reverseRange("ranking:search:daily", 0, 9);
        return new ArrayList<>(ranking);
    }

    public List<String> getAllRanking() {
        // 데일리 조회
    Set<String> ranking = redisTemplate.opsForZSet().reverseRange("ranking:search:all", 0, 9);
    return new ArrayList<>(ranking);
}

    public Set<String> getTopSearchKeywords(int limit) {
        // 점수 높은 순으로 상위 N개 가져오기
        return redisTemplate.opsForZSet().reverseRange("ranking:search", 0, limit - 1);
    }

    // --- [3. 리뷰 좋아요 중복 방지] ---
    public boolean toggleLike(Long reviewId, Long userId) {
        String key = "review:likes:" + reviewId;
        // Set에 유저 ID가 없으면 추가하고 true 반환, 있으면 제거하고 false 반환 (Toggle 방식)
        if (Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(key, String.valueOf(userId)))) {
            redisTemplate.opsForSet().remove(key, String.valueOf(userId));
            return false; // 좋아요 취소
        } else {
            redisTemplate.opsForSet().add(key, String.valueOf(userId));
            return true; // 좋아요 성공
        }
    }
}

