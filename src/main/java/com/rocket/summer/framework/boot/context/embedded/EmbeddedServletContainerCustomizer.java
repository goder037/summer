package com.rocket.summer.framework.boot.context.embedded;

/**
 * Strategy interface for customizing auto-configured embedded servlet containers. Any
 * beans of this type will get a callback with the container factory before the container
 * itself is started, so you can set the port, address, error pages etc.
 * <p>
 * Beware: calls to this interface are usually made from a
 * {@link EmbeddedServletContainerCustomizerBeanPostProcessor} which is a
 * {@link BeanPostProcessor} (so called very early in the ApplicationContext lifecycle).
 * It might be safer to lookup dependencies lazily in the enclosing BeanFactory rather
 * than injecting them with {@code @Autowired}.
 *
 * @author Dave Syer
 * @see EmbeddedServletContainerCustomizerBeanPostProcessor
 */
public interface EmbeddedServletContainerCustomizer {

    /**
     * Customize the specified {@link ConfigurableEmbeddedServletContainer}.
     * @param container the container to customize
     */
    void customize(ConfigurableEmbeddedServletContainer container);

}
