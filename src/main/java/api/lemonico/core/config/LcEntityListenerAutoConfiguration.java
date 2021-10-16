/*
 * Copyright 2021 Lemonico Co.,Ltd. AllRights Reserved.
 */
package api.lemonico.core.config;



import api.lemonico.core.attribute.LcEntityListener;
import api.lemonico.core.attribute.LcEntityListenerManager;
import api.lemonico.core.attribute.LcEntityTimestampListener;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LcEntityListenerAutoConfiguration
{
    public LcEntityListenerAutoConfiguration() {}

    @Bean
    public LcEntityTimestampListener lcEntityTimestampListener() {
        return new LcEntityTimestampListener();
    }

    @Bean
    public LcEntityListenerManager lcEntityListenerManager(@Autowired List<LcEntityListener> listeners) {
        return new LcEntityListenerManager(listeners);
    }


}
