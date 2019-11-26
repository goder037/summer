package com.rocket.summer.framework.data.redis.core.convert;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.rocket.summer.framework.data.geo.Point;
import com.rocket.summer.framework.data.keyvalue.core.mapping.KeyValuePersistentProperty;
import com.rocket.summer.framework.data.mapping.PersistentProperty;
import com.rocket.summer.framework.data.mapping.PersistentPropertyAccessor;
import com.rocket.summer.framework.data.mapping.PropertyHandler;
import com.rocket.summer.framework.data.redis.connection.RedisGeoCommands.GeoLocation;
import com.rocket.summer.framework.data.redis.core.index.ConfigurableIndexDefinitionProvider;
import com.rocket.summer.framework.data.redis.core.index.GeoIndexDefinition;
import com.rocket.summer.framework.data.redis.core.index.GeoIndexed;
import com.rocket.summer.framework.data.redis.core.index.IndexConfiguration;
import com.rocket.summer.framework.data.redis.core.index.IndexDefinition;
import com.rocket.summer.framework.data.redis.core.index.IndexDefinition.Condition;
import com.rocket.summer.framework.data.redis.core.index.IndexDefinition.IndexingContext;
import com.rocket.summer.framework.data.redis.core.index.Indexed;
import com.rocket.summer.framework.data.redis.core.index.SimpleIndexDefinition;
import com.rocket.summer.framework.data.redis.core.mapping.RedisMappingContext;
import com.rocket.summer.framework.data.redis.core.mapping.RedisPersistentEntity;
import com.rocket.summer.framework.data.util.ClassTypeInformation;
import com.rocket.summer.framework.data.util.TypeInformation;
import com.rocket.summer.framework.util.Assert;
import com.rocket.summer.framework.util.ClassUtils;
import com.rocket.summer.framework.util.CollectionUtils;

/**
 * {@link IndexResolver} implementation considering properties annotated with {@link Indexed} or paths set up in
 * {@link IndexConfiguration}.
 *
 * @author Christoph Strobl
 * @author Greg Turnquist
 * @since 1.7
 */
public class PathIndexResolver implements IndexResolver {

    private final Set<Class<?>> VALUE_TYPES = new HashSet<Class<?>>(
            Arrays.<Class<?>> asList(Point.class, GeoLocation.class));

    private ConfigurableIndexDefinitionProvider indexConfiguration;
    private RedisMappingContext mappingContext;
    private IndexedDataFactoryProvider indexedDataFactoryProvider;

    /**
     * Creates new {@link PathIndexResolver} with empty {@link IndexConfiguration}.
     */
    public PathIndexResolver() {
        this(new RedisMappingContext());
    }

