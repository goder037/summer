package com.rocket.summer.framework.web.servlet.resource;

import com.rocket.summer.framework.core.io.Resource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Base class for {@link com.rocket.summer.framework.web.servlet.resource.ResourceResolver}
 * implementations. Provides consistent logging.
 *
 * @author Rossen Stoyanchev
 * @since 4.1
 */
public abstract class AbstractResourceResolver implements ResourceResolver {

    protected final Log logger = LogFactory.getLog(getClass());


    @Override
    public Resource resolveResource(HttpServletRequest request, String requestPath,
                                    List<? extends Resource> locations, ResourceResolverChain chain) {

        if (logger.isTraceEnabled()) {
            logger.trace("Resolving resource for request path \"" + requestPath + "\"");
        }
        return resolveResourceInternal(request, requestPath, locations, chain);
    }

    @Override
    public String resolveUrlPath(String resourceUrlPath, List<? extends Resource> locations,
                                 ResourceResolverChain chain) {

        if (logger.isTraceEnabled()) {
            logger.trace("Resolving public URL for resource path \"" + resourceUrlPath + "\"");
        }

        return resolveUrlPathInternal(resourceUrlPath, locations, chain);
    }


    protected abstract Resource resolveResourceInternal(HttpServletRequest request, String requestPath,
                                                        List<? extends Resource> locations, ResourceResolverChain chain);

    protected abstract String resolveUrlPathInternal(String resourceUrlPath,
                                                     List<? extends Resource> locations, ResourceResolverChain chain);

}

