package com.rocket.summer.framework.boot.context.embedded;

import com.rocket.summer.framework.context.annotation.AnnotatedBeanDefinitionReader;
import com.rocket.summer.framework.context.annotation.ClassPathBeanDefinitionScanner;

/**
 * {@link EmbeddedWebApplicationContext} that accepts annotated classes as input - in
 * particular {@link org.springframework.context.annotation.Configuration @Configuration}
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

}
