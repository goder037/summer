package com.rocket.summer.framework.boot.context.properties;

import com.rocket.summer.framework.beans.factory.support.BeanDefinitionBuilder;
import com.rocket.summer.framework.beans.factory.support.BeanDefinitionRegistry;
import com.rocket.summer.framework.context.annotation.ImportBeanDefinitionRegistrar;
import com.rocket.summer.framework.core.type.AnnotationMetadata;

/**
 * {@link ImportBeanDefinitionRegistrar} for binding externalized application properties
 * to {@link ConfigurationProperties} beans.
 *
 * @author Dave Syer
 * @author Phillip Webb
 */
public class ConfigurationPropertiesBindingPostProcessorRegistrar
        implements ImportBeanDefinitionRegistrar {

    /**
     * The bean name of the {@link ConfigurationPropertiesBindingPostProcessor}.
     */
    public static final String BINDER_BEAN_NAME = ConfigurationPropertiesBindingPostProcessor.class
            .getName();

    private static final String METADATA_BEAN_NAME = BINDER_BEAN_NAME + ".store";

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata,
                                        BeanDefinitionRegistry registry) {
        if (!registry.containsBeanDefinition(BINDER_BEAN_NAME)) {
            BeanDefinitionBuilder meta = BeanDefinitionBuilder
                    .genericBeanDefinition(ConfigurationBeanFactoryMetaData.class);
            BeanDefinitionBuilder bean = BeanDefinitionBuilder.genericBeanDefinition(
                    ConfigurationPropertiesBindingPostProcessor.class);
            bean.addPropertyReference("beanMetaDataStore", METADATA_BEAN_NAME);
            registry.registerBeanDefinition(BINDER_BEAN_NAME, bean.getBeanDefinition());
            registry.registerBeanDefinition(METADATA_BEAN_NAME, meta.getBeanDefinition());
        }
    }

}

