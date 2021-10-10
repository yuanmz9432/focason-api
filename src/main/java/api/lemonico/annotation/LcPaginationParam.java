package api.lemonico.annotation;



import java.lang.annotation.*;

@Target({
    ElementType.PARAMETER
})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LcPaginationParam
{
    int defaultLimitValue() default 20;

    int maxLimitValue() default 1000;
}
