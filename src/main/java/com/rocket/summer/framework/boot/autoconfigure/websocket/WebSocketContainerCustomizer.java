package com.rocket.summer.framework.boot.autoconfigure.websocket;

import com.rocket.summer.framework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import com.rocket.summer.framework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import com.rocket.summer.framework.boot.context.embedded.EmbeddedServletContainerFactory;
import com.rocket.summer.framework.core.Ordered;
import com.rocket.summer.framework.core.ResolvableType;

/**
 * {@link EmbeddedServletContainerCustomizer} to configure websockets for a given
 * {@link EmbeddedServletContainerFactory}.
 *
 * @param <T> the embedded servlet container factory
 * @author Dave Syer
 * @author Phillip Webb
 * @author Andy Wilkinson
 * @since 1.2.0
 */
public abstract class WebSocketContainerCustomizer<T extends EmbeddedServletContainerFactory>
        implements EmbeddedServletContainerCustomizer, Ordered {

    @Override
    public int getOrder() {
        return 0;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void customize(ConfigurableEmbeddedServletContainer container) {
        if (getContainerType().isAssignableFrom(container.getClass())) {
            doCustomize((T) container);
        }
    }

    protected Class<?> getContainerType() {
        return ResolvableType.forClass(WebSocketContainerCustomizer.class, getClass())
                .resolveGeneric();
    }

    protected abstract void doCustomize(T container);

}

