package com.yonsai.rest_food_project.config;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class LogAop {

    @Around("execution(* com.yonsai.rest_food_project.service..*(..))")
    public Object logging(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();

        try {
            return joinPoint.proceed(); // 실제 메소드 실행
        } finally {
            long finish = System.currentTimeMillis();
            long timeMs = finish - start;
            log.info("METHOD: {} | TIME: {}ms", joinPoint.getSignature().toShortString(), timeMs);
        }
    }
}