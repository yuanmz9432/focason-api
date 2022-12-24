/*
 * Copyright 2021 Lemonico Co.,Ltd. AllRights Reserved.
 */
package com.lemonico.core.configurer;



import com.lemonico.core.annotation.RequestLimitInterceptor;
import javax.annotation.Resource;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class LcWebMvcConfigurer implements WebMvcConfigurer
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
        registry.addInterceptor(requestLimitInterceptor).addPathPatterns("/**");
    }

    /**
     * CORS対策
     *
     * @param registry CorsRegistry
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
            .allowedOriginPatterns("*") // SpringBoot2.4.0 [allowedOriginPatterns]代替[allowedOrigins]
            .allowedMethods("*")
            .allowedHeaders("*")
            .exposedHeaders("*")
            .maxAge(3600)
            .allowCredentials(true);
    }
}
