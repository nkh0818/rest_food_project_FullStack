package com.yonsai.rest_food_project.domain.restArea.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.yonsai.rest_food_project.domain.restArea.entity.RestArea;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class RestAreaResponseDto {

    private String dbName; // 휴게소 이름
    private String routeName; // 노선 이름
    private Double y; // 위도
    private Double x; // 경도
    private String type; // 휴게소 종류
    private String stdRestCd; // 휴게소코드
    private String kakaoName; // 카카오맵 이름
    private String details;
    private Double gasolinePrice = 0.0; // 가솔린
    private Double dieselPrice = 0.0; // 디젤
    private Double lpgPrice = 0.0; // lpg
    private Double distance; // 거리

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

    public static RestAreaResponseDto fromEntity(RestArea entity) {
        return fromEntity(entity, null);
    }

    public static RestAreaResponseDto fromEntity(RestArea entity, Double calculatedDist) {
        if (entity == null) return null;

        return RestAreaResponseDto.builder()
                .dbName(entity.getName() != null ? entity.getName() : "이름 없는 휴게소")
                .routeName(entity.getRouteName())
                .y(entity.getLatitude()) // latitude -> y (위도)
                .x(entity.getLongitude()) // longitude -> x (경도)
                .type(entity.getDirection()) // 방향(상/하행)을 일단 type으로 매핑 (필요시 수정)
                .stdRestCd(entity.getStdRestCd())
                .gasolinePrice(entity.getGasolinePrice() != null ? entity.getGasolinePrice().doubleValue() : 0.0)
                .dieselPrice(entity.getDiselPrice() != null ? entity.getDiselPrice().doubleValue() : 0.0)
                .lpgPrice(entity.getLpgPrice() != null ? entity.getLpgPrice().doubleValue() : 0.0)
                .details(entity.getLocation())
                .distance(calculatedDist)
                .build();
    }
}