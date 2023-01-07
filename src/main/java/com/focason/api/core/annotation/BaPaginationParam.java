/*
 * Copyright 2023 Focason Co.,Ltd. AllRights Reserved.
 */
package com.focason.api.core.annotation;



import java.lang.annotation.*;

@Target({
    ElementType.PARAMETER
})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface BaPaginationParam
{
    int defaultLimitValue() default 20;

    int maxLimitValue() default 200;
}
