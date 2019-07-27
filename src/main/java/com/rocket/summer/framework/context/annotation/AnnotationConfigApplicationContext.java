package com.rocket.summer.framework.context.annotation;

import com.rocket.summer.framework.beans.factory.support.BeanNameGenerator;
import com.rocket.summer.framework.context.support.GenericApplicationContext;

/**
 * Standalone application context, accepting annotated classes as input - in particular
 * {@link com.rocket.summer.framework.context.annotation.Configuration @Configuration}-annotated
 * classes, but also plain {@link com.rocket.summer.framework.stereotype.Component @Components}
 * and JSR-330 compliant classes using {@literal javax.inject} annotations. Allows for
 * registering classes one by one ({@link #register}) as well as for classpath scanning
 * ({@link #scan}).
 *
 * <p>In case of multiple Configuration classes, {@link Bean} methods defined in later
 * classes will override those defined in earlier classes. This can be leveraged to
 * deliberately override certain bean definitions via an extra Configuration class.
 *
 * @author Chris Beams
 * @author Juergen Hoeller
 * @since 3.0
 * @see #register
 * @see #scan
 * @see AnnotatedBeanDefinitionReader
 * @see ClassPathBeanDefinitionScanner
 * @see com.rocket.summer.framework.context.support.GenericXmlApplicationContext
 */
public class AnnotationConfigApplicationContext extends GenericApplicationContext {

    private final AnnotatedBeanDefinitionReader reader = new AnnotatedBeanDefinitionReader(this);

    private final ClassPathBeanDefinitionScanner scanner = new ClassPathBeanDefinitionScanner(this);


    /**
     * Create a new AnnotationConfigApplicationContext that needs to be populated
     * through {@link #register} calls and then manually {@link #refresh refreshed}.
     */
    public AnnotationConfigApplicationContext() {
    }

    /**
     * Create a new AnnotationConfigApplicationContext, deriving bean definitions
     * from the given annotated classes and automatically refreshing the context.
     * @param annotatedClasses one or more annotated classes,
     * e.g. {@link Configuration @Configuration} classes
     */
    public AnnotationConfigApplicationContext(Class<?>... annotatedClasses) {
        register(annotatedClasses);
        refresh();
    }

    /**
     * Create a new AnnotationConfigApplicationContext, scanning for bean definitions
     * in the given packages and automatically refreshing the context.
     * @param basePackages the packages to check for annotated classes
     */
    public AnnotationConfigApplicationContext(String... basePackages) {
        scan(basePackages);
        refresh();
    }


    /**
     * Set the BeanNameGenerator to use for detected bean classes.
     * <p>Default is a {@link AnnotationBeanNameGenerator}.
     */
    public void setBeanNameGenerator(BeanNameGenerator beanNameGenerator) {
        this.reader.setBeanNameGenerator(beanNameGenerator);
        this.scanner.setBeanNameGenerator(beanNameGenerator);
    }

    /**
     * Set the ScopeMetadataResolver to use for detected bean classes.
     * <p>The default is an {@link AnnotationScopeMetadataResolver}.
     */
    public void setScopeMetadataResolver(ScopeMetadataResolver scopeMetadataResolver) {
        this.reader.setScopeMetadataResolver(scopeMetadataResolver);
        this.scanner.setScopeMetadataResolver(scopeMetadataResolver);
    }


    /**
     * Register an annotated class to be processed. Allows for programmatically
     * building a {@link AnnotationConfigApplicationContext}. Note that
     * {@link AnnotationConfigApplicationContext#refresh()} must be called in
     * order for the context to fully process the new class.
     * <p>Calls to {@link #register} are idempotent; adding the same
     * annotated class more than once has no additional effect.
     * @param annotatedClasses one or more annotated classes,
     * e.g. {@link Configuration @Configuration} classes
     * @see #refresh()
     */
    public void register(Class<?>... annotatedClasses) {
        this.reader.register(annotatedClasses);
    }

    /**
     * Perform a scan within the specified base packages.
     * Note that {@link AnnotationConfigApplicationContext#refresh()} must be
     * called in order for the context to fully process the new class.
     * @param basePackages the packages to check for annotated classes
     * @see #refresh()
     */
    public void scan(String... basePackages) {
        this.scanner.scan(basePackages);
    }

}

