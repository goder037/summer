package com.rocket.summer.framework.beans.factory.support;

import com.rocket.summer.framework.beans.factory.config.BeanDefinition;

/**
 * GenericBeanDefinition is a one-stop shop for standard bean definition purposes.
 * Like any bean definition, it allows for specifying a class plus optionally
 * constructor argument values and property values. Additionally, deriving from a
 * parent bean definition can be flexibly configured through the "parentName" property.
 *
 * <p>In general, use this <code>GenericBeanDefinition</code> class for the purpose of
 * registering user-visible bean definitions (which a post-processor might operate on,
 * potentially even reconfiguring the parent name). Use <code>RootBeanDefinition</code> /
 * <code>ChildBeanDefinition</code> where parent/child relationships happen to be pre-determined.
 *
 * @author Juergen Hoeller
 * @since 2.5
 * @see #setParentName
 * @see RootBeanDefinition
 * @see ChildBeanDefinition
 */
public class GenericBeanDefinition extends AbstractBeanDefinition {

    private String parentName;

    /**
     * Create a new GenericBeanDefinition, to be configured through its bean
     * properties and configuration methods.
     * @see #setBeanClass
     * @see #setBeanClassName
     * @see #setScope
     * @see #setAutowireMode
     * @see #setDependencyCheck
     * @see #setConstructorArgumentValues
     * @see #setPropertyValues
     */
    public GenericBeanDefinition() {
        super();
    }

    /**
     * Create a new GenericBeanDefinition as deep copy of the given
     * bean definition.
     * @param original the original bean definition to copy from
     */
    public GenericBeanDefinition(BeanDefinition original) {
        super(original);
    }


    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    public String getParentName() {
        return this.parentName;
    }


    public AbstractBeanDefinition cloneBeanDefinition() {
        return new GenericBeanDefinition(this);
    }

    public boolean equals(Object other) {
        return (this == other || (other instanceof GenericBeanDefinition && super.equals(other)));
    }

    public String toString() {
        return "Generic bean: " + super.toString();
    }
}
