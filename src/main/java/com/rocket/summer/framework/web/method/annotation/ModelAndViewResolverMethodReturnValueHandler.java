package com.rocket.summer.framework.web.method.annotation;

import com.rocket.summer.framework.core.MethodParameter;
import com.rocket.summer.framework.web.context.request.NativeWebRequest;
import com.rocket.summer.framework.web.method.support.HandlerMethodReturnValueHandler;
import com.rocket.summer.framework.web.method.support.ModelAndViewContainer;
import com.rocket.summer.framework.web.servlet.ModelAndView;
import com.rocket.summer.framework.web.servlet.mvc.annotation.ModelAndViewResolver;
import com.rocket.summer.framework.web.ui.ExtendedModelMap;

import java.lang.reflect.Method;
import java.util.List;

/**
 * This return value handler is intended to be ordered after all others as it
 * attempts to handle _any_ return value type (i.e. returns {@code true} for
 * all return types).
 *
 * <p>The return value is handled either with a {@link ModelAndViewResolver}
 * or otherwise by regarding it as a model attribute if it is a non-simple
 * type. If neither of these succeeds (essentially simple type other than
 * String), {@link UnsupportedOperationException} is raised.
 *
 * <p><strong>Note:</strong> This class is primarily needed to support
 * {@link ModelAndViewResolver}, which unfortunately cannot be properly
 * adapted to the {@link HandlerMethodReturnValueHandler} contract since the
 * {@link HandlerMethodReturnValueHandler#supportsReturnType} method
 * cannot be implemented. Hence {@code ModelAndViewResolver}s are limited
 * to always being invoked at the end after all other return value
 * handlers have been given a chance. It is recommended to re-implement
 * a {@code ModelAndViewResolver} as {@code HandlerMethodReturnValueHandler},
 * which also provides better access to the return type and method information.
 *
 * @author Rossen Stoyanchev
 * @since 3.1
 */
public class ModelAndViewResolverMethodReturnValueHandler implements HandlerMethodReturnValueHandler {

    private final List<ModelAndViewResolver> mavResolvers;

    private final ModelAttributeMethodProcessor modelAttributeProcessor = new ModelAttributeMethodProcessor(true);

    /**
     * Create a new instance.
     */
    public ModelAndViewResolverMethodReturnValueHandler(List<ModelAndViewResolver> mavResolvers) {
        this.mavResolvers = mavResolvers;
    }

    /**
     * Always returns {@code true}. See class-level note.
     */
    public boolean supportsReturnType(MethodParameter returnType) {
        return true;
    }

    public void handleReturnValue(
            Object returnValue, MethodParameter returnType,
            ModelAndViewContainer mavContainer, NativeWebRequest request)
            throws Exception {

        if (this.mavResolvers != null) {
            for (ModelAndViewResolver mavResolver : this.mavResolvers) {
                Class<?> handlerType = returnType.getDeclaringClass();
                Method method = returnType.getMethod();
                ExtendedModelMap model = (ExtendedModelMap) mavContainer.getModel();
                ModelAndView mav = mavResolver.resolveModelAndView(method, handlerType, returnValue, model, request);
                if (mav != ModelAndViewResolver.UNRESOLVED) {
                    mavContainer.addAllAttributes(mav.getModel());
                    mavContainer.setViewName(mav.getViewName());
                    if (!mav.isReference()) {
                        mavContainer.setView(mav.getView());
                    }
                    return;
                }
            }
        }

        // No suitable ModelAndViewResolver..

        if (this.modelAttributeProcessor.supportsReturnType(returnType)) {
            this.modelAttributeProcessor.handleReturnValue(returnValue, returnType, mavContainer, request);
        }
        else {
            throw new UnsupportedOperationException("Unexpected return type: "
                    + returnType.getParameterType().getName() + " in method: " + returnType.getMethod());
        }
    }

}

