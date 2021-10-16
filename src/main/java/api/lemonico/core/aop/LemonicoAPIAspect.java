/*
 * Copyright 2021 Lemonico Co.,Ltd. AllRights Reserved.
 */
package api.lemonico.core.aop;



import api.lemonico.common.IPUtils;
import java.util.Arrays;
import java.util.Objects;
import javax.servlet.http.HttpServletRequest;
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
 * AOP
 */
@Aspect
@Component
public class LemonicoAPIAspect
{

    private static final Logger logger = LoggerFactory.getLogger(LemonicoAPIAspect.class);

    @Pointcut("execution(* api.lemonico..controller..*(..))")
    public void pointCut() {}

    @Before("pointCut()")
    public void beforeExecution(JoinPoint joinPoint) {
        ServletRequestAttributes requestAttributes =
            (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (requestAttributes != null) {
            HttpServletRequest request = requestAttributes.getRequest();
            logger.info("★ URI : {}", request.getRequestURL().toString());
            logger.info("★ HTTP Method : {}", request.getMethod());
            logger.info("* IP Address : {}", IPUtils.getIpAddress(request));
            logger.info("★ Request Body : {}", Arrays.toString(joinPoint.getArgs()));
        }
    }

    @AfterReturning(returning = "returnObject", pointcut = "pointCut()")
    public void doAfterReturning(Object returnObject) {
        if (!Objects.isNull(returnObject)) {
            // 处理完请求，返回内容
            logger.info("★ Response: {}", returnObject);
        }
    }

    @Around("within(api.lemonico..controller.*)")
    public Object around(final ProceedingJoinPoint pjp) throws Throwable {
        final StopWatch sw = new StopWatch();
        sw.start();
        Object res;
        try {
            res = pjp.proceed();
        } catch (Throwable e) {
            sw.stop();
            logger.info("* Error Message : {}", e.getMessage());
            throw e;
        }
        sw.stop();
        logger.info("* Processing Time: {}s", sw.getTotalTimeSeconds());
        return res;
    }

}
