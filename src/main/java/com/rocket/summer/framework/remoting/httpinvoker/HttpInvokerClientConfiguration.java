package com.rocket.summer.framework.remoting.httpinvoker;

/**
 * Configuration interface for executing HTTP invoker requests.
 *
 * @author Juergen Hoeller
 * @since 1.1
 * @see HttpInvokerRequestExecutor
 * @see HttpInvokerClientInterceptor
 */
public interface HttpInvokerClientConfiguration {

    /**
     * Return the HTTP URL of the target service.
     */
    String getServiceUrl();

    /**
     * Return the codebase URL to download classes from if not found locally.
     * Can consist of multiple URLs, separated by spaces.
     * @return the codebase URL, or {@code null} if none
     * @see java.rmi.server.RMIClassLoader
     */
    String getCodebaseUrl();

}

