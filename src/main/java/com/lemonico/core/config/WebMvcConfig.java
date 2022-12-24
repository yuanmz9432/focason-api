package com.lemonico.core.config;



import com.lemonico.core.annotation.RequestLimitInterceptor;
import javax.annotation.Resource;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * WebMvcConfigurerのカスタマイズ配置
 *
 * @since 1.0.0
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer
{

    @Resource
    private RequestLimitInterceptor requestLimitInterceptor;

    /**
     * カスタマイズインターセプター登録
     *
     * @param registry {@link InterceptorRegistry}
     * @since 1.0.0
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(requestLimitInterceptor);
    }
}
