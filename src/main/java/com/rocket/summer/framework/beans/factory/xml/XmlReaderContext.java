package com.rocket.summer.framework.beans.factory.xml;

import com.rocket.summer.framework.beans.factory.config.BeanDefinition;
import com.rocket.summer.framework.beans.factory.parsing.ProblemReporter;
import com.rocket.summer.framework.beans.factory.parsing.ReaderContext;
import com.rocket.summer.framework.beans.factory.parsing.ReaderEventListener;
import com.rocket.summer.framework.beans.factory.parsing.SourceExtractor;
import com.rocket.summer.framework.beans.factory.support.BeanDefinitionRegistry;
import com.rocket.summer.framework.core.io.Resource;
import com.rocket.summer.framework.core.io.ResourceLoader;

/**
 * Extension of {@link org.springframework.beans.factory.parsing.ReaderContext},
 * specific to use with an {@link XmlBeanDefinitionReader}. Provides access to the
 * {@link NamespaceHandlerResolver} configured in the {@link XmlBeanDefinitionReader}.
 *
 * @author Rob Harrop
 * @author Juergen Hoeller
 * @since 2.0
 */
public class XmlReaderContext extends ReaderContext {

    private final XmlBeanDefinitionReader reader;

    private final NamespaceHandlerResolver namespaceHandlerResolver;


    public XmlReaderContext(
            Resource resource, ProblemReporter problemReporter,
            ReaderEventListener eventListener, SourceExtractor sourceExtractor,
            XmlBeanDefinitionReader reader, NamespaceHandlerResolver namespaceHandlerResolver) {

        super(resource, problemReporter, eventListener, sourceExtractor);
        this.reader = reader;
        this.namespaceHandlerResolver = namespaceHandlerResolver;
    }


    public final XmlBeanDefinitionReader getReader() {
        return this.reader;
    }

    public final BeanDefinitionRegistry getRegistry() {
        return this.reader.getRegistry();
    }

    public final ResourceLoader getResourceLoader() {
        return this.reader.getResourceLoader();
    }

    public final ClassLoader getBeanClassLoader() {
        return this.reader.getBeanClassLoader();
    }

    public final NamespaceHandlerResolver getNamespaceHandlerResolver() {
        return this.namespaceHandlerResolver;
    }


    public String generateBeanName(BeanDefinition beanDefinition) {
        return this.reader.getBeanNameGenerator().generateBeanName(beanDefinition, getRegistry());
    }

    public String registerWithGeneratedName(BeanDefinition beanDefinition) {
        String generatedName = generateBeanName(beanDefinition);
        getRegistry().registerBeanDefinition(generatedName, beanDefinition);
        return generatedName;
    }

}
