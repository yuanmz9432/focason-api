package api.lemonico.core.config;



import api.lemonico.core.annotation.LcConditionalMappingOnProperty;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.NoHandlerFoundException;

public class LcConditionalMappingHandlerInterceptor implements HandlerInterceptor
{
    private final Environment environment;

    public LcConditionalMappingHandlerInterceptor(final Environment environment) {
        this.environment = environment;
    }

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
        throws NoHandlerFoundException {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        } else {
            HandlerMethod method = (HandlerMethod) handler;
            LcConditionalMappingOnProperty methodAnnotation =
                method.getMethodAnnotation(LcConditionalMappingOnProperty.class);
            if (!this.isMappingEnabled(methodAnnotation)) {
                throw this.getNoHandlerFoundException(request);
            } else {
                LcConditionalMappingOnProperty classAnnotation =
                    method.getBeanType().getAnnotation(LcConditionalMappingOnProperty.class);
                if (!this.isMappingEnabled(classAnnotation)) {
                    throw this.getNoHandlerFoundException(request);
                } else {
                    return true;
                }
            }
        }
    }

    private boolean isMappingEnabled(LcConditionalMappingOnProperty annotation) {
        if (annotation != null) {
            String propValue = this.environment.getProperty(annotation.name());
            if (!StringUtils.hasText(propValue)) {
                return !annotation.matchIfMissing();
            } else {
                String havingValue = annotation.havingValue();
                return !StringUtils.hasText(havingValue) || havingValue.equals(propValue);
            }
        } else {
            return true;
        }
    }

    private NoHandlerFoundException getNoHandlerFoundException(HttpServletRequest request) {
        Map<String, List<String>> headerMap = Collections.list(request.getHeaderNames()).stream()
            .collect(Collectors.toMap(Function.identity(), (name) -> Collections.list(request.getHeaders(name))));
        return new NoHandlerFoundException(request.getMethod(), request.getRequestURI(),
            new HttpHeaders(new LinkedMultiValueMap<>(headerMap)));
    }
}
