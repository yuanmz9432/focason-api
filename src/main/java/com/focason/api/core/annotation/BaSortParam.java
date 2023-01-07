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
public @interface BaSortParam
{
    String defaultValue() default "id:ASC";

    String[] allowedValues() default {
        "id:ASC"
    };
}
