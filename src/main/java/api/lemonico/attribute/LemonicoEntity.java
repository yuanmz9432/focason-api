package api.lemonico.attribute;

import org.seasar.doma.Entity;

@Entity(listener = LemonicoEntityListenerHandler.class)
public abstract class LemonicoEntity {
    public LemonicoEntity() {

    }
}
