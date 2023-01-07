/*
 * Copyright 2023 Focason Co.,Ltd. AllRights Reserved.
 */
package com.focason.api.core.handler;



import com.focason.api.core.entity.BaEntityListenerManager;
import com.focason.api.core.entity.LcEntity;
import org.seasar.doma.jdbc.entity.EntityListener;
import org.seasar.doma.jdbc.entity.PostDeleteContext;
import org.seasar.doma.jdbc.entity.PostInsertContext;
import org.seasar.doma.jdbc.entity.PostUpdateContext;
import org.seasar.doma.jdbc.entity.PreDeleteContext;
import org.seasar.doma.jdbc.entity.PreInsertContext;
import org.seasar.doma.jdbc.entity.PreUpdateContext;

public class BaEntityListenerHandler<E extends LcEntity> implements EntityListener<E>
{
    public BaEntityListenerHandler() {}

    @Override
    public void preInsert(E entity, PreInsertContext<E> context) {
        BaEntityListenerManager.forEachListener((listener) -> {
            try {
                listener.preInsert(entity, context);
            } catch (ClassCastException ignored) {
            }
        });
    }

    public void preUpdate(E entity, PreUpdateContext<E> context) {
        BaEntityListenerManager.forEachListener((listener) -> {
            try {
                listener.preUpdate(entity, context);
            } catch (ClassCastException ignored) {
            }

        });
    }

    public void preDelete(E entity, PreDeleteContext<E> context) {
        BaEntityListenerManager.forEachListener((listener) -> {
            try {
                listener.preDelete(entity, context);
            } catch (ClassCastException ignored) {
            }

        });
    }

    public void postInsert(E entity, PostInsertContext<E> context) {
        BaEntityListenerManager.forEachListener((listener) -> {
            try {
                listener.postInsert(entity, context);
            } catch (ClassCastException ignored) {
            }

        });
    }

    public void postUpdate(E entity, PostUpdateContext<E> context) {
        BaEntityListenerManager.forEachListener((listener) -> {
            try {
                listener.postUpdate(entity, context);
            } catch (ClassCastException ignored) {
            }

        });
    }

    public void postDelete(E entity, PostDeleteContext<E> context) {
        BaEntityListenerManager.forEachListener((listener) -> {
            try {
                listener.postDelete(entity, context);
            } catch (ClassCastException ignored) {
            }

        });
    }
}
