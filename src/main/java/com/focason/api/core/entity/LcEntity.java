/*
 * Copyright 2023 Focason Co.,Ltd. AllRights Reserved.
 */
package com.focason.api.core.entity;



import com.focason.api.core.handler.FsEntityListenerHandler;
import org.seasar.doma.Entity;

@Entity(listener = FsEntityListenerHandler.class)
public class LcEntity
{
    public LcEntity() {}
}
