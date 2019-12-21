package com.rocket.summer.framework.web.servlet.resource;

import com.rocket.summer.framework.core.io.Resource;
import com.rocket.summer.framework.util.Assert;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * A default implementation of {@link ResourceResolverChain} for invoking a list
 * of {@link ResourceResolver}s.
 *
 * @author Jeremy Grelle
 * @author Rossen Stoyanchev
 * @author Sam Brannen
 * @since 4.1
 */
class DefaultResourceResolverChain implements ResourceResolverChain {

    private final List<ResourceResolver> resolvers = new ArrayList<ResourceResolver>();

    private int index = -1;


    public DefaultResourceResolverChain(List<? extends ResourceResolver> resolvers) {
        if (resolvers != null) {
            this.resolvers.addAll(resolvers);
        }
    }


    @Override
    public Resource resolveResource(HttpServletRequest request, String requestPath, List<? extends Resource> locations) {
        ResourceResolver resolver = getNext();
        if (resolver == null) {
            return null;
        }

        try {
            return resolver.resolveResource(request, requestPath, locations, this);
        }
        finally {
            this.index--;
        }
    }

    @Override
    public String resolveUrlPath(String resourcePath, List<? extends Resource> locations) {
        ResourceResolver resolver = getNext();
        if (resolver == null) {
            return null;
        }

        try {
            return resolver.resolveUrlPath(resourcePath, locations, this);
        }
        finally {
            this.index--;
        }
    }

    private ResourceResolver getNext() {
        Assert.state(this.index <= this.resolvers.size(),
                "Current index exceeds the number of configured ResourceResolvers");

        if (this.index == (this.resolvers.size() - 1)) {
            return null;
        }
        this.index++;
        return this.resolvers.get(this.index);
    }

}

