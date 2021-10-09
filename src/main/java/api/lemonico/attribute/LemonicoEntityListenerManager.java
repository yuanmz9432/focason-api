package api.lemonico.attribute;

import java.util.List;
import java.util.function.Consumer;

public class LemonicoEntityListenerManager {
    private static List<LemonicoEntityListener> listeners;

    public LemonicoEntityListenerManager(List<LemonicoEntityListener> listeners) {
        LemonicoEntityListenerManager.listeners = listeners;
    }

    public static void forEachListener(Consumer<LemonicoEntityListener> action) {
        if (listeners == null) {
            throw new IllegalStateException("LemonicoListenerManager has not been initialized yet");
        } else {
            listeners.forEach(action);
        }
    }
}
