/*
 * Copyright 2023 Focason Co.,Ltd. AllRights Reserved.
 */
package com.focason.api.core.configurer;



import com.focason.api.core.entity.BaEntityListener;
import com.focason.api.core.entity.BaEntityListenerManager;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FsEntityListenerAutoConfigurer
{
    public FsEntityListenerAutoConfigurer() {}

    @Bean
    public BaEntityListenerManager lcEntityListenerManager(@Autowired List<BaEntityListener> listeners) {
        return new BaEntityListenerManager(listeners);
    }


}
