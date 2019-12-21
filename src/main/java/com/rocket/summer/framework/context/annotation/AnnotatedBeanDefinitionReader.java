package com.rocket.summer.framework.context.annotation;

import com.rocket.summer.framework.beans.factory.annotation.AnnotatedGenericBeanDefinition;
import com.rocket.summer.framework.beans.factory.config.BeanDefinitionHolder;
import com.rocket.summer.framework.beans.factory.support.AutowireCandidateQualifier;
import com.rocket.summer.framework.beans.factory.support.BeanDefinitionReaderUtils;
import com.rocket.summer.framework.beans.factory.support.BeanDefinitionRegistry;
import com.rocket.summer.framework.beans.factory.support.BeanNameGenerator;
import com.rocket.summer.framework.core.env.Environment;
import com.rocket.summer.framework.core.env.StandardEnvironment;
import com.rocket.summer.framework.util.Assert;

import java.lang.annotation.Annotation;

/**
 * Convenient adapter for programmatic registration of annotated bean classes.
 * This is an alternative to {@link ClassPathBeanDefinitionScanner}, applying
 * the same resolution of annotations but for explicitly registered classes only.
 *
 * @author Juergen Hoeller
 * @since 3.0
 * @see AnnotationConfigApplicationContext#register
 */
public class AnnotatedBeanDefinitionReader {

    private final BeanDefinitionRegistry registry;

    private BeanNameGenerator beanNameGenerator = new AnnotationBeanNameGenerator();

    private ScopeMetadataResolver scopeMetadataResolver = new AnnotationScopeMetadataResolver();

    private ConditionEvaluator conditionEvaluator;

    /**
     * Create a new AnnotatedBeanDefinitionReader for the given bean factory.
     * @param registry the BeanFactory to load bean definitions into,
     * in the form of a BeanDefinitionRegistry
     */
    public AnnotatedBeanDefinitionReader(BeanDefinitionRegistry registry) {
        this.registry = registry;
        AnnotationConfigUtils.registerAnnotationConfigProcessors(this.registry);
    }

    /**
     * Create a new {@code AnnotatedBeanDefinitionReader} for the given registry and using
     * the given {@link Environment}.
     * @param registry the {@code BeanFactory} to load bean definitions into,
     * in the form of a {@code BeanDefinitionRegistry}
     * @param environment the {@code Environment} to use when evaluating bean definition
     * profiles.
     * @since 3.1
     */
    public AnnotatedBeanDefinitionReader(BeanDefinitionRegistry registry, Environment environment) {
        Assert.notNull(registry, "BeanDefinitionRegistry must not be null");
        Assert.notNull(environment, "Environment must not be null");
        this.registry = registry;
        this.conditionEvaluator = new ConditionEvaluator(registry, environment, null);
        AnnotationConfigUtils.registerAnnotationConfigProcessors(this.registry);
    }


    /**
     * Set the Environment to use when evaluating whether
     * {@link Conditional @Conditional}-annotated component classes should be registered.
     * <p>The default is a {@link StandardEnvironment}.
     * @see #registerBean(Class, String, Class...)
     */
    public void setEnvironment(Environment environment) {
        this.conditionEvaluator = new ConditionEvaluator(this.registry, environment, null);
    }


    /**
     * Return the BeanDefinitionRegistry that this scanner operates on.
     */
    public final BeanDefinitionRegistry getRegistry() {
        return this.registry;
    }

    /**
     * Set the BeanNameGenerator to use for detected bean classes.
     * <p>Default is a {@link AnnotationBeanNameGenerator}.
     */
    public void setBeanNameGenerator(BeanNameGenerator beanNameGenerator) {
        this.beanNameGenerator = (beanNameGenerator != null ? beanNameGenerator : new AnnotationBeanNameGenerator());
    }

    /**
     * Set the ScopeMetadataResolver to use for detected bean classes.
     * <p>The default is an {@link AnnotationScopeMetadataResolver}.
     */
    public void setScopeMetadataResolver(ScopeMetadataResolver scopeMetadataResolver) {
        this.scopeMetadataResolver = (scopeMetadataResolver != null ? scopeMetadataResolver : new AnnotationScopeMetadataResolver());
    }


    public void register(Class<?>... annotatedClasses) {
        for (Class<?> annotatedClass : annotatedClasses) {
            registerBean(annotatedClass);
        }
    }

    public void registerBean(Class<?> annotatedClass) {
        registerBean(annotatedClass, null, (Class<? extends Annotation>[]) null);
    }

    public void registerBean(Class<?> annotatedClass, Class<? extends Annotation>... qualifiers) {
        registerBean(annotatedClass, null, qualifiers);
    }

    public void registerBean(Class<?> annotatedClass, String name, Class<? extends Annotation>... qualifiers) {
        AnnotatedGenericBeanDefinition abd = new AnnotatedGenericBeanDefinition(annotatedClass);
        ScopeMetadata scopeMetadata = this.scopeMetadataResolver.resolveScopeMetadata(abd);
        abd.setScope(scopeMetadata.getScopeName());
        String beanName = (name != null ? name : this.beanNameGenerator.generateBeanName(abd, this.registry));
        AnnotationConfigUtils.processCommonDefinitionAnnotations(abd);
        if (qualifiers != null) {
            for (Class<? extends Annotation> qualifier : qualifiers) {
                if (Primary.class.equals(qualifier)) {
                    abd.setPrimary(true);
                }
                else if (Lazy.class.equals(qualifier)) {
                    abd.setLazyInit(true);
                }
                else {
                    abd.addQualifier(new AutowireCandidateQualifier(qualifier));
                }
            }
        }
        BeanDefinitionHolder definitionHolder = new BeanDefinitionHolder(abd, beanName);
        definitionHolder = AnnotationConfigUtils.applyScopedProxyMode(scopeMetadata, definitionHolder, this.registry);
        BeanDefinitionReaderUtils.registerBeanDefinition(definitionHolder, this.registry);
    }

}

