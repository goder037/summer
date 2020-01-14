package com.rocket.summer.framework.web.context.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.rocket.summer.framework.context.annotation.Scope;
import com.rocket.summer.framework.context.annotation.ScopedProxyMode;
import com.rocket.summer.framework.core.annotation.AliasFor;
import com.rocket.summer.framework.web.context.WebApplicationContext;

/**
 * {@code @RequestScope} is a specialization of {@link Scope @Scope} for a
 * component whose lifecycle is bound to the current web request.
 *
 * <p>Specifically, {@code @RequestScope} is a <em>composed annotation</em> that
 * acts as a shortcut for {@code @Scope("request")} with the default
 * {@link #proxyMode} set to {@link ScopedProxyMode#TARGET_CLASS TARGET_CLASS}.
 *
 * <p>{@code @RequestScope} may be used as a meta-annotation to create custom
 * composed annotations.
 *
 * @author Sam Brannen
 * @since 4.3
 * @see SessionScope
 * @see ApplicationScope
 * @see com.rocket.summer.framework.context.annotation.Scope
 * @see com.rocket.summer.framework.web.context.WebApplicationContext#SCOPE_REQUEST
 * @see com.rocket.summer.framework.web.context.request.RequestScope
 * @see com.rocket.summer.framework.stereotype.Component
 * @see com.rocket.summer.framework.context.annotation.Bean
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Scope(WebApplicationContext.SCOPE_REQUEST)
public @interface RequestScope {

    /**
     * Alias for {@link Scope#proxyMode}.
     * <p>Defaults to {@link ScopedProxyMode#TARGET_CLASS}.
     */
    @AliasFor(annotation = Scope.class)
    ScopedProxyMode proxyMode() default ScopedProxyMode.TARGET_CLASS;

}

