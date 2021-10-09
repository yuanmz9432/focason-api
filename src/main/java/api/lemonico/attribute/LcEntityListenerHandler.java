package api.lemonico.attribute;


import org.seasar.doma.jdbc.entity.EntityListener;
import org.seasar.doma.jdbc.entity.PostDeleteContext;
import org.seasar.doma.jdbc.entity.PostInsertContext;
import org.seasar.doma.jdbc.entity.PostUpdateContext;
import org.seasar.doma.jdbc.entity.PreDeleteContext;
import org.seasar.doma.jdbc.entity.PreInsertContext;
import org.seasar.doma.jdbc.entity.PreUpdateContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LcEntityListenerHandler<E extends LcEntity> implements EntityListener<E> {
    private static final Logger log = LoggerFactory.getLogger(LcEntityListenerHandler.class);

    public LcEntityListenerHandler() {
    }

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