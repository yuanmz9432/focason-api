/*
 * Copyright 2021 Lemonico Co.,Ltd. AllRights Reserved.
 */
package com.lemonico.core.annotation;



import java.lang.annotation.*;

@Target({
    ElementType.METHOD
})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PlConditionalMappingOnProperty
{
    String name();

    String havingValue() default "";

    boolean matchIfMissing() default false;
}
