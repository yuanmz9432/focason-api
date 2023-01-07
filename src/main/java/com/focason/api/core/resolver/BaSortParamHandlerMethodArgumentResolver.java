/*
 * Copyright 2023 Focason Co.,Ltd. AllRights Reserved.
 */
package com.focason.api.core.resolver;



import com.focason.api.core.annotation.BaSortParam;
import com.focason.api.core.attribute.BaSort;
import com.focason.api.core.exception.BaValidationErrorException;
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
public class BaSortParamHandlerMethodArgumentResolver implements HandlerMethodArgumentResolver
{
    private static final String REGEX =
        "^(?<property>[\\p{Alpha}_][\\p{Alnum}_]*?):(?<direction>[Aa][Ss][Cc]|[Dd][Ee][Ss][Cc]?)$";
    private static final Pattern SORT_PATTERN = Pattern.compile(REGEX);

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().isAssignableFrom(BaSort.class)
            && parameter.hasParameterAnnotation(BaSortParam.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
        NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        BaSortParam annotation = parameter.getParameterAnnotation(BaSortParam.class);
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
                    throw new BaValidationErrorException("The specified sort parameter is not allowed.");
                }
            }

            Matcher m = SORT_PATTERN.matcher(sortValue);
            if (!m.find()) {
                throw new BaValidationErrorException("Parameter '%s' must match the regexp '%s'", "sort", REGEX);
            } else {
                return BaSort.builder().direction(BaSort.Direction.of(m.group("direction")))
                    .property(m.group("property")).build();
            }
        }
    }
}
