package net.minecraft.gametest.framework;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface GameTest {
   int timeoutTicks() default 100;

   String batch() default "defaultBatch";

   int rotationSteps() default 0;

   boolean required() default true;

   /**
    * The namespace of where to grab the structure from, generally a mod id.
    */
   String templateNamespace() default "";

   String template() default "";

   long setupTicks() default 0L;

   int attempts() default 1;

   int requiredSuccesses() default 1;
}
