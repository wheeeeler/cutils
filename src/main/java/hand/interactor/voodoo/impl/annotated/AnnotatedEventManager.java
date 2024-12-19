package hand.interactor.voodoo.impl.annotated;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import hand.interactor.voodoo.EventManager;
import hand.interactor.voodoo.dispatch.EventDispatcher;
import hand.interactor.voodoo.handler.EventHandler;
import hand.interactor.voodoo.handler.scan.EventHandlerScanner;
import hand.interactor.voodoo.impl.annotated.dispatch.MethodEventDispatcher;
import hand.interactor.voodoo.impl.annotated.handler.scan.MethodHandlerScanner;

public final class AnnotatedEventManager implements EventManager {

    private final EventHandlerScanner eventHandlerScanner;

    private final Map<Object, EventDispatcher> listenerDispatchers;

    public AnnotatedEventManager() {
        this.eventHandlerScanner = new MethodHandlerScanner();
        this.listenerDispatchers = new ConcurrentHashMap<>();
    }

    @Override
    public <E> E dispatchEvent(final E event) {

        for (final EventDispatcher dispatcher : listenerDispatchers.values())
            dispatcher.dispatch(event);

        return event;
    }

    @Override
    public boolean isRegisteredListener(final Object listener) {
        return listenerDispatchers.containsKey(listener);
    }

    @Override
    public boolean addEventListener(final Object listenerContainer) {

        if (listenerDispatchers.containsKey(listenerContainer))
            return false;

        final Map<Class<?>, Set<EventHandler>> eventHandlers = eventHandlerScanner.locate(listenerContainer);
        if (eventHandlers.isEmpty())
            return false;

        return listenerDispatchers.put(listenerContainer,
                new MethodEventDispatcher(eventHandlers)) == null;
    }

    @Override
    public boolean removeEventListener(final Object listenerContainer) {

        return listenerDispatchers.remove(listenerContainer) != null;
    }
}
