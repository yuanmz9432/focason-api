/*
 * Copyright 2021 Lemonico Co.,Ltd. AllRights Reserved.
 */
package com.lemonico.core.resolver;



import com.fasterxml.jackson.databind.ObjectMapper;
import com.lemonico.core.annotation.PlConditionParam;
import com.lemonico.core.exception.PlValidationErrorException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.springframework.core.MethodParameter;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

public class PlConditionParamHandlerMethodArgumentResolver implements HandlerMethodArgumentResolver
{
    private final Jackson2ObjectMapperBuilder jackson2ObjectMapperBuilder;

    public PlConditionParamHandlerMethodArgumentResolver(Jackson2ObjectMapperBuilder jackson2ObjectMapperBuilder) {
        this.jackson2ObjectMapperBuilder = jackson2ObjectMapperBuilder;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(PlConditionParam.class);
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
                throw new PlValidationErrorException("Failed to decode 'condition'", e);
            }

            try {
                return this.objectMapper().readValue(json, parameter.getParameterType());
            } catch (IOException e) {
                throw new PlValidationErrorException("Failed to parse 'condition'", e);
            }
        }
    }

    private ObjectMapper objectMapper() {
        return this.jackson2ObjectMapperBuilder.build();
    }
}
