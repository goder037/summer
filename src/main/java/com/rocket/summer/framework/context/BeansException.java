package com.rocket.summer.framework.context;

import com.rocket.summer.framework.util.ObjectUtils;

/**
 * Abstract superclass for all exceptions thrown in the beans package
 * and subpackages.
 *
 * <p>Note that this is a runtime (unchecked) exception. Beans exceptions
 * are usually fatal; there is no reason for them to be checked.
 *
 * @author Rod Johnson 
 * @author Juergen Hoeller
 */
public abstract class BeansException extends NestedRuntimeException {

	/**
	 * Create a new BeansException with the specified message.
	 * @param msg the detail message
	 */
	public BeansException(String msg) {
		super(msg);
	}

	/**
	 * Create a new BeansException with the specified message
	 * and root cause.
	 * @param msg the detail message
	 * @param cause the root cause
	 */
	public BeansException(String msg, Throwable cause) {
		super(msg, cause);
	}


	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof BeansException)) {
			return false;
		}
		BeansException otherBe = (BeansException) other;
		return (getMessage().equals(otherBe.getMessage()) &&
				ObjectUtils.nullSafeEquals(getCause(), otherBe.getCause()));
	}

	public int hashCode() {
		return getMessage().hashCode();
	}

}
