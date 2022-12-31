/*
 * Copyright 2021 Blazeash Co.,Ltd. AllRights Reserved.
 */
package com.blazeash.api.core.entity;



import com.blazeash.api.core.handler.BaEntityListenerHandler;
import org.seasar.doma.Entity;

@Entity(listener = BaEntityListenerHandler.class)
public class LcEntity
{
    public LcEntity() {}
}
