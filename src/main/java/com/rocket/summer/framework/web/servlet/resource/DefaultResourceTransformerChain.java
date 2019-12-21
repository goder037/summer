package com.rocket.summer.framework.web.servlet.resource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;

import com.rocket.summer.framework.core.io.Resource;
import com.rocket.summer.framework.util.Assert;

/**
 * A default implementation of {@link ResourceTransformerChain} for invoking
 * a list of {@link ResourceTransformer}s.
 *
 * @author Rossen Stoyanchev
 * @since 4.1
 */
class DefaultResourceTransformerChain implements ResourceTransformerChain {

    private final ResourceResolverChain resolverChain;

    private final List<ResourceTransformer> transformers = new ArrayList<ResourceTransformer>();

    private int index = -1;


    public DefaultResourceTransformerChain(ResourceResolverChain resolverChain,
                                           List<ResourceTransformer> transformers) {

        Assert.notNull(resolverChain, "ResourceResolverChain is required");
        this.resolverChain = resolverChain;
        if (transformers != null) {
            this.transformers.addAll(transformers);
        }
    }


    public ResourceResolverChain getResolverChain() {
        return this.resolverChain;
    }


    @Override
    public Resource transform(HttpServletRequest request, Resource resource) throws IOException {
        ResourceTransformer transformer = getNext();
        if (transformer == null) {
            return resource;
        }

        try {
            return transformer.transform(request, resource, this);
        }
        finally {
            this.index--;
        }
    }

    private ResourceTransformer getNext() {
        Assert.state(this.index <= this.transformers.size(),
                "Current index exceeds the number of configured ResourceTransformer's");

        if (this.index == (this.transformers.size() - 1)) {
            return null;
        }

        this.index++;
        return this.transformers.get(this.index);
    }

}

