package com.rocket.summer.framework.aop.config;

import org.w3c.dom.Element;

import com.rocket.summer.framework.beans.factory.config.BeanDefinition;
import com.rocket.summer.framework.beans.factory.parsing.BeanComponentDefinition;
import com.rocket.summer.framework.beans.factory.support.RootBeanDefinition;
import com.rocket.summer.framework.beans.factory.xml.BeanDefinitionParser;
import com.rocket.summer.framework.beans.factory.xml.ParserContext;

/**
 * {@link BeanDefinitionParser} responsible for parsing the
 * {@code <aop:spring-configured/>} tag.
 *
 * <p><b>NOTE:</b> This is essentially a duplicate of Spring 2.5's
 * {@link com.rocket.summer.framework.context.config.SpringConfiguredBeanDefinitionParser}
 * for the {@code <context:spring-configured/>} tag, mirrored here for compatibility with
 * Spring 2.0's {@code <aop:spring-configured/>} tag (avoiding a direct dependency on the
 * context package).
 *
 * @author Rob Harrop
 * @author Juergen Hoeller
 * @since 2.0
 */
class SpringConfiguredBeanDefinitionParser implements BeanDefinitionParser {

    /**
     * The bean name of the internally managed bean configurer aspect.
     */
    public static final String BEAN_CONFIGURER_ASPECT_BEAN_NAME =
            "com.rocket.summer.framework.context.config.internalBeanConfigurerAspect";

    private static final String BEAN_CONFIGURER_ASPECT_CLASS_NAME =
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

