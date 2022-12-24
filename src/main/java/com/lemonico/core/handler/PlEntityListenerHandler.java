/*
 * Copyright 2021 Lemonico Co.,Ltd. AllRights Reserved.
 */
package com.lemonico.core.handler;



import com.lemonico.entity.PlEntity;
import com.lemonico.entity.PlEntityListenerManager;
import org.seasar.doma.jdbc.entity.*;

public class PlEntityListenerHandler<E extends PlEntity> implements EntityListener<E>
{
    public PlEntityListenerHandler() {}

    @Override
    public void preInsert(E entity, PreInsertContext<E> context) {
        PlEntityListenerManager.forEachListener((listener) -> {
            try {
                listener.preInsert(entity, context);
            } catch (ClassCastException ignored) {
            }
        });
    }

    public void preUpdate(E entity, PreUpdateContext<E> context) {
        PlEntityListenerManager.forEachListener((listener) -> {
            try {
                listener.preUpdate(entity, context);
            } catch (ClassCastException ignored) {
            }

        });
    }

    public void preDelete(E entity, PreDeleteContext<E> context) {
        PlEntityListenerManager.forEachListener((listener) -> {
            try {
                listener.preDelete(entity, context);
            } catch (ClassCastException ignored) {
            }

        });
    }

    public void postInsert(E entity, PostInsertContext<E> context) {
        PlEntityListenerManager.forEachListener((listener) -> {
            try {
                listener.postInsert(entity, context);
            } catch (ClassCastException ignored) {
            }

        });
    }

    public void postUpdate(E entity, PostUpdateContext<E> context) {
        PlEntityListenerManager.forEachListener((listener) -> {
            try {
                listener.postUpdate(entity, context);
            } catch (ClassCastException ignored) {
            }

        });
    }

    public void postDelete(E entity, PostDeleteContext<E> context) {
        PlEntityListenerManager.forEachListener((listener) -> {
            try {
                listener.postDelete(entity, context);
            } catch (ClassCastException ignored) {
            }

        });
    }
}
