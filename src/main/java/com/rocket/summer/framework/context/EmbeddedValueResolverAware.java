package com.rocket.summer.framework.context;

import com.rocket.summer.framework.beans.factory.Aware;
import com.rocket.summer.framework.util.StringValueResolver;

/**
 * Interface to be implemented by any object that wishes to be notified of a
 * <b>StringValueResolver</b> for the <b> resolution of embedded definition values.
 *
 * <p>This is an alternative to a full ConfigurableBeanFactory dependency via the
 * ApplicationContextAware/BeanFactoryAware interfaces.
 *
 * @author Juergen Hoeller
 * @author Chris Beams
 * @since 3.0.3
 * @see com.rocket.summer.framework.beans.factory.config.ConfigurableBeanFactory#resolveEmbeddedValue
 */
public interface EmbeddedValueResolverAware extends Aware {

    /**
     * Set the StringValueResolver to use for resolving embedded definition values.
     */
    void setEmbeddedValueResolver(StringValueResolver resolver);

}

