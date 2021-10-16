package api.lemonico.core.annotation.config;



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
public class LcApiInterceptorAutoConfiguration implements WebMvcConfigurer
{

    private final Environment environment;
    private final Jackson2ObjectMapperBuilder jackson2ObjectMapperBuilder;

    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LcConditionalMappingHandlerInterceptor(this.environment));
    }

    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new LcPaginationParamHandlerMethodArgumentResolver());
        resolvers.add(new LcSortParamHandlerMethodArgumentResolver());
        resolvers.add(new LcConditionParamHandlerMethodArgumentResolver(this.jackson2ObjectMapperBuilder));
    }

    @Autowired
    public LcApiInterceptorAutoConfiguration(final Environment environment,
        final Jackson2ObjectMapperBuilder jackson2ObjectMapperBuilder) {
        this.environment = environment;
        this.jackson2ObjectMapperBuilder = jackson2ObjectMapperBuilder;
    }
}
