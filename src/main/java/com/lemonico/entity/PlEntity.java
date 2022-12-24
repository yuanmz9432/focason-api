package com.lemonico.entity;



import com.lemonico.core.handler.LcEntityListenerHandler;
import org.seasar.doma.Entity;

@Entity(listener = LcEntityListenerHandler.class)
public class PlEntity
{
    public PlEntity() {}
}
