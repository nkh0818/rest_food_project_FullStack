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

    private static final String DAILY_RANKING_KEY = "ranking:search:daily";
    private static final String ALL_RANKING_KEY = "ranking:search:all";
    private static final String NICKNAME_KEY = "user:nicknames";

    // --- [닉네임] ---
    public void saveNickname(String nickname) {
        redisTemplate.opsForSet().add(NICKNAME_KEY, nickname);
    }

    public boolean isNicknameExists(String nickname) {
        return Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(NICKNAME_KEY, nickname));
    }

    public void deleteNickname(String nickname) {
        redisTemplate.opsForSet().remove(NICKNAME_KEY, nickname);
    }

    // --- [핫 트랜드 검색어] ---
    public void incrementSearchCount(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) return;
        
        String trimmedKeyword = keyword.trim();
        log.info("Redis 검색어 카운트 증가: {}", trimmedKeyword);
        
        redisTemplate.opsForZSet().incrementScore(ALL_RANKING_KEY, trimmedKeyword, 1);
        redisTemplate.opsForZSet().incrementScore(DAILY_RANKING_KEY, trimmedKeyword, 1);
    }

    public List<String> getDailyRanking() {
        Set<String> ranking = redisTemplate.opsForZSet().reverseRange(DAILY_RANKING_KEY, 0, 9); // 상위 10개로 넉넉히
        return ranking != null ? new ArrayList<>(ranking) : new ArrayList<>();
    }

    public List<String> getAllRanking() {
        // 전체 누적(ranking:search:all) 조회 상위 10개
        Set<String> ranking = redisTemplate.opsForZSet().reverseRange(ALL_RANKING_KEY, 0, 9);
        return ranking != null ? new ArrayList<>(ranking) : new ArrayList<>();
    }

    public Set<String> getTopSearchKeywords(int limit) {
        return redisTemplate.opsForZSet().reverseRange(DAILY_RANKING_KEY, 0, limit - 1);
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

