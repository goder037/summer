package com.rocket.summer.framework.web.servlet.mvc.multiaction;

import javax.servlet.http.HttpServletRequest;

/**
 * Interface that parameterizes the MultiActionController class
 * using the <b>Strategy</b> GoF Design pattern, allowing
 * the mapping from incoming request to handler method name
 * to be varied without affecting other application code.
 *
 * <p>Illustrates how delegation can be more flexible than subclassing.
 *
 * @author Rod Johnson
 * @see MultiActionController#setMethodNameResolver
 */
public interface MethodNameResolver {

    /**
     * Return a method name that can handle this request. Such
     * mappings are typically, but not necessarily, based on URL.
     * @param request current HTTP request
     * @return a method name that can handle this request.
     * Never returns <code>null</code>; throws exception if not resolvable.
     * @throws NoSuchRequestHandlingMethodException if no handler method
     * can be found for the given request
     */
    String getHandlerMethodName(HttpServletRequest request) throws NoSuchRequestHandlingMethodException;

}

