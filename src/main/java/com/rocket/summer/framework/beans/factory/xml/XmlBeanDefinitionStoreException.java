package com.rocket.summer.framework.beans.factory.xml;

import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.rocket.summer.framework.beans.factory.BeanDefinitionStoreException;

/**
 * XML-specific BeanDefinitionStoreException subclass that wraps a
 * {@link SAXException}, typically a {@link SAXParseException}
 * which contains information about the error location.
 *
 * @author Juergen Hoeller
 * @since 2.0.2
 * @see #getLineNumber()
 * @see SAXParseException
 */
@SuppressWarnings("serial")
public class XmlBeanDefinitionStoreException extends BeanDefinitionStoreException {

	/**
	 * Create a new XmlBeanDefinitionStoreException.
	 * @param resourceDescription description of the resource that the bean definition came from
	 * @param msg the detail message (used as exception message as-is)
	 * @param cause the SAXException (typically a SAXParseException) root cause
	 * @see SAXParseException
	 */
	public XmlBeanDefinitionStoreException(String resourceDescription, String msg, SAXException cause) {
		super(resourceDescription, msg, cause);
	}

	/**
	 * Return the line number in the XML resource that failed.
	 * @return the line number if available (in case of a SAXParseException); -1 else
	 * @see SAXParseException#getLineNumber()
	 */
	public int getLineNumber() {
		Throwable cause = getCause();
		if (cause instanceof SAXParseException) {
			return ((SAXParseException) cause).getLineNumber();
		}
		return -1;
	}

}
