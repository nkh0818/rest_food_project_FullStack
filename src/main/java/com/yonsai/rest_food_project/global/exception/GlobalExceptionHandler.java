package com.yonsai.rest_food_project.global.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.extern.slf4j.Slf4j;

/** 로직 중 문제가 발생했을 경우 이 핸들러가 잡아옵니다 */

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler({RoadQuestException.class})
    public ResponseEntity<ErrorResponse> handleRoadQuestException(RoadQuestException e) {
        log.error("RoadQuest 비즈니스 예외 발생: {}", e.getMessage());
        
        // 리액트로 해당 내용을 전송
        ErrorResponse response = ErrorResponse.builder()
                .code("BUSINESS_ERROR")
                .message(e.getMessage())
                .build();
        return ResponseEntity.badRequest().body(response);
    }

    // 최후의 수단: 예상치 못한 서버 에러
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        log.error("서버 내부 에러 발생!", e); // 스택트레이스 포함 로그
        
        ErrorResponse response = ErrorResponse.builder()
                .code("SERVER_ERROR")
                .message("서버에 일시적인 오류가 발생했습니다.")
                .build();
                
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
