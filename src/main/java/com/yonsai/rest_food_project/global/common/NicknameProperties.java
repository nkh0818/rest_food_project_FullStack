package com.yonsai.rest_food_project.global.common;

import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import lombok.Getter;
import lombok.Setter;

@Component
@ConfigurationProperties(prefix = "nickname") // <--- 이게 반드시 있어야 합니다!
@Getter
@Setter
public class NicknameProperties {
    private List<String> adjectives;
    private List<String> nouns;

    @jakarta.annotation.PostConstruct
    public void init() {
        System.out.println("로그: 형용사 로드됨 -> " + adjectives);
        System.out.println("로그: 명사 로드됨 -> " + nouns);
    }
}
