package com.rocket.summer.framework.boot.autoconfigure;

import com.rocket.summer.framework.context.annotation.AnnotationConfigApplicationContext;
import com.rocket.summer.framework.core.Ordered;
import com.rocket.summer.framework.core.annotation.Order;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Auto-configuration specific variant of Spring Framework's {@link Order} annotation.
 * Allows auto-configuration classes to be ordered among themselves without affecting the
 * order of configuration classes passed to
 * {@link AnnotationConfigApplicationContext#register(Class...)}.
 *
 * @author Andy Wilkinson
 * @since 1.3.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.METHOD, ElementType.FIELD })
public @interface AutoConfigureOrder {

    /**
     * The order value. Default is {@link Ordered#LOWEST_PRECEDENCE}.
     * @see Ordered#getOrder()
     * @return the order value
     */
    int value() default Ordered.LOWEST_PRECEDENCE;

}