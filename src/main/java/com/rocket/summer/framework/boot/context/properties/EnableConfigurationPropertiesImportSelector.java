package com.rocket.summer.framework.boot.context.properties;

import com.rocket.summer.framework.beans.factory.support.AbstractBeanDefinition;
import com.rocket.summer.framework.beans.factory.support.BeanDefinitionBuilder;
import com.rocket.summer.framework.beans.factory.support.BeanDefinitionRegistry;
import com.rocket.summer.framework.context.annotation.ImportBeanDefinitionRegistrar;
import com.rocket.summer.framework.context.annotation.ImportSelector;
import com.rocket.summer.framework.core.annotation.AnnotationUtils;
import com.rocket.summer.framework.core.type.AnnotationMetadata;
import com.rocket.summer.framework.util.Assert;
import com.rocket.summer.framework.util.MultiValueMap;
import com.rocket.summer.framework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Import selector that sets up binding of external properties to configuration classes
 * (see {@link ConfigurationProperties}). It either registers a
 * {@link ConfigurationProperties} bean or not, depending on whether the enclosing
 * {@link EnableConfigurationProperties} explicitly declares one. If none is declared then
 * a bean post processor will still kick in for any beans annotated as external
 * configuration. If one is declared then it a bean definition is registered with id equal
 * to the class name (thus an application context usually only contains one
 * {@link ConfigurationProperties} bean of each unique type).
 *
 * @author Dave Syer
 * @author Christian Dupuis
 * @author Stephane Nicoll
 */
class EnableConfigurationPropertiesImportSelector implements ImportSelector {

    @Override
    public String[] selectImports(AnnotationMetadata metadata) {
        MultiValueMap<String, Object> attributes = metadata.getAllAnnotationAttributes(
                EnableConfigurationProperties.class.getName(), false);
        Object[] type = (attributes != null) ? (Object[]) attributes.getFirst("value")
                : null;
        if (type == null || type.length == 0) {
            return new String[] {
                    ConfigurationPropertiesBindingPostProcessorRegistrar.class
                            .getName() };
        }
        return new String[] { ConfigurationPropertiesBeanRegistrar.class.getName(),
                ConfigurationPropertiesBindingPostProcessorRegistrar.class.getName() };
    }

    /**
     * {@link ImportBeanDefinitionRegistrar} for configuration properties support.
     */
    public static class ConfigurationPropertiesBeanRegistrar
            implements ImportBeanDefinitionRegistrar {

        @Override
        public void registerBeanDefinitions(AnnotationMetadata metadata,
                                            BeanDefinitionRegistry registry) {
            MultiValueMap<String, Object> attributes = metadata
                    .getAllAnnotationAttributes(
                            EnableConfigurationProperties.class.getName(), false);
            List<Class<?>> types = collectClasses(attributes.get("value"));
            for (Class<?> type : types) {
                String prefix = extractPrefix(type);
                String name = (StringUtils.hasText(prefix) ? prefix + "-" + type.getName()
                        : type.getName());
                if (!registry.containsBeanDefinition(name)) {
                    registerBeanDefinition(registry, type, name);
                }
            }
        }

        private String extractPrefix(Class<?> type) {
            ConfigurationProperties annotation = AnnotationUtils.findAnnotation(type,
                    ConfigurationProperties.class);
            if (annotation != null) {
                return annotation.prefix();
            }
            return "";
        }

        private List<Class<?>> collectClasses(List<Object> list) {
            ArrayList<Class<?>> result = new ArrayList<Class<?>>();
            for (Object object : list) {
                for (Object value : (Object[]) object) {
                    if (value instanceof Class && value != void.class) {
                        result.add((Class<?>) value);
                    }
                }
            }
            return result;
        }

        private void registerBeanDefinition(BeanDefinitionRegistry registry,
                                            Class<?> type, String name) {
            BeanDefinitionBuilder builder = BeanDefinitionBuilder
                    .genericBeanDefinition(type);
            AbstractBeanDefinition beanDefinition = builder.getBeanDefinition();
            registry.registerBeanDefinition(name, beanDefinition);

            ConfigurationProperties properties = AnnotationUtils.findAnnotation(type,
                    ConfigurationProperties.class);
            Assert.notNull(properties,
                    "No " + ConfigurationProperties.class.getSimpleName()
                            + " annotation found on  '" + type.getName() + "'.");
        }

    }

}

