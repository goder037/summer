/*
 * Copyright 2002-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.rocket.summer.framework.beans.factory.xml;

import java.io.StringReader;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import com.rocket.summer.framework.beans.factory.BeanDefinitionStoreException;
import com.rocket.summer.framework.beans.factory.config.BeanDefinition;
import com.rocket.summer.framework.beans.factory.parsing.ProblemReporter;
import com.rocket.summer.framework.beans.factory.parsing.ReaderContext;
import com.rocket.summer.framework.beans.factory.parsing.ReaderEventListener;
import com.rocket.summer.framework.beans.factory.parsing.SourceExtractor;
import com.rocket.summer.framework.beans.factory.support.BeanDefinitionRegistry;
import com.rocket.summer.framework.core.env.Environment;
import com.rocket.summer.framework.core.io.Resource;
import com.rocket.summer.framework.core.io.ResourceLoader;

/**
 * Extension of {@link com.rocket.summer.framework.beans.factory.parsing.ReaderContext},
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

	public final Environment getEnvironment() {
		return this.reader.getEnvironment();
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

	public Document readDocumentFromString(String documentContent) {
		InputSource is = new InputSource(new StringReader(documentContent));
		try {
			return this.reader.doLoadDocument(is, getResource());
		}
		catch (Exception ex) {
			throw new BeanDefinitionStoreException("Failed to read XML document", ex);
		}
	}

}
