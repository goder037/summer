package com.rocket.summer.framework.web.servlet.resource;

import com.rocket.summer.framework.core.io.Resource;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * A contract for invoking a chain of {@link ResourceResolver}s where each resolver
 * is given a reference to the chain allowing it to delegate when necessary.
 *
 * @author Jeremy Grelle
 * @author Rossen Stoyanchev
 * @author Sam Brannen
 * @since 4.1
 */
public interface ResourceResolverChain {

    /**
     * Resolve the supplied request and request path to a {@link Resource} that
     * exists under one of the given resource locations.
     * @param request the current request
     * @param requestPath the portion of the request path to use
     * @param locations the locations to search in when looking up resources
     * @return the resolved resource or {@code null} if unresolved
     */
    Resource resolveResource(HttpServletRequest request, String requestPath, List<? extends Resource> locations);

    /**
     * Resolve the externally facing <em>public</em> URL path for clients to use
     * to access the resource that is located at the given <em>internal</em>
     * resource path.
     * <p>This is useful when rendering URL links to clients.
     * @param resourcePath the internal resource path
     * @param locations the locations to search in when looking up resources
     * @return the resolved public URL path or {@code null} if unresolved
     */
    String resolveUrlPath(String resourcePath, List<? extends Resource> locations);

}
