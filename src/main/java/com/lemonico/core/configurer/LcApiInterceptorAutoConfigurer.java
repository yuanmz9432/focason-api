/*
 * Copyright 2021 Lemonico Co.,Ltd. AllRights Reserved.
 */
package com.lemonico.core.configurer;



import com.lemonico.core.interceptor.LcConditionalMappingHandlerInterceptor;
import com.lemonico.core.resolver.PlConditionParamHandlerMethodArgumentResolver;
import com.lemonico.core.resolver.PlPaginationParamHandlerMethodArgumentResolver;
import com.lemonico.core.resolver.PlSortParamHandlerMethodArgumentResolver;
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
public class LcApiInterceptorAutoConfigurer implements WebMvcConfigurer
{

    private final Environment environment;
    private final Jackson2ObjectMapperBuilder jackson2ObjectMapperBuilder;

    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LcConditionalMappingHandlerInterceptor(this.environment));
    }

    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new PlPaginationParamHandlerMethodArgumentResolver());
        resolvers.add(new PlSortParamHandlerMethodArgumentResolver());
        resolvers.add(new PlConditionParamHandlerMethodArgumentResolver(this.jackson2ObjectMapperBuilder));
    }

    @Autowired
    public LcApiInterceptorAutoConfigurer(final Environment environment,
        final Jackson2ObjectMapperBuilder jackson2ObjectMapperBuilder) {
        this.environment = environment;
        this.jackson2ObjectMapperBuilder = jackson2ObjectMapperBuilder;
    }
}
