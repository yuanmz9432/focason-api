/*
 * Copyright 2021 Blazeash Co.,Ltd. AllRights Reserved.
 */
package com.blazeash.api.core.configurer;



import com.blazeash.api.core.entity.BaEntityListener;
import com.blazeash.api.core.entity.BaEntityListenerManager;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LcEntityListenerAutoConfigurer
{
    public LcEntityListenerAutoConfigurer() {}

    @Bean
    public BaEntityListenerManager lcEntityListenerManager(@Autowired List<BaEntityListener> listeners) {
        return new BaEntityListenerManager(listeners);
    }


}
