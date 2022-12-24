/*
 * Copyright 2021 Lemonico Co.,Ltd. AllRights Reserved.
 */
package com.lemonico.entity;



import java.util.List;
import java.util.function.Consumer;

public class PlEntityListenerManager
{
    private static List<PlEntityListener> listeners;

    public PlEntityListenerManager(List<PlEntityListener> listeners) {
        PlEntityListenerManager.listeners = listeners;
    }

    public static void forEachListener(Consumer<PlEntityListener> action) {
        if (listeners == null) {
            throw new IllegalStateException("ListenerManager has not been initialized yet");
        } else {
            listeners.forEach(action);
        }
    }
}
