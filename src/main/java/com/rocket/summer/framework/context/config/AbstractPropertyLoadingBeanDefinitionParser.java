package com.rocket.summer.framework.context.config;

import org.w3c.dom.Element;

import com.rocket.summer.framework.beans.factory.config.BeanDefinition;
import com.rocket.summer.framework.beans.factory.support.BeanDefinitionBuilder;
import com.rocket.summer.framework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import com.rocket.summer.framework.beans.factory.xml.ParserContext;
import com.rocket.summer.framework.util.StringUtils;

/**
 * Abstract parser for &lt;context:property-.../&gt; elements.
 *
 * @author Juergen Hoeller
 * @author Arjen Poutsma
 * @author Dave Syer
 * @since 2.5.2
 */
abstract class AbstractPropertyLoadingBeanDefinitionParser extends AbstractSingleBeanDefinitionParser {

    @Override
    protected boolean shouldGenerateId() {
        return true;
    }

    @Override
    protected void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
        String location = element.getAttribute("location");
        if (StringUtils.hasLength(location)) {
            location = parserContext.getReaderContext().getEnvironment().resolvePlaceholders(location);
            String[] locations = StringUtils.commaDelimitedListToStringArray(location);
            builder.addPropertyValue("locations", locations);
        }

        String propertiesRef = element.getAttribute("properties-ref");
        if (StringUtils.hasLength(propertiesRef)) {
            builder.addPropertyReference("properties", propertiesRef);
        }

        String fileEncoding = element.getAttribute("file-encoding");
        if (StringUtils.hasLength(fileEncoding)) {
            builder.addPropertyValue("fileEncoding", fileEncoding);
        }

        String order = element.getAttribute("order");
        if (StringUtils.hasLength(order)) {
            builder.addPropertyValue("order", Integer.valueOf(order));
        }

        builder.addPropertyValue("ignoreResourceNotFound",
                Boolean.valueOf(element.getAttribute("ignore-resource-not-found")));

        builder.addPropertyValue("localOverride",
                Boolean.valueOf(element.getAttribute("local-override")));

        builder.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
    }

}

