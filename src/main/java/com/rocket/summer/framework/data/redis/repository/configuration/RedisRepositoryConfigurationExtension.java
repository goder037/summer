package com.rocket.summer.framework.data.redis.repository.configuration;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Collections;

import com.rocket.summer.framework.beans.DirectFieldAccessor;
import com.rocket.summer.framework.beans.MutablePropertyValues;
import com.rocket.summer.framework.beans.factory.config.BeanDefinition;
import com.rocket.summer.framework.beans.factory.config.ConstructorArgumentValues;
import com.rocket.summer.framework.beans.factory.config.RuntimeBeanReference;
import com.rocket.summer.framework.beans.factory.support.AbstractBeanDefinition;
import com.rocket.summer.framework.beans.factory.support.BeanDefinitionRegistry;
import com.rocket.summer.framework.beans.factory.support.GenericBeanDefinition;
import com.rocket.summer.framework.beans.factory.support.RootBeanDefinition;
import com.rocket.summer.framework.core.annotation.AnnotationAttributes;
import com.rocket.summer.framework.data.keyvalue.repository.config.KeyValueRepositoryConfigurationExtension;
import com.rocket.summer.framework.data.redis.core.RedisHash;
import com.rocket.summer.framework.data.redis.core.RedisKeyValueAdapter;
import com.rocket.summer.framework.data.redis.core.RedisKeyValueTemplate;
import com.rocket.summer.framework.data.redis.core.convert.CustomConversions;
import com.rocket.summer.framework.data.redis.core.convert.MappingConfiguration;
import com.rocket.summer.framework.data.redis.core.convert.MappingRedisConverter;
import com.rocket.summer.framework.data.redis.core.mapping.RedisMappingContext;
import com.rocket.summer.framework.data.repository.config.RepositoryConfigurationExtension;
import com.rocket.summer.framework.data.repository.config.RepositoryConfigurationSource;
import com.rocket.summer.framework.util.StringUtils;

/**
 * {@link RepositoryConfigurationExtension} for Redis.
 *
 * @author Christoph Strobl
 * @since 1.7
 */
public class RedisRepositoryConfigurationExtension extends KeyValueRepositoryConfigurationExtension {

