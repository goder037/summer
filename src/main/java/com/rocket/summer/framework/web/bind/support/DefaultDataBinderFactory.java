package com.rocket.summer.framework.web.bind.support;

import com.rocket.summer.framework.web.bind.WebDataBinder;
import com.rocket.summer.framework.web.context.request.NativeWebRequest;

/**
 * Create a {@link WebRequestDataBinder} instance and initialize it with a
 * {@link WebBindingInitializer}.
 *
 * @author Rossen Stoyanchev
 * @since 3.1
 */
public class DefaultDataBinderFactory implements WebDataBinderFactory {

    private final WebBindingInitializer initializer;

    /**
     * Create new instance.
     * @param initializer for global data binder intialization, or {@code null}
     */
    public DefaultDataBinderFactory(WebBindingInitializer initializer) {
        this.initializer = initializer;
    }

    /**
     * Create a new {@link WebDataBinder} for the given target object and
     * initialize it through a {@link WebBindingInitializer}.
     * @throws Exception in case of invalid state or arguments
     */
    public final WebDataBinder createBinder(NativeWebRequest webRequest, Object target, String objectName)
            throws Exception {
        WebDataBinder dataBinder = createBinderInstance(target, objectName, webRequest);
        if (this.initializer != null) {
            this.initializer.initBinder(dataBinder, webRequest);
        }
        initBinder(dataBinder, webRequest);
        return dataBinder;
    }

    /**
     * Extension point to create the WebDataBinder instance.
     * By default this is {@code WebRequestDataBinder}.
     * @param target the binding target or {@code null} for type conversion only
     * @param objectName the binding target object name
     * @param webRequest the current request
     * @throws Exception in case of invalid state or arguments
     */
    protected WebDataBinder createBinderInstance(Object target, String objectName, NativeWebRequest webRequest)
            throws Exception {
        return new WebRequestDataBinder(target, objectName);
    }

    /**
     * Extension point to further initialize the created data binder instance
     * (e.g. with {@code @InitBinder} methods) after "global" initializaton
     * via {@link WebBindingInitializer}.
     * @param dataBinder the data binder instance to customize
     * @param webRequest the current request
     * @throws Exception if initialization fails
     */
    protected void initBinder(WebDataBinder dataBinder, NativeWebRequest webRequest) throws Exception {
    }

}

