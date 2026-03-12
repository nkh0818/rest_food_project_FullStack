package com.yonsai.rest_food_project.global.config;

import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class LogAop {

    // 만약 에러가 났을 경우 !!!로 시작하는 로그를 읽어보세요

    // 모든 도메인 하위의 service 패키지 내 모든 클래스, 메소드를 대상으로 함
    @Around("execution(* com.yonsai.rest_food_project.domain..*service..*(..))")
    public Object logging(ProceedingJoinPoint joinPoint) throws Throwable {
        
        // 1. 메소드 정보 및 파라미터 추출
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        String methodName = methodSignature.getMethod().getName();
        Object[] args = joinPoint.getArgs();

        log.info(">>>> [Method Start] : {} | Parameters : {}", methodName, Arrays.toString(args));
        
        
        // 2. 실행 시간 측정 시작
        long start = System.currentTimeMillis();

        try {
            // 3. 실제 비즈니스 로직 실행
            Object result = joinPoint.proceed();

            // 4. 성공 시 결과 로그
            log.info("<<<< [Method End] : {} | Result : {}", methodName, result);
            return result;

        } catch (Throwable e) {
            // 5. 에러 발생 시 로그 (GlobalExceptionHandler로 넘어가기 전 기록)
            log.error("!!!! [Method Error] : {} | Message : {}", methodName, e.getMessage());
            throw e;
        } finally {
            // 6. 최종 소요 시간 계산 및 출력
            long finish = System.currentTimeMillis();
            long timeMs = finish - start;
            log.info("==== [Performance] : {} | Time : {}ms ====", methodName, timeMs);
        }
    }
}