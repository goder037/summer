package com.rocket.summer.framework.beans.factory.parsing;

import com.rocket.summer.framework.beans.BeanMetadataElement;
import com.rocket.summer.framework.util.Assert;

/**
 * Representation of an alias that has been registered during the parsing process.
 *
 * @author Juergen Hoeller
 * @since 2.0
 * @see ReaderEventListener#aliasRegistered(AliasDefinition)
 */
public class AliasDefinition implements BeanMetadataElement {

    private final String beanName;

    private final String alias;

    private final Object source;


    /**
     * Create a new AliasDefinition.
     * @param beanName the canonical name of the bean
     * @param alias the alias registered for the bean
     */
    public AliasDefinition(String beanName, String alias) {
        this(beanName, alias, null);
    }

    /**
     * Create a new AliasDefinition.
     * @param beanName the canonical name of the bean
     * @param alias the alias registered for the bean
     * @param source the source object (may be <code>null</code>)
     */
    public AliasDefinition(String beanName, String alias, Object source) {
        Assert.notNull(beanName, "Bean name must not be null");
        Assert.notNull(alias, "Alias must not be null");
        this.beanName = beanName;
        this.alias = alias;
        this.source = source;
    }


    /**
     * Return the canonical name of the bean.
     */
    public final String getBeanName() {
        return this.beanName;
    }

    /**
     * Return the alias registered for the bean.
     */
    public final String getAlias() {
        return this.alias;
    }

    public final Object getSource() {
        return this.source;
    }

}

