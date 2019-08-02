package com.rocket.summer.framework.web.accept;

import com.rocket.summer.framework.http.MediaType;
import com.rocket.summer.framework.web.HttpMediaTypeNotAcceptableException;
import com.rocket.summer.framework.web.context.request.NativeWebRequest;

import java.util.List;

/**
 * A strategy for resolving the requested media types for a request.
 *
 * @author Rossen Stoyanchev
 * @since 3.2
 */
public interface ContentNegotiationStrategy {

    /**
     * Resolve the given request to a list of media types. The returned list is
     * ordered by specificity first and by quality parameter second.
     * @param webRequest the current request
     * @return the requested media types or an empty list (never {@code null})
     * @throws HttpMediaTypeNotAcceptableException if the requested media
     * types cannot be parsed
     */
    List<MediaType> resolveMediaTypes(NativeWebRequest webRequest)
            throws HttpMediaTypeNotAcceptableException;

}

