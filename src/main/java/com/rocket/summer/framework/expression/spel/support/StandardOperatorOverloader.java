package com.rocket.summer.framework.expression.spel.support;

import com.rocket.summer.framework.expression.EvaluationException;
import com.rocket.summer.framework.expression.Operation;
import com.rocket.summer.framework.expression.OperatorOverloader;

/**
 * @author Juergen Hoeller
 * @since 3.0
 */
public class StandardOperatorOverloader implements OperatorOverloader {

    @Override
    public boolean overridesOperation(Operation operation, Object leftOperand, Object rightOperand)
            throws EvaluationException {
        return false;
    }

    @Override
    public Object operate(Operation operation, Object leftOperand, Object rightOperand) throws EvaluationException {
        throw new EvaluationException("No operation overloaded by default");
    }

}

