package com.rocket.summer.framework.context.annotation;

import com.rocket.summer.framework.beans.BeanUtils;
import com.rocket.summer.framework.beans.factory.config.ConfigurableListableBeanFactory;
import com.rocket.summer.framework.beans.factory.support.BeanDefinitionRegistry;
import com.rocket.summer.framework.context.ConfigurableApplicationContext;
import com.rocket.summer.framework.core.annotation.AnnotationAwareOrderComparator;
import com.rocket.summer.framework.core.env.Environment;
import com.rocket.summer.framework.core.env.EnvironmentCapable;
import com.rocket.summer.framework.core.io.ResourceLoader;
import com.rocket.summer.framework.core.type.AnnotatedTypeMetadata;
import com.rocket.summer.framework.core.type.AnnotationMetadata;
import com.rocket.summer.framework.util.ClassUtils;
import com.rocket.summer.framework.util.MultiValueMap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Internal class used to evaluate {@link Conditional} annotations.
 *
 * @author Phillip Webb
 * @since 4.0
 */
class ConditionEvaluator {

    private final ConditionContextImpl context;


    /**
     * Create a new {@link ConditionEvaluator} instance.
     */
    public ConditionEvaluator(BeanDefinitionRegistry registry, Environment environment, ResourceLoader resourceLoader) {
        this.context = new ConditionContextImpl(registry, environment, resourceLoader);
    }


    /**
     * Determine if an item should be skipped based on {@code @Conditional} annotations.
     * The {@link ConfigurationCondition.ConfigurationPhase} will be deduced from the type of item (i.e. a
     * {@code @Configuration} class will be {@link ConfigurationCondition.ConfigurationPhase#PARSE_CONFIGURATION})
     * @param metadata the meta data
     * @return if the item should be skipped
     */
    public boolean shouldSkip(AnnotatedTypeMetadata metadata) {
        return shouldSkip(metadata, null);
    }

    /**
     * Determine if an item should be skipped based on {@code @Conditional} annotations.
     * @param metadata the meta data
     * @param phase the phase of the call
     * @return if the item should be skipped
     */
    public boolean shouldSkip(AnnotatedTypeMetadata metadata, ConfigurationCondition.ConfigurationPhase phase) {
        if (metadata == null || !metadata.isAnnotated(Conditional.class.getName())) {
            return false;
        }

        if (phase == null) {
            if (metadata instanceof AnnotationMetadata &&
                    ConfigurationClassUtils.isConfigurationCandidate((AnnotationMetadata) metadata)) {
                return shouldSkip(metadata, ConfigurationCondition.ConfigurationPhase.PARSE_CONFIGURATION);
            }
            return shouldSkip(metadata, ConfigurationCondition.ConfigurationPhase.REGISTER_BEAN);
        }

        List<Condition> conditions = new ArrayList<Condition>();
        for (String[] conditionClasses : getConditionClasses(metadata)) {
            for (String conditionClass : conditionClasses) {
                Condition condition = getCondition(conditionClass, this.context.getClassLoader());
                conditions.add(condition);
            }
        }

        AnnotationAwareOrderComparator.sort(conditions);

        for (Condition condition : conditions) {
            ConfigurationCondition.ConfigurationPhase requiredPhase = null;
            if (condition instanceof ConfigurationCondition) {
                requiredPhase = ((ConfigurationCondition) condition).getConfigurationPhase();
            }
            if (requiredPhase == null || requiredPhase == phase) {
                if (!condition.matches(this.context, metadata)) {
                    return true;
                }
            }
        }

        return false;
    }

    @SuppressWarnings("unchecked")
    private List<String[]> getConditionClasses(AnnotatedTypeMetadata metadata) {
        MultiValueMap<String, Object> attributes = metadata.getAllAnnotationAttributes(Conditional.class.getName(), true);
        Object values = (attributes != null ? attributes.get("value") : null);
        return (List<String[]>) (values != null ? values : Collections.emptyList());
    }

    private Condition getCondition(String conditionClassName, ClassLoader classloader) {
        Class<?> conditionClass = ClassUtils.resolveClassName(conditionClassName, classloader);
        return (Condition) BeanUtils.instantiateClass(conditionClass);
    }


    /**
     * Implementation of a {@link ConditionContext}.
     */
    private static class ConditionContextImpl implements ConditionContext {

        private final BeanDefinitionRegistry registry;

        private final ConfigurableListableBeanFactory beanFactory;

        private final Environment environment;

        private final ResourceLoader resourceLoader;

        public ConditionContextImpl(BeanDefinitionRegistry registry, Environment environment, ResourceLoader resourceLoader) {
            this.registry = registry;
            this.beanFactory = deduceBeanFactory(registry);
            this.environment = (environment != null ? environment : deduceEnvironment(registry));
            this.resourceLoader = (resourceLoader != null ? resourceLoader : deduceResourceLoader(registry));
        }

        private ConfigurableListableBeanFactory deduceBeanFactory(BeanDefinitionRegistry source) {
            if (source instanceof ConfigurableListableBeanFactory) {
                return (ConfigurableListableBeanFactory) source;
            }
            if (source instanceof ConfigurableApplicationContext) {
                return (((ConfigurableApplicationContext) source).getBeanFactory());
            }
            return null;
        }

        private Environment deduceEnvironment(BeanDefinitionRegistry source) {
            if (source instanceof EnvironmentCapable) {
                return ((EnvironmentCapable) source).getEnvironment();
            }
            return null;
        }

        private ResourceLoader deduceResourceLoader(BeanDefinitionRegistry source) {
            if (source instanceof ResourceLoader) {
                return (ResourceLoader) source;
            }
            return null;
        }

        @Override
        public BeanDefinitionRegistry getRegistry() {
            return this.registry;
        }

        @Override
        public ConfigurableListableBeanFactory getBeanFactory() {
            return this.beanFactory;
        }

        @Override
        public Environment getEnvironment() {
            return this.environment;
        }

        @Override
        public ResourceLoader getResourceLoader() {
            return this.resourceLoader;
        }

        @Override
        public ClassLoader getClassLoader() {
            if (this.resourceLoader != null) {
                return this.resourceLoader.getClassLoader();
            }
            if (this.beanFactory != null) {
                return this.beanFactory.getBeanClassLoader();
            }
            return null;
        }
    }

}
