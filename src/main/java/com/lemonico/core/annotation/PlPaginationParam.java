/*
 * Copyright 2021 Lemonico Co.,Ltd. AllRights Reserved.
 */
package com.lemonico.core.annotation;



import java.lang.annotation.*;

@Target({
    ElementType.PARAMETER
})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PlPaginationParam
{
    int defaultLimitValue() default 20;

    int maxLimitValue() default 200;
}
