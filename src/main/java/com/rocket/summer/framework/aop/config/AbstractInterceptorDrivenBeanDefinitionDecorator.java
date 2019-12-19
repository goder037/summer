package com.rocket.summer.framework.aop.config;

import java.util.List;

import org.w3c.dom.Node;

import com.rocket.summer.framework.aop.framework.ProxyFactoryBean;
import com.rocket.summer.framework.beans.factory.config.BeanDefinition;
import com.rocket.summer.framework.beans.factory.config.BeanDefinitionHolder;
import com.rocket.summer.framework.beans.factory.support.AbstractBeanDefinition;
import com.rocket.summer.framework.beans.factory.support.BeanDefinitionReaderUtils;
import com.rocket.summer.framework.beans.factory.support.BeanDefinitionRegistry;
import com.rocket.summer.framework.beans.factory.support.ManagedList;
import com.rocket.summer.framework.beans.factory.support.RootBeanDefinition;
import com.rocket.summer.framework.beans.factory.xml.BeanDefinitionDecorator;
import com.rocket.summer.framework.beans.factory.xml.ParserContext;
import com.rocket.summer.framework.util.ClassUtils;
import com.rocket.summer.framework.util.StringUtils;

/**
 * Base implementation for
 * {@link com.rocket.summer.framework.beans.factory.xml.BeanDefinitionDecorator BeanDefinitionDecorators}
 * wishing to add an {@link org.aopalliance.intercept.MethodInterceptor interceptor}
 * to the resulting bean.
 *
 * <p>This base class controls the creation of the {@link ProxyFactoryBean} bean definition
 * and wraps the original as an inner-bean definition for the {@code target} property
 * of {@link ProxyFactoryBean}.
 *
 * <p>Chaining is correctly handled, ensuring that only one {@link ProxyFactoryBean} definition
 * is created. If a previous {@link com.rocket.summer.framework.beans.factory.xml.BeanDefinitionDecorator}
 * already created the {@link com.rocket.summer.framework.aop.framework.ProxyFactoryBean} then the
 * interceptor is simply added to the existing definition.
 *
 * <p>Subclasses have only to create the {@code BeanDefinition} to the interceptor that
 * they wish to add.
 *
 * @author Rob Harrop
 * @author Juergen Hoeller
 * @since 2.0
 * @see org.aopalliance.intercept.MethodInterceptor
 */
public abstract class AbstractInterceptorDrivenBeanDefinitionDecorator implements BeanDefinitionDecorator {

    @Override
    public final BeanDefinitionHolder decorate(Node node, BeanDefinitionHolder definitionHolder, ParserContext parserContext) {
        BeanDefinitionRegistry registry = parserContext.getRegistry();

        // get the root bean name - will be the name of the generated proxy factory bean
        String existingBeanName = definitionHolder.getBeanName();
        BeanDefinition targetDefinition = definitionHolder.getBeanDefinition();
        BeanDefinitionHolder targetHolder = new BeanDefinitionHolder(targetDefinition, existingBeanName + ".TARGET");

        // delegate to subclass for interceptor definition
        BeanDefinition interceptorDefinition = createInterceptorDefinition(node);

        // generate name and register the interceptor
        String interceptorName = existingBeanName + '.' + getInterceptorNameSuffix(interceptorDefinition);
        BeanDefinitionReaderUtils.registerBeanDefinition(
                new BeanDefinitionHolder(interceptorDefinition, interceptorName), registry);

        BeanDefinitionHolder result = definitionHolder;

        if (!isProxyFactoryBeanDefinition(targetDefinition)) {
            // create the proxy definition
            RootBeanDefinition proxyDefinition = new RootBeanDefinition();
            // create proxy factory bean definition
            proxyDefinition.setBeanClass(ProxyFactoryBean.class);
            proxyDefinition.setScope(targetDefinition.getScope());
            proxyDefinition.setLazyInit(targetDefinition.isLazyInit());
            // set the target
            proxyDefinition.setDecoratedDefinition(targetHolder);
            proxyDefinition.getPropertyValues().add("target", targetHolder);
            // create the interceptor names list
            proxyDefinition.getPropertyValues().add("interceptorNames", new ManagedList<String>());
            // copy autowire settings from original bean definition.
            proxyDefinition.setAutowireCandidate(targetDefinition.isAutowireCandidate());
            proxyDefinition.setPrimary(targetDefinition.isPrimary());
            if (targetDefinition instanceof AbstractBeanDefinition) {
                proxyDefinition.copyQualifiersFrom((AbstractBeanDefinition) targetDefinition);
            }
            // wrap it in a BeanDefinitionHolder with bean name
            result = new BeanDefinitionHolder(proxyDefinition, existingBeanName);
        }

        addInterceptorNameToList(interceptorName, result.getBeanDefinition());
        return result;
    }

    @SuppressWarnings("unchecked")
    private void addInterceptorNameToList(String interceptorName, BeanDefinition beanDefinition) {
        List<String> list = (List<String>)
                beanDefinition.getPropertyValues().getPropertyValue("interceptorNames").getValue();
        list.add(interceptorName);
    }

    private boolean isProxyFactoryBeanDefinition(BeanDefinition existingDefinition) {
        return ProxyFactoryBean.class.getName().equals(existingDefinition.getBeanClassName());
    }

    protected String getInterceptorNameSuffix(BeanDefinition interceptorDefinition) {
        return StringUtils.uncapitalize(ClassUtils.getShortName(interceptorDefinition.getBeanClassName()));
    }

    /**
     * Subclasses should implement this method to return the {@code BeanDefinition}
     * for the interceptor they wish to apply to the bean being decorated.
     */
    protected abstract BeanDefinition createInterceptorDefinition(Node node);

}

