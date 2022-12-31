/*
 * Copyright 2021 Blazeash Co.,Ltd. AllRights Reserved.
 */
package com.blazeash.api.core.entity;



import java.util.List;
import java.util.function.Consumer;

public class BaEntityListenerManager
{
    private static List<BaEntityListener> listeners;

    public BaEntityListenerManager(List<BaEntityListener> listeners) {
        BaEntityListenerManager.listeners = listeners;
    }

    public static void forEachListener(Consumer<BaEntityListener> action) {
        if (listeners == null) {
            throw new IllegalStateException("BlazeashListenerManager has not been initialized yet");
        } else {
            listeners.forEach(action);
        }
    }
}
