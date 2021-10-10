package api.lemonico.annotation;



import java.lang.annotation.*;
import javax.swing.*;

@Target({
    ElementType.PARAMETER
})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LcConditionParam
{
}
