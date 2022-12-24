package com.lemonico.core.annotation;



import java.lang.annotation.*;

/**
 * @className: RequestLimit
 * @description: インターフェース リフレッシュ制限をカスタマイズする
 * @date: 2020/05/15 13:56
 **/
@Documented
@Inherited
@Target({
    ElementType.METHOD, ElementType.TYPE
})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestLimit
{
    int maxCount() default 60;

    int second() default 60;
}
