package com.rocket.summer.framework.web.ui.context.support;

import com.rocket.summer.framework.context.MessageSource;
import com.rocket.summer.framework.util.Assert;
import com.rocket.summer.framework.web.ui.context.Theme;

/**
 * Default {@link Theme} implementation, wrapping a name and an
 * underlying {@link com.rocket.summer.framework.context.MessageSource}.
 *
 * @author Juergen Hoeller
 * @since 17.06.2003
 */
public class SimpleTheme implements Theme {

    private final String name;

    private final MessageSource messageSource;


    /**
     * Create a SimpleTheme.
     * @param name the name of the theme
     * @param messageSource the MessageSource that resolves theme messages
     */
    public SimpleTheme(String name, MessageSource messageSource) {
        Assert.notNull(name, "Name must not be null");
        Assert.notNull(messageSource, "MessageSource must not be null");
        this.name = name;
        this.messageSource = messageSource;
    }


    public final String getName() {
        return this.name;
    }

    public final MessageSource getMessageSource() {
        return this.messageSource;
    }

}

