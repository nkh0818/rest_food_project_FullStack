package com.yonsai.rest_food_project.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.yonsai.rest_food_project.domain.ai.service.ReviewSummarizer;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import dev.langchain4j.service.AiServices;

@Configuration
public class AiConfig {

    @Value("${spring.ai.google.genai.api-key}")
    private String GOOGLE_API_KEY;

    // 리뷰, 키워드를 뽑을 수 있는 머리인 재미니를 객체로 등록하기 
    @Bean
    public ChatLanguageModel geminiChatModel(){
        
        return GoogleAiGeminiChatModel
                        .builder()
                        .apiKey(GOOGLE_API_KEY)
                        .modelName("gemini-2.5-flash-lite")
                        .temperature(0.7)
                        .build();
    }
    
    @Bean
    public ReviewSummarizer reviewSummarizer(ChatLanguageModel chatLanguageModel) {
        return AiServices.builder(ReviewSummarizer.class)
                .chatLanguageModel(chatLanguageModel)
                .build();
    }
}
