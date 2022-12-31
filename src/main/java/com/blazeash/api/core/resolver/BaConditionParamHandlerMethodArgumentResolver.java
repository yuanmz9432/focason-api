/*
 * Copyright 2021 Blazeash Co.,Ltd. AllRights Reserved.
 */
package com.blazeash.api.core.resolver;



import com.blazeash.api.core.annotation.BaConditionParam;
import com.blazeash.api.core.exception.BaValidationErrorException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.springframework.core.MethodParameter;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

public class BaConditionParamHandlerMethodArgumentResolver implements HandlerMethodArgumentResolver
{
    private final Jackson2ObjectMapperBuilder jackson2ObjectMapperBuilder;

    public BaConditionParamHandlerMethodArgumentResolver(Jackson2ObjectMapperBuilder jackson2ObjectMapperBuilder) {
        this.jackson2ObjectMapperBuilder = jackson2ObjectMapperBuilder;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(BaConditionParam.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
        NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        String value = webRequest.getParameter("condition");
        if (value == null) {
            return null;
        } else {
            String json;
            try {
                json = new String(Base64.getDecoder().decode(value), StandardCharsets.UTF_8);
            } catch (IllegalArgumentException e) {
                throw new BaValidationErrorException("Failed to decode 'condition'", e);
            }

            try {
                return this.objectMapper().readValue(json, parameter.getParameterType());
            } catch (IOException e) {
                throw new BaValidationErrorException("Failed to parse 'condition'", e);
            }
        }
    }

    private ObjectMapper objectMapper() {
        return this.jackson2ObjectMapperBuilder.build();
    }
}