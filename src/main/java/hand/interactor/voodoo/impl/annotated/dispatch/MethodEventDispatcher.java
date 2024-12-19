package hand.interactor.voodoo.impl.annotated.dispatch;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import hand.interactor.voodoo.dispatch.EventDispatcher;
import hand.interactor.voodoo.handler.EventHandler;

public final class MethodEventDispatcher implements EventDispatcher {

    private final Map<Class<?>, Set<EventHandler>> eventHandlers;

    public MethodEventDispatcher(final Map<Class<?>, Set<EventHandler>> eventHandlers) {
        this.eventHandlers = eventHandlers;
    }

    @Override
    public <E> void dispatch(final E event) {

        for (final EventHandler eventHandler : eventHandlers.getOrDefault(
                event.getClass(), Collections.emptySet()))
            eventHandler.handle(event);
    }
}
