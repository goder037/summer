package com.rocket.summer.framework.web.servlet.mvc.method.annotation;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import com.rocket.summer.framework.core.MethodParameter;
import com.rocket.summer.framework.util.CollectionUtils;
import com.rocket.summer.framework.util.StringUtils;
import com.rocket.summer.framework.web.bind.annotation.PathVariable;
import com.rocket.summer.framework.web.bind.support.WebDataBinderFactory;
import com.rocket.summer.framework.web.context.request.NativeWebRequest;
import com.rocket.summer.framework.web.context.request.RequestAttributes;
import com.rocket.summer.framework.web.method.support.HandlerMethodArgumentResolver;
import com.rocket.summer.framework.web.method.support.ModelAndViewContainer;
import com.rocket.summer.framework.web.servlet.HandlerMapping;

/**
 * Resolves {@link Map} method arguments annotated with an @{@link PathVariable}
 * where the annotation does not specify a path variable name. The created
 * {@link Map} contains all URI template name/value pairs.
 *
 * @author Rossen Stoyanchev
 * @since 3.2
 * @see PathVariableMethodArgumentResolver
 */
public class PathVariableMapMethodArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        PathVariable ann = parameter.getParameterAnnotation(PathVariable.class);
        return (ann != null && (Map.class.isAssignableFrom(parameter.getParameterType()))
                && !StringUtils.hasText(ann.value()));
    }

    /**
     * Return a Map with all URI template variables or an empty map.
     */
    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {

        @SuppressWarnings("unchecked")
        Map<String, String> uriTemplateVars =
                (Map<String, String>) webRequest.getAttribute(
                        HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE, RequestAttributes.SCOPE_REQUEST);

        if (!CollectionUtils.isEmpty(uriTemplateVars)) {
            return new LinkedHashMap<String, String>(uriTemplateVars);
        }
        else {
            return Collections.emptyMap();
        }
    }

}

