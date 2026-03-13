package com.yonsai.rest_food_project.domain.restArea.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class RestAreaWrapperDto {
    private List<RestAreaResponseDto> records;
}