package com.yonsai.rest_food_project.global.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

// [레디스 설정] In-Memory 데이터베이스인 Redis와의 연결 방식 및 데이터 직렬화 규칙을 설정

@Configuration
public class RedisConfig {

    @Value("${spring.data.redis.host:localhost}") // 환경 변수에서 호스트 읽기
    private String host;

    @Value("${spring.data.redis.port:6379}") // 환경 변수에서 포트 읽기
    private int port;

    
    // [연결 팩토리] 비동기 방식인 Lettuce 라이브러리를 사용하여 Redis 서버와의 물리적인 연결을 생성
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory(host, port); // 환경 변수로 받은 값을 주입
    }

    // [템플릿 설정] Redis에 데이터를 저장하거나 조회할 때, Key와 Value를 사람이 읽을 수 있는 String 형태로 직렬화하여 관리하도록 설정
    @Bean
    public RedisTemplate<String, String> redisTemplate() {
        RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory());

        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new StringRedisSerializer());

        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new StringRedisSerializer());

        return template;
    }
}