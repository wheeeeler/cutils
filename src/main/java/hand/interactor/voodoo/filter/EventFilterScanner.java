package hand.interactor.voodoo.filter;

import java.util.Set;

public interface EventFilterScanner<T> {

    Set<EventFilter> scan(T listener);
}
