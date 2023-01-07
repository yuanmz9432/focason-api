/*
 * Copyright 2023 Focason Co.,Ltd. AllRights Reserved.
 */
package com.focason.api.core.annotation;



import java.lang.annotation.*;

@Target({
    ElementType.METHOD
})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface FsConditionalMappingOnProperty
{
    String name();

    String havingValue() default "";

    boolean matchIfMissing() default false;
}
