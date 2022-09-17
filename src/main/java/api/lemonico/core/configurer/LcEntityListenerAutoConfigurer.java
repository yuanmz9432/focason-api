/*
 * Copyright 2021 Lemonico Co.,Ltd. AllRights Reserved.
 */
package api.lemonico.core.configurer;



import api.lemonico.core.entity.LcEntityListener;
import api.lemonico.core.entity.LcEntityListenerManager;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LcEntityListenerAutoConfigurer
{
    public LcEntityListenerAutoConfigurer() {}

    @Bean
    public LcEntityListenerManager lcEntityListenerManager(@Autowired List<LcEntityListener> listeners) {
        return new LcEntityListenerManager(listeners);
    }


}
