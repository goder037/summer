package com.rocket.summer.framework.beans.factory.config;

import com.rocket.summer.framework.beans.BeanMetadataElement;

/**
 * Interface that exposes a reference to a bean name in an abstract fashion.
 * This interface does not necessarily imply a reference to an actual bean
 * instance; it just expresses a logical reference to the name of a bean.
 *
 * <p>Serves as common interface implemented by any kind of bean reference
 * holder, such as {@link RuntimeBeanReference RuntimeBeanReference} and
 * {@link RuntimeBeanNameReference RuntimeBeanNameReference}.
 *
 * @author Juergen Hoeller
 * @since 2.0
 */
public interface BeanReference extends BeanMetadataElement {

    /**
     * Return the target bean name that this reference points to (never <code>null</code>).
     */
    String getBeanName();

}
