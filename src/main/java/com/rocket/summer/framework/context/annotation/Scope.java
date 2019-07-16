package com.rocket.summer.framework.context.annotation;

import com.rocket.summer.framework.beans.factory.config.BeanDefinition;

import java.lang.annotation.*;

/**
 * Indicates the name of a scope to use for instances of the annotated class.
 *
 * <p>In this context, scope means the lifecycle of an instance, such as
 * '<code>singleton</code>', '<code>prototype</code>', and so forth.
 *
 * @author Mark Fisher
 * @since 2.5
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Scope {

    /**
     * Specifies the scope to use for instances of the annotated class.
     * @return the desired scope
     */
    String value() default BeanDefinition.SCOPE_SINGLETON;


}