    /**
     * Creates new {@link PathIndexResolver} with given {@link IndexConfiguration}.
     *
     * @param mappingContext must not be {@literal null}.
     */
    public PathIndexResolver(RedisMappingContext mappingContext) {

        Assert.notNull(mappingContext, "MappingContext must not be null!");
        this.mappingContext = mappingContext;
        this.indexConfiguration = mappingContext.getMappingConfiguration().getIndexConfiguration();
        this.indexedDataFactoryProvider = new IndexedDataFactoryProvider();
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.redis.core.convert.IndexResolver#resolveIndexesFor(com.rocket.summer.framework.data.util.TypeInformation, java.lang.Object)
     */
    public Set<IndexedData> resolveIndexesFor(TypeInformation<?> typeInformation, Object value) {
        return doResolveIndexesFor(mappingContext.getPersistentEntity(typeInformation).getKeySpace(), "", typeInformation,
                null, value);
    }

    /* (non-Javadoc)
     * @see com.rocket.summer.framework.data.redis.core.convert.IndexResolver#resolveIndexesFor(java.lang.String, java.lang.String, com.rocket.summer.framework.data.util.TypeInformation, java.lang.Object)
     */
    @Override
    public Set<IndexedData> resolveIndexesFor(String keyspace, String path, TypeInformation<?> typeInformation,
                                              Object value) {
        return doResolveIndexesFor(keyspace, path, typeInformation, null, value);
    }

    private Set<IndexedData> doResolveIndexesFor(final String keyspace, final String path,
                                                 TypeInformation<?> typeInformation, PersistentProperty<?> fallback, Object value) {

        RedisPersistentEntity<?> entity = mappingContext.getPersistentEntity(typeInformation);

        if (entity == null || (value != null && VALUE_TYPES.contains(value.getClass()))) {
            return resolveIndex(keyspace, path, fallback, value);
        }

        // this might happen on update where we address a property within an entity directly
        if (!ClassUtils.isAssignable(entity.getType(), value.getClass())) {

            String propertyName = path.lastIndexOf('.') > 0 ? path.substring(path.lastIndexOf('.') + 1, path.length()) : path;
            return resolveIndex(keyspace, path, entity.getPersistentProperty(propertyName), value);
        }

        final PersistentPropertyAccessor accessor = entity.getPropertyAccessor(value);
        final Set<IndexedData> indexes = new LinkedHashSet<IndexedData>();

        entity.doWithProperties(new PropertyHandler<KeyValuePersistentProperty>() {

            @Override
            public void doWithPersistentProperty(KeyValuePersistentProperty persistentProperty) {

                String currentPath = !path.isEmpty() ? path + "." + persistentProperty.getName() : persistentProperty.getName();

                Object propertyValue = accessor.getProperty(persistentProperty);

                if (propertyValue != null) {

                    TypeInformation<?> typeHint = persistentProperty.isMap()
                            ? persistentProperty.getTypeInformation().getMapValueType()
                            : persistentProperty.getTypeInformation().getActualType();

                    if (persistentProperty.isMap()) {

                        for (Entry<?, ?> entry : ((Map<?, ?>) propertyValue).entrySet()) {

                            TypeInformation<?> typeToUse = updateTypeHintForActualValue(typeHint, entry.getValue());
                            indexes.addAll(doResolveIndexesFor(keyspace, currentPath + "." + entry.getKey(),
                                    typeToUse.getActualType(), persistentProperty, entry.getValue()));
                        }

                    } else if (persistentProperty.isCollectionLike()) {

                        final Iterable<?> iterable;

                        if (Iterable.class.isAssignableFrom(propertyValue.getClass())) {
                            iterable = (Iterable<?>) propertyValue;
                        } else if (propertyValue.getClass().isArray()) {
                            iterable = CollectionUtils.arrayToList(propertyValue);
                        } else {
                            throw new RuntimeException(
                                    "Don't know how to handle " + propertyValue.getClass() + " type of collection");
                        }

                        for (Object listValue : iterable) {

                            if (listValue != null) {
                                TypeInformation<?> typeToUse = updateTypeHintForActualValue(typeHint, listValue);
                                indexes.addAll(doResolveIndexesFor(keyspace, currentPath, typeToUse.getActualType(), persistentProperty,
                                        listValue));
                            }
                        }
                    }

                    else if (persistentProperty.isEntity()
                            || persistentProperty.getTypeInformation().getActualType().equals(ClassTypeInformation.OBJECT)) {

                        typeHint = updateTypeHintForActualValue(typeHint, propertyValue);
                        indexes.addAll(doResolveIndexesFor(keyspace, currentPath, typeHint.getActualType(), persistentProperty,
                                propertyValue));
                    } else {
                        indexes.addAll(resolveIndex(keyspace, currentPath, persistentProperty, propertyValue));
                    }
                }

            }

            private TypeInformation<?> updateTypeHintForActualValue(TypeInformation<?> typeHint, Object propertyValue) {

                if (typeHint.equals(ClassTypeInformation.OBJECT) || typeHint.getClass().isInterface()) {
                    try {
                        typeHint = mappingContext.getPersistentEntity(propertyValue.getClass()).getTypeInformation();
                    } catch (Exception e) {
                        // ignore for cases where property value cannot be resolved as an entity, in that case the provided type
                        // hint has to be sufficient
                    }
                }
                return typeHint;
            }

        });

        return indexes;
    }

    protected Set<IndexedData> resolveIndex(String keyspace, String propertyPath, PersistentProperty<?> property,
                                            Object value) {

        String path = normalizeIndexPath(propertyPath, property);

        Set<IndexedData> data = new LinkedHashSet<IndexedData>();

        if (indexConfiguration.hasIndexFor(keyspace, path)) {

            IndexingContext context = new IndexingContext(keyspace, path,
                    property != null ? property.getTypeInformation() : ClassTypeInformation.OBJECT);

            for (IndexDefinition indexDefinition : indexConfiguration.getIndexDefinitionsFor(keyspace, path)) {

                if (!verifyConditions(indexDefinition.getConditions(), value, context)) {
                    continue;
                }

                Object transformedValue = indexDefinition.valueTransformer().convert(value);

                IndexedData indexedData = null;
                if (transformedValue == null) {
                    indexedData = new RemoveIndexedData(indexedData);
                } else {
                    indexedData = indexedDataFactoryProvider.getIndexedDataFactory(indexDefinition).createIndexedDataFor(value);
                }
                data.add(indexedData);
            }
        }

        else if (property != null && property.isAnnotationPresent(Indexed.class)) {

            SimpleIndexDefinition indexDefinition = new SimpleIndexDefinition(keyspace, path);
            indexConfiguration.addIndexDefinition(indexDefinition);

            data.add(indexedDataFactoryProvider.getIndexedDataFactory(indexDefinition).createIndexedDataFor(value));
        } else if (property != null && property.isAnnotationPresent(GeoIndexed.class)) {

            GeoIndexDefinition indexDefinition = new GeoIndexDefinition(keyspace, path);
            indexConfiguration.addIndexDefinition(indexDefinition);

            data.add(indexedDataFactoryProvider.getIndexedDataFactory(indexDefinition).createIndexedDataFor(value));
        }

        return data;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private boolean verifyConditions(Iterable<Condition<?>> conditions, Object value, IndexingContext context) {

        for (Condition condition : conditions) {

            // TODO: generics lookup
            if (!condition.matches(value, context)) {
                return false;
            }
        }

        return true;
    }

    private String normalizeIndexPath(String path, PersistentProperty<?> property) {

        if (property == null) {
            return path;
        }

        if (property.isMap()) {
            return path.replaceAll("\\[", "").replaceAll("\\]", "");
        }
        if (property.isCollectionLike()) {
            return path.replaceAll("\\[(\\p{Digit})*\\]", "").replaceAll("\\.\\.", ".");
        }

        return path;
    }
}

