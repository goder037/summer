package com.rocket.summer.framework.beans.factory.config;

import com.rocket.summer.framework.util.Assert;

/**
 * Immutable placeholder class used for a property value object when it's
 * a reference to another bean in the factory, to be resolved at runtime.
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @see BeanDefinition#getPropertyValues()
 * @see com.rocket.summer.framework.beans.factory.BeanFactory#getBean
 */
public class RuntimeBeanReference implements BeanReference {

    private final String beanName;

    private final boolean toParent;

    private Object source;


    /**
     * Create a new RuntimeBeanReference to the given bean name,
     * without explicitly marking it as reference to a bean in
     * the parent factory.
     * @param beanName name of the target bean
     */
    public RuntimeBeanReference(String beanName) {
        this(beanName, false);
    }

    /**
     * Create a new RuntimeBeanReference to the given bean name,
     * with the option to mark it as reference to a bean in
     * the parent factory.
     * @param beanName name of the target bean
     * @param toParent whether this is an explicit reference to
     * a bean in the parent factory
     */
    public RuntimeBeanReference(String beanName, boolean toParent) {
        Assert.hasText(beanName, "'beanName' must not be empty");
        this.beanName = beanName;
        this.toParent = toParent;
    }


    public String getBeanName() {
        return this.beanName;
    }

    /**
     * Return whether this is an explicit reference to a bean
     * in the parent factory.
     */
    public boolean isToParent() {
        return this.toParent;
    }

    /**
     * Set the configuration source <code>Object</code> for this metadata element.
     * <p>The exact type of the object will depend on the configuration mechanism used.
     */
    public void setSource(Object source) {
        this.source = source;
    }

    public Object getSource() {
        return this.source;
    }


    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof RuntimeBeanReference)) {
            return false;
        }
        RuntimeBeanReference that = (RuntimeBeanReference) other;
        return (this.beanName.equals(that.beanName) && this.toParent == that.toParent);
    }

    public int hashCode() {
        int result = this.beanName.hashCode();
        result = 29 * result + (this.toParent ? 1 : 0);
        return result;
    }

    public String toString() {
        return '<' + getBeanName() + '>';
    }

}
