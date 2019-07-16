package com.rocket.summer.framework.beans.factory.annotation;

import com.rocket.summer.framework.beans.PropertyValues;
import com.rocket.summer.framework.beans.factory.BeanInitializationException;
import com.rocket.summer.framework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter;
import com.rocket.summer.framework.context.BeansException;
import com.rocket.summer.framework.core.Ordered;
import com.rocket.summer.framework.core.PriorityOrdered;
import com.rocket.summer.framework.core.annotation.AnnotationUtils;
import com.rocket.summer.framework.util.Assert;

import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;

/**
 * {@link org.springframework.beans.factory.config.BeanPostProcessor} implementation
 * that enforces required JavaBean properties to have been configured.
 * Required bean properties are detected through a Java 5 annotation:
 * by default, Spring's {@link Required} annotation.
 *
 * <p>The motivation for the existence of this BeanPostProcessor is to allow
 * developers to annotate the setter properties of their own classes with an
 * arbitrary JDK 1.5 annotation to indicate that the container must check
 * for the configuration of a dependency injected value. This neatly pushes
 * responsibility for such checking onto the container (where it arguably belongs),
 * and obviates the need (<b>in part</b>) for a developer to code a method that
 * simply checks that all required properties have actually been set.
 *
 * <p>Please note that an 'init' method may still need to implemented (and may
 * still be desirable), because all that this class does is enforce that a
 * 'required' property has actually been configured with a value. It does
 * <b>not</b> check anything else... In particular, it does not check that a
 * configured value is not <code>null</code>.
 *
 * <p>Note: A default RequiredAnnotationBeanPostProcessor will be registered
 * by the "context:annotation-config" and "context:component-scan" XML tags.
 * Remove or turn off the default annotation configuration there if you intend
 * to specify a custom RequiredAnnotationBeanPostProcessor bean definition.
 *
 * @author Rob Harrop
 * @author Juergen Hoeller
 * @since 2.0
 * @see #setRequiredAnnotationType
 * @see Required
 */
public class RequiredAnnotationBeanPostProcessor extends InstantiationAwareBeanPostProcessorAdapter
        implements PriorityOrdered {

    private Class<? extends Annotation> requiredAnnotationType = Required.class;

    private int order = Ordered.LOWEST_PRECEDENCE - 1;

    /** Cache for validated bean names, skipping re-validation for the same bean */
    private final Set<String> validatedBeanNames = Collections.synchronizedSet(new HashSet<String>());


    /**
     * Set the 'required' annotation type, to be used on bean property
     * setter methods.
     * <p>The default required annotation type is the Spring-provided
     * {@link Required} annotation.
     * <p>This setter property exists so that developers can provide their own
     * (non-Spring-specific) annotation type to indicate that a property value
     * is required.
     */
    public void setRequiredAnnotationType(Class<? extends Annotation> requiredAnnotationType) {
        Assert.notNull(requiredAnnotationType, "'requiredAnnotationType' must not be null");
        this.requiredAnnotationType = requiredAnnotationType;
    }

    /**
     * Return the 'required' annotation type.
     */
    protected Class<? extends Annotation> getRequiredAnnotationType() {
        return this.requiredAnnotationType;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public int getOrder() {
        return this.order;
    }


    public PropertyValues postProcessPropertyValues(
            PropertyValues pvs, PropertyDescriptor[] pds, Object bean, String beanName)
            throws BeansException {

        if (!this.validatedBeanNames.contains(beanName)) {
            List<String> invalidProperties = new ArrayList<String>();
            for (PropertyDescriptor pd : pds) {
                if (isRequiredProperty(pd) && !pvs.contains(pd.getName())) {
                    invalidProperties.add(pd.getName());
                }
            }
            if (!invalidProperties.isEmpty()) {
                throw new BeanInitializationException(buildExceptionMessage(invalidProperties, beanName));
            }
            this.validatedBeanNames.add(beanName);
        }
        return pvs;
    }

    /**
     * Is the supplied property required to have a value (that is, to be dependency-injected)?
     * <p>This implementation looks for the existence of a
     * {@link #setRequiredAnnotationType "required" annotation}
     * on the supplied {@link PropertyDescriptor property}.
     * @param propertyDescriptor the target PropertyDescriptor (never <code>null</code>)
     * @return <code>true</code> if the supplied property has been marked as being required;
     * <code>false</code> if not, or if the supplied property does not have a setter method
     */
    protected boolean isRequiredProperty(PropertyDescriptor propertyDescriptor) {
        Method setter = propertyDescriptor.getWriteMethod();
        return (setter != null && AnnotationUtils.getAnnotation(setter, getRequiredAnnotationType()) != null);
    }

    /**
     * Build an exception message for the given list of invalid properties.
     * @param invalidProperties the list of names of invalid properties
     * @param beanName the name of the bean
     * @return the exception message
     */
    private String buildExceptionMessage(List<String> invalidProperties, String beanName) {
        int size = invalidProperties.size();
        StringBuilder sb = new StringBuilder();
        sb.append(size == 1 ? "Property" : "Properties");
        for (int i = 0; i < size; i++) {
            String propertyName = invalidProperties.get(i);
            if (i > 0) {
                if (i == (size - 1)) {
                    sb.append(" and");
                }
                else {
                    sb.append(",");
                }
            }
            sb.append(" '").append(propertyName).append("'");
        }
        sb.append(size == 1 ? " is" : " are");
        sb.append(" required for bean '").append(beanName).append("'");
        return sb.toString();
    }

}