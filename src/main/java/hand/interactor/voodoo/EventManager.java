package hand.interactor.voodoo;

public interface EventManager {

    <E> E dispatchEvent(E event);

    boolean isRegisteredListener(Object listener);

    boolean addEventListener(Object listener);

    boolean removeEventListener(Object listener);
}
