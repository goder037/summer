package com.rocket.summer.framework.data.keyvalue.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.rocket.summer.framework.data.keyvalue.core.query.KeyValueQuery;
import com.rocket.summer.framework.expression.spel.SpelEvaluationException;
import com.rocket.summer.framework.expression.spel.standard.SpelExpression;
import com.rocket.summer.framework.expression.spel.standard.SpelExpressionParser;

/**
 * {@link QueryEngine} implementation specific for executing {@link SpelExpression} based {@link KeyValueQuery} against
 * {@link KeyValueAdapter}.
 *
 * @author Christoph Strobl
 * @author Oliver Gierke
 * @param <T>
 */
class SpelQueryEngine<T extends KeyValueAdapter> extends QueryEngine<KeyValueAdapter, SpelCriteria, Comparator<?>> {

    private static final SpelExpressionParser PARSER = new SpelExpressionParser();

    /**
     * Creates a new {@link SpelQueryEngine}.
     */
    public SpelQueryEngine() {
        super(new SpelCriteriaAccessor(PARSER), new SpelSortAccessor(PARSER));
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.keyvalue.core.QueryEngine#execute(java.lang.Object, java.lang.Object, int, int, java.io.Serializable)
     */
    @Override
    public Collection<?> execute(SpelCriteria criteria, Comparator<?> sort, int offset, int rows, Serializable keyspace) {
        return sortAndFilterMatchingRange(getAdapter().getAllOf(keyspace), criteria, sort, offset, rows);
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.keyvalue.core.QueryEngine#count(java.lang.Object, java.io.Serializable)
     */
    @Override
    public long count(SpelCriteria criteria, Serializable keyspace) {
        return filterMatchingRange(getAdapter().getAllOf(keyspace), criteria, -1, -1).size();
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private List<?> sortAndFilterMatchingRange(Iterable<?> source, SpelCriteria criteria, Comparator sort, int offset,
                                               int rows) {

        List<?> tmp = IterableConverter.toList(source);
        if (sort != null) {
            Collections.sort(tmp, sort);
        }

        return filterMatchingRange(tmp, criteria, offset, rows);
    }

    private static <S> List<S> filterMatchingRange(Iterable<S> source, SpelCriteria criteria, int offset, int rows) {

        List<S> result = new ArrayList<S>();

        boolean compareOffsetAndRows = 0 < offset || 0 <= rows;
        int remainingRows = rows;
        int curPos = 0;

        for (S candidate : source) {

            boolean matches = criteria == null;

            if (!matches) {
                try {
                    matches = criteria.getExpression().getValue(criteria.getContext(), candidate, Boolean.class);
                } catch (SpelEvaluationException e) {
                    criteria.getContext().setVariable("it", candidate);
                    matches = criteria.getExpression().getValue(criteria.getContext()) == null ? false : criteria.getExpression()
                            .getValue(criteria.getContext(), Boolean.class);
                }
            }

            if (matches) {
                if (compareOffsetAndRows) {
                    if (curPos >= offset && rows > 0) {
                        result.add(candidate);
                        remainingRows--;
                        if (remainingRows <= 0) {
                            break;
                        }
                    }
                    curPos++;
                } else {
                    result.add(candidate);
                }
            }
        }

        return result;
    }
}

