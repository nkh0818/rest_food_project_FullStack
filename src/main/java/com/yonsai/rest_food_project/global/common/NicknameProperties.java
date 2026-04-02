package com.yonsai.rest_food_project.global.common;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Component
@ConfigurationProperties(prefix = "nickname") // yml 설정파일을 객체로 바인딩
@Setter @Getter
public class NicknameProperties {
    private List<String> adjectives;
    private List<String> nouns;
}
