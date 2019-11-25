package com.rocket.summer.framework.data.projection;

import com.rocket.summer.framework.aop.RawTargetAccess;
import com.rocket.summer.framework.core.DecoratingProxy;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Extension of {@link com.rocket.summer.framework.aop.TargetClassAware} to be able to ignore the getter on JSON rendering.
 *
 * @author Oliver Gierke
 */
public interface TargetAware extends com.rocket.summer.framework.aop.TargetClassAware, RawTargetAccess {

    /**
     * Returns the type of the proxy target.
     *
     * @return will never be {@literal null}.
     */
    @JsonIgnore
    Class<?> getTargetClass();

    /**
     * Returns the proxy target.
     *
     * @return will never be {@literal null}.
     */
    @JsonIgnore
    Object getTarget();

    /**
     * Re-declaration of Spring Framework 4.3's {@link DecoratingProxy#getDecoratedClass()} so that we can exclude it from
     * Jackson serialization.
     *
     * @return
     */
    @JsonIgnore
    Class<?> getDecoratedClass();
}

