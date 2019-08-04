package com.rocket.summer.framework.web.context.support;

import com.rocket.summer.framework.beans.factory.NoSuchBeanDefinitionException;
import com.rocket.summer.framework.beans.factory.config.BeanDefinition;
import com.rocket.summer.framework.beans.factory.support.BeanNameGenerator;
import com.rocket.summer.framework.beans.factory.support.DefaultListableBeanFactory;
import com.rocket.summer.framework.context.annotation.*;
import com.rocket.summer.framework.core.ResolvableType;
import com.rocket.summer.framework.util.Assert;
import com.rocket.summer.framework.util.ClassUtils;
import com.rocket.summer.framework.util.StringUtils;
import com.rocket.summer.framework.web.context.ContextLoader;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * {@link com.rocket.summer.framework.web.context.WebApplicationContext WebApplicationContext}
 * implementation which accepts annotated classes as input - in particular
 * {@link com.rocket.summer.framework.context.annotation.Configuration @Configuration}-annotated
 * classes, but also plain {@link com.rocket.summer.framework.stereotype.Component @Component}
 * classes and JSR-330 compliant classes using {@code javax.inject} annotations. Allows
 * for registering classes one by one (specifying class names as config location) as well
 * as for classpath scanning (specifying base packages as config location).
 *
 * <p>This is essentially the equivalent of
 * {@link com.rocket.summer.framework.context.annotation.AnnotationConfigApplicationContext
 * AnnotationConfigApplicationContext} for a web environment.
 *
 * <p>To make use of this application context, the
 * {@linkplain ContextLoader#CONTEXT_CLASS_PARAM "contextClass"} context-param for
 * ContextLoader and/or "contextClass" init-param for FrameworkServlet must be set to
 * the fully-qualified name of this class.
 *
 * <p>As of Spring 3.1, this class may also be directly instantiated and injected into
 * Spring's {@code DispatcherServlet} or {@code ContextLoaderListener} when using the
 * new {@link com.rocket.summer.framework.web.WebApplicationInitializer WebApplicationInitializer}
 * code-based alternative to {@code web.xml}. See its Javadoc for details and usage examples.
 *
 * <p>Unlike {@link XmlWebApplicationContext}, no default configuration class locations
 * are assumed. Rather, it is a requirement to set the
 * {@linkplain ContextLoader#CONFIG_LOCATION_PARAM "contextConfigLocation"}
 * context-param for {@link ContextLoader} and/or "contextConfigLocation" init-param for
 * FrameworkServlet.  The param-value may contain both fully-qualified
 * class names and base packages to scan for components. See {@link #loadBeanDefinitions}
 * for exact details on how these locations are processed.
 *
 * <p>As an alternative to setting the "contextConfigLocation" parameter, users may
 * implement an {@link com.rocket.summer.framework.context.ApplicationContextInitializer
 * ApplicationContextInitializer} and set the
 * {@linkplain ContextLoader#CONTEXT_INITIALIZER_CLASSES_PARAM "contextInitializerClasses"}
 * context-param / init-param. In such cases, users should favor the {@link #refresh()}
 * and {@link #scan(String...)} methods over the {@link #setConfigLocation(String)}
 * method, which is primarily for use by {@code ContextLoader}.
 *
 * <p>Note: In case of multiple {@code @Configuration} classes, later {@code @Bean}
 * definitions will override ones defined in earlier loaded files. This can be leveraged
 * to deliberately override certain bean definitions via an extra Configuration class.
 *
 * @author Chris Beams
 * @author Juergen Hoeller
 * @since 3.0
 * @see com.rocket.summer.framework.context.annotation.AnnotationConfigApplicationContext
 */
public class AnnotationConfigWebApplicationContext extends AbstractRefreshableWebApplicationContext
        implements AnnotationConfigRegistry {

    private BeanNameGenerator beanNameGenerator;

    private ScopeMetadataResolver scopeMetadataResolver;

    private final Set<Class<?>> annotatedClasses = new LinkedHashSet<Class<?>>();

    private final Set<String> basePackages = new LinkedHashSet<String>();


    /**
     * Set a custom {@link BeanNameGenerator} for use with {@link AnnotatedBeanDefinitionReader}
     * and/or {@link ClassPathBeanDefinitionScanner}.
     * <p>Default is {@link com.rocket.summer.framework.context.annotation.AnnotationBeanNameGenerator}.
     * @see AnnotatedBeanDefinitionReader#setBeanNameGenerator
     * @see ClassPathBeanDefinitionScanner#setBeanNameGenerator
     */
    public void setBeanNameGenerator(BeanNameGenerator beanNameGenerator) {
        this.beanNameGenerator = beanNameGenerator;
    }

    /**
     * Return the custom {@link BeanNameGenerator} for use with {@link AnnotatedBeanDefinitionReader}
     * and/or {@link ClassPathBeanDefinitionScanner}, if any.
     */
    protected BeanNameGenerator getBeanNameGenerator() {
        return this.beanNameGenerator;
    }

    /**
     * Set a custom {@link ScopeMetadataResolver} for use with {@link AnnotatedBeanDefinitionReader}
     * and/or {@link ClassPathBeanDefinitionScanner}.
     * <p>Default is an {@link com.rocket.summer.framework.context.annotation.AnnotationScopeMetadataResolver}.
     * @see AnnotatedBeanDefinitionReader#setScopeMetadataResolver
     * @see ClassPathBeanDefinitionScanner#setScopeMetadataResolver
     */
    public void setScopeMetadataResolver(ScopeMetadataResolver scopeMetadataResolver) {
        this.scopeMetadataResolver = scopeMetadataResolver;
    }

    /**
     * Return the custom {@link ScopeMetadataResolver} for use with {@link AnnotatedBeanDefinitionReader}
     * and/or {@link ClassPathBeanDefinitionScanner}, if any.
     */
    protected ScopeMetadataResolver getScopeMetadataResolver() {
        return this.scopeMetadataResolver;
    }


    /**
     * Register one or more annotated classes to be processed.
     * <p>Note that {@link #refresh()} must be called in order for the context
     * to fully process the new classes.
     * @param annotatedClasses one or more annotated classes,
     * e.g. {@link com.rocket.summer.framework.context.annotation.Configuration @Configuration} classes
     * @see #scan(String...)
     * @see #loadBeanDefinitions(DefaultListableBeanFactory)
     * @see #setConfigLocation(String)
     * @see #refresh()
     */
    public void register(Class<?>... annotatedClasses) {
        Assert.notEmpty(annotatedClasses, "At least one annotated class must be specified");
        this.annotatedClasses.addAll(Arrays.asList(annotatedClasses));
    }

    /**
     * Perform a scan within the specified base packages.
     * <p>Note that {@link #refresh()} must be called in order for the context
     * to fully process the new classes.
     * @param basePackages the packages to check for annotated classes
     * @see #loadBeanDefinitions(DefaultListableBeanFactory)
     * @see #register(Class...)
     * @see #setConfigLocation(String)
     * @see #refresh()
     */
    public void scan(String... basePackages) {
        Assert.notEmpty(basePackages, "At least one base package must be specified");
        this.basePackages.addAll(Arrays.asList(basePackages));
    }


    /**
     * Register a {@link com.rocket.summer.framework.beans.factory.config.BeanDefinition} for
     * any classes specified by {@link #register(Class...)} and scan any packages
     * specified by {@link #scan(String...)}.
     * <p>For any values specified by {@link #setConfigLocation(String)} or
     * {@link #setConfigLocations(String[])}, attempt first to load each location as a
     * class, registering a {@code BeanDefinition} if class loading is successful,
     * and if class loading fails (i.e. a {@code ClassNotFoundException} is raised),
     * assume the value is a package and attempt to scan it for annotated classes.
     * <p>Enables the default set of annotation configuration post processors, such that
     * {@code @Autowired}, {@code @Required}, and associated annotations can be used.
     * <p>Configuration class bean definitions are registered with generated bean
     * definition names unless the {@code value} attribute is provided to the stereotype
     * annotation.
     * @param beanFactory the bean factory to load bean definitions into
     * @see #register(Class...)
     * @see #scan(String...)
     * @see #setConfigLocation(String)
     * @see #setConfigLocations(String[])
     * @see AnnotatedBeanDefinitionReader
     * @see ClassPathBeanDefinitionScanner
     */
    @Override
    protected void loadBeanDefinitions(DefaultListableBeanFactory beanFactory) {
        AnnotatedBeanDefinitionReader reader = getAnnotatedBeanDefinitionReader(beanFactory);
        ClassPathBeanDefinitionScanner scanner = getClassPathBeanDefinitionScanner(beanFactory);

        BeanNameGenerator beanNameGenerator = getBeanNameGenerator();
        if (beanNameGenerator != null) {
            reader.setBeanNameGenerator(beanNameGenerator);
            scanner.setBeanNameGenerator(beanNameGenerator);
            beanFactory.registerSingleton(AnnotationConfigUtils.CONFIGURATION_BEAN_NAME_GENERATOR, beanNameGenerator);
        }

        ScopeMetadataResolver scopeMetadataResolver = getScopeMetadataResolver();
        if (scopeMetadataResolver != null) {
            reader.setScopeMetadataResolver(scopeMetadataResolver);
            scanner.setScopeMetadataResolver(scopeMetadataResolver);
        }

        if (!this.annotatedClasses.isEmpty()) {
            if (logger.isInfoEnabled()) {
                logger.info("Registering annotated classes: [" +
                        StringUtils.collectionToCommaDelimitedString(this.annotatedClasses) + "]");
            }
            reader.register(ClassUtils.toClassArray(this.annotatedClasses));
        }

        if (!this.basePackages.isEmpty()) {
            if (logger.isInfoEnabled()) {
                logger.info("Scanning base packages: [" +
                        StringUtils.collectionToCommaDelimitedString(this.basePackages) + "]");
            }
            scanner.scan(StringUtils.toStringArray(this.basePackages));
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
     * Build an {@link AnnotatedBeanDefinitionReader} for the given bean factory.
     * <p>This should be pre-configured with the {@code Environment} (if desired)
     * but not with a {@code BeanNameGenerator} or {@code ScopeMetadataResolver} yet.
     * @param beanFactory the bean factory to load bean definitions into
     * @since 4.1.9
     * @see #getEnvironment()
     * @see #getBeanNameGenerator()
     * @see #getScopeMetadataResolver()
     */
    protected AnnotatedBeanDefinitionReader getAnnotatedBeanDefinitionReader(DefaultListableBeanFactory beanFactory) {
        return new AnnotatedBeanDefinitionReader(beanFactory, getEnvironment());
    }

    /**
     * Build a {@link ClassPathBeanDefinitionScanner} for the given bean factory.
     * <p>This should be pre-configured with the {@code Environment} (if desired)
     * but not with a {@code BeanNameGenerator} or {@code ScopeMetadataResolver} yet.
     * @param beanFactory the bean factory to load bean definitions into
     * @since 4.1.9
     * @see #getEnvironment()
     * @see #getBeanNameGenerator()
     * @see #getScopeMetadataResolver()
     */
    protected ClassPathBeanDefinitionScanner getClassPathBeanDefinitionScanner(DefaultListableBeanFactory beanFactory) {
        return new ClassPathBeanDefinitionScanner(beanFactory, true, getEnvironment());
    }

}
