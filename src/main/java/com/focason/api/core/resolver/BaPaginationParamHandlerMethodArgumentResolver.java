/*
 * Copyright 2023 Focason Co.,Ltd. AllRights Reserved.
 */
package com.focason.api.core.resolver;



import com.focason.api.core.annotation.FsPaginationParam;
import com.focason.api.core.attribute.FsPagination;
import com.focason.api.core.exception.BaValidationErrorException;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

public class BaPaginationParamHandlerMethodArgumentResolver implements HandlerMethodArgumentResolver
{

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().isAssignableFrom(FsPagination.class)
            && parameter.hasParameterAnnotation(FsPaginationParam.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
        NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        FsPaginationParam annotation = parameter.getParameterAnnotation(FsPaginationParam.class);
        if (annotation == null) {
            return null;
        } else {
            int limit;
            try {
                limit = extractIntegerParameter(webRequest, "limit", annotation.defaultLimitValue());
                if (limit > annotation.maxLimitValue()) {
                    throw new BaValidationErrorException("Parameter '%s' must be less than or equal to '%s'.", "limit",
                        annotation.maxLimitValue());
                }
            } catch (NumberFormatException e) {
                throw new BaValidationErrorException("Parameter '%s' must be in the correct number format", "limit");
            }

            int page;
            try {
                page = extractIntegerParameter(webRequest, "page", 1);
                if (page <= 0) {
                    throw new BaValidationErrorException("Parameter '%s' must be greater than or equal to 1.", "page");
                }
            } catch (NumberFormatException e) {
                throw new BaValidationErrorException("Parameter '%s' must be in the correct number format", "page");
            }
            return FsPagination.of(limit, page);
        }
    }

    private int extractIntegerParameter(NativeWebRequest webRequest, String name, int defaultValue)
        throws NumberFormatException {
        String value = webRequest.getParameter(name);
        return value != null ? Integer.parseInt(value) : defaultValue;
    }
}
