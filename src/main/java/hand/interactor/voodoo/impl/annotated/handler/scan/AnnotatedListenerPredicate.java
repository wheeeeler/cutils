package hand.interactor.voodoo.impl.annotated.handler.scan;

import java.lang.reflect.Method;
import java.util.function.Predicate;

import hand.interactor.voodoo.impl.annotated.handler.annotation.Listener;

public final class AnnotatedListenerPredicate implements Predicate<Method> {

    @Override
    public boolean test(final Method method) {
        return method.isAnnotationPresent(Listener.class) &&
                method.getParameterCount() == 1;
    }
}
