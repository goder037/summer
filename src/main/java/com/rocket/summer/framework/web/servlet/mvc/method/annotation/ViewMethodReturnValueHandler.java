package com.rocket.summer.framework.web.servlet.mvc.method.annotation;

import com.rocket.summer.framework.core.MethodParameter;
import com.rocket.summer.framework.web.context.request.NativeWebRequest;
import com.rocket.summer.framework.web.method.support.HandlerMethodReturnValueHandler;
import com.rocket.summer.framework.web.method.support.ModelAndViewContainer;
import com.rocket.summer.framework.web.servlet.SmartView;
import com.rocket.summer.framework.web.servlet.View;

/**
 * Handles return values that are of type {@link View}.
 *
 * <p>A {@code null} return value is left as-is leaving it to the configured
 * {@link RequestToViewNameTranslator} to select a view name by convention.
 *
 * <p>A {@link View} return type has a set purpose. Therefore this handler
 * should be configured ahead of handlers that support any return value type
 * annotated with {@code @ModelAttribute} or {@code @ResponseBody} to ensure
 * they don't take over.
 *
 * @author Rossen Stoyanchev
 * @since 3.1
 */
public class ViewMethodReturnValueHandler implements HandlerMethodReturnValueHandler {

    public boolean supportsReturnType(MethodParameter returnType) {
        return View.class.isAssignableFrom(returnType.getParameterType());
    }

    public void handleReturnValue(
            Object returnValue, MethodParameter returnType,
            ModelAndViewContainer mavContainer, NativeWebRequest webRequest)
            throws Exception {

        if (returnValue == null) {
            return;
        }
        else if (returnValue instanceof View){
            View view = (View) returnValue;
            mavContainer.setView(view);
            if (view instanceof SmartView) {
                if (((SmartView) view).isRedirectView()) {
                    mavContainer.setRedirectModelScenario(true);
                }
            }
        }
        else {
            // should not happen
            throw new UnsupportedOperationException("Unexpected return type: " +
                    returnType.getParameterType().getName() + " in method: " + returnType.getMethod());
        }
    }

}
