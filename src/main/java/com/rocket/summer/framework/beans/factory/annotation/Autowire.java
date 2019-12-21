package com.rocket.summer.framework.beans.factory.annotation;

import com.rocket.summer.framework.beans.factory.config.AutowireCapableBeanFactory;

/**
 * Enumeration determining autowiring status: that is, whether a bean should
 * have its dependencies automatically injected by the Spring container using
 * setter injection. This is a core concept in Spring DI.
 *
 * <p>Available for use in annotation-based configurations, such as for the
 * AspectJ AnnotationBeanConfigurer aspect.
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @since 2.0
 * @see com.rocket.summer.framework.beans.factory.annotation.Configurable
 * @see com.rocket.summer.framework.beans.factory.config.AutowireCapableBeanFactory
 */
public enum Autowire {

    /**
     * Constant that indicates no autowiring at all.
     */
    NO(AutowireCapableBeanFactory.AUTOWIRE_NO),

    /**
     * Constant that indicates autowiring bean properties by name.
     */
    BY_NAME(AutowireCapableBeanFactory.AUTOWIRE_BY_NAME),

    /**
     * Constant that indicates autowiring bean properties by type.
     */
    BY_TYPE(AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE);


    private final int value;


    Autowire(int value) {
        this.value = value;
    }

    public int value() {
        return this.value;
    }

    /**
     * Return whether this represents an actual autowiring value.
     * @return whether actual autowiring was specified
     * (either BY_NAME or BY_TYPE)
     */
    public boolean isAutowire() {
        return (this == BY_NAME || this == BY_TYPE);
    }

}

