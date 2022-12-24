/*
 * Copyright 2021 Lemonico Co.,Ltd. AllRights Reserved.
 */
package com.lemonico.core.configurer;



import com.lemonico.core.interceptor.CorsInterceptor;
import javax.annotation.Resource;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class PlWebMvcConfigurer implements WebMvcConfigurer
{
    @Resource
    private CorsInterceptor corsInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 跨域拦截器需放在最上面
        registry.addInterceptor(corsInterceptor).addPathPatterns("/**");
    }
}