package com.rocket.summer.framework.data.redis.core.convert;

import com.rocket.summer.framework.data.geo.Point;
import com.rocket.summer.framework.data.redis.core.index.GeoIndexDefinition;
import com.rocket.summer.framework.data.redis.core.index.IndexDefinition;
import com.rocket.summer.framework.data.redis.core.index.SimpleIndexDefinition;

/**
 * @author Christoph Strobl
 * @since 1.8
 */
class IndexedDataFactoryProvider {

    /**
     * @author Christoph Strobl
     * @since 1.8
     */
    IndexedDataFactory getIndexedDataFactory(IndexDefinition definition) {

        if (definition instanceof SimpleIndexDefinition) {
            return new SimpleIndexedPropertyValueFactory((SimpleIndexDefinition) definition);
        } else if (definition instanceof GeoIndexDefinition) {
            return new GeoIndexedPropertyValueFactory(((GeoIndexDefinition) definition));
        }
        return null;
    }

    static interface IndexedDataFactory {
        IndexedData createIndexedDataFor(Object value);
    }

    /**
     * @author Christoph Strobl
     * @since 1.8
     */
    static class SimpleIndexedPropertyValueFactory implements IndexedDataFactory {

        final SimpleIndexDefinition indexDefinition;

        public SimpleIndexedPropertyValueFactory(SimpleIndexDefinition indexDefinition) {
            this.indexDefinition = indexDefinition;
        }

        public SimpleIndexedPropertyValue createIndexedDataFor(Object value) {

            return new SimpleIndexedPropertyValue(indexDefinition.getKeyspace(), indexDefinition.getIndexName(),
                    indexDefinition.valueTransformer().convert(value));
        }
    }

    /**
     * @author Christoph Strobl
     * @since 1.8
     */
    static class GeoIndexedPropertyValueFactory implements IndexedDataFactory {

        final GeoIndexDefinition indexDefinition;

        public GeoIndexedPropertyValueFactory(GeoIndexDefinition indexDefinition) {
            this.indexDefinition = indexDefinition;
        }

        public GeoIndexedPropertyValue createIndexedDataFor(Object value) {

            return new GeoIndexedPropertyValue(indexDefinition.getKeyspace(), indexDefinition.getPath(),
                    (Point) indexDefinition.valueTransformer().convert(value));
        }
    }
}

