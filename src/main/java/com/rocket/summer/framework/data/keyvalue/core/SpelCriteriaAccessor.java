package com.rocket.summer.framework.data.keyvalue.core;

import com.rocket.summer.framework.data.keyvalue.core.query.KeyValueQuery;
import com.rocket.summer.framework.expression.spel.standard.SpelExpression;
import com.rocket.summer.framework.expression.spel.standard.SpelExpressionParser;
import com.rocket.summer.framework.util.Assert;

/**
 * {@link CriteriaAccessor} implementation capable of {@link SpelExpression}s.
 *
 * @author Christoph Strobl
 * @author Oliver Gierke
 */
class SpelCriteriaAccessor implements CriteriaAccessor<SpelCriteria> {

    private final SpelExpressionParser parser;

    /**
     * Creates a new {@link SpelCriteriaAccessor} using the given {@link SpelExpressionParser}.
     *
     * @param parser must not be {@literal null}.
     */
    public SpelCriteriaAccessor(SpelExpressionParser parser) {

        Assert.notNull(parser, "SpelExpressionParser must not be null!");

        this.parser = parser;
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.keyvalue.core.CriteriaAccessor#resolve(com.rocket.summer.framework.data.keyvalue.core.query.KeyValueQuery)
     */
    @Override
    public SpelCriteria resolve(KeyValueQuery<?> query) {

        if (query.getCriteria() == null) {
            return null;
        }

        if (query.getCriteria() instanceof SpelExpression) {
            return new SpelCriteria((SpelExpression) query.getCriteria());
        }

        if (query.getCriteria() instanceof String) {
            return new SpelCriteria(parser.parseRaw((String) query.getCriteria()));
        }

        if (query.getCriteria() instanceof SpelCriteria) {
            return (SpelCriteria) query.getCriteria();
        }

        throw new IllegalArgumentException("Cannot create SpelCriteria for " + query.getCriteria());
    }
}

