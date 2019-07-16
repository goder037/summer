package com.rocket.summer.framework.beans.factory.annotation;

import com.rocket.summer.framework.beans.factory.support.GenericBeanDefinition;
import com.rocket.summer.framework.core.type.AnnotationMetadata;
import com.rocket.summer.framework.core.type.StandardAnnotationMetadata;

/**
 * Extension of the {@link org.springframework.beans.factory.support.GenericBeanDefinition}
 * class, adding support for annotation metadata exposed through the
 * {@link AnnotatedBeanDefinition} interface.
 *
 * <p>This GenericBeanDefinition variant is mainly useful for testing code that expects
 * to operate on an AnnotatedBeanDefinition, for example strategy implementations
 * in Spring's component scanning support (where the default definition class is
 * {@link org.springframework.context.annotation.ScannedGenericBeanDefinition},
 * which also implements the AnnotatedBeanDefinition interface).
 *
 * @author Juergen Hoeller
 * @since 2.5
 * @see AnnotatedBeanDefinition#getMetadata()
 * @see org.springframework.core.type.StandardAnnotationMetadata
 */
public class AnnotatedGenericBeanDefinition extends GenericBeanDefinition implements AnnotatedBeanDefinition {

    private final AnnotationMetadata annotationMetadata;


    /**
     * Create a new AnnotatedGenericBeanDefinition for the given bean class.
     * @param beanClass the loaded bean class
     */
    public AnnotatedGenericBeanDefinition(Class beanClass) {
        setBeanClass(beanClass);
        this.annotationMetadata = new StandardAnnotationMetadata(beanClass);
    }


    public final AnnotationMetadata getMetadata() {
        return this.annotationMetadata;
    }

}