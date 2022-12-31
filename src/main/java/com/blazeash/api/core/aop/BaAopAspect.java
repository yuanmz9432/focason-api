/*
 * Copyright 2021 Blazeash Co.,Ltd. AllRights Reserved.
 */
package com.blazeash.api.core.aop;



import java.util.Arrays;
import java.util.Objects;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * AOPアスペクト
 */
@Aspect
@Component
@Slf4j
public class BaAopAspect
{

    private static final Logger logger = LoggerFactory.getLogger(BaAopAspect.class);

    @Pointcut("execution(* com.blazeash.api..controller..*(..))")
    public void pointCut() {}

    @Before("pointCut()")
    public void beforeExecution(JoinPoint joinPoint) {
        ServletRequestAttributes requestAttributes =
            (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (requestAttributes == null) {
            throw new RuntimeException();
        }

        HttpServletRequest request = requestAttributes.getRequest();
        logger.info("★★★ {} {} {} ★★★", request.getMethod(), request.getRequestURL().toString(),
            Arrays.toString(joinPoint.getArgs()));
    }

    @AfterReturning(returning = "returnObject", pointcut = "pointCut()")
    public void doAfterReturning(Object returnObject) {
        if (!Objects.isNull(returnObject)) {
            logger.info("★★★ {} ★★★", returnObject);
        }
    }

    @Around("within(com.blazeash.api..controller.*)")
    public Object around(final ProceedingJoinPoint pjp) throws Throwable {
        final StopWatch sw = new StopWatch();
        sw.start();
        Object res;
        try {
            res = pjp.proceed();
        } catch (Throwable e) {
            sw.stop();
            logger.info("★★★ {} ★★★", e.getMessage());
            throw e;
        }
        sw.stop();
        logger.info("★★★ {}s ★★★", sw.getTotalTimeSeconds());
        return res;
    }

}
