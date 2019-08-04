package com.rocket.summer.framework.web.servlet.resource;

import com.rocket.summer.framework.core.io.Resource;

/**
 * Interface for a resource descriptor that describes its version with a
 * version string that can be derived from its content and/or metadata.
 *
 * @author Brian Clozel
 * @since 4.2.5
 * @see VersionResourceResolver
 */
public interface VersionedResource extends Resource {

    String getVersion();

}
