package com.rocket.summer.framework.scheduling.annotation;

import java.util.concurrent.Executor;

import com.rocket.summer.framework.aop.interceptor.AsyncUncaughtExceptionHandler;

/**
 * A convenience {@link AsyncConfigurer} that implements all methods
 * so that the defaults are used. Provides a backward compatible alternative
 * of implementing {@link AsyncConfigurer} directly.
 *
 * @author Stephane Nicoll
 * @since 4.1
 */
public class AsyncConfigurerSupport implements AsyncConfigurer {

    @Override
    public Executor getAsyncExecutor() {
        return null;
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return null;
    }

}