package com.rocket.summer.framework.context.annotation;

import com.rocket.summer.framework.core.annotation.AnnotatedElementUtils;

import java.lang.reflect.Method;

/**
 * Utilities for processing {@link Bean}-annotated methods.
 *
 * @author Chris Beams
 * @author Juergen Hoeller
 * @since 3.1
 */
class BeanAnnotationHelper {

    public static boolean isBeanAnnotated(Method method) {
        return AnnotatedElementUtils.hasAnnotation(method, Bean.class);
    }

    public static String determineBeanNameFor(Method beanMethod) {
        // By default, the bean name is the name of the @Bean-annotated method
        String beanName = beanMethod.getName();
        // Check to see if the user has explicitly set a custom bean name...
        Bean bean = AnnotatedElementUtils.findMergedAnnotation(beanMethod, Bean.class);
        if (bean != null) {
            String[] names = bean.name();
            if (names.length > 0) {
                beanName = names[0];
            }
        }
        return beanName;
    }

}
