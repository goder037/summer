package com.rocket.summer.framework.remoting.support;

import org.aopalliance.intercept.MethodInvocation;

/**
 * Strategy interface for creating a {@link RemoteInvocation} from an AOP Alliance
 * {@link org.aopalliance.intercept.MethodInvocation}.
 *
 * <p>Used by {@link com.rocket.summer.framework.remoting.rmi.RmiClientInterceptor} (for RMI invokers)
 * and by {@link com.rocket.summer.framework.remoting.httpinvoker.HttpInvokerClientInterceptor}.
 *
 * @author Juergen Hoeller
 * @since 1.1
 * @see DefaultRemoteInvocationFactory
 * @see com.rocket.summer.framework.remoting.rmi.RmiClientInterceptor#setRemoteInvocationFactory
 * @see com.rocket.summer.framework.remoting.httpinvoker.HttpInvokerClientInterceptor#setRemoteInvocationFactory
 */
public interface RemoteInvocationFactory {

    /**
     * Create a serializable RemoteInvocation object from the given AOP
     * MethodInvocation.
     * <p>Can be implemented to add custom context information to the
     * remote invocation, for example user credentials.
     * @param methodInvocation the original AOP MethodInvocation object
     * @return the RemoteInvocation object
     */
    RemoteInvocation createRemoteInvocation(MethodInvocation methodInvocation);

}
