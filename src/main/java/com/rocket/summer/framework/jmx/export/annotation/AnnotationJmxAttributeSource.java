package com.rocket.summer.framework.jmx.export.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Set;

import com.rocket.summer.framework.beans.BeanUtils;
import com.rocket.summer.framework.beans.annotation.AnnotationBeanUtils;
import com.rocket.summer.framework.beans.factory.BeanFactory;
import com.rocket.summer.framework.beans.factory.BeanFactoryAware;
import com.rocket.summer.framework.beans.factory.config.ConfigurableBeanFactory;
import com.rocket.summer.framework.beans.factory.config.EmbeddedValueResolver;
import com.rocket.summer.framework.core.annotation.AnnotationUtils;
import com.rocket.summer.framework.jmx.export.metadata.InvalidMetadataException;
import com.rocket.summer.framework.jmx.export.metadata.JmxAttributeSource;
import com.rocket.summer.framework.util.StringValueResolver;

/**
 * Implementation of the {@code JmxAttributeSource} interface that
 * reads annotations and exposes the corresponding attributes.
 *
 * @author Rob Harrop
 * @author Juergen Hoeller
 * @author Jennifer Hickey
 * @author Stephane Nicoll
 * @since 1.2
 * @see ManagedResource
 * @see ManagedAttribute
 * @see ManagedOperation
 */
public class AnnotationJmxAttributeSource implements JmxAttributeSource, BeanFactoryAware {

    private StringValueResolver embeddedValueResolver;


    @Override
    public void setBeanFactory(BeanFactory beanFactory) {
        if (beanFactory instanceof ConfigurableBeanFactory) {
            this.embeddedValueResolver = new EmbeddedValueResolver((ConfigurableBeanFactory) beanFactory);
        }
    }


    @Override
    public com.rocket.summer.framework.jmx.export.metadata.ManagedResource getManagedResource(Class<?> beanClass) throws InvalidMetadataException {
        ManagedResource ann = AnnotationUtils.findAnnotation(beanClass, ManagedResource.class);
        if (ann == null) {
            return null;
        }
        Class<?> declaringClass = AnnotationUtils.findAnnotationDeclaringClass(ManagedResource.class, beanClass);
        Class<?> target = (declaringClass != null && !declaringClass.isInterface() ? declaringClass : beanClass);
        if (!Modifier.isPublic(target.getModifiers())) {
            throw new InvalidMetadataException("@ManagedResource class '" + target.getName() + "' must be public");
        }
        com.rocket.summer.framework.jmx.export.metadata.ManagedResource managedResource = new com.rocket.summer.framework.jmx.export.metadata.ManagedResource();
        AnnotationBeanUtils.copyPropertiesToBean(ann, managedResource, this.embeddedValueResolver);
        return managedResource;
    }

    @Override
    public com.rocket.summer.framework.jmx.export.metadata.ManagedAttribute getManagedAttribute(Method method) throws InvalidMetadataException {
        ManagedAttribute ann = AnnotationUtils.findAnnotation(method, ManagedAttribute.class);
        if (ann == null) {
            return null;
        }
        com.rocket.summer.framework.jmx.export.metadata.ManagedAttribute managedAttribute = new com.rocket.summer.framework.jmx.export.metadata.ManagedAttribute();
        AnnotationBeanUtils.copyPropertiesToBean(ann, managedAttribute, "defaultValue");
        if (ann.defaultValue().length() > 0) {
            managedAttribute.setDefaultValue(ann.defaultValue());
        }
        return managedAttribute;
    }

    @Override
    public com.rocket.summer.framework.jmx.export.metadata.ManagedMetric getManagedMetric(Method method) throws InvalidMetadataException {
        ManagedMetric ann = AnnotationUtils.findAnnotation(method, ManagedMetric.class);
        return copyPropertiesToBean(ann, com.rocket.summer.framework.jmx.export.metadata.ManagedMetric.class);
    }

    @Override
    public com.rocket.summer.framework.jmx.export.metadata.ManagedOperation getManagedOperation(Method method) throws InvalidMetadataException {
        ManagedOperation ann = AnnotationUtils.findAnnotation(method, ManagedOperation.class);
        return copyPropertiesToBean(ann, com.rocket.summer.framework.jmx.export.metadata.ManagedOperation.class);
    }

    @Override
    public com.rocket.summer.framework.jmx.export.metadata.ManagedOperationParameter[] getManagedOperationParameters(Method method)
            throws InvalidMetadataException {

        Set<ManagedOperationParameter> anns = AnnotationUtils.getRepeatableAnnotations(
                method, ManagedOperationParameter.class, ManagedOperationParameters.class);
        return copyPropertiesToBeanArray(anns, com.rocket.summer.framework.jmx.export.metadata.ManagedOperationParameter.class);
    }

    @Override
    public com.rocket.summer.framework.jmx.export.metadata.ManagedNotification[] getManagedNotifications(Class<?> clazz)
            throws InvalidMetadataException {

        Set<ManagedNotification> anns = AnnotationUtils.getRepeatableAnnotations(
                clazz, ManagedNotification.class, ManagedNotifications.class);
        return copyPropertiesToBeanArray(anns, com.rocket.summer.framework.jmx.export.metadata.ManagedNotification.class);
    }


    @SuppressWarnings("unchecked")
    private static <T> T[] copyPropertiesToBeanArray(Collection<? extends Annotation> anns, Class<T> beanClass) {
        T[] beans = (T[]) Array.newInstance(beanClass, anns.size());
        int i = 0;
        for (Annotation ann : anns) {
            beans[i++] = copyPropertiesToBean(ann, beanClass);
        }
        return beans;
    }

    private static <T> T copyPropertiesToBean(Annotation ann, Class<T> beanClass) {
        if (ann == null) {
            return null;
        }
        T bean = BeanUtils.instantiateClass(beanClass);
        AnnotationBeanUtils.copyPropertiesToBean(ann, bean);
        return bean;
    }

}

