package com.rocket.summer.framework.web.servlet.mvc.method;

import com.rocket.summer.framework.http.MediaType;
import com.rocket.summer.framework.util.CollectionUtils;
import com.rocket.summer.framework.util.StringUtils;
import com.rocket.summer.framework.web.HttpMediaTypeNotAcceptableException;
import com.rocket.summer.framework.web.HttpMediaTypeNotSupportedException;
import com.rocket.summer.framework.web.HttpRequestMethodNotSupportedException;
import com.rocket.summer.framework.web.bind.UnsatisfiedServletRequestParameterException;
import com.rocket.summer.framework.web.bind.annotation.RequestMethod;
import com.rocket.summer.framework.web.method.HandlerMethod;
import com.rocket.summer.framework.web.servlet.HandlerMapping;
import com.rocket.summer.framework.web.servlet.handler.AbstractHandlerMethodMapping;
import com.rocket.summer.framework.web.servlet.mvc.condition.NameValueExpression;
import com.rocket.summer.framework.web.servlet.mvc.condition.ParamsRequestCondition;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * Abstract base class for classes for which {@link RequestMappingInfo} defines
 * the mapping between a request and a handler method.
 *
 * @author Arjen Poutsma
 * @author Rossen Stoyanchev
 * @since 3.1.0
 */
public abstract class RequestMappingInfoHandlerMapping extends AbstractHandlerMethodMapping<RequestMappingInfo> {

    /**
     * Get the URL path patterns associated with this {@link RequestMappingInfo}.
     */
    @Override
    protected Set<String> getMappingPathPatterns(RequestMappingInfo info) {
        return info.getPatternsCondition().getPatterns();
    }

    /**
     * Check if the given RequestMappingInfo matches the current request and
     * return a (potentially new) instance with conditions that match the
     * current request -- for example with a subset of URL patterns.
     * @returns an info in case of a match; or {@code null} otherwise.
     */
    @Override
    protected RequestMappingInfo getMatchingMapping(RequestMappingInfo info, HttpServletRequest request) {
        return info.getMatchingCondition(request);
    }

    /**
     * Provide a Comparator to sort RequestMappingInfos matched to a request.
     */
    @Override
    protected Comparator<RequestMappingInfo> getMappingComparator(final HttpServletRequest request) {
        return new Comparator<RequestMappingInfo>() {
            public int compare(RequestMappingInfo info1, RequestMappingInfo info2) {
                return info1.compareTo(info2, request);
            }
        };
    }

    /**
     * Expose URI template variables and producible media types in the request.
     * @see HandlerMapping#URI_TEMPLATE_VARIABLES_ATTRIBUTE
     * @see HandlerMapping#PRODUCIBLE_MEDIA_TYPES_ATTRIBUTE
     */
    @Override
    protected void handleMatch(RequestMappingInfo info, String lookupPath, HttpServletRequest request) {
        super.handleMatch(info, lookupPath, request);

        Set<String> patterns = info.getPatternsCondition().getPatterns();
        String bestPattern = patterns.isEmpty() ? lookupPath : patterns.iterator().next();
        request.setAttribute(BEST_MATCHING_PATTERN_ATTRIBUTE, bestPattern);

        Map<String, String> uriTemplateVariables = getPathMatcher().extractUriTemplateVariables(bestPattern, lookupPath);
        request.setAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE, uriTemplateVariables);

