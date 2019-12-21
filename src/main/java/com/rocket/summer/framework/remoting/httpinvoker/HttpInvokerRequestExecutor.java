package com.rocket.summer.framework.remoting.httpinvoker;

import java.io.IOException;

import com.rocket.summer.framework.remoting.support.RemoteInvocation;
import com.rocket.summer.framework.remoting.support.RemoteInvocationResult;

/**
 * Strategy interface for actual execution of an HTTP invoker request.
 * Used by HttpInvokerClientInterceptor and its subclass
 * HttpInvokerProxyFactoryBean.
 *
 * <p>Two implementations are provided out of the box:
 * <ul>
 * <li><b>{@code SimpleHttpInvokerRequestExecutor}:</b>
 * Uses JDK facilities to execute POST requests, without support
 * for HTTP authentication or advanced configuration options.
 * <li><b>{@code HttpComponentsHttpInvokerRequestExecutor}:</b>
 * Uses Apache's Commons HttpClient to execute POST requests,
 * allowing to use a preconfigured HttpClient instance
 * (potentially with authentication, HTTP connection pooling, etc).
 * </ul>
 *
 * @author Juergen Hoeller
 * @since 1.1
 * @see HttpInvokerClientInterceptor#setHttpInvokerRequestExecutor
 */
public interface HttpInvokerRequestExecutor {

    /**
     * Execute a request to send the given remote invocation.
     * @param config the HTTP invoker configuration that specifies the
     * target service
     * @param invocation the RemoteInvocation to execute
     * @return the RemoteInvocationResult object
     * @throws IOException if thrown by I/O operations
     * @throws ClassNotFoundException if thrown during deserialization
     * @throws Exception in case of general errors
     */
    RemoteInvocationResult executeRequest(HttpInvokerClientConfiguration config, RemoteInvocation invocation)
            throws Exception;

}

