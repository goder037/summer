package com.rocket.summer.framework.validation.beanvalidation;

import com.rocket.summer.framework.context.i18n.LocaleContextHolder;
import com.rocket.summer.framework.util.Assert;

import javax.validation.MessageInterpolator;
import java.util.Locale;

/**
 * Delegates to a target {@link MessageInterpolator} implementation but enforces Spring's
 * managed Locale. Typically used to wrap the validation provider's default interpolator.
 *
 * @author Juergen Hoeller
 * @since 3.0
 * @see com.rocket.summer.framework.context.i18n.LocaleContextHolder#getLocale()
 */
public class LocaleContextMessageInterpolator implements MessageInterpolator {

    private final MessageInterpolator targetInterpolator;


    /**
     * Create a new LocaleContextMessageInterpolator, wrapping the given target interpolator.
     * @param targetInterpolator the target MessageInterpolator to wrap
     */
    public LocaleContextMessageInterpolator(MessageInterpolator targetInterpolator) {
        Assert.notNull(targetInterpolator, "Target MessageInterpolator must not be null");
        this.targetInterpolator = targetInterpolator;
    }


    @Override
    public String interpolate(String message, Context context) {
        return this.targetInterpolator.interpolate(message, context, LocaleContextHolder.getLocale());
    }

    @Override
    public String interpolate(String message, Context context, Locale locale) {
        return this.targetInterpolator.interpolate(message, context, locale);
    }

}

