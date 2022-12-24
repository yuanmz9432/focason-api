package com.lemonico.core.configurer;



import com.lemonico.core.utils.apiLimit.GetIp;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * AOPコンフィグ
 *
 * @since 1.0.0
 */
@Aspect
@Component
public class LcAOPConfigurer
{
    private final static Logger logger = LoggerFactory.getLogger(LcAOPConfigurer.class);

    // 切入点
    @Pointcut("execution(public * com.lemonico.*.controller.*.*(..))")
    private void point() {}

    /**
     * コントローラーに入る前に、ログ出力処理を行う
     *
     * @param joinPoint ポイント
     * @since 1.0.0
     */
    @Before("point()")
    public void before(JoinPoint joinPoint) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        assert attributes != null;
        HttpServletRequest request = attributes.getRequest();

        // ログ容量を減らすのため、AWSのヘルスチェックログを表示しないようにする
        if ("heartbeat".equals(joinPoint.getSignature().getName())) {
            return;
        }
        // URL
        logger.info("URL = {} ", request.getRequestURL());
        // IPアドレス
        logger.info("IP = {}", GetIp.getIp(request));
        // クラス名、クラスメソッド
        logger.info("ClASS_METHOD = {}",
            joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName());
        // パラメーター
        logger.info("ARGS = {}", showParameter(request.getParameterMap()));
    }

    /**
     * パラメータ表示
     *
     * @param map パラメータのマップ
     * @return パラメータ文字列
     * @since 1.0.0
     */
    public String showParameter(Map<String, String[]> map) {
        StringBuilder result = new StringBuilder();
        for (String key : map.keySet()) {
            result.append(key).append(":").append(map.get(key)[0]).append(" ");
        }
        return result.toString();
    }
}
