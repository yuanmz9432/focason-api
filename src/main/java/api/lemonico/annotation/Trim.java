package api.lemonico.annotation;

import java.lang.annotation.*;

/**
 * 空白を除去
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE })
public @interface Trim {
}
