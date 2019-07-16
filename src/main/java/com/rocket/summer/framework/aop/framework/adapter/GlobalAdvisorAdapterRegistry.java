package com.rocket.summer.framework.aop.framework.adapter;

/**
 * Singleton to publish a shared DefaultAdvisorAdapterRegistry instance.
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @see DefaultAdvisorAdapterRegistry
 */
public abstract class GlobalAdvisorAdapterRegistry {

    /**
     * Keep track of a single instance so we can return it to classes that request it.
     */
    private static final AdvisorAdapterRegistry instance = new DefaultAdvisorAdapterRegistry();

    /**
     * Return the singleton DefaultAdvisorAdapterRegistry instance.
     */
    public static AdvisorAdapterRegistry getInstance() {
        return instance;
    }

}
