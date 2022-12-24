package com.lemonico.entity;



import com.lemonico.core.handler.PlEntityListenerHandler;
import org.seasar.doma.Entity;

@Entity(listener = PlEntityListenerHandler.class)
public class PlEntity
{
    public PlEntity() {}
}
