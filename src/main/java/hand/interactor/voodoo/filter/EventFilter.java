package hand.interactor.voodoo.filter;

import hand.interactor.voodoo.handler.EventHandler;

public interface EventFilter<E> {

    boolean test(EventHandler eventHandler, E event);
}
