package com.rocket.summer.framework.data.keyvalue.repository.config;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import com.rocket.summer.framework.beans.factory.support.AbstractBeanDefinition;
import com.rocket.summer.framework.beans.factory.support.BeanDefinitionBuilder;
import com.rocket.summer.framework.beans.factory.support.BeanDefinitionRegistry;
import com.rocket.summer.framework.beans.factory.support.RootBeanDefinition;
import com.rocket.summer.framework.core.annotation.AnnotationAttributes;
import com.rocket.summer.framework.core.type.AnnotationMetadata;
import com.rocket.summer.framework.data.keyvalue.core.mapping.context.KeyValueMappingContext;
import com.rocket.summer.framework.data.keyvalue.repository.KeyValueRepository;
import com.rocket.summer.framework.data.keyvalue.repository.query.KeyValuePartTreeQuery;
import com.rocket.summer.framework.data.keyvalue.repository.query.SpelQueryCreator;
import com.rocket.summer.framework.data.keyvalue.repository.support.KeyValueRepositoryFactoryBean;
import com.rocket.summer.framework.data.repository.config.AnnotationRepositoryConfigurationSource;
import com.rocket.summer.framework.data.repository.config.RepositoryConfigurationExtension;
import com.rocket.summer.framework.data.repository.config.RepositoryConfigurationExtensionSupport;
import com.rocket.summer.framework.data.repository.config.RepositoryConfigurationSource;

/**
 * {@link RepositoryConfigurationExtension} for {@link KeyValueRepository}.
 *
 * @author Christoph Strobl
 * @author Oliver Gierke
 */
public abstract class KeyValueRepositoryConfigurationExtension extends RepositoryConfigurationExtensionSupport {

    protected static final String MAPPING_CONTEXT_BEAN_NAME = "keyValueMappingContext";
    protected static final String KEY_VALUE_TEMPLATE_BEAN_REF_ATTRIBUTE = "keyValueTemplateRef";

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.repository.config.RepositoryConfigurationExtension#getRepositoryFactoryClassName()
     */
    @Override
    public String getRepositoryFactoryClassName() {
        return KeyValueRepositoryFactoryBean.class.getName();
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.repository.config.RepositoryConfigurationExtensionSupport#getModuleName()
     */
    @Override
    public String getModuleName() {
        return "KeyValue";
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.repository.config.RepositoryConfigurationExtensionSupport#getModulePrefix()
     */
    @Override
    protected String getModulePrefix() {
        return "keyvalue";
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.repository.config.RepositoryConfigurationExtensionSupport#getIdentifyingTypes()
     */
    @Override
    protected Collection<Class<?>> getIdentifyingTypes() {
        return Collections.<Class<?>> singleton(KeyValueRepository.class);
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.repository.config.RepositoryConfigurationExtensionSupport#postProcess(com.rocket.summer.framework.beans.factory.support.BeanDefinitionBuilder, com.rocket.summer.framework.data.repository.config.AnnotationRepositoryConfigurationSource)
     */
    @Override
    public void postProcess(BeanDefinitionBuilder builder, AnnotationRepositoryConfigurationSource config) {

        AnnotationAttributes attributes = config.getAttributes();

        builder.addPropertyReference("keyValueOperations", attributes.getString(KEY_VALUE_TEMPLATE_BEAN_REF_ATTRIBUTE));
        builder.addPropertyValue("queryCreator", getQueryCreatorType(config));
        builder.addPropertyValue("queryType", getQueryType(config));
        builder.addPropertyReference("mappingContext", MAPPING_CONTEXT_BEAN_NAME);
    }

    /**
     * Detects the query creator type to be used for the factory to set. Will lookup a {@link QueryCreatorType} annotation
     * on the {@code @Enable}-annotation or use {@link SpelQueryCreator} if not found.
     *
     * @param config
     * @return
     */
    private static Class<?> getQueryCreatorType(AnnotationRepositoryConfigurationSource config) {

        AnnotationMetadata metadata = config.getEnableAnnotationMetadata();

        Map<String, Object> queryCreatorAnnotationAttributes = metadata.getAnnotationAttributes(QueryCreatorType.class.getName());

        if (queryCreatorAnnotationAttributes == null) {
            return SpelQueryCreator.class;
        }

        AnnotationAttributes queryCreatorAttributes = new AnnotationAttributes(queryCreatorAnnotationAttributes);
        return queryCreatorAttributes.getClass("value");
    }

    /**
     * Detects the query creator type to be used for the factory to set. Will lookup a {@link QueryCreatorType} annotation
     * on the {@code @Enable}-annotation or use {@link SpelQueryCreator} if not found.
     *
     * @param config
     * @return
     */
    private static Class<?> getQueryType(AnnotationRepositoryConfigurationSource config) {

        AnnotationMetadata metadata = config.getEnableAnnotationMetadata();

        Map<String, Object> queryCreatorAnnotationAttributes = metadata.getAnnotationAttributes(QueryCreatorType.class.getName());

        if (queryCreatorAnnotationAttributes == null) {
            return KeyValuePartTreeQuery.class;
        }

        AnnotationAttributes queryCreatorAttributes = new AnnotationAttributes(queryCreatorAnnotationAttributes);
        return queryCreatorAttributes.getClass("repositoryQueryType");
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.repository.config.RepositoryConfigurationExtensionSupport#registerBeansForRoot(com.rocket.summer.framework.beans.factory.support.BeanDefinitionRegistry, com.rocket.summer.framework.data.repository.config.RepositoryConfigurationSource)
     */
    @Override
    public void registerBeansForRoot(BeanDefinitionRegistry registry, RepositoryConfigurationSource configurationSource) {

        super.registerBeansForRoot(registry, configurationSource);

        RootBeanDefinition mappingContextDefinition = new RootBeanDefinition(KeyValueMappingContext.class);
        mappingContextDefinition.setSource(configurationSource.getSource());

        registerIfNotAlreadyRegistered(mappingContextDefinition, registry, MAPPING_CONTEXT_BEAN_NAME, configurationSource);

        String keyValueTemplateName = configurationSource.getAttribute(KEY_VALUE_TEMPLATE_BEAN_REF_ATTRIBUTE);

        // No custom template reference configured and no matching bean definition found
        if (getDefaultKeyValueTemplateRef().equals(keyValueTemplateName)
                && !registry.containsBeanDefinition(keyValueTemplateName)) {

            AbstractBeanDefinition beanDefinition = getDefaultKeyValueTemplateBeanDefinition(configurationSource);

            if (beanDefinition != null) {
                registerIfNotAlreadyRegistered(beanDefinition, registry, keyValueTemplateName, configurationSource.getSource());
            }
        }
    }

    /**
     * Get the default {@link RootBeanDefinition} for {@link com.rocket.summer.framework.data.keyvalue.core.KeyValueTemplate}.
     *
     * @return {@literal null} to explicitly not register a template.
     */
    protected AbstractBeanDefinition getDefaultKeyValueTemplateBeanDefinition(
            RepositoryConfigurationSource configurationSource) {
        return null;
    }

    protected abstract String getDefaultKeyValueTemplateRef();
}

