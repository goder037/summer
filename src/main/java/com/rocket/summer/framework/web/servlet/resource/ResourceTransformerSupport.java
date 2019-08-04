package com.rocket.summer.framework.web.servlet.resource;

import com.rocket.summer.framework.core.io.Resource;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;

/**
 * A base class for a {@code ResourceTransformer} with an optional helper method
 * for resolving public links within a transformed resource.
 *
 * @author Brian Clozel
 * @author Rossen Stoyanchev
 * @since 4.1
 */
public abstract class ResourceTransformerSupport implements ResourceTransformer {

    private ResourceUrlProvider resourceUrlProvider;


    /**
     * Configure a {@link ResourceUrlProvider} to use when resolving the public
     * URL of links in a transformed resource (e.g. import links in a CSS file).
     * This is required only for links expressed as full paths, i.e. including
     * context and servlet path, and not for relative links.
     * <p>By default this property is not set. In that case if a
     * {@code ResourceUrlProvider} is needed an attempt is made to find the
     * {@code ResourceUrlProvider} exposed through the
     * {@link com.rocket.summer.framework.web.servlet.resource.ResourceUrlProviderExposingInterceptor
     * ResourceUrlProviderExposingInterceptor} (configured by default in the MVC
     * Java config and XML namespace). Therefore explicitly configuring this
     * property should not be needed in most cases.
     * @param resourceUrlProvider the URL provider to use
     */
    public void setResourceUrlProvider(ResourceUrlProvider resourceUrlProvider) {
        this.resourceUrlProvider = resourceUrlProvider;
    }

    /**
     * Return the configured {@code ResourceUrlProvider}.
     */
    public ResourceUrlProvider getResourceUrlProvider() {
        return this.resourceUrlProvider;
    }


    /**
     * A transformer can use this method when a resource being transformed
     * contains links to other resources. Such links need to be replaced with the
     * public facing link as determined by the resource resolver chain (e.g. the
     * public URL may have a version inserted).
     * @param resourcePath the path to a resource that needs to be re-written
     * @param request the current request
     * @param resource the resource being transformed
     * @param transformerChain the transformer chain
     * @return the resolved URL, or {@code} if not resolvable
     */
    protected String resolveUrlPath(String resourcePath, HttpServletRequest request,
                                    Resource resource, ResourceTransformerChain transformerChain) {

        if (resourcePath.startsWith("/")) {
            // full resource path
            ResourceUrlProvider urlProvider = findResourceUrlProvider(request);
            return (urlProvider != null ? urlProvider.getForRequestUrl(request, resourcePath) : null);
        }
        else {
            // try resolving as relative path
            return transformerChain.getResolverChain().resolveUrlPath(
                    resourcePath, Collections.singletonList(resource));
        }
    }

    private ResourceUrlProvider findResourceUrlProvider(HttpServletRequest request) {
        if (this.resourceUrlProvider != null) {
            return this.resourceUrlProvider;
        }
        return (ResourceUrlProvider) request.getAttribute(
                ResourceUrlProviderExposingInterceptor.RESOURCE_URL_PROVIDER_ATTR);
    }

}

