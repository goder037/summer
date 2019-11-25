package com.rocket.summer.framework.data.repository.query;

import com.rocket.summer.framework.expression.EvaluationContext;
import com.rocket.summer.framework.expression.spel.support.StandardEvaluationContext;
import com.rocket.summer.framework.util.ObjectUtils;

/**
 * Default implementation of {@link EvaluationContextProvider} that always creates a new {@link EvaluationContext}.
 *
 * @author Thomas Darimont
 * @author Oliver Gierke
 * @author Christoph Strobl
 * @since 1.9
 */
public enum DefaultEvaluationContextProvider implements EvaluationContextProvider {

    INSTANCE;

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.repository.query.EvaluationContextProvider#getEvaluationContext(com.rocket.summer.framework.data.repository.query.Parameters, java.lang.Object[])
     */
    @Override
    public <T extends Parameters<?, ?>> EvaluationContext getEvaluationContext(T parameters, Object[] parameterValues) {

        return ObjectUtils.isEmpty(parameterValues) ? new StandardEvaluationContext() : new StandardEvaluationContext(
                parameterValues);
    }
}

