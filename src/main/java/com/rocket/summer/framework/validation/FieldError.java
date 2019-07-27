package com.rocket.summer.framework.validation;

import com.rocket.summer.framework.util.Assert;

/**
 * Encapsulates a field error, that is, a reason for rejecting a specific
 * field value.
 *
 * <p>See the {@link DefaultMessageCodesResolver} javadoc for details on
 * how a message code list is built for a <code>FieldError</code>.
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @since 10.03.2003
 * @see DefaultMessageCodesResolver
 */
@SuppressWarnings("serial")
public class FieldError extends ObjectError {

    private final String field;

    private final Object rejectedValue;

    private final boolean bindingFailure;


    /**
     * Create a new FieldError instance.
     * @param objectName the name of the affected object
     * @param field the affected field of the object
     * @param defaultMessage the default message to be used to resolve this message
     */
    public FieldError(String objectName, String field, String defaultMessage) {
        this(objectName, field, null, false, null, null, defaultMessage);
    }

    /**
     * Create a new FieldError instance.
     * @param objectName the name of the affected object
     * @param field the affected field of the object
     * @param rejectedValue the rejected field value
     * @param bindingFailure whether this error represents a binding failure
     * (like a type mismatch); else, it is a validation failure
     * @param codes the codes to be used to resolve this message
     * @param arguments the array of arguments to be used to resolve this message
     * @param defaultMessage the default message to be used to resolve this message
     */
    public FieldError(
            String objectName, String field, Object rejectedValue, boolean bindingFailure,
            String[] codes, Object[] arguments, String defaultMessage) {

        super(objectName, codes, arguments, defaultMessage);
        Assert.notNull(field, "Field must not be null");
        this.field = field;
        this.rejectedValue = rejectedValue;
        this.bindingFailure = bindingFailure;
    }

    /**
     * Return the affected field of the object.
     */
    public String getField() {
        return this.field;
    }

    /**
     * Return the rejected field value.
     */
    public Object getRejectedValue() {
        return this.rejectedValue;
    }

    /**
     * Return whether this error represents a binding failure
     * (like a type mismatch); otherwise it is a validation failure.
     */
    public boolean isBindingFailure() {
        return this.bindingFailure;
    }
}
