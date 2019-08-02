package com.rocket.summer.framework.boot.context.config;

import com.rocket.summer.framework.beans.BeanUtils;
import com.rocket.summer.framework.context.ApplicationContextException;
import com.rocket.summer.framework.context.ConfigurableApplicationContext;
import com.rocket.summer.framework.core.GenericTypeResolver;
import com.rocket.summer.framework.core.Ordered;
import com.rocket.summer.framework.core.annotation.AnnotationAwareOrderComparator;
import com.rocket.summer.framework.core.env.ConfigurableEnvironment;
import com.rocket.summer.framework.util.Assert;
import com.rocket.summer.framework.util.ClassUtils;
import com.rocket.summer.framework.util.StringUtils;
import com.rocket.summer.framework.context.ApplicationContextInitializer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * {@link ApplicationContextInitializer} that delegates to other initializers that are
 * specified under a {@literal context.initializer.classes} environment property.
 *
 * @author Dave Syer
 * @author Phillip Webb
 */
public class DelegatingApplicationContextInitializer implements
        ApplicationContextInitializer<ConfigurableApplicationContext>, Ordered {

    // NOTE: Similar to org.springframework.web.context.ContextLoader

    private static final String PROPERTY_NAME = "context.initializer.classes";

    private int order = 0;

    @Override
    public void initialize(ConfigurableApplicationContext context) {
        ConfigurableEnvironment environment = context.getEnvironment();
        List<Class<?>> initializerClasses = getInitializerClasses(environment);
        if (!initializerClasses.isEmpty()) {
            applyInitializerClasses(context, initializerClasses);
        }
    }

    private List<Class<?>> getInitializerClasses(ConfigurableEnvironment env) {
        String classNames = env.getProperty(PROPERTY_NAME);
        List<Class<?>> classes = new ArrayList<Class<?>>();
        if (StringUtils.hasLength(classNames)) {
            for (String className : StringUtils.tokenizeToStringArray(classNames, ",")) {
                classes.add(getInitializerClass(className));
            }
        }
        return classes;
    }

    private Class<?> getInitializerClass(String className) throws LinkageError {
        try {
            Class<?> initializerClass = ClassUtils.forName(className,
                    ClassUtils.getDefaultClassLoader());
            Assert.isAssignable(ApplicationContextInitializer.class, initializerClass);
            return initializerClass;
        }
        catch (ClassNotFoundException ex) {
            throw new ApplicationContextException(
                    "Failed to load context initializer class [" + className + "]", ex);
        }
    }

    private void applyInitializerClasses(ConfigurableApplicationContext context,
                                         List<Class<?>> initializerClasses) {
        Class<?> contextClass = context.getClass();
        List<ApplicationContextInitializer<?>> initializers = new ArrayList<ApplicationContextInitializer<?>>();
        for (Class<?> initializerClass : initializerClasses) {
            initializers.add(instantiateInitializer(contextClass, initializerClass));
        }
        applyInitializers(context, initializers);
    }

    private ApplicationContextInitializer<?> instantiateInitializer(Class<?> contextClass,
                                                                    Class<?> initializerClass) {
        Class<?> requireContextClass = GenericTypeResolver.resolveTypeArgument(
                initializerClass, ApplicationContextInitializer.class);
        Assert.isAssignable(requireContextClass, contextClass,
                String.format(
                        "Could not add context initializer [%s]"
                                + " as its generic parameter [%s] is not assignable "
                                + "from the type of application context used by this "
                                + "context loader [%s]: ",
                        initializerClass.getName(), requireContextClass.getName(),
                        contextClass.getName()));
        return (ApplicationContextInitializer<?>) BeanUtils
                .instantiateClass(initializerClass);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private void applyInitializers(ConfigurableApplicationContext context,
                                   List<ApplicationContextInitializer<?>> initializers) {
        Collections.sort(initializers, new AnnotationAwareOrderComparator());
        for (ApplicationContextInitializer initializer : initializers) {
            initializer.initialize(context);
        }
    }

    public void setOrder(int order) {
        this.order = order;
    }

    @Override
    public int getOrder() {
        return this.order;
    }

}

