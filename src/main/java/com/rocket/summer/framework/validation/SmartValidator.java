package com.rocket.summer.framework.validation;

/**
 * Extended variant of the {@link Validator} interface, adding support for
 * validation 'hints'.
 *
 * @author Juergen Hoeller
 * @since 3.1
 */
public interface SmartValidator extends Validator {

    /**
     * Validate the supplied {@code target} object, which must be of a {@link Class} for
     * which the {@link #supports(Class)} method typically has (or would) return {@code true}.
     * <p>The supplied {@link Errors errors} instance can be used to report any
     * resulting validation errors.
     * <p><b>This variant of {@code validate} supports validation hints, such as
     * validation groups against a JSR-303 provider</b> (in this case, the provided hint
     * objects need to be annotation arguments of type {@code Class}).
     * <p>Note: Validation hints may get ignored by the actual target {@code Validator},
     * in which case this method is supposed to be behave just like its regular
     * {@link #validate(Object, Errors)} sibling.
     * @param target the object that is to be validated (can be {@code null})
     * @param errors contextual state about the validation process (never {@code null})
     * @param validationHints one or more hint objects to be passed to the validation engine
     * @see ValidationUtils
     */
    void validate(Object target, Errors errors, Object... validationHints);

}