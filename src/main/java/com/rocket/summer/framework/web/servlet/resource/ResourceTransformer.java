package com.rocket.summer.framework.web.servlet.resource;

import com.rocket.summer.framework.core.io.Resource;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * An abstraction for transforming the content of a resource.
 *
 * @author Jeremy Grelle
 * @author Rossen Stoyanchev
 * @since 4.1
 */
public interface ResourceTransformer {

    /**
     * Transform the given resource.
     * @param request the current request
     * @param resource the resource to transform
     * @param transformerChain the chain of remaining transformers to delegate to
     * @return the transformed resource (never {@code null})
     * @throws IOException if the transformation fails
     */
    Resource transform(HttpServletRequest request, Resource resource, ResourceTransformerChain transformerChain)
            throws IOException;

}