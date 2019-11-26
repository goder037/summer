package com.rocket.summer.framework.data.redis.core.mapping;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.rocket.summer.framework.core.annotation.AnnotationUtils;
import com.rocket.summer.framework.data.keyvalue.annotation.KeySpace;
import com.rocket.summer.framework.data.keyvalue.core.mapping.KeySpaceResolver;
import com.rocket.summer.framework.data.keyvalue.core.mapping.KeyValuePersistentEntity;
import com.rocket.summer.framework.data.keyvalue.core.mapping.KeyValuePersistentProperty;
import com.rocket.summer.framework.data.keyvalue.core.mapping.context.KeyValueMappingContext;
import com.rocket.summer.framework.data.mapping.PersistentProperty;
import com.rocket.summer.framework.data.mapping.context.MappingContext;
import com.rocket.summer.framework.data.mapping.model.SimpleTypeHolder;
import com.rocket.summer.framework.data.redis.core.PartialUpdate;
import com.rocket.summer.framework.data.redis.core.PartialUpdate.PropertyUpdate;
import com.rocket.summer.framework.data.redis.core.PartialUpdate.UpdateCommand;
import com.rocket.summer.framework.data.redis.core.RedisHash;
import com.rocket.summer.framework.data.redis.core.TimeToLive;
import com.rocket.summer.framework.data.redis.core.TimeToLiveAccessor;
import com.rocket.summer.framework.data.redis.core.convert.KeyspaceConfiguration;
import com.rocket.summer.framework.data.redis.core.convert.KeyspaceConfiguration.KeyspaceSettings;
import com.rocket.summer.framework.data.redis.core.convert.MappingConfiguration;
import com.rocket.summer.framework.data.redis.core.index.IndexConfiguration;
import com.rocket.summer.framework.data.util.TypeInformation;
import com.rocket.summer.framework.util.Assert;
import com.rocket.summer.framework.util.ClassUtils;
import com.rocket.summer.framework.util.NumberUtils;
import com.rocket.summer.framework.util.ReflectionUtils;
import com.rocket.summer.framework.util.ReflectionUtils.MethodCallback;
import com.rocket.summer.framework.util.ReflectionUtils.MethodFilter;
import com.rocket.summer.framework.util.StringUtils;

/**
 * Redis specific {@link MappingContext}.
 *
 * @author Christoph Strobl
 * @author Oliver Gierke
 * @since 1.7
 */
public class RedisMappingContext extends KeyValueMappingContext {

    private final MappingConfiguration mappingConfiguration;
    private final TimeToLiveAccessor timeToLiveAccessor;

    private KeySpaceResolver fallbackKeySpaceResolver;

    /**
     * Creates new {@link RedisMappingContext} with empty {@link MappingConfiguration}.
     */
    public RedisMappingContext() {
        this(new MappingConfiguration(new IndexConfiguration(), new KeyspaceConfiguration()));
    }

    /**
     * Creates new {@link RedisMappingContext}.
     *
     * @param mappingConfiguration can be {@literal null}.
     */
    public RedisMappingContext(MappingConfiguration mappingConfiguration) {

        this.mappingConfiguration = mappingConfiguration != null ? mappingConfiguration
                : new MappingConfiguration(new IndexConfiguration(), new KeyspaceConfiguration());

        setFallbackKeySpaceResolver(new ConfigAwareKeySpaceResolver(this.mappingConfiguration.getKeyspaceConfiguration()));
        this.timeToLiveAccessor = new ConfigAwareTimeToLiveAccessor(this.mappingConfiguration.getKeyspaceConfiguration(),
                this);
    }

