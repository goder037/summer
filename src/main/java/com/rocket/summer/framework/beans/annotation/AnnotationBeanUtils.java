package com.rocket.summer.framework.beans.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.rocket.summer.framework.beans.BeanWrapper;
import com.rocket.summer.framework.beans.PropertyAccessorFactory;
import com.rocket.summer.framework.util.ReflectionUtils;
import com.rocket.summer.framework.util.StringValueResolver;

/**
 * General utility methods for working with annotations in JavaBeans style.
 *
 * @author Rob Harrop
 * @author Juergen Hoeller
 * @since 2.0
 */
public abstract class AnnotationBeanUtils {

    /**
     * Copy the properties of the supplied {@link Annotation} to the supplied target bean.
     * Any properties defined in {@code excludedProperties} will not be copied.
     * @param ann the annotation to copy from
     * @param bean the bean instance to copy to
     * @param excludedProperties the names of excluded properties, if any
     * @see com.rocket.summer.framework.beans.BeanWrapper
     */
    public static void copyPropertiesToBean(Annotation ann, Object bean, String... excludedProperties) {
        copyPropertiesToBean(ann, bean, null, excludedProperties);
    }

    /**
     * Copy the properties of the supplied {@link Annotation} to the supplied target bean.
     * Any properties defined in {@code excludedProperties} will not be copied.
     * <p>A specified value resolver may resolve placeholders in property values, for example.
     * @param ann the annotation to copy from
     * @param bean the bean instance to copy to
     * @param valueResolver a resolve to post-process String property values (may be {@code null})
     * @param excludedProperties the names of excluded properties, if any
     * @see com.rocket.summer.framework.beans.BeanWrapper
     */
    public static void copyPropertiesToBean(Annotation ann, Object bean, StringValueResolver valueResolver,
                                            String... excludedProperties) {

        Set<String> excluded = (excludedProperties.length == 0 ? Collections.<String>emptySet() :
                new HashSet<String>(Arrays.asList(excludedProperties)));
        Method[] annotationProperties = ann.annotationType().getDeclaredMethods();
        BeanWrapper bw = PropertyAccessorFactory.forBeanPropertyAccess(bean);
        for (Method annotationProperty : annotationProperties) {
            String propertyName = annotationProperty.getName();
            if (!excluded.contains(propertyName) && bw.isWritableProperty(propertyName)) {
                Object value = ReflectionUtils.invokeMethod(annotationProperty, ann);
                if (valueResolver != null && value instanceof String) {
                    value = valueResolver.resolveStringValue((String) value);
                }
                bw.setPropertyValue(propertyName, value);
            }
        }
    }

}

