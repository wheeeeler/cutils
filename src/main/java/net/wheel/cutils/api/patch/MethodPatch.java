package net.wheel.cutils.api.patch;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface MethodPatch {

    String mcpName() default "";

    String notchName() default "";

    String mcpDesc() default "";

    String notchDesc() default "";

}
