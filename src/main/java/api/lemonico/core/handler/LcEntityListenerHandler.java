/*
 * Copyright 2021 Lemonico Co.,Ltd. AllRights Reserved.
 */
package api.lemonico.core.handler;



import api.lemonico.core.attribute.LcEntity;
import api.lemonico.core.attribute.LcEntityListenerManager;
import api.lemonico.domain.ClientStatus;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.seasar.doma.jdbc.entity.EntityListener;
import org.seasar.doma.jdbc.entity.PostDeleteContext;
import org.seasar.doma.jdbc.entity.PostInsertContext;
import org.seasar.doma.jdbc.entity.PostUpdateContext;
import org.seasar.doma.jdbc.entity.PreDeleteContext;
import org.seasar.doma.jdbc.entity.PreInsertContext;
import org.seasar.doma.jdbc.entity.PreUpdateContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

@Slf4j
public class LcEntityListenerHandler<E extends LcEntity> implements EntityListener<E>
{
    private static final Logger logger = LoggerFactory.getLogger(LcEntityListenerHandler.class);

    public LcEntityListenerHandler() {}

    /**
     * デフォルトのユーザID
     */
    private static final String DEFAULT_CLIENT_CODE = "LC-admin";

    private static String getClientCode() {
        final String clientCode = MDC.get("CLIENT_CODE");
        return Objects.requireNonNullElse(clientCode, DEFAULT_CLIENT_CODE);
    }

    @Override
    public void preInsert(E entity, PreInsertContext<E> context) {
        entity.setCreatedAt(LocalDateTime.now());
        entity.setCreatedBy(getClientCode());
        entity.setModifiedAt(LocalDateTime.now());
        entity.setModifiedBy(getClientCode());
        entity.setIsDeleted(ClientStatus.NORMAL.getValue());
        LcEntityListenerManager.forEachListener((listener) -> {
            try {
                listener.preInsert(entity, context);
            } catch (ClassCastException ignored) {
            }
        });
    }

    public void preUpdate(E entity, PreUpdateContext<E> context) {
        LcEntityListenerManager.forEachListener((listener) -> {
            try {
                listener.preUpdate(entity, context);
            } catch (ClassCastException ignored) {
            }

        });
    }

    public void preDelete(E entity, PreDeleteContext<E> context) {
        LcEntityListenerManager.forEachListener((listener) -> {
            try {
                listener.preDelete(entity, context);
            } catch (ClassCastException ignored) {
            }

        });
    }

    public void postInsert(E entity, PostInsertContext<E> context) {
        LcEntityListenerManager.forEachListener((listener) -> {
            try {
                listener.postInsert(entity, context);
            } catch (ClassCastException ignored) {
            }

        });
    }

    public void postUpdate(E entity, PostUpdateContext<E> context) {
        LcEntityListenerManager.forEachListener((listener) -> {
            try {
                listener.postUpdate(entity, context);
            } catch (ClassCastException ignored) {
            }

        });
    }

    public void postDelete(E entity, PostDeleteContext<E> context) {
        LcEntityListenerManager.forEachListener((listener) -> {
            try {
                listener.postDelete(entity, context);
            } catch (ClassCastException ignored) {
            }

        });
    }
}
