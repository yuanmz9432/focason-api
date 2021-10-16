/*
 * Copyright 2021 Lemonico Co.,Ltd. AllRights Reserved.
 */
package api.lemonico.core.attribute;



import org.seasar.doma.Entity;

@Entity(listener = LcEntityListenerHandler.class)
public abstract class LcEntity
{
    public LcEntity() {

    }
}
