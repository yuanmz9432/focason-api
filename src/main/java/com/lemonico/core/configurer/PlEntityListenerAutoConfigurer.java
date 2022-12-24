/*
 * Copyright 2021 Lemonico Co.,Ltd. AllRights Reserved.
 */
package com.lemonico.core.configurer;



import com.lemonico.entity.PlEntityListener;
import com.lemonico.entity.PlEntityListenerManager;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PlEntityListenerAutoConfigurer
{
    public PlEntityListenerAutoConfigurer() {}

    @Bean
    public PlEntityListenerManager lcEntityListenerManager(@Autowired List<PlEntityListener> listeners) {
        return new PlEntityListenerManager(listeners);
    }


}
