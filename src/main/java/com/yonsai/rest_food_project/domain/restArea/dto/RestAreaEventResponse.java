package com.yonsai.rest_food_project.domain.restArea.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import java.util.List;

@Getter
@Setter
@ToString
public class RestAreaEventResponse {
    private String pageSize;
    private String count;
    private String pageNo;
    private String numOfRows;
    private List<RestAreaEventDto> list; // Service의 .getList()와 연결
}