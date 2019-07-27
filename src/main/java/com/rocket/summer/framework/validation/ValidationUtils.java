package com.rocket.summer.framework.validation;

import com.rocket.summer.framework.util.Assert;
import com.rocket.summer.framework.util.ObjectUtils;
import com.rocket.summer.framework.util.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Utility class offering convenient methods for invoking a {@link Validator}
 * and for rejecting empty fields.
 *
 * <p>Checks for an empty field in <code>Validator</code> implementations can become
 * one-liners when using {@link #rejectIfEmpty} or {@link #rejectIfEmptyOrWhitespace}.
 *
 * @author Juergen Hoeller
 * @author Dmitriy Kopylenko
 * @since 06.05.2003
 * @see Validator
 * @see Errors
 */
public abstract class ValidationUtils {

    private static Log logger = LogFactory.getLog(ValidationUtils.class);


    /**
     * Invoke the given {@link Validator} for the supplied object and
     * {@link Errors} instance.
     * @param validator the <code>Validator</code> to be invoked (must not be <code>null</code>)
     * @param obj the object to bind the parameters to
     * @param errors the {@link Errors} instance that should store the errors (must not be <code>null</code>)
     * @throws IllegalArgumentException if either of the <code>Validator</code> or <code>Errors</code> arguments is
     * <code>null</code>, or if the supplied <code>Validator</code> does not {@link Validator#supports(Class) support}
     * the validation of the supplied object's type
     */
    public static void invokeValidator(Validator validator, Object obj, Errors errors) {
        invokeValidator(validator, obj, errors, (Class[]) null);
    }

    /**
     * Invoke the given {@link Validator}/{@link SmartValidator} for the supplied object and
     * {@link Errors} instance.
     * @param validator the <code>Validator</code> to be invoked (must not be <code>null</code>)
     * @param obj the object to bind the parameters to
     * @param errors the {@link Errors} instance that should store the errors (must not be <code>null</code>)
     * @param validationHints one or more hint objects to be passed to the validation engine
     * @throws IllegalArgumentException if either of the <code>Validator</code> or <code>Errors</code> arguments is
     * <code>null</code>, or if the supplied <code>Validator</code> does not {@link Validator#supports(Class) support}
     * the validation of the supplied object's type
     */
    public static void invokeValidator(Validator validator, Object obj, Errors errors, Object... validationHints) {
        Assert.notNull(validator, "Validator must not be null");
        Assert.notNull(errors, "Errors object must not be null");
        if (logger.isDebugEnabled()) {
            logger.debug("Invoking validator [" + validator + "]");
        }
        if (obj != null && !validator.supports(obj.getClass())) {
            throw new IllegalArgumentException(
                    "Validator [" + validator.getClass() + "] does not support [" + obj.getClass() + "]");
        }
        if (!ObjectUtils.isEmpty(validationHints) && validator instanceof SmartValidator) {
            ((SmartValidator) validator).validate(obj, errors, validationHints);
        }
        else {
            validator.validate(obj, errors);
        }
        if (logger.isDebugEnabled()) {
            if (errors.hasErrors()) {
                logger.debug("Validator found " + errors.getErrorCount() + " errors");
            }
            else {
                logger.debug("Validator found no errors");
            }
        }
    }


    /**
     * Reject the given field with the given error code if the value is empty.
     * <p>An 'empty' value in this context means either <code>null</code> or
     * the empty string "".
     * <p>The object whose field is being validated does not need to be passed
     * in because the {@link Errors} instance can resolve field values by itself
     * (it will usually hold an internal reference to the target object).
     * @param errors the <code>Errors</code> instance to register errors on
     * @param field the field name to check
     * @param errorCode the error code, interpretable as message key
     */
    public static void rejectIfEmpty(Errors errors, String field, String errorCode) {
        rejectIfEmpty(errors, field, errorCode, null, null);
    }

    /**
     * Reject the given field with the given error code and default message
     * if the value is empty.
     * <p>An 'empty' value in this context means either <code>null</code> or
     * the empty string "".
     * <p>The object whose field is being validated does not need to be passed
     * in because the {@link Errors} instance can resolve field values by itself
     * (it will usually hold an internal reference to the target object).
     * @param errors the <code>Errors</code> instance to register errors on
     * @param field the field name to check
     * @param errorCode error code, interpretable as message key
     * @param defaultMessage fallback default message
     */
    public static void rejectIfEmpty(Errors errors, String field, String errorCode, String defaultMessage) {
        rejectIfEmpty(errors, field, errorCode, null, defaultMessage);
    }

    /**
     * Reject the given field with the given error codea nd error arguments
     * if the value is empty.
     * <p>An 'empty' value in this context means either <code>null</code> or
     * the empty string "".
     * <p>The object whose field is being validated does not need to be passed
     * in because the {@link Errors} instance can resolve field values by itself
     * (it will usually hold an internal reference to the target object).
     * @param errors the <code>Errors</code> instance to register errors on
     * @param field the field name to check
     * @param errorCode the error code, interpretable as message key
     * @param errorArgs the error arguments, for argument binding via MessageFormat
     * (can be <code>null</code>)
     */
    public static void rejectIfEmpty(Errors errors, String field, String errorCode, Object[] errorArgs) {
        rejectIfEmpty(errors, field, errorCode, errorArgs, null);
    }

    /**
     * Reject the given field with the given error code, error arguments
     * and default message if the value is empty.
     * <p>An 'empty' value in this context means either <code>null</code> or
     * the empty string "".
     * <p>The object whose field is being validated does not need to be passed
     * in because the {@link Errors} instance can resolve field values by itself
     * (it will usually hold an internal reference to the target object).
     * @param errors the <code>Errors</code> instance to register errors on
     * @param field the field name to check
     * @param errorCode the error code, interpretable as message key
     * @param errorArgs the error arguments, for argument binding via MessageFormat
     * (can be <code>null</code>)
     * @param defaultMessage fallback default message
     */
    public static void rejectIfEmpty(
            Errors errors, String field, String errorCode, Object[] errorArgs, String defaultMessage) {

        Assert.notNull(errors, "Errors object must not be null");
        Object value = errors.getFieldValue(field);
        if (value == null || !StringUtils.hasLength(value.toString())) {
            errors.rejectValue(field, errorCode, errorArgs, defaultMessage);
        }
    }

    /**
     * Reject the given field with the given error code if the value is empty
     * or just contains whitespace.
     * <p>An 'empty' value in this context means either <code>null</code>,
     * the empty string "", or consisting wholly of whitespace.
     * <p>The object whose field is being validated does not need to be passed
     * in because the {@link Errors} instance can resolve field values by itself
     * (it will usually hold an internal reference to the target object).
     * @param errors the <code>Errors</code> instance to register errors on
     * @param field the field name to check
     * @param errorCode the error code, interpretable as message key
     */
    public static void rejectIfEmptyOrWhitespace(Errors errors, String field, String errorCode) {
        rejectIfEmptyOrWhitespace(errors, field, errorCode, null, null);
    }

    /**
     * Reject the given field with the given error code and default message
     * if the value is empty or just contains whitespace.
     * <p>An 'empty' value in this context means either <code>null</code>,
     * the empty string "", or consisting wholly of whitespace.
     * <p>The object whose field is being validated does not need to be passed
     * in because the {@link Errors} instance can resolve field values by itself
     * (it will usually hold an internal reference to the target object).
     * @param errors the <code>Errors</code> instance to register errors on
     * @param field the field name to check
     * @param errorCode the error code, interpretable as message key
     * @param defaultMessage fallback default message
     */
    public static void rejectIfEmptyOrWhitespace(
            Errors errors, String field, String errorCode, String defaultMessage) {

        rejectIfEmptyOrWhitespace(errors, field, errorCode, null, defaultMessage);
    }

    /**
     * Reject the given field with the given error code and error arguments
     * if the value is empty or just contains whitespace.
     * <p>An 'empty' value in this context means either <code>null</code>,
     * the empty string "", or consisting wholly of whitespace.
     * <p>The object whose field is being validated does not need to be passed
     * in because the {@link Errors} instance can resolve field values by itself
     * (it will usually hold an internal reference to the target object).
     * @param errors the <code>Errors</code> instance to register errors on
     * @param field the field name to check
     * @param errorCode the error code, interpretable as message key
     * @param errorArgs the error arguments, for argument binding via MessageFormat
     * (can be <code>null</code>)
     */
    public static void rejectIfEmptyOrWhitespace(
            Errors errors, String field, String errorCode, Object[] errorArgs) {

        rejectIfEmptyOrWhitespace(errors, field, errorCode, errorArgs, null);
    }

    /**
     * Reject the given field with the given error code, error arguments
     * and default message if the value is empty or just contains whitespace.
     * <p>An 'empty' value in this context means either <code>null</code>,
     * the empty string "", or consisting wholly of whitespace.
     * <p>The object whose field is being validated does not need to be passed
     * in because the {@link Errors} instance can resolve field values by itself
     * (it will usually hold an internal reference to the target object).
     * @param errors the <code>Errors</code> instance to register errors on
     * @param field the field name to check
     * @param errorCode the error code, interpretable as message key
     * @param errorArgs the error arguments, for argument binding via MessageFormat
     * (can be <code>null</code>)
     * @param defaultMessage fallback default message
     */
    public static void rejectIfEmptyOrWhitespace(
            Errors errors, String field, String errorCode, Object[] errorArgs, String defaultMessage) {

        Assert.notNull(errors, "Errors object must not be null");
        Object value = errors.getFieldValue(field);
        if (value == null ||!StringUtils.hasText(value.toString())) {
            errors.rejectValue(field, errorCode, errorArgs, defaultMessage);
        }
    }

}