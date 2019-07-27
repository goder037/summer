package com.rocket.summer.framework.beans.factory;

/**
 * Callback that allows a bean to be aware of the bean
 * {@link ClassLoader class loader}; that is, the class loader used by the
 * present bean factory to load bean classes.
 *
 * <p>This is mainly intended to be implemented by framework classes which
 * have to pick up application classes by name despite themselves potentially
 * being loaded from a shared class loader.
 *
 * <p>For a list of all bean lifecycle methods, see the
 * {@link BeanFactory BeanFactory javadocs}.
 *
 * @author Juergen Hoeller
 * @since 2.0
 * @see BeanNameAware
 * @see BeanFactoryAware
 * @see InitializingBean
 */
public interface BeanClassLoaderAware {

    /**
     * Callback that supplies the bean {@link ClassLoader class loader} to
     * a bean instance.
     * <p>Invoked <i>after</i> the population of normal bean properties but
     * <i>before</i> an initialization callback such as
     * {@link com.rocket.summer.framework.beans.factory.InitializingBean InitializingBean's}
     * {@link com.rocket.summer.framework.beans.factory.InitializingBean#afterPropertiesSet()}
     * method or a custom init-method.
     * @param classLoader the owning class loader; may be <code>null</code> in
     * which case a default <code>ClassLoader</code> must be used, for example
     * the <code>ClassLoader</code> obtained via
     * {@link com.rocket.summer.framework.util.ClassUtils#getDefaultClassLoader()}
     */
    void setBeanClassLoader(ClassLoader classLoader);

}

