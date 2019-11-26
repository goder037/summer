package com.rocket.summer.framework.data.keyvalue.core;

import java.util.Comparator;

import com.rocket.summer.framework.data.domain.Sort.Direction;
import com.rocket.summer.framework.data.domain.Sort.NullHandling;
import com.rocket.summer.framework.data.domain.Sort.Order;
import com.rocket.summer.framework.data.keyvalue.core.query.KeyValueQuery;
import com.rocket.summer.framework.expression.spel.standard.SpelExpressionParser;
import com.rocket.summer.framework.util.Assert;
import com.rocket.summer.framework.util.comparator.CompoundComparator;

/**
 * {@link SortAccessor} implementation capable of creating {@link SpelPropertyComparator}.
 *
 * @author Christoph Strobl
 * @author Oliver Gierke
 */
class SpelSortAccessor implements SortAccessor<Comparator<?>> {

    private final SpelExpressionParser parser;

    /**
     * @param parser must not be {@literal null}.
     */
    public SpelSortAccessor(SpelExpressionParser parser) {

        Assert.notNull(parser, "SpelExpressionParser must not be null!");
        this.parser = parser;
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.keyvalue.core.SortAccessor#resolve(com.rocket.summer.framework.data.keyvalue.core.query.KeyValueQuery)
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public Comparator<?> resolve(KeyValueQuery<?> query) {

        if (query == null || query.getSort() == null) {
            return null;
        }

        CompoundComparator compoundComperator = new CompoundComparator();
        for (Order order : query.getSort()) {

            SpelPropertyComparator<?> spelSort = new SpelPropertyComparator(order.getProperty(), parser);

            if (Direction.DESC.equals(order.getDirection())) {

                spelSort.desc();

                if (order.getNullHandling() != null && !NullHandling.NATIVE.equals(order.getNullHandling())) {
                    spelSort = NullHandling.NULLS_FIRST.equals(order.getNullHandling()) ? spelSort.nullsFirst() : spelSort
                            .nullsLast();
                }
            }
            compoundComperator.addComparator(spelSort);
        }

        return compoundComperator;
    }
}

