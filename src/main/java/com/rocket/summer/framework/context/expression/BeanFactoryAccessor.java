package com.rocket.summer.framework.context.expression;

import com.rocket.summer.framework.beans.factory.BeanFactory;
import com.rocket.summer.framework.expression.AccessException;
import com.rocket.summer.framework.expression.EvaluationContext;
import com.rocket.summer.framework.expression.PropertyAccessor;
import com.rocket.summer.framework.expression.TypedValue;

/**
 * EL property accessor that knows how to traverse the beans of a
 * Spring {@link com.rocket.summer.framework.beans.factory.BeanFactory}.
 *
 * @author Juergen Hoeller
 * @author Andy Clement
 * @since 3.0
 */
public class BeanFactoryAccessor implements PropertyAccessor {

    @Override
    public Class<?>[] getSpecificTargetClasses() {
        return new Class<?>[] {BeanFactory.class};
    }

    @Override
    public boolean canRead(EvaluationContext context, Object target, String name) throws AccessException {
        return (((BeanFactory) target).containsBean(name));
    }

    @Override
    public TypedValue read(EvaluationContext context, Object target, String name) throws AccessException {
        return new TypedValue(((BeanFactory) target).getBean(name));
    }

    @Override
    public boolean canWrite(EvaluationContext context, Object target, String name) throws AccessException {
        return false;
    }

    @Override
    public void write(EvaluationContext context, Object target, String name, Object newValue) throws AccessException {
        throw new AccessException("Beans in a BeanFactory are read-only");
    }

}

