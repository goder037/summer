package com.rocket.summer.framework.data.repository.query;

import com.rocket.summer.framework.expression.EvaluationContext;
import com.rocket.summer.framework.expression.spel.support.StandardEvaluationContext;

/**
 * Provides a way to access a centrally defined potentially shared {@link StandardEvaluationContext}.
 *
 * @author Thomas Darimont
 * @author Oliver Gierke
 * @author Christoph Strobl
 * @since 1.9
 */
public interface EvaluationContextProvider {

    /**
     * Returns an {@link EvaluationContext} built using the given {@link Parameters} and parameter values.
     *
     * @param parameters the {@link Parameters} instance obtained from the query method the context is built for.
     * @param parameterValues the values for the parameters.
     * @return
     */
    <T extends Parameters<?, ?>> EvaluationContext getEvaluationContext(T parameters, Object[] parameterValues);
}