        if (!info.getProducesCondition().getProducibleMediaTypes().isEmpty()) {
            Set<MediaType> mediaTypes = info.getProducesCondition().getProducibleMediaTypes();
            request.setAttribute(PRODUCIBLE_MEDIA_TYPES_ATTRIBUTE, mediaTypes);
        }
    }

    /**
     * Iterate all RequestMappingInfos once again, look if any match by URL at
     * least and raise exceptions accordingly.
     *
     * @throws HttpRequestMethodNotSupportedException
     * 		if there are matches by URL but not by HTTP method
     * @throws HttpMediaTypeNotAcceptableException
     * 		if there are matches by URL but not by consumable media types
     * @throws HttpMediaTypeNotAcceptableException
     * 		if there are matches by URL but not by producible media types
     */
    @Override
    protected HandlerMethod handleNoMatch(Set<RequestMappingInfo> requestMappingInfos,
                                          String lookupPath, HttpServletRequest request) throws ServletException {

        Set<String> allowedMethods = new HashSet<String>(6);

        Set<RequestMappingInfo> patternMatches = new HashSet<RequestMappingInfo>();
        Set<RequestMappingInfo> patternAndMethodMatches = new HashSet<RequestMappingInfo>();

        for (RequestMappingInfo info : requestMappingInfos) {
            if (info.getPatternsCondition().getMatchingCondition(request) != null) {
                patternMatches.add(info);
                if (info.getMethodsCondition().getMatchingCondition(request) != null) {
                    patternAndMethodMatches.add(info);
                }
                else {
                    for (RequestMethod method : info.getMethodsCondition().getMethods()) {
                        allowedMethods.add(method.name());
                    }
                }
            }
        }

        if (patternMatches.isEmpty()) {
            return null;
        }
        else if (patternAndMethodMatches.isEmpty() && !allowedMethods.isEmpty()) {
            throw new HttpRequestMethodNotSupportedException(request.getMethod(), allowedMethods);
        }

        Set<MediaType> consumableMediaTypes;
        Set<MediaType> producibleMediaTypes;
        Set<String> paramConditions;

        if (patternAndMethodMatches.isEmpty()) {
            consumableMediaTypes = getConsumableMediaTypes(request, patternMatches);
            producibleMediaTypes = getProdicubleMediaTypes(request, patternMatches);
            paramConditions = getRequestParams(request, patternMatches);
        }
        else {
            consumableMediaTypes = getConsumableMediaTypes(request, patternAndMethodMatches);
            producibleMediaTypes = getProdicubleMediaTypes(request, patternAndMethodMatches);
            paramConditions = getRequestParams(request, patternAndMethodMatches);
        }

        if (!consumableMediaTypes.isEmpty()) {
            MediaType contentType = null;
            if (StringUtils.hasLength(request.getContentType())) {
                contentType = MediaType.parseMediaType(request.getContentType());
            }
            throw new HttpMediaTypeNotSupportedException(contentType, new ArrayList<MediaType>(consumableMediaTypes));
        }
        else if (!producibleMediaTypes.isEmpty()) {
            throw new HttpMediaTypeNotAcceptableException(new ArrayList<MediaType>(producibleMediaTypes));
        }
        else if (!CollectionUtils.isEmpty(paramConditions)) {
            String[] params = paramConditions.toArray(new String[paramConditions.size()]);
            throw new UnsatisfiedServletRequestParameterException(params, request.getParameterMap());
        }
        else {
            return null;
        }
    }

    private Set<MediaType> getConsumableMediaTypes(HttpServletRequest request, Set<RequestMappingInfo> partialMatches) {
        Set<MediaType> result = new HashSet<MediaType>();
        for (RequestMappingInfo partialMatch : partialMatches) {
            if (partialMatch.getConsumesCondition().getMatchingCondition(request) == null) {
                result.addAll(partialMatch.getConsumesCondition().getConsumableMediaTypes());
            }
        }
        return result;
    }

    private Set<MediaType> getProdicubleMediaTypes(HttpServletRequest request, Set<RequestMappingInfo> partialMatches) {
        Set<MediaType> result = new HashSet<MediaType>();
        for (RequestMappingInfo partialMatch : partialMatches) {
            if (partialMatch.getProducesCondition().getMatchingCondition(request) == null) {
                result.addAll(partialMatch.getProducesCondition().getProducibleMediaTypes());
            }
        }
        return result;
    }

    private Set<String> getRequestParams(HttpServletRequest request, Set<RequestMappingInfo> partialMatches) {
        for (RequestMappingInfo partialMatch : partialMatches) {
            ParamsRequestCondition condition = partialMatch.getParamsCondition();
            if (!CollectionUtils.isEmpty(condition.getExpressions()) && (condition.getMatchingCondition(request) == null)) {
                Set<String> expressions = new HashSet<String>();
                for (NameValueExpression expr : condition.getExpressions()) {
                    expressions.add(expr.toString());
                }
                return expressions;
            }
        }
        return null;
    }

}