    private static final String REDIS_CONVERTER_BEAN_NAME = "redisConverter";
    private static final String REDIS_REFERENCE_RESOLVER_BEAN_NAME = "redisReferenceResolver";
    private static final String REDIS_ADAPTER_BEAN_NAME = "redisKeyValueAdapter";
    private static final String REDIS_CUSTOM_CONVERSIONS_BEAN_NAME = "redisCustomConversions";

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.keyvalue.repository.config.KeyValueRepositoryConfigurationExtension#getModuleName()
     */
    @Override
    public String getModuleName() {
        return "Redis";
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.keyvalue.repository.config.KeyValueRepositoryConfigurationExtension#getModulePrefix()
     */
    @Override
    protected String getModulePrefix() {
        return "redis";
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.keyvalue.repository.config.KeyValueRepositoryConfigurationExtension#getDefaultKeyValueTemplateRef()
     */
    @Override
    protected String getDefaultKeyValueTemplateRef() {
        return "redisKeyValueTemplate";
    }

    @Override
    public void registerBeansForRoot(BeanDefinitionRegistry registry, RepositoryConfigurationSource configurationSource) {

        String redisTemplateRef = configurationSource.getAttribute("redisTemplateRef");

        RootBeanDefinition mappingContextDefinition = createRedisMappingContext(configurationSource);
        mappingContextDefinition.setSource(configurationSource.getSource());

        registerIfNotAlreadyRegistered(mappingContextDefinition, registry, MAPPING_CONTEXT_BEAN_NAME, configurationSource);

        // register coustom conversions
        RootBeanDefinition customConversions = new RootBeanDefinition(CustomConversions.class);
        registerIfNotAlreadyRegistered(customConversions, registry, REDIS_CUSTOM_CONVERSIONS_BEAN_NAME,
                configurationSource);

        // Register referenceResolver
        RootBeanDefinition redisReferenceResolver = createRedisReferenceResolverDefinition(redisTemplateRef);
        redisReferenceResolver.setSource(configurationSource.getSource());
        registerIfNotAlreadyRegistered(redisReferenceResolver, registry, REDIS_REFERENCE_RESOLVER_BEAN_NAME,
                configurationSource);

        // Register converter
        RootBeanDefinition redisConverterDefinition = createRedisConverterDefinition();
        redisConverterDefinition.setSource(configurationSource.getSource());

        registerIfNotAlreadyRegistered(redisConverterDefinition, registry, REDIS_CONVERTER_BEAN_NAME, configurationSource);

        // register Adapter
        RootBeanDefinition redisKeyValueAdapterDefinition = new RootBeanDefinition(RedisKeyValueAdapter.class);

        ConstructorArgumentValues constructorArgumentValuesForRedisKeyValueAdapter = new ConstructorArgumentValues();
        if (StringUtils.hasText(redisTemplateRef)) {

            constructorArgumentValuesForRedisKeyValueAdapter.addIndexedArgumentValue(0,
                    new RuntimeBeanReference(redisTemplateRef));
        }

        constructorArgumentValuesForRedisKeyValueAdapter.addIndexedArgumentValue(1,
                new RuntimeBeanReference(REDIS_CONVERTER_BEAN_NAME));

        redisKeyValueAdapterDefinition.setConstructorArgumentValues(constructorArgumentValuesForRedisKeyValueAdapter);

        DirectFieldAccessor fieldAccessor = new DirectFieldAccessor(configurationSource);
        AnnotationAttributes attributes = (AnnotationAttributes) fieldAccessor.getPropertyValue("attributes");

        MutablePropertyValues redisKeyValueAdapterProps = new MutablePropertyValues();
        redisKeyValueAdapterProps.add("enableKeyspaceEvents", attributes.getEnum("enableKeyspaceEvents"));
        redisKeyValueAdapterProps.add("keyspaceNotificationsConfigParameter",
                attributes.getString("keyspaceNotificationsConfigParameter"));
        redisKeyValueAdapterDefinition.setPropertyValues(redisKeyValueAdapterProps);

        registerIfNotAlreadyRegistered(redisKeyValueAdapterDefinition, registry, REDIS_ADAPTER_BEAN_NAME,
                configurationSource);

        super.registerBeansForRoot(registry, configurationSource);
    }

    private RootBeanDefinition createRedisReferenceResolverDefinition(String redisTemplateRef) {

        RootBeanDefinition beanDef = new RootBeanDefinition();
        beanDef.setBeanClassName("com.rocket.summer.framework.data.redis.core.convert.ReferenceResolverImpl");

        ConstructorArgumentValues constructorArgs = new ConstructorArgumentValues();
        constructorArgs.addIndexedArgumentValue(0, new RuntimeBeanReference(redisTemplateRef));

        beanDef.setConstructorArgumentValues(constructorArgs);

        return beanDef;
    }

    private RootBeanDefinition createRedisMappingContext(RepositoryConfigurationSource configurationSource) {

        ConstructorArgumentValues mappingContextArgs = new ConstructorArgumentValues();
        mappingContextArgs.addIndexedArgumentValue(0, createMappingConfigBeanDef(configurationSource));

        RootBeanDefinition mappingContextBeanDef = new RootBeanDefinition(RedisMappingContext.class);
        mappingContextBeanDef.setConstructorArgumentValues(mappingContextArgs);

        return mappingContextBeanDef;
    }

    private BeanDefinition createMappingConfigBeanDef(RepositoryConfigurationSource configurationSource) {

        DirectFieldAccessor dfa = new DirectFieldAccessor(configurationSource);
        AnnotationAttributes aa = (AnnotationAttributes) dfa.getPropertyValue("attributes");

        GenericBeanDefinition indexConfiguration = new GenericBeanDefinition();
        indexConfiguration.setBeanClass(aa.getClass("indexConfiguration"));

        GenericBeanDefinition keyspaceConfig = new GenericBeanDefinition();
        keyspaceConfig.setBeanClass(aa.getClass("keyspaceConfiguration"));

        ConstructorArgumentValues mappingConfigArgs = new ConstructorArgumentValues();
        mappingConfigArgs.addIndexedArgumentValue(0, indexConfiguration);
        mappingConfigArgs.addIndexedArgumentValue(1, keyspaceConfig);

        GenericBeanDefinition mappingConfigBeanDef = new GenericBeanDefinition();
        mappingConfigBeanDef.setBeanClass(MappingConfiguration.class);
        mappingConfigBeanDef.setConstructorArgumentValues(mappingConfigArgs);

        return mappingConfigBeanDef;
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.keyvalue.repository.config.KeyValueRepositoryConfigurationExtension#getDefaultKeyValueTemplateBeanDefinition(com.rocket.summer.framework.data.repository.config.RepositoryConfigurationSource)
     */
    @Override
    protected AbstractBeanDefinition getDefaultKeyValueTemplateBeanDefinition(
            RepositoryConfigurationSource configurationSource) {

        RootBeanDefinition keyValueTemplateDefinition = new RootBeanDefinition(RedisKeyValueTemplate.class);

        ConstructorArgumentValues constructorArgumentValuesForKeyValueTemplate = new ConstructorArgumentValues();
        constructorArgumentValuesForKeyValueTemplate.addIndexedArgumentValue(0,
                new RuntimeBeanReference(REDIS_ADAPTER_BEAN_NAME));
        constructorArgumentValuesForKeyValueTemplate.addIndexedArgumentValue(1,
                new RuntimeBeanReference(MAPPING_CONTEXT_BEAN_NAME));

        keyValueTemplateDefinition.setConstructorArgumentValues(constructorArgumentValuesForKeyValueTemplate);

        return keyValueTemplateDefinition;
    }

    private RootBeanDefinition createRedisConverterDefinition() {

        RootBeanDefinition beanDef = new RootBeanDefinition();
        beanDef.setBeanClass(MappingRedisConverter.class);

        ConstructorArgumentValues args = new ConstructorArgumentValues();
        args.addIndexedArgumentValue(0, new RuntimeBeanReference(MAPPING_CONTEXT_BEAN_NAME));
        beanDef.setConstructorArgumentValues(args);

        MutablePropertyValues props = new MutablePropertyValues();
        props.add("referenceResolver", new RuntimeBeanReference(REDIS_REFERENCE_RESOLVER_BEAN_NAME));
        props.add("customConversions", new RuntimeBeanReference(REDIS_CUSTOM_CONVERSIONS_BEAN_NAME));
        beanDef.setPropertyValues(props);

        return beanDef;
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.repository.config.RepositoryConfigurationExtensionSupport#getIdentifyingAnnotations()
     */
    @Override
    protected Collection<Class<? extends Annotation>> getIdentifyingAnnotations() {
        return Collections.<Class<? extends Annotation>> singleton(RedisHash.class);
    }

}
