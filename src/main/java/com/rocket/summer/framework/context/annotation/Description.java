package com.rocket.summer.framework.context.annotation;

import java.lang.annotation.*;

/**
 * Adds a textual description to bean definitions derived from
 * {@link com.rocket.summer.framework.stereotype.Component} or {@link Bean}.
 *
 * @author Juergen Hoeller
 * @since 4.0
 * @see com.rocket.summer.framework.beans.factory.config.BeanDefinition#getDescription()
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Description {

    /**
     * The textual description to associate with the bean definition.
     */
    String value();

}
