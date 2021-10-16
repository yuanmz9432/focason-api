/*
 * Copyright 2021 Lemonico Co.,Ltd. AllRights Reserved.
 */
package api.lemonico.core.annotation.config;



import api.lemonico.core.annotation.LcSortParam;
import api.lemonico.core.attribute.LcSort;
import api.lemonico.core.exception.LcValidationErrorException;
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
public class LcSortParamHandlerMethodArgumentResolver implements HandlerMethodArgumentResolver
{
    private static final String REGEX =
        "^(?<property>[\\p{Alpha}_][\\p{Alnum}_]*?):(?<direction>[Aa][Ss][Cc]|[Dd][Ee][Ss][Cc]?)$";
    private static final Pattern SORT_PATTERN = Pattern.compile(REGEX);

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().isAssignableFrom(LcSort.class)
            && parameter.hasParameterAnnotation(LcSortParam.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
        NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        LcSortParam annotation = parameter.getParameterAnnotation(LcSortParam.class);
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
                    throw new LcValidationErrorException("The specified sort parameter is not allowed.");
                }
            }

            Matcher m = SORT_PATTERN.matcher(sortValue);
            if (!m.find()) {
                throw new LcValidationErrorException("Parameter '%s' must match the regexp '%s'", "sort", REGEX);
            } else {
                return LcSort.builder().direction(LcSort.Direction.of(m.group("direction")))
                    .property(m.group("property")).build();
            }
        }
    }
}
