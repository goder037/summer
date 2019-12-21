package com.rocket.summer.framework.web.servlet.mvc.method.annotation;

import javax.servlet.ServletException;

import com.rocket.summer.framework.core.MethodParameter;
import com.rocket.summer.framework.web.bind.ServletRequestBindingException;
import com.rocket.summer.framework.web.bind.annotation.SessionAttribute;
import com.rocket.summer.framework.web.bind.annotation.ValueConstants;
import com.rocket.summer.framework.web.context.request.NativeWebRequest;
import com.rocket.summer.framework.web.context.request.RequestAttributes;
import com.rocket.summer.framework.web.method.annotation.AbstractNamedValueMethodArgumentResolver;

/**
 * Resolves method arguments annotated with an @{@link SessionAttribute}.
 *
 * @author Rossen Stoyanchev
 * @since 4.3
 */
public class SessionAttributeMethodArgumentResolver extends AbstractNamedValueMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(SessionAttribute.class);
    }

    @Override
    protected NamedValueInfo createNamedValueInfo(MethodParameter parameter) {
        SessionAttribute ann = parameter.getParameterAnnotation(SessionAttribute.class);
        return new NamedValueInfo(ann.name(), ann.required(), ValueConstants.DEFAULT_NONE);
    }

    @Override
    protected Object resolveName(String name, MethodParameter parameter, NativeWebRequest request){
        return request.getAttribute(name, RequestAttributes.SCOPE_SESSION);
    }

    @Override
    protected void handleMissingValue(String name, MethodParameter parameter) throws ServletException {
        throw new ServletRequestBindingException("Missing session attribute '" + name +
                "' of type " + parameter.getNestedParameterType().getSimpleName());
    }

}

