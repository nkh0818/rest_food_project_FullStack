package com.yonsai.rest_food_project.global.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;


/* 해당 클래스는 BE에서 발생한 오류를 FE로 정리해서 보내주기 위한 클래스입니다 */

@Getter
@Builder
@AllArgsConstructor
public class ErrorResponse {

    private final String code;
    private final String message;

}