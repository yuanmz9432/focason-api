package api.lemonico.core.annotation;



import java.lang.annotation.*;

@Target({
    ElementType.METHOD
})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LcConditionalMappingOnProperty
{
    String name();

    String havingValue() default "";

    boolean matchIfMissing() default false;
}
