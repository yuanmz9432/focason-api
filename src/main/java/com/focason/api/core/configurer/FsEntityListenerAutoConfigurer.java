/*
 * Copyright 2023 Focason Co.,Ltd. AllRights Reserved.
 */
package com.focason.api.core.configurer;



import com.focason.api.core.entity.FsEntityListener;
import com.focason.api.core.entity.FsEntityListenerManager;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FsEntityListenerAutoConfigurer
{
    public FsEntityListenerAutoConfigurer() {}

    @Bean
    public FsEntityListenerManager lcEntityListenerManager(@Autowired List<FsEntityListener> listeners) {
        return new FsEntityListenerManager(listeners);
    }


}
