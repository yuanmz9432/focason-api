/*
 * Copyright 2023 Focason Co.,Ltd. AllRights Reserved.
 */
package com.focason.api.core.configurer;



import com.focason.api.core.interceptor.FsConditionalMappingHandlerInterceptor;
import com.focason.api.core.resolver.FsConditionParamHandlerMethodArgumentResolver;
import com.focason.api.core.resolver.FsPaginationParamHandlerMethodArgumentResolver;
import com.focason.api.core.resolver.FsSortParamHandlerMethodArgumentResolver;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@ConditionalOnWebApplication(
    type = ConditionalOnWebApplication.Type.SERVLET)
@Configuration
public class FsApiInterceptorAutoConfigurer implements WebMvcConfigurer
{

    private final Environment environment;
    private final Jackson2ObjectMapperBuilder jackson2ObjectMapperBuilder;

    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new FsConditionalMappingHandlerInterceptor(this.environment));
    }

    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new FsPaginationParamHandlerMethodArgumentResolver());
        resolvers.add(new FsSortParamHandlerMethodArgumentResolver());
        resolvers.add(new FsConditionParamHandlerMethodArgumentResolver(this.jackson2ObjectMapperBuilder));
    }

    @Autowired
    public FsApiInterceptorAutoConfigurer(final Environment environment,
                                          final Jackson2ObjectMapperBuilder jackson2ObjectMapperBuilder) {
        this.environment = environment;
        this.jackson2ObjectMapperBuilder = jackson2ObjectMapperBuilder;
    }
}
