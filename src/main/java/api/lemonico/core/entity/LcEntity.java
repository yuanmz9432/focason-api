/*
 * Copyright 2021 Lemonico Co.,Ltd. AllRights Reserved.
 */
package api.lemonico.core.entity;



import api.lemonico.core.handler.LcEntityListenerHandler;
import org.seasar.doma.Entity;

@Entity(listener = LcEntityListenerHandler.class)
public class LcEntity
{
    public LcEntity() {}
}
