package com.rocket.summer.framework.validation;

import com.rocket.summer.framework.context.support.DefaultMessageSourceResolvable;
import com.rocket.summer.framework.util.Assert;

/**
 * Encapsulates an object error, that is, a global reason for rejecting
 * an object.
 *
 * <p>See the {@link DefaultMessageCodesResolver} javadoc for details on
 * how a message code list is built for an {@code ObjectError}.
 *
 * @author Juergen Hoeller
 * @since 10.03.2003
 * @see FieldError
 * @see DefaultMessageCodesResolver
 */
public class ObjectError extends DefaultMessageSourceResolvable {

    private final String objectName;


    /**
     * Create a new instance of the ObjectError class.
     * @param objectName the name of the affected object
     * @param defaultMessage the default message to be used to resolve this message
     */
    public ObjectError(String objectName, String defaultMessage) {
        this(objectName, null, null, defaultMessage);
    }

    /**
     * Create a new instance of the ObjectError class.
     * @param objectName the name of the affected object
     * @param codes the codes to be used to resolve this message
     * @param arguments	the array of arguments to be used to resolve this message
     * @param defaultMessage the default message to be used to resolve this message
     */
    public ObjectError(String objectName, String[] codes, Object[] arguments, String defaultMessage) {
        super(codes, arguments, defaultMessage);
        Assert.notNull(objectName, "Object name must not be null");
        this.objectName = objectName;
    }


    /**
     * Return the name of the affected object.
     */
    public String getObjectName() {
        return this.objectName;
    }


    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || other.getClass() != getClass() || !super.equals(other)) {
            return false;
        }
        ObjectError otherError = (ObjectError) other;
        return getObjectName().equals(otherError.getObjectName());
    }

    @Override
    public int hashCode() {
        return super.hashCode() * 29 + getObjectName().hashCode();
    }

    @Override
    public String toString() {
        return "Error in object '" + this.objectName + "': " + resolvableToString();
    }

}
