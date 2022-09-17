/*
 * Copyright 2021 Lemonico Co.,Ltd. AllRights Reserved.
 */
package api.lemonico.core.handler;



import api.lemonico.core.entity.LcEntity;
import api.lemonico.core.entity.LcEntityListenerManager;
import org.seasar.doma.jdbc.entity.EntityListener;
import org.seasar.doma.jdbc.entity.PostDeleteContext;
import org.seasar.doma.jdbc.entity.PostInsertContext;
import org.seasar.doma.jdbc.entity.PostUpdateContext;
import org.seasar.doma.jdbc.entity.PreDeleteContext;
import org.seasar.doma.jdbc.entity.PreInsertContext;
import org.seasar.doma.jdbc.entity.PreUpdateContext;

public class LcEntityListenerHandler<E extends LcEntity> implements EntityListener<E>
{
    public LcEntityListenerHandler() {}

    @Override
    public void preInsert(E entity, PreInsertContext<E> context) {
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
