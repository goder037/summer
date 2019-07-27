package com.rocket.summer.framework.validation.support;


import com.rocket.summer.framework.web.ui.ExtendedModelMap;

/**
 * Subclass of {@link com.rocket.summer.framework.ui.ExtendedModelMap} that
 * automatically removes a {@link com.rocket.summer.framework.validation.BindingResult}
 * object if the corresponding target attribute gets replaced.
 *
 * <p>Used by {@link com.rocket.summer.framework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter}
 *
 * @author Juergen Hoeller
 * @since 2.5.6
 * @see com.rocket.summer.framework.validation.BindingResult
 */
public class BindingAwareModelMap extends ExtendedModelMap {
}
