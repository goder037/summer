package com.rocket.summer.framework.validation;

import com.rocket.summer.framework.beans.PropertyAccessException;
import com.rocket.summer.framework.context.support.DefaultMessageSourceResolvable;
import com.rocket.summer.framework.util.ObjectUtils;
import com.rocket.summer.framework.util.StringUtils;

/**
 * Default {@link BindingErrorProcessor} implementation.
 *
 * <p>Uses the "required" error code and the field name to resolve message codes
 * for a missing field error.
 *
 * <p>Creates a <code>FieldError</code> for each <code>PropertyAccessException</code>
 * given, using the <code>PropertyAccessException</code>'s error code ("typeMismatch",
 * "methodInvocation") for resolving message codes.
 *
 * @author Alef Arendsen
 * @author Juergen Hoeller
 * @since 1.2
 * @see #MISSING_FIELD_ERROR_CODE
 * @see DataBinder#setBindingErrorProcessor
 * @see BeanPropertyBindingResult#addError
 * @see BeanPropertyBindingResult#resolveMessageCodes
 * @see org.springframework.beans.PropertyAccessException#getErrorCode
 * @see org.springframework.beans.TypeMismatchException#ERROR_CODE
 * @see org.springframework.beans.MethodInvocationException#ERROR_CODE
 */
public class DefaultBindingErrorProcessor implements BindingErrorProcessor {

    /**
     * Error code that a missing field error (i.e. a required field not
     * found in the list of property values) will be registered with:
     * "required".
     */
    public static final String MISSING_FIELD_ERROR_CODE = "required";


    public void processMissingFieldError(String missingField, BindingResult bindingResult) {
        // Create field error with code "required".
        String fixedField = bindingResult.getNestedPath() + missingField;
        String[] codes = bindingResult.resolveMessageCodes(MISSING_FIELD_ERROR_CODE, missingField);
        Object[] arguments = getArgumentsForBindError(bindingResult.getObjectName(), fixedField);
        bindingResult.addError(new FieldError(
                bindingResult.getObjectName(), fixedField, "", true,
                codes, arguments, "Field '" + fixedField + "' is required"));
    }

    public void processPropertyAccessException(PropertyAccessException ex, BindingResult bindingResult) {
        // Create field error with the exceptions's code, e.g. "typeMismatch".
        String field = ex.getPropertyName();
        String[] codes = bindingResult.resolveMessageCodes(ex.getErrorCode(), field);
        Object[] arguments = getArgumentsForBindError(bindingResult.getObjectName(), field);
        Object rejectedValue = ex.getValue();
        if (rejectedValue != null && rejectedValue.getClass().isArray()) {
            rejectedValue = StringUtils.arrayToCommaDelimitedString(ObjectUtils.toObjectArray(rejectedValue));
        }
        bindingResult.addError(new FieldError(
                bindingResult.getObjectName(), field, rejectedValue, true,
                codes, arguments, ex.getLocalizedMessage()));
    }

    /**
     * Return FieldError arguments for a binding error on the given field.
     * Invoked for each missing required field and each type mismatch.
     * <p>The default implementation returns a single argument indicating the field name
     * (of type DefaultMessageSourceResolvable, with "objectName.field" and "field" as codes).
     * @param objectName the name of the target object
     * @param field the field that caused the binding error
     * @return the Object array that represents the FieldError arguments
     * @see org.springframework.validation.FieldError#getArguments
     * @see org.springframework.context.support.DefaultMessageSourceResolvable
     */
    protected Object[] getArgumentsForBindError(String objectName, String field) {
        String[] codes = new String[] {objectName + Errors.NESTED_PATH_SEPARATOR + field, field};
        return new Object[] {new DefaultMessageSourceResolvable(codes, field)};
    }

}

