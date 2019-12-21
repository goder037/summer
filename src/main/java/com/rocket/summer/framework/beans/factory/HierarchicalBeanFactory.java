package com.rocket.summer.framework.beans.factory;

/**
 * Sub-interface implemented by bean factories that can be part
 * of a hierarchy.
 *
 * <p>The corresponding <code>setParentBeanFactory</code> method for bean
 * factories that allow setting the parent in a configurable
 * fashion can be found in the ConfigurableBeanFactory interface.
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @since 07.07.2003
 * @see com.rocket.summer.framework.beans.factory.config.ConfigurableBeanFactory#setParentBeanFactory
 */
public interface HierarchicalBeanFactory extends BeanFactory {

    /**
     * Return the parent bean factory, or <code>null</code> if there is none.
     */
    BeanFactory getParentBeanFactory();

    /**
     * Return whether the local bean factory contains a bean of the given name,
     * ignoring beans defined in ancestor contexts.
     * <p>This is an alternative to <code>containsBean</code>, ignoring a bean
     * of the given name from an ancestor bean factory.
     * @param name the name of the bean to query
     * @return whether a bean with the given name is defined in the local factory
     * @see com.rocket.summer.framework.beans.factory.BeanFactory#containsBean
     */
    boolean containsLocalBean(String name);



}
