package com.rocket.summer.framework.data.redis.repository.query;

import java.util.Iterator;

import com.rocket.summer.framework.dao.InvalidDataAccessApiUsageException;
import com.rocket.summer.framework.data.domain.Sort;
import com.rocket.summer.framework.data.geo.Circle;
import com.rocket.summer.framework.data.geo.Distance;
import com.rocket.summer.framework.data.geo.Metrics;
import com.rocket.summer.framework.data.geo.Point;
import com.rocket.summer.framework.data.keyvalue.core.query.KeyValueQuery;
import com.rocket.summer.framework.data.redis.repository.query.RedisOperationChain.NearPath;
import com.rocket.summer.framework.data.repository.query.ParameterAccessor;
import com.rocket.summer.framework.data.repository.query.parser.AbstractQueryCreator;
import com.rocket.summer.framework.data.repository.query.parser.Part;
import com.rocket.summer.framework.data.repository.query.parser.PartTree;
import com.rocket.summer.framework.util.CollectionUtils;

/**
 * Redis specific query creator.
 *
 * @author Christoph Strobl
 * @author Mark Paluch
 * @since 1.7
 */
public class RedisQueryCreator extends AbstractQueryCreator<KeyValueQuery<RedisOperationChain>, RedisOperationChain> {

    public RedisQueryCreator(PartTree tree, ParameterAccessor parameters) {
        super(tree, parameters);
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.repository.query.parser.AbstractQueryCreator#create(com.rocket.summer.framework.data.repository.query.parser.Part, java.util.Iterator)
     */
    @Override
    protected RedisOperationChain create(Part part, Iterator<Object> iterator) {
        return from(part, iterator, new RedisOperationChain());
    }

    private RedisOperationChain from(Part part, Iterator<Object> iterator, RedisOperationChain sink) {

        switch (part.getType()) {
            case SIMPLE_PROPERTY:
                sink.sismember(part.getProperty().toDotPath(), iterator.next());
                break;
            case WITHIN:
            case NEAR:
                sink.near(getNearPath(part, iterator));
                break;
            default:
                throw new IllegalArgumentException(part.getType() + "is not supported for redis query derivation");
        }

        return sink;
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.repository.query.parser.AbstractQueryCreator#and(com.rocket.summer.framework.data.repository.query.parser.Part, java.lang.Object, java.util.Iterator)
     */
    @Override
    protected RedisOperationChain and(Part part, RedisOperationChain base, Iterator<Object> iterator) {
        return from(part, iterator, base);
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.repository.query.parser.AbstractQueryCreator#or(java.lang.Object, java.lang.Object)
     */
    @Override
    protected RedisOperationChain or(RedisOperationChain base, RedisOperationChain criteria) {
        base.orSismember(criteria.getSismember());
        return base;
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.repository.query.parser.AbstractQueryCreator#complete(java.lang.Object, com.rocket.summer.framework.data.domain.Sort)
     */
    @Override
    protected KeyValueQuery<RedisOperationChain> complete(final RedisOperationChain criteria, Sort sort) {

        KeyValueQuery<RedisOperationChain> query = new KeyValueQuery<RedisOperationChain>(criteria);

        if (query.getCriteria() != null && !CollectionUtils.isEmpty(query.getCriteria().getSismember())
                && !CollectionUtils.isEmpty(query.getCriteria().getOrSismember()))
            if (query.getCriteria().getSismember().size() == 1 && query.getCriteria().getOrSismember().size() == 1) {

                query.getCriteria().getOrSismember().add(query.getCriteria().getSismember().iterator().next());
                query.getCriteria().getSismember().clear();
            }

        if (sort != null) {
            query.setSort(sort);
        }

        return query;
    }

    private NearPath getNearPath(Part part, Iterator<Object> iterator) {

        Object o = iterator.next();

        Point point = null;
        Distance distance = null;

        if (o instanceof Circle) {

            point = ((Circle) o).getCenter();
            distance = ((Circle) o).getRadius();
        } else if (o instanceof Point) {

            point = (Point) o;

            if (!iterator.hasNext()) {
                throw new InvalidDataAccessApiUsageException(
                        "Expected to find distance value for geo query. Are you missing a parameter?");
            }

            Object distObject = iterator.next();
            if (distObject instanceof Distance) {
                distance = (Distance) distObject;
            } else if (distObject instanceof Number) {
                distance = new Distance(((Number) distObject).doubleValue(), Metrics.KILOMETERS);
            } else {
                throw new InvalidDataAccessApiUsageException(String
                        .format("Expected to find Distance or Numeric value for geo query but was %s.", distObject.getClass()));
            }
        } else {
            throw new InvalidDataAccessApiUsageException(
                    String.format("Expected to find a Circle or Point/Distance for geo query but was %s.", o.getClass()));
        }

        return new NearPath(part.getProperty().toDotPath(), point, distance);
    }
}

