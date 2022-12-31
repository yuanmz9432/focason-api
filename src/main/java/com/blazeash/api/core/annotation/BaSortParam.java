/*
 * Copyright 2021 Blazeash Co.,Ltd. AllRights Reserved.
 */
package com.blazeash.api.core.annotation;



import java.lang.annotation.*;

@Target({
    ElementType.PARAMETER
})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface BaSortParam
{
    String defaultValue() default "id:ASC";

    String[] allowedValues() default {
        "id:ASC"
    };
}
