package com.yonsai.rest_food_project.domain.ranking.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SearchRankingRequestDTO {

    @NotBlank(message = "검색어가 빠져 있습니다.")
    @Size(min = 2, message = "완성형 단어로 입력해야 합니다.")
    @Pattern(regexp = "^[가-힣a-zA-Z0-9\\s]*$", message = "자/모음은 등록할 수 없습니다.")
    private String keyword;
    
}

