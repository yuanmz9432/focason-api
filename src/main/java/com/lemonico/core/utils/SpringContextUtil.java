package com.lemonico.core.utils;



import javax.annotation.Nonnull;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * アプリケーションコンテクストツール
 *
 * @since 1.0.0
 */
@Component
public class SpringContextUtil implements ApplicationContextAware
{

    private static ApplicationContext context = null;

    public static ApplicationContext getApplicationContext() {
        return context;
    }

    @Override
    public void setApplicationContext(@Nonnull ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }

    // 获取当前环境
    public static String getActiveProfile() {
        return context.getEnvironment().getActiveProfiles()[0];
    }

    public static Object getBean(String name) {
        return getApplicationContext().getBean(name);
    }
}
