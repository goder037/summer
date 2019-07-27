package com.rocket.summer.framework.web.context.support;

import com.rocket.summer.framework.beans.factory.config.BeanDefinition;
import com.rocket.summer.framework.beans.factory.support.BeanNameGenerator;
import com.rocket.summer.framework.beans.factory.support.DefaultListableBeanFactory;
import com.rocket.summer.framework.context.annotation.AnnotatedBeanDefinitionReader;
import com.rocket.summer.framework.context.annotation.ClassPathBeanDefinitionScanner;
import com.rocket.summer.framework.context.annotation.Configuration;
import com.rocket.summer.framework.context.annotation.ScopeMetadataResolver;
import com.rocket.summer.framework.util.Assert;
import com.rocket.summer.framework.web.context.ConfigurableWebEnvironment;

/**
 * {@link com.rocket.summer.framework.web.context.WebApplicationContext} implementation
 * which accepts annotated classes as input - in particular
 * {@link com.rocket.summer.framework.context.annotation.Configuration @Configuration}-annotated
 * classes, but also plain {@link com.rocket.summer.framework.stereotype.Component @Components}
 * and JSR-330 compliant classes using {@literal javax.inject} annotations. Allows for
 * registering classes one by one (specifying class names as config location) as well
 * as for classpath scanning (specifying base packages as config location).
 *
 * <p>This is essentially the equivalent of
 * {@link com.rocket.summer.framework.context.annotation.AnnotationConfigApplicationContext}
 * for a web environment.
 *
 * <p>To make use of this application context, the "contextClass" context-param for
 * ContextLoader and/or "contextClass" init-param for FrameworkServlet must be set to
 * the fully-qualified name of this class.
 *
 * <p>Unlike {@link XmlWebApplicationContext}, no default configuration class locations
 * are assumed. Rather, it is a requirement to set the "contextConfigLocation"
 * context-param for ContextLoader and/or "contextConfigLocation" init-param for
 * FrameworkServlet.  The param-value may contain both fully-qualified
 * class names and base packages to scan for components.
 *
 * <p>Note: In case of multiple {@literal @Configuration} classes, later {@literal @Bean}
 * definitions will override ones defined in earlier loaded files. This can be leveraged
 * to deliberately override certain bean definitions via an extra Configuration class.
 *
 * @author Chris Beams
 * @author Juergen Hoeller
 * @since 3.0
 * @see com.rocket.summer.framework.context.annotation.AnnotationConfigApplicationContext
 */
public class AnnotationConfigWebApplicationContext extends AbstractRefreshableWebApplicationContext {

    private Class<?>[] annotatedClasses;

    private String[] basePackages;

    private BeanNameGenerator beanNameGenerator;

    private ScopeMetadataResolver scopeMetadataResolver;

    /**
     * Register a {@link BeanDefinition} for each class specified by {@link #getConfigLocations()},
     * or scan each specified package for annotated classes. Enables the default set of
     * annotation configuration post processors, such that {@literal @Autowired},
     * {@literal @Required}, and associated annotations can be used.
     * <p>Configuration class bean definitions are registered with generated bean definition
     * names unless the {@literal value} attribute is provided to the stereotype annotation.
     * @see #getConfigLocations()
     * @see AnnotatedBeanDefinitionReader
     * @see ClassPathBeanDefinitionScanner
     */
    @Override
    protected void loadBeanDefinitions(DefaultListableBeanFactory beanFactory) {
        AnnotatedBeanDefinitionReader reader = new AnnotatedBeanDefinitionReader(beanFactory);
        ClassPathBeanDefinitionScanner scanner = new ClassPathBeanDefinitionScanner(beanFactory);
        BeanNameGenerator beanNameGenerator = getBeanNameGenerator();
        ScopeMetadataResolver scopeMetadataResolver = getScopeMetadataResolver();
        if (beanNameGenerator != null) {
            reader.setBeanNameGenerator(beanNameGenerator);
            scanner.setBeanNameGenerator(beanNameGenerator);
        }
        if (scopeMetadataResolver != null) {
            reader.setScopeMetadataResolver(scopeMetadataResolver);
            scanner.setScopeMetadataResolver(scopeMetadataResolver);
        }

        String[] configLocations = getConfigLocations();
        if (configLocations != null) {
            for (String configLocation : configLocations) {
                try {
                    Class<?> clazz = getClassLoader().loadClass(configLocation);
                    if (logger.isInfoEnabled()) {
                        logger.info("Successfully resolved class for [" + configLocation + "]");
                    }
                    reader.register(clazz);
                }
                catch (ClassNotFoundException ex) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Could not load class for config location [" + configLocation +
                                "] - trying package scan. " + ex);
                    }
                    int count = scanner.scan(configLocation);
                    if (logger.isInfoEnabled()) {
                        if (count == 0) {
                            logger.info("No annotated classes found for specified class/package [" + configLocation + "]");
                        }
                        else {
                            logger.info("Found " + count + " annotated classes in package [" + configLocation + "]");
                        }
                    }
                }
            }
        }
    }

    /**
     * Provide a custom {@link BeanNameGenerator} for use with {@link AnnotatedBeanDefinitionReader}
     * and/or {@link ClassPathBeanDefinitionScanner}, if any.
     * <p>Default is {@link com.rocket.summer.framework.context.annotation.AnnotationBeanNameGenerator}.
     * @see AnnotatedBeanDefinitionReader#setBeanNameGenerator
     * @see ClassPathBeanDefinitionScanner#setBeanNameGenerator
     */
    protected BeanNameGenerator getBeanNameGenerator() {
        return null;
    }

    /**
     * Provide a custom {@link ScopeMetadataResolver} for use with {@link AnnotatedBeanDefinitionReader}
     * and/or {@link ClassPathBeanDefinitionScanner}, if any.
     * <p>Default is {@link com.rocket.summer.framework.context.annotation.AnnotationScopeMetadataResolver}.
     * @see AnnotatedBeanDefinitionReader#setScopeMetadataResolver
     * @see ClassPathBeanDefinitionScanner#setScopeMetadataResolver
     */
    protected ScopeMetadataResolver getScopeMetadataResolver() {
        return null;
    }

    /**
     * Register one or more annotated classes to be processed.
     * Note that {@link #refresh()} must be called in order for the context to fully
     * process the new class.
     * <p>Calls to {@link #register} are idempotent; adding the same
     * annotated class more than once has no additional effect.
     * @param annotatedClasses one or more annotated classes,
     * e.g. {@link Configuration @Configuration} classes
     * @see #scan(String...)
     * @see #loadBeanDefinitions(DefaultListableBeanFactory)
     * @see #setConfigLocation(String)
     * @see #refresh()
     */
    public void register(Class<?>... annotatedClasses) {
        Assert.notEmpty(annotatedClasses, "At least one annotated class must be specified");
        this.annotatedClasses = annotatedClasses;
    }

    @Override
    public ConfigurableWebEnvironment getEnvironment() {
        return null;
    }


}

