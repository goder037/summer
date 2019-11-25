package com.rocket.summer.framework.data.keyvalue.core;

import com.rocket.summer.framework.expression.EvaluationContext;
import com.rocket.summer.framework.expression.spel.standard.SpelExpression;
import com.rocket.summer.framework.expression.spel.support.SimpleEvaluationContext;
import com.rocket.summer.framework.util.Assert;

/**
 * {@link SpelCriteria} allows to pass on a {@link SpelExpression} and {@link EvaluationContext} to the actual query
 * processor. This decouples the {@link SpelExpression} from the context it is used in.
 *
 * @author Christoph Strobl
 * @author Oliver Gierke
 */
public class SpelCriteria {

    private final SpelExpression expression;
    private final EvaluationContext context;

    /**
     * Creates a new {@link SpelCriteria} for the given {@link SpelExpression}.
     *
     * @param expression must not be {@literal null}.
     */
    public SpelCriteria(SpelExpression expression) {
        this(expression, SimpleEvaluationContext.forReadOnlyDataBinding().withInstanceMethods().build());
    }

    /**
     * Creates new {@link SpelCriteria}.
     *
     * @param expression must not be {@literal null}.
     * @param context must not be {@literal null}.
     */
    public SpelCriteria(SpelExpression expression, EvaluationContext context) {

        Assert.notNull(expression, "SpEL expression must not be null!");
        Assert.notNull(context, "EvaluationContext must not be null!");

        this.expression = expression;
        this.context = context;
    }

    /**
     * @return will never be {@literal null}.
     */
    public EvaluationContext getContext() {
        return context;
    }

    /**
     * @return will never be {@literal null}.
     */
    public SpelExpression getExpression() {
        return expression;
    }
}

