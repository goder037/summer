package com.rocket.summer.framework.boot.autoconfigure.web;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.rocket.summer.framework.boot.autoconfigure.web.ErrorProperties.IncludeStacktrace;
import com.rocket.summer.framework.boot.context.embedded.AbstractEmbeddedServletContainerFactory;
import com.rocket.summer.framework.http.HttpStatus;
import com.rocket.summer.framework.http.MediaType;
import com.rocket.summer.framework.http.ResponseEntity;
import com.rocket.summer.framework.stereotype.Controller;
import com.rocket.summer.framework.util.Assert;
import com.rocket.summer.framework.web.bind.annotation.RequestMapping;
import com.rocket.summer.framework.web.bind.annotation.ResponseBody;
import com.rocket.summer.framework.web.servlet.ModelAndView;

/**
 * Basic global error {@link Controller}, rendering {@link ErrorAttributes}. More specific
 * errors can be handled either using Spring MVC abstractions (e.g.
 * {@code @ExceptionHandler}) or by adding servlet
 * {@link AbstractEmbeddedServletContainerFactory#setErrorPages container error pages}.
 *
 * @author Dave Syer
 * @author Phillip Webb
 * @author Michael Stummvoll
 * @author Stephane Nicoll
 * @see ErrorAttributes
 * @see ErrorProperties
 */
@Controller
@RequestMapping("${server.error.path:${error.path:/error}}")
public class BasicErrorController extends AbstractErrorController {

    private final ErrorProperties errorProperties;

    /**
     * Create a new {@link BasicErrorController} instance.
     * @param errorAttributes the error attributes
     * @param errorProperties configuration properties
     */
    public BasicErrorController(ErrorAttributes errorAttributes,
                                ErrorProperties errorProperties) {
        this(errorAttributes, errorProperties,
                Collections.<ErrorViewResolver>emptyList());
    }

    /**
     * Create a new {@link BasicErrorController} instance.
     * @param errorAttributes the error attributes
     * @param errorProperties configuration properties
     * @param errorViewResolvers error view resolvers
     */
    public BasicErrorController(ErrorAttributes errorAttributes,
                                ErrorProperties errorProperties, List<ErrorViewResolver> errorViewResolvers) {
        super(errorAttributes, errorViewResolvers);
        Assert.notNull(errorProperties, "ErrorProperties must not be null");
        this.errorProperties = errorProperties;
    }

    @Override
    public String getErrorPath() {
        return this.errorProperties.getPath();
    }

    @RequestMapping(produces = "text/html")
    public ModelAndView errorHtml(HttpServletRequest request,
                                  HttpServletResponse response) {
        HttpStatus status = getStatus(request);
        Map<String, Object> model = Collections.unmodifiableMap(getErrorAttributes(
                request, isIncludeStackTrace(request, MediaType.TEXT_HTML)));
        response.setStatus(status.value());
        ModelAndView modelAndView = resolveErrorView(request, response, status, model);
        return (modelAndView != null) ? modelAndView : new ModelAndView("error", model);
    }

    @RequestMapping
    @ResponseBody
    public ResponseEntity<Map<String, Object>> error(HttpServletRequest request) {
        Map<String, Object> body = getErrorAttributes(request,
                isIncludeStackTrace(request, MediaType.ALL));
        HttpStatus status = getStatus(request);
        return new ResponseEntity<Map<String, Object>>(body, status);
    }

    /**
     * Determine if the stacktrace attribute should be included.
     * @param request the source request
     * @param produces the media type produced (or {@code MediaType.ALL})
     * @return if the stacktrace attribute should be included
     */
    protected boolean isIncludeStackTrace(HttpServletRequest request,
                                          MediaType produces) {
        IncludeStacktrace include = getErrorProperties().getIncludeStacktrace();
        if (include == IncludeStacktrace.ALWAYS) {
            return true;
        }
        if (include == IncludeStacktrace.ON_TRACE_PARAM) {
            return getTraceParameter(request);
        }
        return false;
    }

    /**
     * Provide access to the error properties.
     * @return the error properties
     */
    protected ErrorProperties getErrorProperties() {
        return this.errorProperties;
    }

}

