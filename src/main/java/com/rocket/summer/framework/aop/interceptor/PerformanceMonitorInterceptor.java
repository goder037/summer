package com.rocket.summer.framework.aop.interceptor;

import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.logging.Log;

import com.rocket.summer.framework.util.StopWatch;

/**
 * Simple AOP Alliance {@code MethodInterceptor} for performance monitoring.
 * This interceptor has no effect on the intercepted method call.
 *
 * <p>Uses a {@code StopWatch} for the actual performance measuring.
 *
 * @author Rod Johnson
 * @author Dmitriy Kopylenko
 * @author Rob Harrop
 * @see com.rocket.summer.framework.util.StopWatch
 * @see JamonPerformanceMonitorInterceptor
 */
@SuppressWarnings("serial")
public class PerformanceMonitorInterceptor extends AbstractMonitoringInterceptor {

    /**
     * Create a new PerformanceMonitorInterceptor with a static logger.
     */
    public PerformanceMonitorInterceptor() {
    }

    /**
     * Create a new PerformanceMonitorInterceptor with a dynamic or static logger,
     * according to the given flag.
     * @param useDynamicLogger whether to use a dynamic logger or a static logger
     * @see #setUseDynamicLogger
     */
    public PerformanceMonitorInterceptor(boolean useDynamicLogger) {
        setUseDynamicLogger(useDynamicLogger);
    }


    @Override
    protected Object invokeUnderTrace(MethodInvocation invocation, Log logger) throws Throwable {
        String name = createInvocationTraceName(invocation);
        StopWatch stopWatch = new StopWatch(name);
        stopWatch.start(name);
        try {
            return invocation.proceed();
        }
        finally {
            stopWatch.stop();
            writeToLog(logger, stopWatch.shortSummary());
        }
    }

}

