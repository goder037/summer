package com.rocket.summer.framework.data.repository.query;

import java.lang.reflect.Method;
import java.util.List;

import com.rocket.summer.framework.core.MethodParameter;

/**
 * Default implementation of {@link Parameters}.
 *
 * @author Oliver Gierke
 */
public final class DefaultParameters extends Parameters<DefaultParameters, Parameter> {

    /**
     * Creates a new {@link DefaultParameters} instance from the given {@link Method}.
     *
     * @param method must not be {@literal null}.
     */
    public DefaultParameters(Method method) {
        super(method);
    }

    private DefaultParameters(List<Parameter> parameters) {
        super(parameters);
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.repository.query.Parameters#createParameter(com.rocket.summer.framework.core.MethodParameter)
     */
    @Override
    protected Parameter createParameter(MethodParameter parameter) {
        return new Parameter(parameter);
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.repository.query.Parameters#createFrom(java.util.List)
     */
    @Override
    protected DefaultParameters createFrom(List<Parameter> parameters) {
        return new DefaultParameters(parameters);
    }
}

