package com.rocket.summer.framework.web.servlet.resource;

import com.rocket.summer.framework.core.io.Resource;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * A strategy for resolving a request to a server-side resource.
 *
 * <p>Provides mechanisms for resolving an incoming request to an actual
 * {@link com.rocket.summer.framework.core.io.Resource} and for obtaining the
 * public URL path that clients should use when requesting the resource.
 *
 * @author Jeremy Grelle
 * @author Rossen Stoyanchev
 * @author Sam Brannen
 * @since 4.1
 * @see com.rocket.summer.framework.web.servlet.resource.ResourceResolverChain
 */
public interface ResourceResolver {

    /**
     * Resolve the supplied request and request path to a {@link Resource} that
     * exists under one of the given resource locations.
     * @param request the current request
     * @param requestPath the portion of the request path to use
     * @param locations the locations to search in when looking up resources
     * @param chain the chain of remaining resolvers to delegate to
     * @return the resolved resource or {@code null} if unresolved
     */
    Resource resolveResource(HttpServletRequest request, String requestPath, List<? extends Resource> locations,
                             ResourceResolverChain chain);

    /**
     * Resolve the externally facing <em>public</em> URL path for clients to use
     * to access the resource that is located at the given <em>internal</em>
     * resource path.
     * <p>This is useful when rendering URL links to clients.
     * @param resourcePath the internal resource path
     * @param locations the locations to search in when looking up resources
     * @param chain the chain of resolvers to delegate to
     * @return the resolved public URL path or {@code null} if unresolved
     */
    String resolveUrlPath(String resourcePath, List<? extends Resource> locations, ResourceResolverChain chain);

}
