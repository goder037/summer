package com.rocket.summer.framework.context.i18n;

import com.rocket.summer.framework.core.NamedInheritableThreadLocal;
import com.rocket.summer.framework.core.NamedThreadLocal;

import java.util.Locale;
import java.util.TimeZone;

/**
 * Simple holder class that associates a LocaleContext instance
 * with the current thread. The LocaleContext will be inherited
 * by any child threads spawned by the current thread if the
 * <code>inheritable<code> flag is set to <code>true</code>.
 *
 * <p>Used as a central holder for the current Locale in Spring,
 * wherever necessary: for example, in MessageSourceAccessor.
 * DispatcherServlet automatically exposes its current Locale here.
 * Other applications can expose theirs too, to make classes like
 * MessageSourceAccessor automatically use that Locale.
 *
 * @author Juergen Hoeller
 * @since 1.2
 * @see LocaleContext
 * @see com.rocket.summer.framework.context.support.MessageSourceAccessor
 * @see com.rocket.summer.framework.web.servlet.DispatcherServlet
 */
public abstract class LocaleContextHolder {

    private static final ThreadLocal<LocaleContext> localeContextHolder =
            new NamedThreadLocal<LocaleContext>("Locale context");

    private static final ThreadLocal<LocaleContext> inheritableLocaleContextHolder =
            new NamedInheritableThreadLocal<LocaleContext>("Locale context");

    // Shared default time zone at the framework level
    private static TimeZone defaultTimeZone;


    /**
     * Reset the LocaleContext for the current thread.
     */
    public static void resetLocaleContext() {
        localeContextHolder.remove();
        inheritableLocaleContextHolder.remove();
    }

    /**
     * Associate the given LocaleContext with the current thread,
     * <i>not</i> exposing it as inheritable for child threads.
     * @param localeContext the current LocaleContext
     */
    public static void setLocaleContext(LocaleContext localeContext) {
        setLocaleContext(localeContext, false);
    }

    /**
     * Associate the given LocaleContext with the current thread.
     * @param localeContext the current LocaleContext,
     * or <code>null</code> to reset the thread-bound context
     * @param inheritable whether to expose the LocaleContext as inheritable
     * for child threads (using an {@link java.lang.InheritableThreadLocal})
     */
    public static void setLocaleContext(LocaleContext localeContext, boolean inheritable) {
        if (localeContext == null) {
            resetLocaleContext();
        }
        else {
            if (inheritable) {
                inheritableLocaleContextHolder.set(localeContext);
                localeContextHolder.remove();
            }
            else {
                localeContextHolder.set(localeContext);
                inheritableLocaleContextHolder.remove();
            }
        }
    }

    /**
     * Return the LocaleContext associated with the current thread, if any.
     * @return the current LocaleContext, or <code>null</code> if none
     */
    public static LocaleContext getLocaleContext() {
        LocaleContext localeContext = localeContextHolder.get();
        if (localeContext == null) {
            localeContext = inheritableLocaleContextHolder.get();
        }
        return localeContext;
    }

    /**
     * Associate the given Locale with the current thread.
     * <p>Will implicitly create a LocaleContext for the given Locale,
     * <i>not</i> exposing it as inheritable for child threads.
     * @param locale the current Locale, or <code>null</code> to reset
     * the thread-bound context
     * @see SimpleLocaleContext#SimpleLocaleContext(java.util.Locale)
     */
    public static void setLocale(Locale locale) {
        setLocale(locale, false);
    }

    /**
     * Associate the given Locale with the current thread.
     * <p>Will implicitly create a LocaleContext for the given Locale.
     * @param locale the current Locale, or <code>null</code> to reset
     * the thread-bound context
     * @param inheritable whether to expose the LocaleContext as inheritable
     * for child threads (using an {@link java.lang.InheritableThreadLocal})
     * @see SimpleLocaleContext#SimpleLocaleContext(java.util.Locale)
     */
    public static void setLocale(Locale locale, boolean inheritable) {
        LocaleContext localeContext = (locale != null ? new SimpleLocaleContext(locale) : null);
        setLocaleContext(localeContext, inheritable);
    }

    /**
     * Return the Locale associated with the current thread, if any,
     * or the system default Locale else.
     * @return the current Locale, or the system default Locale if no
     * specific Locale has been associated with the current thread
     * @see LocaleContext#getLocale()
     * @see java.util.Locale#getDefault()
     */
    public static Locale getLocale() {
        LocaleContext localeContext = getLocaleContext();
        return (localeContext != null ? localeContext.getLocale() : Locale.getDefault());
    }

    /**
     * Return the TimeZone associated with the current thread, if any,
     * or the system default TimeZone otherwise. This is effectively a
     * replacement for {@link java.util.TimeZone#getDefault()},
     * able to optionally respect a user-level TimeZone setting.
     * <p>Note: This method has a fallback to the shared default TimeZone,
     * either at the framework level or at the JVM-wide system level.
     * If you'd like to check for the raw LocaleContext content
     * (which may indicate no specific time zone through {@code null}, use
     * {@link #getLocaleContext()} and call {@link TimeZoneAwareLocaleContext#getTimeZone()}
     * after downcasting to {@link TimeZoneAwareLocaleContext}.
     * @return the current TimeZone, or the system default TimeZone if no
     * specific TimeZone has been associated with the current thread
     * @see TimeZoneAwareLocaleContext#getTimeZone()
     * @see #setDefaultTimeZone(TimeZone)
     * @see java.util.TimeZone#getDefault()
     */
    public static TimeZone getTimeZone() {
        LocaleContext localeContext = getLocaleContext();
        if (localeContext instanceof TimeZoneAwareLocaleContext) {
            TimeZone timeZone = ((TimeZoneAwareLocaleContext) localeContext).getTimeZone();
            if (timeZone != null) {
                return timeZone;
            }
        }
        return (defaultTimeZone != null ? defaultTimeZone : TimeZone.getDefault());
    }

}

