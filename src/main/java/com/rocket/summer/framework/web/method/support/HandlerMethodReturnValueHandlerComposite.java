package com.rocket.summer.framework.web.method.support;

import com.rocket.summer.framework.core.MethodParameter;
import com.rocket.summer.framework.util.Assert;
import com.rocket.summer.framework.web.context.request.NativeWebRequest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Handles method return values by delegating to a list of registered {@link HandlerMethodReturnValueHandler}s.
 * Previously resolved return types are cached for faster lookups.
 *
 * @author Rossen Stoyanchev
 * @since 3.1
 */
public class HandlerMethodReturnValueHandlerComposite implements HandlerMethodReturnValueHandler {

    protected final Log logger = LogFactory.getLog(getClass());

    private final List<HandlerMethodReturnValueHandler> returnValueHandlers =
            new ArrayList<HandlerMethodReturnValueHandler>();

    /**
     * Return a read-only list with the registered handlers, or an empty list.
     */
    public List<HandlerMethodReturnValueHandler> getHandlers() {
        return Collections.unmodifiableList(this.returnValueHandlers);
    }

    /**
     * Whether the given {@linkplain MethodParameter method return type} is supported by any registered
     * {@link HandlerMethodReturnValueHandler}.
     */
    public boolean supportsReturnType(MethodParameter returnType) {
        return getReturnValueHandler(returnType) != null;
    }

    /**
     * Iterate over registered {@link HandlerMethodReturnValueHandler}s and invoke the one that supports it.
     * @exception IllegalStateException if no suitable {@link HandlerMethodReturnValueHandler} is found.
     */
    public void handleReturnValue(
            Object returnValue, MethodParameter returnType,
            ModelAndViewContainer mavContainer, NativeWebRequest webRequest)
            throws Exception {

        HandlerMethodReturnValueHandler handler = getReturnValueHandler(returnType);
        Assert.notNull(handler, "Unknown return value type [" + returnType.getParameterType().getName() + "]");
        handler.handleReturnValue(returnValue, returnType, mavContainer, webRequest);
    }

    /**
     * Find a registered {@link HandlerMethodReturnValueHandler} that supports the given return type.
     */
    private HandlerMethodReturnValueHandler getReturnValueHandler(MethodParameter returnType) {
        for (HandlerMethodReturnValueHandler returnValueHandler : returnValueHandlers) {
            if (logger.isTraceEnabled()) {
                logger.trace("Testing if return value handler [" + returnValueHandler + "] supports [" +
                        returnType.getGenericParameterType() + "]");
            }
            if (returnValueHandler.supportsReturnType(returnType)) {
                return returnValueHandler;
            }
        }
        return null;
    }

    /**
     * Add the given {@link HandlerMethodReturnValueHandler}.
     */
    public HandlerMethodReturnValueHandlerComposite addHandler(HandlerMethodReturnValueHandler returnValuehandler) {
        returnValueHandlers.add(returnValuehandler);
        return this;
    }

    /**
     * Add the given {@link HandlerMethodReturnValueHandler}s.
     */
    public HandlerMethodReturnValueHandlerComposite addHandlers(
            List<? extends HandlerMethodReturnValueHandler> returnValueHandlers) {
        if (returnValueHandlers != null) {
            for (HandlerMethodReturnValueHandler handler : returnValueHandlers) {
                this.returnValueHandlers.add(handler);
            }
        }
        return this;
    }

}