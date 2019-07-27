package com.rocket.summer.framework.beans.factory.config;

import com.rocket.summer.framework.beans.BeanMetadataElement;
import com.rocket.summer.framework.util.Assert;
import com.rocket.summer.framework.util.ObjectUtils;
import com.rocket.summer.framework.util.StringUtils;

/**
 * Holder for a BeanDefinition with name and aliases.
 * Can be registered as a placeholder for an inner bean.
 *
 * <p>Can also be used for programmatic registration of inner bean
 * definitions. If you don't care about BeanNameAware and the like,
 * registering RootBeanDefinition or ChildBeanDefinition is good enough.
 *
 * @author Juergen Hoeller
 * @since 1.0.2
 * @see com.rocket.summer.framework.beans.factory.BeanNameAware
 * @see com.rocket.summer.framework.beans.factory.support.RootBeanDefinition
 * @see com.rocket.summer.framework.beans.factory.support.ChildBeanDefinition
 */
public class BeanDefinitionHolder implements BeanMetadataElement {

    private final BeanDefinition beanDefinition;

    private final String beanName;

    private final String[] aliases;


    /**
     * Create a new BeanDefinitionHolder.
     * @param beanDefinition the BeanDefinition to wrap
     * @param beanName the name of the bean, as specified for the bean definition
     */
    public BeanDefinitionHolder(BeanDefinition beanDefinition, String beanName) {
        this(beanDefinition, beanName, null);
    }

    /**
     * Create a new BeanDefinitionHolder.
     * @param beanDefinition the BeanDefinition to wrap
     * @param beanName the name of the bean, as specified for the bean definition
     * @param aliases alias names for the bean, or <code>null</code> if none
     */
    public BeanDefinitionHolder(BeanDefinition beanDefinition, String beanName, String[] aliases) {
        Assert.notNull(beanDefinition, "BeanDefinition must not be null");
        Assert.notNull(beanName, "Bean name must not be null");
        this.beanDefinition = beanDefinition;
        this.beanName = beanName;
        this.aliases = aliases;
    }

    /**
     * Copy constructor: Create a new BeanDefinitionHolder with the
     * same contents as the given BeanDefinitionHolder instance.
     * <p>Note: The wrapped BeanDefinition reference is taken as-is;
     * it is <code>not</code> deeply copied.
     * @param beanDefinitionHolder the BeanDefinitionHolder to copy
     */
    public BeanDefinitionHolder(BeanDefinitionHolder beanDefinitionHolder) {
        Assert.notNull(beanDefinitionHolder, "BeanDefinitionHolder must not be null");
        this.beanDefinition = beanDefinitionHolder.getBeanDefinition();
        this.beanName = beanDefinitionHolder.getBeanName();
        this.aliases = beanDefinitionHolder.getAliases();
    }

    /**
     * Expose the bean definition's source object.
     * @see BeanDefinition#getSource()
     */
    public Object getSource() {
        return this.beanDefinition.getSource();
    }

    /**
     * This implementation returns the long description. Can be overridden
     * to return the short description or any kind of custom description instead.
     * @see #getLongDescription()
     * @see #getShortDescription()
     */
    public String toString() {
        return getLongDescription();
    }

    /**
     * Return a long description for the bean, including name and aliases
     * as well as a description of the contained {@link BeanDefinition}.
     * @see #getShortDescription()
     * @see #getBeanDefinition()
     */
    public String getLongDescription() {
        StringBuffer sb = new StringBuffer(getShortDescription());
        sb.append(": ").append(this.beanDefinition);
        return sb.toString();
    }

    /**
     * Return the wrapped BeanDefinition.
     */
    public BeanDefinition getBeanDefinition() {
        return this.beanDefinition;
    }

    /**
     * Return a friendly, short description for the bean, stating name and aliases.
     * @see #getBeanName()
     * @see #getAliases()
     */
    public String getShortDescription() {
        StringBuffer sb = new StringBuffer();
        sb.append("Bean definition with name '").append(this.beanName).append("'");
        if (this.aliases != null) {
            sb.append(" and aliases [").append(StringUtils.arrayToCommaDelimitedString(this.aliases)).append("]");
        }
        return sb.toString();
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof BeanDefinitionHolder)) {
            return false;
        }
        BeanDefinitionHolder otherHolder = (BeanDefinitionHolder) other;
        return this.beanDefinition.equals(otherHolder.beanDefinition) &&
                this.beanName.equals(otherHolder.beanName) &&
                ObjectUtils.nullSafeEquals(this.aliases, otherHolder.aliases);
    }

    public int hashCode() {
        int hashCode = this.beanDefinition.hashCode();
        hashCode = 29 * hashCode + this.beanName.hashCode();
        hashCode = 29 * hashCode + ObjectUtils.nullSafeHashCode(this.aliases);
        return hashCode;
    }

    /**
     * Return the primary name of the bean, as specified for the bean definition.
     */
    public String getBeanName() {
        return this.beanName;
    }

    /**
     * Return the alias names for the bean, as specified directly for the bean definition.
     * @return the array of alias names, or <code>null</code> if none
     */
    public String[] getAliases() {
        return this.aliases;
    }
}
