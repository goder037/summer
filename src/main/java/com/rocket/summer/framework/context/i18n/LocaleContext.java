package com.rocket.summer.framework.context.i18n;

import java.util.Locale;

/**
 * Strategy interface for determining the current Locale.
 *
 * <p>A LocaleContext instance can be associated with a thread
 * via the LocaleContextHolder class.
 *
 * @author Juergen Hoeller
 * @since 1.2
 * @see LocaleContextHolder
 * @see java.util.Locale
 */
public interface LocaleContext {

    /**
     * Return the current Locale, which can be fixed or determined dynamically,
     * depending on the implementation strategy.
     */
    Locale getLocale();

}