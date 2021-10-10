package api.lemonico.attribute;



import java.util.List;
import java.util.function.Consumer;

public class LcEntityListenerManager
{
    private static List<LcEntityListener> listeners;

    public LcEntityListenerManager(List<LcEntityListener> listeners) {
        LcEntityListenerManager.listeners = listeners;
    }

    public static void forEachListener(Consumer<LcEntityListener> action) {
        if (listeners == null) {
            throw new IllegalStateException("LemonicoListenerManager has not been initialized yet");
        } else {
            listeners.forEach(action);
        }
    }
}
