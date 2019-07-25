package com.rocket.summer.framework.validation;

/**
 * Strategy interface for building message codes from validation error codes.
 * Used by DataBinder to build the codes list for ObjectErrors and FieldErrors.
 *
 * <p>The resulting message codes correspond to the codes of a
 * MessageSourceResolvable (as implemented by ObjectError and FieldError).
 *
 * @author Juergen Hoeller
 * @since 1.0.1
 * @see DataBinder#setMessageCodesResolver
 * @see ObjectError
 * @see FieldError
 * @see org.springframework.context.MessageSourceResolvable#getCodes()
 */
public interface MessageCodesResolver {

    /**
     * Build message codes for the given error code and object name.
     * Used for building the codes list of an ObjectError.
     * @param errorCode the error code used for rejecting the object
     * @param objectName the name of the object
     * @return the message codes to use
     */
    String[] resolveMessageCodes(String errorCode, String objectName);

    /**
     * Build message codes for the given error code and field specification.
     * Used for building the codes list of an FieldError.
     * @param errorCode the error code used for rejecting the value
     * @param objectName the name of the object
     * @param field the field name
     * @param fieldType the field type (may be <code>null</code> if not determinable)
     * @return the message codes to use
     */
    String[] resolveMessageCodes(String errorCode, String objectName, String field, Class<?> fieldType);

}
