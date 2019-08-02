package com.rocket.summer.framework.boot.autoconfigure.condition;

import com.rocket.summer.framework.context.annotation.Conditional;

import java.lang.annotation.*;

/**
 * {@link Conditional} that only matches when the specified classes are not on the
 * classpath.
 *
 * @author Dave Syer
 */
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Conditional(OnClassCondition.class)
public @interface ConditionalOnMissingClass {

    /**
     * The names of the classes that must not be present.
     * @return the names of the classes that must not be present
     */
    String[] value() default {};

}
