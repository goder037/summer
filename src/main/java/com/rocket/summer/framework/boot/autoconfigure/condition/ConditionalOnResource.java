package com.rocket.summer.framework.boot.autoconfigure.condition;

import com.rocket.summer.framework.context.annotation.Conditional;

import java.lang.annotation.*;

/**
 * {@link Conditional} that only matches when the specified resources are on the
 * classpath.
 *
 * @author Dave Syer
 */
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Conditional(OnResourceCondition.class)
public @interface ConditionalOnResource {

    /**
     * The resources that must be present.
     * @return the resource paths that must be present.
     */
    String[] resources() default {};

}
