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
        
        // 메소드 정보 및 파라미터 추출
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        String methodName = methodSignature.getMethod().getName();
        Object[] args = joinPoint.getArgs();

        log.info(">>>> [Method Start] : {} | Parameters : {}", methodName, Arrays.toString(args));
        
        long start = System.currentTimeMillis();

        try {
            Object result = joinPoint.proceed();

            log.info("<<<< [Method End] : {} | Result : {}", methodName, result);
            return result;

        } catch (Throwable e) {
            // 에러 발생 시 로그 (GlobalExceptionHandler로 넘어가기 전 기록)
            log.error("!!!! [Method Error] : {} | Message : {}", methodName, e.getMessage());
            throw e;
        } finally {
            long finish = System.currentTimeMillis();
            long timeMs = finish - start;
            log.info("==== [Performance] : {} | Time : {}ms ====", methodName, timeMs);
        }
    }
}