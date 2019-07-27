package com.rocket.summer.framework.web.servlet.mvc.method.annotation;

import com.rocket.summer.framework.web.bind.ServletRequestDataBinder;
import com.rocket.summer.framework.web.bind.support.WebBindingInitializer;
import com.rocket.summer.framework.web.context.request.NativeWebRequest;
import com.rocket.summer.framework.web.method.annotation.InitBinderDataBinderFactory;
import com.rocket.summer.framework.web.method.support.InvocableHandlerMethod;

import java.util.List;

/**
 * Creates a {@code ServletRequestDataBinder}.
 *
 * @author Rossen Stoyanchev
 * @since 3.1
 */
public class ServletRequestDataBinderFactory extends InitBinderDataBinderFactory {

    /**
     * Create a new instance.
     * @param binderMethods one or more {@code @InitBinder} methods
     * @param initializer provides global data binder initialization
     */
    public ServletRequestDataBinderFactory(List<InvocableHandlerMethod> binderMethods, WebBindingInitializer initializer) {
        super(binderMethods, initializer);
    }

    /**
     * Returns an instance of {@link ExtendedServletRequestDataBinder}.
     */
    @Override
    protected ServletRequestDataBinder createBinderInstance(Object target, String objectName, NativeWebRequest request) {
        return new ExtendedServletRequestDataBinder(target, objectName);
    }

}