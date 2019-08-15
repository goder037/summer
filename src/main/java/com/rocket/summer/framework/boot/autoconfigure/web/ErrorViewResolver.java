package com.rocket.summer.framework.boot.autoconfigure.web;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.rocket.summer.framework.http.HttpStatus;
import com.rocket.summer.framework.web.servlet.ModelAndView;

/**
 * Interface that can be implemented by beans that resolve error views.
 *
 * @author Phillip Webb
 * @since 1.4.0
 */
public interface ErrorViewResolver {

    /**
     * Resolve an error view for the specified details.
     * @param request the source request
     * @param status the http status of the error
     * @param model the suggested model to be used with the view
     * @return a resolved {@link ModelAndView} or {@code null}
     */
    ModelAndView resolveErrorView(HttpServletRequest request, HttpStatus status,
                                  Map<String, Object> model);

}
