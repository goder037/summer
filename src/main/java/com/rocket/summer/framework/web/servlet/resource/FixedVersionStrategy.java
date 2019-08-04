package com.rocket.summer.framework.web.servlet.resource;

import com.rocket.summer.framework.core.io.Resource;

/**
 * A {@code VersionStrategy} that relies on a fixed version applied as a request
 * path prefix, e.g. reduced SHA, version name, release date, etc.
 *
 * <p>This is useful for example when {@link ContentVersionStrategy} cannot be
 * used such as when using JavaScript module loaders which are in charge of
 * loading the JavaScript resources and need to know their relative paths.
 *
 * @author Brian Clozel
 * @author Rossen Stoyanchev
 * @since 4.1
 * @see VersionResourceResolver
 */
public class FixedVersionStrategy extends AbstractVersionStrategy {

    private final String version;


    /**
     * Create a new FixedVersionStrategy with the given version string.
     * @param version the fixed version string to use
     */
    public FixedVersionStrategy(String version) {
        super(new PrefixVersionPathStrategy(version));
        this.version = version;
    }


    @Override
    public String getResourceVersion(Resource resource) {
        return this.version;
    }

}