    /**
     * Configures the {@link KeySpaceResolver} to be used if not explicit key space is annotated to the domain type.
     *
     * @param fallbackKeySpaceResolver can be {@literal null}.
     */
    public void setFallbackKeySpaceResolver(KeySpaceResolver fallbackKeySpaceResolver) {
        this.fallbackKeySpaceResolver = fallbackKeySpaceResolver;
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.mapping.context.AbstractMappingContext#createPersistentEntity(com.rocket.summer.framework.data.util.TypeInformation)
     */
    @Override
    protected <T> RedisPersistentEntity<T> createPersistentEntity(TypeInformation<T> typeInformation) {
        return new BasicRedisPersistentEntity<T>(typeInformation, fallbackKeySpaceResolver, timeToLiveAccessor);
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.mapping.context.AbstractMappingContext#getPersistentEntity(java.lang.Class)
     */
    @Override
    public RedisPersistentEntity<?> getPersistentEntity(Class<?> type) {
        return (RedisPersistentEntity<?>) super.getPersistentEntity(type);
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.mapping.context.AbstractMappingContext#getPersistentEntity(com.rocket.summer.framework.data.mapping.PersistentProperty)
     */
    @Override
    public RedisPersistentEntity<?> getPersistentEntity(KeyValuePersistentProperty persistentProperty) {
        return (RedisPersistentEntity<?>) super.getPersistentEntity(persistentProperty);
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.mapping.context.AbstractMappingContext#getPersistentEntity(com.rocket.summer.framework.data.util.TypeInformation)
     */
    @Override
    public RedisPersistentEntity<?> getPersistentEntity(TypeInformation<?> type) {
        return (RedisPersistentEntity<?>) super.getPersistentEntity(type);
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.keyvalue.core.mapping.context.KeyValueMappingContext#createPersistentProperty(java.lang.reflect.Field, java.beans.PropertyDescriptor, com.rocket.summer.framework.data.keyvalue.core.mapping.KeyValuePersistentEntity, com.rocket.summer.framework.data.mapping.model.SimpleTypeHolder)
     */
    @Override
    protected KeyValuePersistentProperty createPersistentProperty(Field field, PropertyDescriptor descriptor,
                                                                  KeyValuePersistentEntity<?> owner, SimpleTypeHolder simpleTypeHolder) {
        return new RedisPersistentProperty(field, descriptor, owner, simpleTypeHolder);
    }

    /**
     * Get the {@link MappingConfiguration} used.
     *
     * @return never {@literal null}.
     */
    public MappingConfiguration getMappingConfiguration() {
        return mappingConfiguration;
    }

    /**
     * {@link KeySpaceResolver} implementation considering {@link KeySpace} and {@link KeyspaceConfiguration}.
     *
     * @author Christoph Strobl
     * @since 1.7
     */
    static class ConfigAwareKeySpaceResolver implements KeySpaceResolver {

        private final KeyspaceConfiguration keyspaceConfig;

        public ConfigAwareKeySpaceResolver(KeyspaceConfiguration keyspaceConfig) {

            this.keyspaceConfig = keyspaceConfig;
        }

        /*
         * (non-Javadoc)
         * @see com.rocket.summer.framework.data.keyvalue.core.mapping.KeySpaceResolver#resolveKeySpace(java.lang.Class)
         */
        @Override
        public String resolveKeySpace(Class<?> type) {

            Assert.notNull(type, "Type must not be null!");
            if (keyspaceConfig.hasSettingsFor(type)) {

                String value = keyspaceConfig.getKeyspaceSettings(type).getKeyspace();
                if (StringUtils.hasText(value)) {
                    return value;
                }
            }

            return ClassNameKeySpaceResolver.INSTANCE.resolveKeySpace(type);
        }
    }

    /**
     * {@link KeySpaceResolver} implementation considering {@link KeySpace}.
     *
     * @author Christoph Strobl
     * @since 1.7
     */
    enum ClassNameKeySpaceResolver implements KeySpaceResolver {

        INSTANCE;

        /*
         * (non-Javadoc)
         * @see com.rocket.summer.framework.data.keyvalue.core.KeySpaceResolver#resolveKeySpace(java.lang.Class)
         */
        @Override
        public String resolveKeySpace(Class<?> type) {

            Assert.notNull(type, "Type must not be null!");
            return ClassUtils.getUserClass(type).getName();
        }
    }

    /**
     * {@link TimeToLiveAccessor} implementation considering {@link KeyspaceConfiguration}.
     *
     * @author Christoph Strobl
     * @since 1.7
     */
    static class ConfigAwareTimeToLiveAccessor implements TimeToLiveAccessor {

        private final Map<Class<?>, Long> defaultTimeouts;
        private final Map<Class<?>, PersistentProperty<?>> timeoutProperties;
        private final Map<Class<?>, Method> timeoutMethods;
        private final KeyspaceConfiguration keyspaceConfig;
        private final RedisMappingContext mappingContext;

        /**
         * Creates new {@link ConfigAwareTimeToLiveAccessor}
         *
         * @param keyspaceConfig must not be {@literal null}.
         * @param mappingContext must not be {@literal null}.
         */
        ConfigAwareTimeToLiveAccessor(KeyspaceConfiguration keyspaceConfig, RedisMappingContext mappingContext) {

            Assert.notNull(keyspaceConfig, "KeyspaceConfiguration must not be null!");
            Assert.notNull(mappingContext, "MappingContext must not be null!");

            this.defaultTimeouts = new HashMap<Class<?>, Long>();
            this.timeoutProperties = new HashMap<Class<?>, PersistentProperty<?>>();
            this.timeoutMethods = new HashMap<Class<?>, Method>();
            this.keyspaceConfig = keyspaceConfig;
            this.mappingContext = mappingContext;
        }

        /*
         * (non-Javadoc)
         * @see com.rocket.summer.framework.data.redis.core.TimeToLiveResolver#resolveTimeToLive(java.lang.Object)
         */
        @Override
        @SuppressWarnings({ "rawtypes" })
        public Long getTimeToLive(final Object source) {

            Assert.notNull(source, "Source must not be null!");
            Class<?> type = source instanceof Class<?> ? (Class<?>) source
                    : (source instanceof PartialUpdate ? ((PartialUpdate) source).getTarget() : source.getClass());

            Long defaultTimeout = resolveDefaultTimeOut(type);
            TimeUnit unit = TimeUnit.SECONDS;

            PersistentProperty<?> ttlProperty = resolveTtlProperty(type);

            if (ttlProperty != null) {

                if (ttlProperty.findAnnotation(TimeToLive.class) != null) {
                    unit = ttlProperty.findAnnotation(TimeToLive.class).unit();
                }
            }

            if (source instanceof PartialUpdate) {

                PartialUpdate<?> update = (PartialUpdate<?>) source;

                if (ttlProperty != null && !update.getPropertyUpdates().isEmpty()) {
                    for (PropertyUpdate pUpdate : update.getPropertyUpdates()) {

                        if (UpdateCommand.SET.equals(pUpdate.getCmd()) && ttlProperty.getName().equals(pUpdate.getPropertyPath())) {

                            return TimeUnit.SECONDS
                                    .convert(NumberUtils.convertNumberToTargetClass((Number) pUpdate.getValue(), Long.class), unit);
                        }
                    }
                }

            } else if (ttlProperty != null) {

                RedisPersistentEntity entity = mappingContext.getPersistentEntity(type);
                Number timeout = (Number) entity.getPropertyAccessor(source).getProperty(ttlProperty);
                if (timeout != null) {
                    return TimeUnit.SECONDS.convert(timeout.longValue(), unit);
                }

            } else {

                Method timeoutMethod = resolveTimeMethod(type);
                if (timeoutMethod != null) {

                    TimeToLive ttl = AnnotationUtils.findAnnotation(timeoutMethod, TimeToLive.class);
                    try {
                        Number timeout = (Number) timeoutMethod.invoke(source);
                        if (timeout != null) {
                            return TimeUnit.SECONDS.convert(timeout.longValue(), ttl.unit());
                        }
                    } catch (IllegalAccessException e) {
                        throw new IllegalStateException(
                                "Not allowed to access method '" + timeoutMethod.getName() + "': " + e.getMessage(), e);
                    } catch (IllegalArgumentException e) {
                        throw new IllegalStateException(
                                "Cannot invoke method '" + timeoutMethod.getName() + " without arguments': " + e.getMessage(), e);
                    } catch (InvocationTargetException e) {
                        throw new IllegalStateException("Cannot access method '" + timeoutMethod.getName() + "': " + e.getMessage(),
                                e);
                    }
                }
            }

            return defaultTimeout;
        }

        private Long resolveDefaultTimeOut(Class<?> type) {

            if (this.defaultTimeouts.containsKey(type)) {
                return defaultTimeouts.get(type);
            }

            Long defaultTimeout = null;

            if (keyspaceConfig.hasSettingsFor(type)) {
                defaultTimeout = keyspaceConfig.getKeyspaceSettings(type).getTimeToLive();
            }

            RedisHash hash = mappingContext.getPersistentEntity(type).findAnnotation(RedisHash.class);
            if (hash != null && hash.timeToLive() > 0) {
                defaultTimeout = hash.timeToLive();
            }

            defaultTimeouts.put(type, defaultTimeout);
            return defaultTimeout;
        }

        @SuppressWarnings({ "rawtypes", "unchecked" })
        private PersistentProperty<?> resolveTtlProperty(Class<?> type) {

            if (timeoutProperties.containsKey(type)) {
                return timeoutProperties.get(type);
            }

            RedisPersistentEntity entity = mappingContext.getPersistentEntity(type);
            PersistentProperty<?> ttlProperty = entity.getPersistentProperty(TimeToLive.class);

            if (ttlProperty != null) {

                timeoutProperties.put(type, ttlProperty);
                return ttlProperty;
            }

            if (keyspaceConfig.hasSettingsFor(type)) {

                KeyspaceSettings settings = keyspaceConfig.getKeyspaceSettings(type);
                if (StringUtils.hasText(settings.getTimeToLivePropertyName())) {

                    ttlProperty = entity.getPersistentProperty(settings.getTimeToLivePropertyName());
                    timeoutProperties.put(type, ttlProperty);
                    return ttlProperty;
                }
            }

            timeoutProperties.put(type, null);
            return null;
        }

        private Method resolveTimeMethod(final Class<?> type) {

            if (timeoutMethods.containsKey(type)) {
                return timeoutMethods.get(type);
            }

            timeoutMethods.put(type, null);
            ReflectionUtils.doWithMethods(type, new MethodCallback() {

                @Override
                public void doWith(Method method) throws IllegalArgumentException, IllegalAccessException {
                    timeoutMethods.put(type, method);
                }
            }, new MethodFilter() {

                @Override
                public boolean matches(Method method) {
                    return ClassUtils.isAssignable(Number.class, method.getReturnType())
                            && AnnotationUtils.findAnnotation(method, TimeToLive.class) != null;
                }
            });

            return timeoutMethods.get(type);
        }
    }

}
