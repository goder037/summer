package com.rocket.summer.framework.ui.context;

import com.rocket.summer.framework.context.MessageSource;

/**
 * A Theme can resolve theme-specific messages, codes, file paths, etcetera
 * (e&#46;g&#46; CSS and image files in a web environment).
 * The exposed {@link com.rocket.summer.framework.context.MessageSource} supports
 * theme-specific parameterization and internationalization.
 *
 * @author Juergen Hoeller
 * @since 17.06.2003
 * @see ThemeSource
 * @see com.rocket.summer.framework.web.servlet.ThemeResolver
 */
public interface Theme {

    /**
     * Return the name of the theme.
     * @return the name of the theme (never {@code null})
     */
    String getName();

    /**
     * Return the specific MessageSource that resolves messages
     * with respect to this theme.
     * @return the theme-specific MessageSource (never {@code null})
     */
    MessageSource getMessageSource();

}

