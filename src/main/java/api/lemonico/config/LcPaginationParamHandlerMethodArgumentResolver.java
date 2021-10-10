package api.lemonico.config;

import api.lemonico.annotation.LcPaginationParam;
import api.lemonico.attribute.LcPagination;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.Objects;

public class LcPaginationParamHandlerMethodArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().isAssignableFrom(LcPagination.class) && parameter.hasParameterAnnotation(LcPaginationParam.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        int limit = webRequest.getParameter("limit") == null ? LcPagination.DEFAULT.getLimit() : Integer.parseInt(Objects.requireNonNull(webRequest.getParameter("limit")));
        int page = webRequest.getParameter("page") == null ? LcPagination.DEFAULT.getPage() : Integer.parseInt(Objects.requireNonNull(webRequest.getParameter("page")));
        return LcPagination.builder().limit(limit).page(page).build();
    }
}
