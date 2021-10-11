package api.lemonico.config;



import api.lemonico.annotation.LcPaginationParam;
import api.lemonico.attribute.LcPagination;
import api.lemonico.exception.LcValidationErrorException;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

public class LcPaginationParamHandlerMethodArgumentResolver implements HandlerMethodArgumentResolver
{

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().isAssignableFrom(LcPagination.class)
            && parameter.hasParameterAnnotation(LcPaginationParam.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
        NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        LcPaginationParam annotation = parameter.getParameterAnnotation(LcPaginationParam.class);
        if (annotation == null) {
            return null;
        } else {
            int limit;
            try {
                limit = extractIntegerParameter(webRequest, "limit", annotation.defaultLimitValue());
                if (limit > annotation.defaultLimitValue()) {
                    throw new LcValidationErrorException("Parameter '{}' must be less than or equal to {}.", "limit",
                        annotation.maxLimitValue());
                }
            } catch (NumberFormatException e) {
                throw new LcValidationErrorException("Parameter '{}' must be in the correct number format", "limit");
            }

            int page;
            try {
                page = extractIntegerParameter(webRequest, "page", 1);
                if (page <= 0) {
                    throw new LcValidationErrorException("Parameter '{}' must be greater than or equal to 1.", "page");
                }
            } catch (NumberFormatException e) {
                throw new LcValidationErrorException("Parameter '{}' must be in the correct number format", "page");
            }
            return LcPagination.of(limit, page);
        }
    }

    private int extractIntegerParameter(NativeWebRequest webRequest, String name, int defaultValue)
        throws NumberFormatException {
        String value = webRequest.getParameter(name);
        return value != null ? Integer.parseInt(value) : defaultValue;
    }
}
