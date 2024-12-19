package hand.interactor.voodoo.impl.annotated.handler;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;

import hand.interactor.voodoo.filter.EventFilter;
import hand.interactor.voodoo.handler.EventHandler;
import hand.interactor.voodoo.handler.ListenerPriority;
import hand.interactor.voodoo.impl.annotated.handler.annotation.Listener;

public final class MethodEventHandler implements EventHandler {

    private final Object listenerParent;

    private final Method method;

    private final Set<EventFilter> eventFilters;

    private final Listener listenerAnnotation;

    public MethodEventHandler(final Object listenerParent, final Method method,
            final Set<EventFilter> eventFilters) {
        this.listenerParent = listenerParent;
        if (!method.isAccessible())
            method.setAccessible(true);

        this.method = method;
        this.eventFilters = eventFilters;
        this.listenerAnnotation = method.getAnnotation(Listener.class);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <E> void handle(final E event) {

        for (final EventFilter filter : eventFilters)
            if (!filter.test(this, event))
                return;

        try {

            method.invoke(listenerParent, event);
        } catch (final IllegalAccessException | InvocationTargetException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public Object getListener() {
        return method;
    }

    @Override
    public ListenerPriority getPriority() {
        return listenerAnnotation.priority();
    }

    @Override
    public Iterable<EventFilter> getFilters() {
        return eventFilters;
    }

    @Override
    public int compareTo(final EventHandler eventHandler) {
        return Integer.compare(eventHandler.getPriority().getPriorityLevel(),
                getPriority().getPriorityLevel());
    }
}
