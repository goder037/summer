package com.rocket.summer.framework.beans.factory.support;

import com.rocket.summer.framework.beans.factory.config.BeanDefinition;
import com.rocket.summer.framework.core.io.AbstractResource;
import com.rocket.summer.framework.util.Assert;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Descriptive {@link com.rocket.summer.framework.core.io.Resource} wrapper for
 * a {@link com.rocket.summer.framework.beans.factory.config.BeanDefinition}.
 *
 * @author Juergen Hoeller
 * @since 2.5.2
 * @see com.rocket.summer.framework.core.io.DescriptiveResource
 */
class BeanDefinitionResource extends AbstractResource {

    private final BeanDefinition beanDefinition;


    /**
     * Create a new BeanDefinitionResource.
     * @param beanDefinition the BeanDefinition objectto wrap
     */
    public BeanDefinitionResource(BeanDefinition beanDefinition) {
        Assert.notNull(beanDefinition, "BeanDefinition must not be null");
        this.beanDefinition = beanDefinition;
    }

    /**
     * Return the wrapped BeanDefinition object.
     */
    public final BeanDefinition getBeanDefinition() {
        return this.beanDefinition;
    }


    public boolean exists() {
        return false;
    }

    public boolean isReadable() {
        return false;
    }

    public InputStream getInputStream() throws IOException {
        throw new FileNotFoundException(
                "Resource cannot be opened because it points to " + getDescription());
    }

    public String getDescription() {
        return "BeanDefinition defined in " + this.beanDefinition.getResourceDescription();
    }


    /**
     * This implementation compares the underlying BeanDefinition.
     */
    public boolean equals(Object obj) {
        return (obj == this ||
                (obj instanceof BeanDefinitionResource &&
                        ((BeanDefinitionResource) obj).beanDefinition.equals(this.beanDefinition)));
    }

    /**
     * This implementation returns the hash code of the underlying BeanDefinition.
     */
    public int hashCode() {
        return this.beanDefinition.hashCode();
    }

}
