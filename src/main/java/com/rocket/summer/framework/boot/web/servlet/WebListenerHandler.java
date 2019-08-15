package com.rocket.summer.framework.boot.web.servlet;

import java.util.Map;

import javax.servlet.annotation.WebListener;

import com.rocket.summer.framework.beans.factory.support.BeanDefinitionBuilder;
import com.rocket.summer.framework.beans.factory.support.BeanDefinitionRegistry;
import com.rocket.summer.framework.context.annotation.ScannedGenericBeanDefinition;

/**
 * Handler for {@link WebListener}-annotated classes.
 *
 * @author Andy Wilkinson
 */
class WebListenerHandler extends ServletComponentHandler {

    WebListenerHandler() {
        super(WebListener.class);
    }

    @Override
    protected void doHandle(Map<String, Object> attributes,
                            ScannedGenericBeanDefinition beanDefinition,
                            BeanDefinitionRegistry registry) {
        BeanDefinitionBuilder builder = BeanDefinitionBuilder
                .rootBeanDefinition(ServletListenerRegistrationBean.class);
        builder.addPropertyValue("listener", beanDefinition);
        registry.registerBeanDefinition(beanDefinition.getBeanClassName(),
                builder.getBeanDefinition());
    }

}

