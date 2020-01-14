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
 * {@code @SessionScope} is a specialization of {@link Scope @Scope} for a
 * component whose lifecycle is bound to the current web session.
 *
 * <p>Specifically, {@code @SessionScope} is a <em>composed annotation</em> that
 * acts as a shortcut for {@code @Scope("session")} with the default
 * {@link #proxyMode} set to {@link ScopedProxyMode#TARGET_CLASS TARGET_CLASS}.
 *
 * <p>{@code @SessionScope} may be used as a meta-annotation to create custom
 * composed annotations.
 *
 * @author Sam Brannen
 * @since 4.3
 * @see RequestScope
 * @see ApplicationScope
 * @see com.rocket.summer.framework.context.annotation.Scope
 * @see com.rocket.summer.framework.web.context.WebApplicationContext#SCOPE_SESSION
 * @see com.rocket.summer.framework.web.context.request.SessionScope
 * @see com.rocket.summer.framework.stereotype.Component
 * @see com.rocket.summer.framework.context.annotation.Bean
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Scope(WebApplicationContext.SCOPE_SESSION)
public @interface SessionScope {

    /**
     * Alias for {@link Scope#proxyMode}.
     * <p>Defaults to {@link ScopedProxyMode#TARGET_CLASS}.
     */
    @AliasFor(annotation = Scope.class)
    ScopedProxyMode proxyMode() default ScopedProxyMode.TARGET_CLASS;

}

