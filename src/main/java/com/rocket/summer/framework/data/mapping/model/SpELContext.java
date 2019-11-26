package com.rocket.summer.framework.data.mapping.model;

import com.rocket.summer.framework.beans.factory.BeanFactory;
import com.rocket.summer.framework.context.expression.BeanFactoryResolver;
import com.rocket.summer.framework.expression.EvaluationContext;
import com.rocket.summer.framework.expression.ExpressionParser;
import com.rocket.summer.framework.expression.PropertyAccessor;
import com.rocket.summer.framework.expression.spel.standard.SpelExpressionParser;
import com.rocket.summer.framework.expression.spel.support.StandardEvaluationContext;

/**
 * Simple factory to create {@link SpelExpressionParser} and {@link EvaluationContext} instances.
 *
 * @author Oliver Gierke
 */
public class SpELContext {

    private final SpelExpressionParser parser;
    private final PropertyAccessor accessor;
    private final BeanFactory factory;

    /**
     * Creates a new {@link SpELContext} with the given {@link PropertyAccessor}. Defaults the
     * {@link SpelExpressionParser}.
     *
     * @param accessor
     */
    public SpELContext(PropertyAccessor accessor) {
        this(accessor, null, null);
    }

    /**
     * Creates a new {@link SpELContext} using the given {@link SpelExpressionParser} and {@link PropertyAccessor}. Will
     * default the {@link SpelExpressionParser} in case the given value for it is {@literal null}.
     *
     * @param parser
     * @param accessor
     */
    public SpELContext(SpelExpressionParser parser, PropertyAccessor accessor) {
        this(accessor, parser, null);
    }

    /**
     * Copy constructor to create a {@link SpELContext} using the given one's {@link PropertyAccessor} and
     * {@link SpelExpressionParser} as well as the given {@link BeanFactory}.
     *
     * @param source
     * @param factory
     */
    public SpELContext(SpELContext source, BeanFactory factory) {
        this(source.accessor, source.parser, factory);
    }

    /**
     * Creates a new {@link SpELContext} using the given {@link SpelExpressionParser}, {@link PropertyAccessor} and
     * {@link BeanFactory}. Will default the {@link SpelExpressionParser} in case the given value for it is
     * {@literal null}.
     *
     * @param accessor
     * @param parser
     * @param factory
     */
    private SpELContext(PropertyAccessor accessor, SpelExpressionParser parser, BeanFactory factory) {

        this.parser = parser == null ? new SpelExpressionParser() : parser;
        this.accessor = accessor;
        this.factory = factory;
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.mapping.model.SpELContext#getParser()
     */
    public ExpressionParser getParser() {
        return this.parser;
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.mapping.model.SpELContext#getEvaluationContext(java.lang.Object)
     */
    public EvaluationContext getEvaluationContext(Object source) {

        StandardEvaluationContext evaluationContext = new StandardEvaluationContext(source);

        if (accessor != null) {
            evaluationContext.addPropertyAccessor(accessor);
        }

        if (factory != null) {
            evaluationContext.setBeanResolver(new BeanFactoryResolver(factory));
        }

        return evaluationContext;
    }

}
