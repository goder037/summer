package com.rocket.summer.framework.beans.factory.parsing;

import com.rocket.summer.framework.beans.factory.config.BeanDefinition;
import com.rocket.summer.framework.beans.factory.config.BeanReference;

/**
 * Base implementation of {@link ComponentDefinition} that provides a basic implementation of
 * {@link #getDescription} which delegates to {@link #getName}. Also provides a base implementation
 * of {@link #toString} which delegates to {@link #getDescription} in keeping with the recommended
 * implementation strategy. Also provides default implementations of {@link #getInnerBeanDefinitions}
 * and {@link #getBeanReferences} that return an empty array.
 *
 * @author Rob Harrop
 * @author Juergen Hoeller
 * @since 2.0
 */
public abstract class AbstractComponentDefinition implements ComponentDefinition {
    /**
     * Delegates to {@link #getName}.
     */
    public String getDescription() {
        return getName();
    }

    /**
     * Returns an empty array.
     */
    public BeanDefinition[] getBeanDefinitions() {
        return new BeanDefinition[0];
    }

    /**
     * Returns an empty array.
     */
    public BeanDefinition[] getInnerBeanDefinitions() {
        return new BeanDefinition[0];
    }

    /**
     * Returns an empty array.
     */
    public BeanReference[] getBeanReferences() {
        return new BeanReference[0];
    }

    /**
     * Delegates to {@link #getDescription}.
     */
    public String toString() {
        return getDescription();
    }

}
