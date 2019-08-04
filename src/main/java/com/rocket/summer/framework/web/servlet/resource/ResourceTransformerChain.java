package com.rocket.summer.framework.web.servlet.resource;

import com.rocket.summer.framework.core.io.Resource;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * A contract for invoking a chain of {@link ResourceTransformer}s where each resolver
 * is given a reference to the chain allowing it to delegate when necessary.
 *
 * @author Rossen Stoyanchev
 * @since 4.1
 */
public interface ResourceTransformerChain {

    /**
     * Return the {@code ResourceResolverChain} that was used to resolve the
     * {@code Resource} being transformed. This may be needed for resolving
     * related resources, e.g. links to other resources.
     */
    ResourceResolverChain getResolverChain();

    /**
     * Transform the given resource.
     * @param request the current request
     * @param resource the candidate resource to transform
     * @return the transformed or the same resource, never {@code null}
     * @throws IOException if transformation fails
     */
    Resource transform(HttpServletRequest request, Resource resource) throws IOException;

}