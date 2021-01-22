package api.lemonico.aop;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import lombok.extern.slf4j.Slf4j;

/**
 * サービス層の開始・終了のトレース
 */
@Aspect
@Component
@Slf4j
public class LemonicoAPIAspect {

    private LocalDateTime start = null;

    private LocalDateTime end = null;

    private Double getDuration() {
        if (start != null && end != null) {
            Duration duration = Duration.between(this.start, this.end);
            BigDecimal unit = new BigDecimal(1000);
            BigDecimal millis = new BigDecimal(duration.toMillis());
            return millis.divide(unit).doubleValue();
        }
        return 0.00;
    }

    // 配置切点
    @Pointcut("execution(* api.lemonico.controller..*(..))")
    public void pointcut() {
    }

    @Before("pointcut()")
    public void beforeExcution(JoinPoint joinPoint) {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (requestAttributes != null) {
            HttpServletRequest request = requestAttributes.getRequest();
            this.start = LocalDateTime.now();
            log.info("=====================リクエスト START=====================");
            log.info("開始時間 : {}", this.start);
            log.info("★URL : {}", request.getRequestURL().toString());
            log.info("★HTTPメソッド : {}", request.getMethod());
            log.info("IPアドレス : {}", request.getRemoteAddr());
            log.info("★クラスメソッド : {}", joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName());
            log.info("★リクエストボディー : {}", Arrays.toString(joinPoint.getArgs()));
            log.info("=====================リクエスト END=====================");
        }
    }

    @AfterReturning(returning = "returnObject", pointcut = "pointcut()")
    public void doAfterReturning(JoinPoint joinPoint, Object returnObject) {
        System.out.println(joinPoint);
        System.out.println(returnObject);
        if (!Objects.isNull(returnObject)) {
            String code = "code";
            String message = "message";
            Object data = "data";
            // 处理完请求，返回内容
            this.end = LocalDateTime.now();
            log.info("=====================レスポンス START=====================");
            log.info("終了時間 : {}", this.end);
            log.info("ret : {}", returnObject.getClass());
            log.info("★コード : {}", code);
            log.info("★メッセージ : {}", message);
            log.info("★データ : {}", data);
            log.info("処理所用時間 : {}秒", getDuration());
            log.info("=====================レスポンス END=====================");
        }
    }

    @Around("within(api.lemonico.controller.*)")
    public Object around(final ProceedingJoinPoint pjp) throws Throwable {

        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();

        log.trace("メソッド開始:{}\t引数:{}", pjp.getSignature(), Arrays.asList(pjp.getArgs()));
        final StopWatch sw = new StopWatch();
        sw.start();
        Object res;
        try {
            res = pjp.proceed();
        } catch (Throwable e) {
            sw.stop();
            log.trace("メソッド異常終了:{}\t処理時間:{}ミリ秒\t例外:{}", pjp.getSignature(), sw.getTotalTimeMillis(), e);
            throw e;
        }
        sw.stop();
        log.trace("メソッド終了:{}\t処理時間:{}ミリ秒\t戻り値:{}", pjp.getSignature(), sw.getLastTaskTimeMillis(), res);
        return res;
    }

}
