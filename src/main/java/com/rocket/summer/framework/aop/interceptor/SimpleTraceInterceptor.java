package com.rocket.summer.framework.aop.interceptor;

import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.logging.Log;

/**
 * Simple AOP Alliance {@code MethodInterceptor} that can be introduced
 * in a chain to display verbose trace information about intercepted method
 * invocations, with method entry and method exit info.
 *
 * <p>Consider using {@code CustomizableTraceInterceptor} for more
 * advanced needs.
 *
 * @author Dmitriy Kopylenko
 * @author Juergen Hoeller
 * @since 1.2
 * @see CustomizableTraceInterceptor
 */
@SuppressWarnings("serial")
public class SimpleTraceInterceptor extends AbstractTraceInterceptor {

    /**
     * Create a new SimpleTraceInterceptor with a static logger.
     */
    public SimpleTraceInterceptor() {
    }

    /**
     * Create a new SimpleTraceInterceptor with dynamic or static logger,
     * according to the given flag.
     * @param useDynamicLogger whether to use a dynamic logger or a static logger
     * @see #setUseDynamicLogger
     */
    public SimpleTraceInterceptor(boolean useDynamicLogger) {
        setUseDynamicLogger(useDynamicLogger);
    }


    @Override
    protected Object invokeUnderTrace(MethodInvocation invocation, Log logger) throws Throwable {
        String invocationDescription = getInvocationDescription(invocation);
        writeToLog(logger, "Entering " + invocationDescription);
        try {
            Object rval = invocation.proceed();
            writeToLog(logger, "Exiting " + invocationDescription);
            return rval;
        }
        catch (Throwable ex) {
            writeToLog(logger, "Exception thrown in " + invocationDescription, ex);
            throw ex;
        }
    }

    /**
     * Return a description for the given method invocation.
     * @param invocation the invocation to describe
     * @return the description
     */
    protected String getInvocationDescription(MethodInvocation invocation) {
        return "method '" + invocation.getMethod().getName() + "' of class [" +
                invocation.getThis().getClass().getName() + "]";
    }

}

