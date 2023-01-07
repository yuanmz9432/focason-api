/*
 * Copyright 2023 Focason Co.,Ltd. AllRights Reserved.
 */
package com.focason.api.core.entity;



import com.focason.api.core.handler.BaEntityListenerHandler;
import org.seasar.doma.Entity;

@Entity(listener = BaEntityListenerHandler.class)
public class LcEntity
{
    public LcEntity() {}
}
