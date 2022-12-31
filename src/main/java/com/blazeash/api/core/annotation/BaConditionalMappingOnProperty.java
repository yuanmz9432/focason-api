/*
 * Copyright 2021 Blazeash Co.,Ltd. AllRights Reserved.
 */
package com.blazeash.api.core.annotation;



import java.lang.annotation.*;

@Target({
    ElementType.METHOD
})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface BaConditionalMappingOnProperty
{
    String name();

    String havingValue() default "";

    boolean matchIfMissing() default false;
}
