package com.rocket.summer.framework.beans.factory.annotation;


import com.rocket.summer.framework.beans.factory.config.BeanDefinition;
import com.rocket.summer.framework.core.type.AnnotationMetadata;
import com.rocket.summer.framework.core.type.MethodMetadata;

/**
 * Extended {@link com.rocket.summer.framework.beans.factory.config.BeanDefinition}
 * interface that exposes {@link com.rocket.summer.framework.core.type.AnnotationMetadata}
 * about its bean class - without requiring the class to be loaded yet.
 *
 * @author Juergen Hoeller
 * @since 2.5
 * @see AnnotatedGenericBeanDefinition
 * @see com.rocket.summer.framework.core.type.AnnotationMetadata
 */
public interface AnnotatedBeanDefinition extends BeanDefinition {

    /**
     * Obtain the annotation metadata (as well as basic class metadata)
     * for this bean definition's bean class.
     * @return the annotation metadata object (never <code>null</code>)
     */
    AnnotationMetadata getMetadata();

    /**
     * Obtain metadata for this bean definition's factory method, if any.
     * @return the factory method metadata, or {@code null} if none
     * @since 4.1.1
     */
    MethodMetadata getFactoryMethodMetadata();

}
