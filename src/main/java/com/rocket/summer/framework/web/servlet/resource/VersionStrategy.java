package com.rocket.summer.framework.web.servlet.resource;

import com.rocket.summer.framework.core.io.Resource;

/**
 * An extension of {@link VersionPathStrategy} that adds a method
 * to determine the actual version of a {@link Resource}.
 *
 * @author Brian Clozel
 * @author Rossen Stoyanchev
 * @since 4.1
 * @see VersionResourceResolver
 */
public interface VersionStrategy extends VersionPathStrategy {

    /**
     * Determine the version for the given resource.
     * @param resource the resource to check
     * @return the version (never {@code null})
     */
    String getResourceVersion(Resource resource);

}
