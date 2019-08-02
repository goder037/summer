package com.rocket.summer.framework.boot.context.embedded;

import com.rocket.summer.framework.beans.factory.config.ConfigurableListableBeanFactory;
import com.rocket.summer.framework.beans.factory.support.BeanNameGenerator;
import com.rocket.summer.framework.context.annotation.AnnotatedBeanDefinitionReader;
import com.rocket.summer.framework.context.annotation.AnnotationConfigUtils;
import com.rocket.summer.framework.context.annotation.ClassPathBeanDefinitionScanner;
import com.rocket.summer.framework.context.annotation.ScopeMetadataResolver;
import com.rocket.summer.framework.core.env.ConfigurableEnvironment;
import com.rocket.summer.framework.stereotype.Component;
import com.rocket.summer.framework.util.Assert;

/**
 * {@link EmbeddedWebApplicationContext} that accepts annotated classes as input - in
 * particular {@link com.rocket.summer.framework.context.annotation.Configuration @Configuration}
 * -annotated classes, but also plain {@link Component @Component} classes and JSR-330
 * compliant classes using {@code javax.inject} annotations. Allows for registering
 * classes one by one (specifying class names as config location) as well as for classpath
 * scanning (specifying base packages as config location).
 * <p>
 * Note: In case of multiple {@code @Configuration} classes, later {@code @Bean}
 * definitions will override ones defined in earlier loaded files. This can be leveraged
 * to deliberately override certain bean definitions via an extra Configuration class.
 *
 * @author Phillip Webb
 * @see #register(Class...)
 * @see #scan(String...)
 * @see EmbeddedWebApplicationContext
 * @see AnnotationConfigWebApplicationContext
 */
public class AnnotationConfigEmbeddedWebApplicationContext
        extends EmbeddedWebApplicationContext {

    private final AnnotatedBeanDefinitionReader reader;

    private final ClassPathBeanDefinitionScanner scanner;

    private Class<?>[] annotatedClasses;

    private String[] basePackages;

    /**
     * Create a new {@link AnnotationConfigEmbeddedWebApplicationContext} that needs to be
     * populated through {@link #register} calls and then manually {@linkplain #refresh
     * refreshed}.
     */
    public AnnotationConfigEmbeddedWebApplicationContext() {
        this.reader = new AnnotatedBeanDefinitionReader(this);
        this.scanner = new ClassPathBeanDefinitionScanner(this);
    }

    /**
     * Create a new {@link AnnotationConfigEmbeddedWebApplicationContext}, deriving bean
     * definitions from the given annotated classes and automatically refreshing the
     * context.
     * @param annotatedClasses one or more annotated classes, e.g. {@code @Configuration}
     * classes
     */
    public AnnotationConfigEmbeddedWebApplicationContext(Class<?>... annotatedClasses) {
        this();
        register(annotatedClasses);
        refresh();
    }

    /**
     * Create a new {@link AnnotationConfigEmbeddedWebApplicationContext}, scanning for
     * bean definitions in the given packages and automatically refreshing the context.
     * @param basePackages the packages to check for annotated classes
     */
    public AnnotationConfigEmbeddedWebApplicationContext(String... basePackages) {
        this();
        scan(basePackages);
        refresh();
    }

    /**
     * {@inheritDoc}
     * <p>
     * Delegates given environment to underlying {@link AnnotatedBeanDefinitionReader} and
     * {@link ClassPathBeanDefinitionScanner} members.
     */
    @Override
    public void setEnvironment(ConfigurableEnvironment environment) {
        super.setEnvironment(environment);
        this.reader.setEnvironment(environment);
        this.scanner.setEnvironment(environment);
    }

    /**
     * Provide a custom {@link BeanNameGenerator} for use with
     * {@link AnnotatedBeanDefinitionReader} and/or {@link ClassPathBeanDefinitionScanner}
     * , if any.
     * <p>
     * Default is
     * {@link com.rocket.summer.framework.context.annotation.AnnotationBeanNameGenerator}.
     * <p>
     * Any call to this method must occur prior to calls to {@link #register(Class...)}
     * and/or {@link #scan(String...)}.
     * @param beanNameGenerator the bean name generator
     * @see AnnotatedBeanDefinitionReader#setBeanNameGenerator
     * @see ClassPathBeanDefinitionScanner#setBeanNameGenerator
     */
    public void setBeanNameGenerator(BeanNameGenerator beanNameGenerator) {
        this.reader.setBeanNameGenerator(beanNameGenerator);
        this.scanner.setBeanNameGenerator(beanNameGenerator);
        this.getBeanFactory().registerSingleton(
                AnnotationConfigUtils.CONFIGURATION_BEAN_NAME_GENERATOR,
                beanNameGenerator);
    }

    /**
     * Set the {@link ScopeMetadataResolver} to use for detected bean classes.
     * <p>
     * The default is an {@link AnnotationScopeMetadataResolver}.
     * <p>
     * Any call to this method must occur prior to calls to {@link #register(Class...)}
     * and/or {@link #scan(String...)}.
     * @param scopeMetadataResolver the scope metadata resolver
     */
    public void setScopeMetadataResolver(ScopeMetadataResolver scopeMetadataResolver) {
        this.reader.setScopeMetadataResolver(scopeMetadataResolver);
        this.scanner.setScopeMetadataResolver(scopeMetadataResolver);
    }

    /**
     * Register one or more annotated classes to be processed. Note that
     * {@link #refresh()} must be called in order for the context to fully process the new
     * class.
     * <p>
     * Calls to {@code #register} are idempotent; adding the same annotated class more
     * than once has no additional effect.
     * @param annotatedClasses one or more annotated classes, e.g. {@code @Configuration}
     * classes
     * @see #scan(String...)
     * @see #refresh()
     */
    public final void register(Class<?>... annotatedClasses) {
        this.annotatedClasses = annotatedClasses;
        Assert.notEmpty(annotatedClasses,
                "At least one annotated class must be specified");
    }

    /**
     * Perform a scan within the specified base packages. Note that {@link #refresh()}
     * must be called in order for the context to fully process the new class.
     * @param basePackages the packages to check for annotated classes
     * @see #register(Class...)
     * @see #refresh()
     */
    public final void scan(String... basePackages) {
        this.basePackages = basePackages;
        Assert.notEmpty(basePackages, "At least one base package must be specified");
    }

    @Override
    protected void prepareRefresh() {
        this.scanner.clearCache();
        super.prepareRefresh();
    }

    @Override
    protected void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) {
        super.postProcessBeanFactory(beanFactory);
        if (this.basePackages != null && this.basePackages.length > 0) {
            this.scanner.scan(this.basePackages);
        }
        if (this.annotatedClasses != null && this.annotatedClasses.length > 0) {
            this.reader.register(this.annotatedClasses);
        }
    }

}
