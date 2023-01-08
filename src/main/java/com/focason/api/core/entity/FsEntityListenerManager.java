/*
 * Copyright 2023 Focason Co.,Ltd. AllRights Reserved.
 */
package com.focason.api.core.entity;



import java.util.List;
import java.util.function.Consumer;

public class FsEntityListenerManager
{
    private static List<FsEntityListener> listeners;

    public FsEntityListenerManager(List<FsEntityListener> listeners) {
        FsEntityListenerManager.listeners = listeners;
    }

    public static void forEachListener(Consumer<FsEntityListener> action) {
        if (listeners == null) {
            throw new IllegalStateException("FocasonListenerManager has not been initialized yet");
        } else {
            listeners.forEach(action);
        }
    }
}
