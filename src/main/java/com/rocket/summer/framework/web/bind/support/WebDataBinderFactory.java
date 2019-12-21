package com.rocket.summer.framework.web.bind.support;

import com.rocket.summer.framework.web.bind.WebDataBinder;
import com.rocket.summer.framework.web.context.request.NativeWebRequest;

/**
 * A factory for creating a {@link WebDataBinder} instance for a named target object.
 *
 * @author Arjen Poutsma
 * @since 3.1
 */
public interface WebDataBinderFactory {

    /**
     * Create a {@link WebDataBinder} for the given object.
     * @param webRequest the current request
     * @param target the object to create a data binder for, or {@code null} if creating a binder for a simple type
     * @param objectName the name of the target object
     * @return the created {@link WebDataBinder} instance, never null
     * @throws Exception raised if the creation and initialization of the data binder fails
     */
    WebDataBinder createBinder(NativeWebRequest webRequest, Object target, String objectName) throws Exception;

}
