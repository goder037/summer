package com.rocket.summer.framework.expression.spel.support;

import java.lang.reflect.Constructor;

import com.rocket.summer.framework.expression.AccessException;
import com.rocket.summer.framework.expression.ConstructorExecutor;
import com.rocket.summer.framework.expression.EvaluationContext;
import com.rocket.summer.framework.expression.TypedValue;
import com.rocket.summer.framework.util.ReflectionUtils;

/**
 * A simple ConstructorExecutor implementation that runs a constructor using reflective
 * invocation.
 *
 * @author Andy Clement
 * @author Juergen Hoeller
 * @since 3.0
 */
public class ReflectiveConstructorExecutor implements ConstructorExecutor {

    private final Constructor<?> ctor;

    private final Integer varargsPosition;


    public ReflectiveConstructorExecutor(Constructor<?> ctor) {
        this.ctor = ctor;
        if (ctor.isVarArgs()) {
            Class<?>[] paramTypes = ctor.getParameterTypes();
            this.varargsPosition = paramTypes.length - 1;
        }
        else {
            this.varargsPosition = null;
        }
    }

    @Override
    public TypedValue execute(EvaluationContext context, Object... arguments) throws AccessException {
        try {
            if (arguments != null) {
                ReflectionHelper.convertArguments(context.getTypeConverter(), arguments, this.ctor, this.varargsPosition);
            }
            if (this.ctor.isVarArgs()) {
                arguments = ReflectionHelper.setupArgumentsForVarargsInvocation(this.ctor.getParameterTypes(), arguments);
            }
            ReflectionUtils.makeAccessible(this.ctor);
            return new TypedValue(this.ctor.newInstance(arguments));
        }
        catch (Exception ex) {
            throw new AccessException("Problem invoking constructor: " + this.ctor, ex);
        }
    }

    public Constructor<?> getConstructor() {
        return this.ctor;
    }

}

