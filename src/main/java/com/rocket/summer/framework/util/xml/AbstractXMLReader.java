
package com.rocket.summer.framework.util.xml;

import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.LexicalHandler;

/**
 * Abstract base class for SAX {@code XMLReader} implementations.
 * Contains properties as defined in {@link XMLReader}, and does not recognize any features.
 *
 * @author Arjen Poutsma
 * @author Juergen Hoeller
 * @since 3.0
 * @see #setContentHandler(ContentHandler)
 * @see #setDTDHandler(DTDHandler)
 * @see #setEntityResolver(EntityResolver)
 * @see #setErrorHandler(ErrorHandler)
 */
abstract class AbstractXMLReader implements XMLReader {

	private DTDHandler dtdHandler;

	private ContentHandler contentHandler;

	private EntityResolver entityResolver;

	private ErrorHandler errorHandler;

	private LexicalHandler lexicalHandler;


	@Override
	public void setContentHandler(ContentHandler contentHandler) {
		this.contentHandler = contentHandler;
	}

	@Override
	public ContentHandler getContentHandler() {
		return this.contentHandler;
	}

	@Override
	public void setDTDHandler(DTDHandler dtdHandler) {
		this.dtdHandler = dtdHandler;
	}

	@Override
	public DTDHandler getDTDHandler() {
		return this.dtdHandler;
	}

	@Override
	public void setEntityResolver(EntityResolver entityResolver) {
		this.entityResolver = entityResolver;
	}

	@Override
	public EntityResolver getEntityResolver() {
		return this.entityResolver;
	}

	@Override
	public void setErrorHandler(ErrorHandler errorHandler) {
		this.errorHandler = errorHandler;
	}

	@Override
	public ErrorHandler getErrorHandler() {
		return this.errorHandler;
	}

	protected LexicalHandler getLexicalHandler() {
		return this.lexicalHandler;
	}


	/**
	 * This implementation throws a {@code SAXNotRecognizedException} exception
	 * for any feature outside of the "http://xml.org/sax/features/" namespace
	 * and returns {@code false} for any feature within.
	 */
	@Override
	public boolean getFeature(String name) throws SAXNotRecognizedException, SAXNotSupportedException {
		if (name.startsWith("http://xml.org/sax/features/")) {
			return false;
		}
		else {
			throw new SAXNotRecognizedException(name);
		}
	}

	/**
	 * This implementation throws a {@code SAXNotRecognizedException} exception
	 * for any feature outside of the "http://xml.org/sax/features/" namespace
	 * and accepts a {@code false} value for any feature within.
	 */
	@Override
	public void setFeature(String name, boolean value) throws SAXNotRecognizedException, SAXNotSupportedException {
		if (name.startsWith("http://xml.org/sax/features/")) {
			if (value) {
				throw new SAXNotSupportedException(name);
			}
		}
		else {
			throw new SAXNotRecognizedException(name);
		}
	}

	/**
	 * Throws a {@code SAXNotRecognizedException} exception when the given property does not signify a lexical
	 * handler. The property name for a lexical handler is {@code http://xml.org/sax/properties/lexical-handler}.
	 */
	@Override
	public Object getProperty(String name) throws SAXNotRecognizedException, SAXNotSupportedException {
		if ("http://xml.org/sax/properties/lexical-handler".equals(name)) {
			return this.lexicalHandler;
		}
		else {
			throw new SAXNotRecognizedException(name);
		}
	}

	/**
	 * Throws a {@code SAXNotRecognizedException} exception when the given property does not signify a lexical
	 * handler. The property name for a lexical handler is {@code http://xml.org/sax/properties/lexical-handler}.
	 */
	@Override
	public void setProperty(String name, Object value) throws SAXNotRecognizedException, SAXNotSupportedException {
		if ("http://xml.org/sax/properties/lexical-handler".equals(name)) {
			this.lexicalHandler = (LexicalHandler) value;
		}
		else {
			throw new SAXNotRecognizedException(name);
		}
	}

}
