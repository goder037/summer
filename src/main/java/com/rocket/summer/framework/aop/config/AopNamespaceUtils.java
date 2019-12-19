package com.rocket.summer.framework.aop.config;

import org.w3c.dom.Element;

import com.rocket.summer.framework.beans.factory.config.BeanDefinition;
import com.rocket.summer.framework.beans.factory.parsing.BeanComponentDefinition;
import com.rocket.summer.framework.beans.factory.support.BeanDefinitionRegistry;
import com.rocket.summer.framework.beans.factory.xml.ParserContext;

/**
 * Utility class for handling registration of auto-proxy creators used internally
 * by the '{@code aop}' namespace tags.
 *
 * <p>Only a single auto-proxy creator should be registered and multiple configuration
 * elements may wish to register different concrete implementations. As such this class
 * delegates to {@link AopConfigUtils} which provides a simple escalation protocol.
 * Callers may request a particular auto-proxy creator and know that creator,
 * <i>or a more capable variant thereof</i>, will be registered as a post-processor.
 *
 * @author Rob Harrop
 * @author Juergen Hoeller
 * @author Mark Fisher
 * @since 2.0
 * @see AopConfigUtils
 */
public abstract class AopNamespaceUtils {

    /**
     * The {@code proxy-target-class} attribute as found on AOP-related XML tags.
     */
    public static final String PROXY_TARGET_CLASS_ATTRIBUTE = "proxy-target-class";

    /**
     * The {@code expose-proxy} attribute as found on AOP-related XML tags.
     */
    private static final String EXPOSE_PROXY_ATTRIBUTE = "expose-proxy";


    public static void registerAutoProxyCreatorIfNecessary(
            ParserContext parserContext, Element sourceElement) {

        BeanDefinition beanDefinition = AopConfigUtils.registerAutoProxyCreatorIfNecessary(
                parserContext.getRegistry(), parserContext.extractSource(sourceElement));
        useClassProxyingIfNecessary(parserContext.getRegistry(), sourceElement);
        registerComponentIfNecessary(beanDefinition, parserContext);
    }

    public static void registerAspectJAutoProxyCreatorIfNecessary(
            ParserContext parserContext, Element sourceElement) {

        BeanDefinition beanDefinition = AopConfigUtils.registerAspectJAutoProxyCreatorIfNecessary(
                parserContext.getRegistry(), parserContext.extractSource(sourceElement));
        useClassProxyingIfNecessary(parserContext.getRegistry(), sourceElement);
        registerComponentIfNecessary(beanDefinition, parserContext);
    }

    public static void registerAspectJAnnotationAutoProxyCreatorIfNecessary(
            ParserContext parserContext, Element sourceElement) {

        BeanDefinition beanDefinition = AopConfigUtils.registerAspectJAnnotationAutoProxyCreatorIfNecessary(
                parserContext.getRegistry(), parserContext.extractSource(sourceElement));
        useClassProxyingIfNecessary(parserContext.getRegistry(), sourceElement);
        registerComponentIfNecessary(beanDefinition, parserContext);
    }

    private static void useClassProxyingIfNecessary(BeanDefinitionRegistry registry, Element sourceElement) {
        if (sourceElement != null) {
            boolean proxyTargetClass = Boolean.parseBoolean(sourceElement.getAttribute(PROXY_TARGET_CLASS_ATTRIBUTE));
            if (proxyTargetClass) {
                AopConfigUtils.forceAutoProxyCreatorToUseClassProxying(registry);
            }
            boolean exposeProxy = Boolean.parseBoolean(sourceElement.getAttribute(EXPOSE_PROXY_ATTRIBUTE));
            if (exposeProxy) {
                AopConfigUtils.forceAutoProxyCreatorToExposeProxy(registry);
            }
        }
    }

    private static void registerComponentIfNecessary(BeanDefinition beanDefinition, ParserContext parserContext) {
        if (beanDefinition != null) {
            parserContext.registerComponent(
                    new BeanComponentDefinition(beanDefinition, AopConfigUtils.AUTO_PROXY_CREATOR_BEAN_NAME));
        }
    }

}

