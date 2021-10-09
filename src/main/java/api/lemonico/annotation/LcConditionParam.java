package api.lemonico.annotation;

import javax.swing.*;
import java.lang.annotation.*;

@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LcConditionParam {
}
