package com.rocket.summer.framework.web.servlet.mvc.annotation;

import com.rocket.summer.framework.web.context.request.NativeWebRequest;
import com.rocket.summer.framework.web.servlet.ModelAndView;
import com.rocket.summer.framework.web.ui.ExtendedModelMap;

import java.lang.reflect.Method;

/**
 * SPI for resolving custom return values from a specific handler method. Typically implemented to detect special return
 * types, resolving well-known result values for them.
 *
 * <p>A typical implementation could look like as follows:
 *
 * <pre class="code">
 * public class MyModelAndViewResolver implements ModelAndViewResolver {
 *
 *   public ModelAndView resolveModelAndView(Method handlerMethod,
 *		   			Class handlerType,
 *		   			Object returnValue,
 *		 			ExtendedModelMap implicitModel,
 *		 			NativeWebRequest webRequest) {
 *     if (returnValue instanceof MySpecialRetVal.class)) {
 *       return new MySpecialRetVal(returnValue);
 *     }
 *     return UNRESOLVED;
 *   }
 * }</pre>
 *
 * @author Arjen Poutsma
 * @see com.rocket.summer.framework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter#setCustomModelAndViewResolvers
 * @since 3.0
 */
public interface ModelAndViewResolver {

    /** Marker to be returned when the resolver does not know how to handle the given method parameter. */
    ModelAndView UNRESOLVED = new ModelAndView();

    ModelAndView resolveModelAndView(Method handlerMethod,
                                     Class handlerType,
                                     Object returnValue,
                                     ExtendedModelMap implicitModel,
                                     NativeWebRequest webRequest);
}
