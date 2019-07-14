package com.rocket.summer.framework.context.support;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public abstract class MessageSourceSupport {

    /** Logger available to subclasses */
    protected final Log logger = LogFactory.getLog(getClass());

    private boolean alwaysUseMessageFormat = false;

    /**
     * Cache to hold already generated MessageFormats per message.
     * Used for passed-in default messages. MessageFormats for resolved
     * codes are cached on a specific basis in subclasses.
     */
    private final Map cachedMessageFormats = new HashMap();

    /**
     * Render the given default message String. The default message is
     * passed in as specified by the caller and can be rendered into
     * a fully formatted default message shown to the user.
     * <p>The default implementation passes the String to <code>formatMessage</code>,
     * resolving any argument placeholders found in them. Subclasses may override
     * this method to plug in custom processing of default messages.
     * @param defaultMessage the passed-in default message String
     * @param args array of arguments that will be filled in for params within
     * the message, or <code>null</code> if none.
     * @param locale the Locale used for formatting
     * @return the rendered default message (with resolved arguments)
     * @see #formatMessage(String, Object[], java.util.Locale)
     */
    protected String renderDefaultMessage(String defaultMessage, Object[] args, Locale locale) {
        return formatMessage(defaultMessage, args, locale);
    }

    /**
     * Format the given message String, using cached MessageFormats.
     * By default invoked for passed-in default messages, to resolve
     * any argument placeholders found in them.
     * @param msg the message to format
     * @param args array of arguments that will be filled in for params within
     * the message, or <code>null</code> if none.
     * @param locale the Locale used for formatting
     * @return the formatted message (with resolved arguments)
     */
    protected String formatMessage(String msg, Object[] args, Locale locale) {
        if (msg == null || (!this.alwaysUseMessageFormat && (args == null || args.length == 0))) {
            return msg;
        }
        MessageFormat messageFormat = null;
        synchronized (this.cachedMessageFormats) {
            messageFormat = (MessageFormat) this.cachedMessageFormats.get(msg);
            if (messageFormat == null) {
                messageFormat = createMessageFormat(msg, locale);
                this.cachedMessageFormats.put(msg, messageFormat);
            }
        }
        synchronized (messageFormat) {
            return messageFormat.format(resolveArguments(args));
        }
    }

    /**
     * Template method for resolving argument objects.
     * <p>The default implementation simply returns the given argument
     * array as-is. Can be overridden in subclasses in order to resolve
     * special argument types.
     * @param args the original argument array
     * @param locale the Locale to resolve against
     * @return the resolved argument array
     */
    protected Object[] resolveArguments(Object[] args) {
        return args;
    }

    /**
     * Create a MessageFormat for the given message and Locale.
     * @param msg the message to create a MessageFormat for
     * @param locale the Locale to create a MessageFormat for
     * @return the MessageFormat instance
     */
    protected MessageFormat createMessageFormat(String msg, Locale locale) {
        if (logger.isDebugEnabled()) {
            logger.debug("Creating MessageFormat for pattern [" + msg + "] and locale '" + locale + "'");
        }
        return new MessageFormat((msg != null ? msg : ""), locale);
    }
}
