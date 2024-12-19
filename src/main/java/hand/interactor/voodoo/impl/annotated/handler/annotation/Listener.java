package hand.interactor.voodoo.impl.annotated.handler.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import hand.interactor.voodoo.filter.EventFilter;
import hand.interactor.voodoo.handler.ListenerPriority;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Listener {

    Class<? extends EventFilter>[] filters() default {};

    ListenerPriority priority() default ListenerPriority.NORMAL;
}
