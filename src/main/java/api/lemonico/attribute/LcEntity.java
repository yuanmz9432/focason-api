package api.lemonico.attribute;

import org.seasar.doma.Entity;

@Entity(listener = LcEntityListenerHandler.class)
public abstract class LcEntity {
    public LcEntity() {

    }
}