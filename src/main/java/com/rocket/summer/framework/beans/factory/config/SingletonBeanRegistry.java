package com.rocket.summer.framework.beans.factory.config;

/**
 * Interface that defines a registry for shared bean instances.
 * Can be implemented by {@link com.rocket.summer.framework.beans.factory.BeanFactory}
 * implementations in order to expose their singleton management facility
 * in a uniform manner.
 *
 * <p>The {@link ConfigurableBeanFactory} interface extends this interface.
 *
 * @author Juergen Hoeller
 * @since 2.0
 * @see ConfigurableBeanFactory
 * @see com.rocket.summer.framework.beans.factory.support.DefaultSingletonBeanRegistry
 * @see com.rocket.summer.framework.beans.factory.support.AbstractBeanFactory
 */
public interface SingletonBeanRegistry {

    /**
     * Register the given existing object as singleton in the bean registry,
     * under the given bean name.
     * <p>The given instance is supposed to be fully initialized; the registry
     * will not perform any initialization callbacks (in particular, it won't
     * call InitializingBean's <code>afterPropertiesSet</code> method).
     * The given instance will not receive any destruction callbacks
     * (like DisposableBean's <code>destroy</code> method) either.
     * <p>If running within a full BeanFactory: <b>Register a bean definition
     * instead of an existing instance if your bean is supposed to receive
     * initialization and/or destruction callbacks.</b>
     * <p>Typically invoked during registry configuration, but can also be used
     * for runtime registration of singletons. As a consequence, a registry
     * implementation should synchronize singleton access; it will have to do
     * this anyway if it supports a BeanFactory's lazy initialization of singletons.
     * @param beanName the name of the bean
     * @param singletonObject the existing singleton object
     * @see com.rocket.summer.framework.beans.factory.InitializingBean#afterPropertiesSet
     * @see com.rocket.summer.framework.beans.factory.DisposableBean#destroy
     * @see com.rocket.summer.framework.beans.factory.support.BeanDefinitionRegistry#registerBeanDefinition
     */
    void registerSingleton(String beanName, Object singletonObject);

    /**
     * Return the (raw) singleton object registered under the given name.
     * <p>Only checks already instantiated singletons; does not return an Object
     * for singleton bean definitions which have not been instantiated yet.
     * <p>The main purpose of this method is to access manually registered singletons
     * (see {@link #registerSingleton}). Can also be used to access a singleton
     * defined by a bean definition that already been created, in a raw fashion.
     * @param beanName the name of the bean to look for
     * @return the registered singleton object, or <code>null</code> if none found
     * @see ConfigurableListableBeanFactory#getBeanDefinition
     */
    Object getSingleton(String beanName);

    /**
     * Check if this registry contains a singleton instance with the given name.
     * <p>Only checks already instantiated singletons; does not return <code>true</code>
     * for singleton bean definitions which have not been instantiated yet.
     * <p>The main purpose of this method is to check manually registered singletons
     * (see {@link #registerSingleton}). Can also be used to check whether a
     * singleton defined by a bean definition has already been created.
     * <p>To check whether a bean factory contains a bean definition with a given name,
     * use ListableBeanFactory's <code>containsBeanDefinition</code>. Calling both
     * <code>containsBeanDefinition</code> and <code>containsSingleton</code> answers
     * whether a specific bean factory contains an own bean with the given name.
     * <p>Use BeanFactory's <code>containsBean</code> for general checks whether the
     * factory knows about a bean with a given name (whether manually registered singleton
     * instance or created by bean definition), also checking ancestor factories.
     * @param beanName the name of the bean to look for
     * @return if this bean factory contains a singleton instance with the given name
     * @see #registerSingleton
     * @see com.rocket.summer.framework.beans.factory.ListableBeanFactory#containsBeanDefinition
     * @see com.rocket.summer.framework.beans.factory.BeanFactory#containsBean
     */
    boolean containsSingleton(String beanName);

    /**
     * Return the names of singleton beans registered in this registry.
     * <p>Only checks already instantiated singletons; does not return names
     * for singleton bean definitions which have not been instantiated yet.
     * <p>The main purpose of this method is to check manually registered singletons
     * (see {@link #registerSingleton}). Can also be used to check which
     * singletons defined by a bean definition have already been created.
     * @return the list of names as String array (never <code>null</code>)
     * @see #registerSingleton
     * @see com.rocket.summer.framework.beans.factory.support.BeanDefinitionRegistry#getBeanDefinitionNames
     * @see com.rocket.summer.framework.beans.factory.ListableBeanFactory#getBeanDefinitionNames
     */
    String[] getSingletonNames();

    /**
     * Return the number of singleton beans registered in this registry.
     * <p>Only checks already instantiated singletons; does not count
     * singleton bean definitions which have not been instantiated yet.
     * <p>The main purpose of this method is to check manually registered singletons
     * (see {@link #registerSingleton}). Can also be used to count the number of
     * singletons defined by a bean definition that have already been created.
     * @return the number of singleton beans
     * @see #registerSingleton
     * @see com.rocket.summer.framework.beans.factory.support.BeanDefinitionRegistry#getBeanDefinitionCount
     * @see com.rocket.summer.framework.beans.factory.ListableBeanFactory#getBeanDefinitionCount
     */
    int getSingletonCount();

}
