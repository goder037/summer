package com.rocket.summer.framework.aop.interceptor;

import java.lang.reflect.Method;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A default {@link AsyncUncaughtExceptionHandler} that simply logs the exception.
 *
 * @author Stephane Nicoll
 * @since 4.1
 */
public class SimpleAsyncUncaughtExceptionHandler implements AsyncUncaughtExceptionHandler {

    private static final Log logger = LogFactory.getLog(SimpleAsyncUncaughtExceptionHandler.class);


    @Override
    public void handleUncaughtException(Throwable ex, Method method, Object... params) {
        if (logger.isErrorEnabled()) {
            logger.error("Unexpected error occurred invoking async method: " + method, ex);
        }
    }

}
