/*
 * Copyright 2023 Focason Co.,Ltd. AllRights Reserved.
 */
package com.focason.api.core.resolver;



import com.focason.api.core.annotation.FsSortParam;
import com.focason.api.core.attribute.FsSort;
import com.focason.api.core.exception.FsValidationErrorException;
import java.util.Arrays;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.NoArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@NoArgsConstructor
public class FsSortParamHandlerMethodArgumentResolver implements HandlerMethodArgumentResolver
{
    private static final String REGEX =
        "^(?<property>[\\p{Alpha}_][\\p{Alnum}_]*?):(?<direction>[Aa][Ss][Cc]|[Dd][Ee][Ss][Cc]?)$";
    private static final Pattern SORT_PATTERN = Pattern.compile(REGEX);

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().isAssignableFrom(FsSort.class)
            && parameter.hasParameterAnnotation(FsSortParam.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
        NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        FsSortParam annotation = parameter.getParameterAnnotation(FsSortParam.class);
        if (annotation == null) {
            return null;
        } else {
            String sortValue = webRequest.getParameter("sort");
            if (sortValue == null) {
                sortValue = annotation.defaultValue();
            }

            if (annotation.allowedValues().length > 0) {
                Objects.requireNonNull(sortValue);
                boolean allowed = Arrays.stream(annotation.allowedValues()).anyMatch(sortValue::equalsIgnoreCase);
                if (!allowed) {
                    throw new FsValidationErrorException("The specified sort parameter is not allowed.");
                }
            }

            Matcher m = SORT_PATTERN.matcher(sortValue);
            if (!m.find()) {
                throw new FsValidationErrorException("Parameter '%s' must match the regexp '%s'", "sort", REGEX);
            } else {
                return FsSort.builder().direction(FsSort.Direction.of(m.group("direction")))
                    .property(m.group("property")).build();
            }
        }
    }
}
