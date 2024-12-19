package hand.interactor.voodoo.impl.annotated.handler.scan;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Stream;

import hand.interactor.voodoo.filter.EventFilterScanner;
import hand.interactor.voodoo.handler.EventHandler;
import hand.interactor.voodoo.handler.scan.EventHandlerScanner;
import hand.interactor.voodoo.impl.annotated.filter.MethodFilterScanner;
import hand.interactor.voodoo.impl.annotated.handler.MethodEventHandler;

public final class MethodHandlerScanner implements EventHandlerScanner {
    private final AnnotatedListenerPredicate annotatedListenerPredicate = new AnnotatedListenerPredicate();
    private final EventFilterScanner<Method> filterScanner = new MethodFilterScanner();

    @Override
    public Map<Class<?>, Set<EventHandler>> locate(final Object listenerContainer) {
        final Map<Class<?>, Set<EventHandler>> eventHandlers = new HashMap<>();
        Stream.of(listenerContainer.getClass().getDeclaredMethods())
                .filter(annotatedListenerPredicate).forEach(method -> eventHandlers
                        .computeIfAbsent(method.getParameterTypes()[0], obj -> new TreeSet<>())
                        .add(new MethodEventHandler(listenerContainer, method,
                                filterScanner.scan(method))));
        return eventHandlers;
    }
}
