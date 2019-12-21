package com.rocket.summer.framework.context.annotation;

import com.rocket.summer.framework.beans.factory.config.BeanDefinition;
import com.rocket.summer.framework.stereotype.Component;

import java.lang.annotation.*;

/**
 * When used as a type-level annotation in conjunction with the {@link Component}
 * annotation, indicates the name of a scope to use for instances of the annotated
 * type.
 *
 * <p>When used as a method-level annotation in conjunction with the
 * {@link Bean} annotation, indicates the name of a scope to use for
 * the instance returned from the method.
 *
 * <p>In this context, scope means the lifecycle of an instance, such as
 * {@literal singleton}, {@literal prototype}, and so forth.
 *
 * @author Mark Fisher
 * @author Chris Beams
 * @since 2.5
 * @see Component
 * @see Bean
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Scope {

    /**
     * Specifies the scope to use for the annotated component/bean.
     * @return the specified scope
     */
    String value() default BeanDefinition.SCOPE_SINGLETON;

    /**
     * Specifies whether a component should be configured as a scoped proxy
     * and if so, whether the proxy should be interface-based or subclass-based.
     * <p>Defaults to {@link ScopedProxyMode#NO}, indicating that no scoped
     * proxy should be created.
     * <p>Analogous to {@literal <aop:scoped-proxy/>} support in Spring XML.
     */
    ScopedProxyMode proxyMode() default ScopedProxyMode.DEFAULT;

}