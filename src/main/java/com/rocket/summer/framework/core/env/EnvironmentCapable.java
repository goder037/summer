package com.rocket.summer.framework.core.env;

/**
 * Interface indicating a component contains and makes available an {@link Environment} object.
 *
 * <p>All Spring application contexts are EnvironmentCapable, and the interface is used primarily
 * for performing {@code instanceof} checks in framework methods that accept BeanFactory
 * instances that may or may not actually be ApplicationContext instances in order to interact
 * with the environment if indeed it is available.
 *
 * <p>As mentioned, {@link com.rocket.summer.framework.context.ApplicationContext ApplicationContext}
 * extends EnvironmentCapable, and thus exposes a {@link #getEnvironment()} method; however,
 * {@link com.rocket.summer.framework.context.ConfigurableApplicationContext ConfigurableApplicationContext}
 * redefines {@link com.rocket.summer.framework.context.ConfigurableApplicationContext#getEnvironment
 * getEnvironment()} and narrows the signature to return a {@link ConfigurableEnvironment}. The effect
 * is that an Environment object is 'read-only' until it accessed from a ConfigurableApplicationContext,
 * at which point it too may be configured.
 *
 * @author Chris Beams
 * @since 3.1
 * @see Environment
 * @see ConfigurableEnvironment
 * @see com.rocket.summer.framework.context.ConfigurableApplicationContext#getEnvironment
 */
public interface EnvironmentCapable {

    /**
     * Return the Environment for this object
     */
    Environment getEnvironment();

}
