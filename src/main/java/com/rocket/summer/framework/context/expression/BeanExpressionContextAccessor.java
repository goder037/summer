package com.rocket.summer.framework.context.expression;

import com.rocket.summer.framework.beans.factory.config.BeanExpressionContext;
import com.rocket.summer.framework.expression.AccessException;
import com.rocket.summer.framework.expression.EvaluationContext;
import com.rocket.summer.framework.expression.PropertyAccessor;
import com.rocket.summer.framework.expression.TypedValue;

/**
 * EL property accessor that knows how to traverse the beans and contextual objects
 * of a Spring {@link com.rocket.summer.framework.beans.factory.config.BeanExpressionContext}.
 *
 * @author Juergen Hoeller
 * @author Andy Clement
 * @since 3.0
 */
public class BeanExpressionContextAccessor implements PropertyAccessor {

    @Override
    public boolean canRead(EvaluationContext context, Object target, String name) throws AccessException {
        return ((BeanExpressionContext) target).containsObject(name);
    }

    @Override
    public TypedValue read(EvaluationContext context, Object target, String name) throws AccessException {
        return new TypedValue(((BeanExpressionContext) target).getObject(name));
    }

    @Override
    public boolean canWrite(EvaluationContext context, Object target, String name) throws AccessException {
        return false;
    }

    @Override
    public void write(EvaluationContext context, Object target, String name, Object newValue) throws AccessException {
        throw new AccessException("Beans in a BeanFactory are read-only");
    }

    @Override
    public Class<?>[] getSpecificTargetClasses() {
        return new Class<?>[] {BeanExpressionContext.class};
    }

}

