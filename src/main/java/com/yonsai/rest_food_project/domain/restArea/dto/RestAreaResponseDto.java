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
    @JsonProperty("휴게소코드")
    private String stdRestCd;
    
    private String kakaoName;
    private String details;
    private Double gasolinePrice = 0.0;
    private Double dieselPrice = 0.0;
    private Double lpgPrice = 0.0;

    public Double getPriceByFuelType(String fuelType) {
        if (fuelType == null)
            return 0.0;

        return switch (fuelType) {
            case "gasoline" -> (gasolinePrice != null) ? gasolinePrice : 0.0;
            case "diesel" -> (dieselPrice != null) ? dieselPrice : 0.0;
            case "lpg" -> (lpgPrice != null) ? lpgPrice : 0.0;
            default -> 0.0;
        };
    }
}