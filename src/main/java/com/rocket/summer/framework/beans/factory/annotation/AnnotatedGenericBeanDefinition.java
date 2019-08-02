package com.rocket.summer.framework.beans.factory.annotation;

import com.rocket.summer.framework.beans.factory.support.GenericBeanDefinition;
import com.rocket.summer.framework.core.type.AnnotationMetadata;
import com.rocket.summer.framework.core.type.MethodMetadata;
import com.rocket.summer.framework.core.type.StandardAnnotationMetadata;
import com.rocket.summer.framework.util.Assert;

/**
 * Extension of the {@link com.rocket.summer.framework.beans.factory.support.GenericBeanDefinition}
 * class, adding support for annotation metadata exposed through the
 * {@link AnnotatedBeanDefinition} interface.
 *
 * <p>This GenericBeanDefinition variant is mainly useful for testing code that expects
 * to operate on an AnnotatedBeanDefinition, for example strategy implementations
 * in Spring's component scanning support (where the default definition class is
 * {@link com.rocket.summer.framework.context.annotation.ScannedGenericBeanDefinition},
 * which also implements the AnnotatedBeanDefinition interface).
 *
 * @author Juergen Hoeller
 * @since 2.5
 * @see AnnotatedBeanDefinition#getMetadata()
 * @see com.rocket.summer.framework.core.type.StandardAnnotationMetadata
 */
public class AnnotatedGenericBeanDefinition extends GenericBeanDefinition implements AnnotatedBeanDefinition {

    private final AnnotationMetadata metadata;

    private MethodMetadata factoryMethodMetadata;


    /**
     * Create a new AnnotatedGenericBeanDefinition for the given bean class.
     * @param beanClass the loaded bean class
     */
    public AnnotatedGenericBeanDefinition(Class<?> beanClass) {
        setBeanClass(beanClass);
        this.metadata = new StandardAnnotationMetadata(beanClass, true);
    }

    /**
     * Create a new AnnotatedGenericBeanDefinition for the given annotation metadata,
     * allowing for ASM-based processing and avoidance of early loading of the bean class.
     * Note that this constructor is functionally equivalent to
     * {@link com.rocket.summer.framework.context.annotation.ScannedGenericBeanDefinition
     * ScannedGenericBeanDefinition}, however the semantics of the latter indicate that a
     * bean was discovered specifically via component-scanning as opposed to other means.
     * @param metadata the annotation metadata for the bean class in question
     * @since 3.1.1
     */
    public AnnotatedGenericBeanDefinition(AnnotationMetadata metadata) {
        Assert.notNull(metadata, "AnnotationMetadata must not be null");
        if (metadata instanceof StandardAnnotationMetadata) {
            setBeanClass(((StandardAnnotationMetadata) metadata).getIntrospectedClass());
        }
        else {
            setBeanClassName(metadata.getClassName());
        }
        this.metadata = metadata;
    }


    @Override
    public final AnnotationMetadata getMetadata() {
        return this.metadata;
    }

    @Override
    public final MethodMetadata getFactoryMethodMetadata() {
        return this.factoryMethodMetadata;
    }

}