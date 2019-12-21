package com.rocket.summer.framework.context.config;

import org.w3c.dom.Element;

import com.rocket.summer.framework.beans.factory.config.BeanDefinition;
import com.rocket.summer.framework.beans.factory.parsing.BeanComponentDefinition;
import com.rocket.summer.framework.beans.factory.support.RootBeanDefinition;
import com.rocket.summer.framework.beans.factory.xml.BeanDefinitionParser;
import com.rocket.summer.framework.beans.factory.xml.ParserContext;

/**
 * {@link BeanDefinitionParser} responsible for parsing the
 * {@code <context:spring-configured/>} tag.
 *
 * @author Juergen Hoeller
 * @since 2.5
 */
class SpringConfiguredBeanDefinitionParser implements BeanDefinitionParser {

    /**
     * The bean name of the internally managed bean configurer aspect.
     */
    public static final String BEAN_CONFIGURER_ASPECT_BEAN_NAME =
            "com.rocket.summer.framework.context.config.internalBeanConfigurerAspect";

    static final String BEAN_CONFIGURER_ASPECT_CLASS_NAME =
            "com.rocket.summer.framework.beans.factory.aspectj.AnnotationBeanConfigurerAspect";


    @Override
    public BeanDefinition parse(Element element, ParserContext parserContext) {
        if (!parserContext.getRegistry().containsBeanDefinition(BEAN_CONFIGURER_ASPECT_BEAN_NAME)) {
            RootBeanDefinition def = new RootBeanDefinition();
            def.setBeanClassName(BEAN_CONFIGURER_ASPECT_CLASS_NAME);
            def.setFactoryMethodName("aspectOf");
            def.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
            def.setSource(parserContext.extractSource(element));
            parserContext.registerBeanComponent(new BeanComponentDefinition(def, BEAN_CONFIGURER_ASPECT_BEAN_NAME));
        }
        return null;
    }

}

