package com.yonsai.rest_food_project.domain.restArea.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
// JSON에 있는 필드가 DTO에 없어도 에러를 내지 않고 무시함
@JsonIgnoreProperties(ignoreUnknown = true)
public class RestAreaResponseDto {
    @JsonProperty("휴게소명")
    private String dbName;

    @JsonProperty("위도")
    private Double y;

    @JsonProperty("경도")
    private Double x;

    // JSON의 "휴게소종류" 필드를 DTO의 type에 매핑
    @JsonProperty("휴게소종류")
    private String type;

    // 카카오 API 연동 등 내부 로직용 필드 (JSON에 없으므로 자동 무시됨)
    private String kakaoName;
    private String details;
}