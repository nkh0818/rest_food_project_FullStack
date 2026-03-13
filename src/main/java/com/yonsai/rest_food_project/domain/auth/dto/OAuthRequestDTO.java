package com.yonsai.rest_food_project.domain.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OAuthRequestDTO {

    private Long id; //  User providerId에 해당

    @JsonProperty("kakao_account")
    private KakaoAccount kakaoAccount;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class KakaoAccount{
        private String email;
        private Profile profile;

        @Getter
        @NoArgsConstructor
        @AllArgsConstructor
        public static class Profile{
            private String nickname;
            @JsonProperty("thumbnail_image_url")
            private String thumbnailImageUrl;
        }
    }

    // 서비스에서 Id와 ProviderId를 동일하게 쓰기 위한 헬퍼 메서드
    public String getProviderId(){
        return String.valueOf(this.id);
    }

    /**
     * 
     * {
            "id": 123456789,                // <--- 1. OAuthRequestDTO
            "kakao_account": {              // <--- 2. KakaoAccount (클래스 안의 클래스)
            "email": "user@kakao.com",
            "profile": {                  // <--- 3. Profile (클래스 안의 클래스 안의 클래스)
            "nickname": "길동이",
            "thumbnail_image_url": "..."
                }
            }
        }
     * 
     */

}
