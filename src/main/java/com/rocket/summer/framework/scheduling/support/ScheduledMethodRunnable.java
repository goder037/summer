package com.rocket.summer.framework.scheduling.support;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.UndeclaredThrowableException;

import com.rocket.summer.framework.util.ReflectionUtils;

/**
 * Variant of {@link MethodInvokingRunnable} meant to be used for processing
 * of no-arg scheduled methods. Propagates user exceptions to the caller,
 * assuming that an error strategy for Runnables is in place.
 *
 * @author Juergen Hoeller
 * @since 3.0.6
 * @see com.rocket.summer.framework.scheduling.annotation.ScheduledAnnotationBeanPostProcessor
 */
public class ScheduledMethodRunnable implements Runnable {

    private final Object target;

    private final Method method;


    public ScheduledMethodRunnable(Object target, Method method) {
        this.target = target;
        this.method = method;
    }

    public ScheduledMethodRunnable(Object target, String methodName) throws NoSuchMethodException {
        this.target = target;
        this.method = target.getClass().getMethod(methodName);
    }


    public Object getTarget() {
        return this.target;
    }

    public Method getMethod() {
        return this.method;
    }


    @Override
    public void run() {
        try {
            ReflectionUtils.makeAccessible(this.method);
            this.method.invoke(this.target);
        }
        catch (InvocationTargetException ex) {
            ReflectionUtils.rethrowRuntimeException(ex.getTargetException());
        }
        catch (IllegalAccessException ex) {
            throw new UndeclaredThrowableException(ex);
        }
    }

}

