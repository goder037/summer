package com.rocket.summer.framework.web.bind.support;

import com.rocket.summer.framework.web.bind.WebDataBinder;
import com.rocket.summer.framework.web.context.request.WebRequest;

/**
 * Callback interface for initializing a {@link com.rocket.summer.framework.web.bind.WebDataBinder}
 * for performing data binding in the context of a specific web request.
 *
 * @author Juergen Hoeller
 * @since 2.5
 */
public interface WebBindingInitializer {

    /**
     * Initialize the given DataBinder for the given request.
     * @param binder the DataBinder to initialize
     * @param request the web request that the data binding happens within
     */
    void initBinder(WebDataBinder binder, WebRequest request);

}
