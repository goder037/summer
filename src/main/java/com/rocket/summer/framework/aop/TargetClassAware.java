package com.rocket.summer.framework.aop;

/**
 * Minimal interface for exposing the target class behind a proxy.
 *
 * <p>Implemented by AOP proxy objects and proxy factories
 * (via {@link com.rocket.summer.framework.aop.framework.Advised}}
 * as well as by {@link TargetSource TargetSources}.
 *
 * @author Juergen Hoeller
 * @since 2.0.3
 * @see com.rocket.summer.framework.aop.support.AopUtils#getTargetClass(Object)
 */
public interface TargetClassAware {

    /**
     * Return the target class behind the implementing object
     * (typically a proxy configuration or an actual proxy).
     * @return the target Class, or <code>null</code> if not known
     */
    Class getTargetClass();

}

