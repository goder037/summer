package com.rocket.summer.framework.web.servlet.theme;

import com.rocket.summer.framework.web.servlet.ThemeResolver;

/**
 * Abstract base class for {@link ThemeResolver} implementations.
 * Provides support for a default theme name.
 *
 * @author Juergen Hoeller
 * @author Jean-Pierre Pawlak
 * @since 17.06.2003
 */
public abstract class AbstractThemeResolver implements ThemeResolver {

    /**
     * Out-of-the-box value for the default theme name: "theme".
     */
    public final static String ORIGINAL_DEFAULT_THEME_NAME = "theme";

    private String defaultThemeName = ORIGINAL_DEFAULT_THEME_NAME;


    /**
     * Set the name of the default theme.
     * Out-of-the-box value is "theme".
     */
    public void setDefaultThemeName(String defaultThemeName) {
        this.defaultThemeName = defaultThemeName;
    }

    /**
     * Return the name of the default theme.
     */
    public String getDefaultThemeName() {
        return this.defaultThemeName;
    }

}

