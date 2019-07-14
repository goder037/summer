package com.rocket.summer.framework.context.support;

import com.rocket.summer.framework.context.HierarchicalMessageSource;
import com.rocket.summer.framework.context.MessageSource;
import com.rocket.summer.framework.context.MessageSourceResolvable;
import com.rocket.summer.framework.context.NoSuchMessageException;

import java.util.Locale;

public class DelegatingMessageSource extends MessageSourceSupport implements HierarchicalMessageSource {

    private MessageSource parentMessageSource;

    public void setParentMessageSource(MessageSource parent) {
        this.parentMessageSource = parent;
    }

    public MessageSource getParentMessageSource() {
        return this.parentMessageSource;
    }

    public String getMessage(String code, Object[] args, String defaultMessage, Locale locale) {
        if (this.parentMessageSource != null) {
            return this.parentMessageSource.getMessage(code, args, defaultMessage, locale);
        }
        else {
            return renderDefaultMessage(defaultMessage, args, locale);
        }
    }

    public String getMessage(String code, Object[] args, Locale locale) throws NoSuchMessageException {
        if (this.parentMessageSource != null) {
            return this.parentMessageSource.getMessage(code, args, locale);
        }
        else {
            throw new NoSuchMessageException(code, locale);
        }
    }

    public String getMessage(MessageSourceResolvable resolvable, Locale locale) throws NoSuchMessageException {
        if (this.parentMessageSource != null) {
            return this.parentMessageSource.getMessage(resolvable, locale);
        }
        else {
            if (resolvable.getDefaultMessage() != null) {
                return renderDefaultMessage(resolvable.getDefaultMessage(), resolvable.getArguments(), locale);
            }
            String[] codes = resolvable.getCodes();
            String code = (codes != null && codes.length > 0 ? codes[0] : null);
            throw new NoSuchMessageException(code, locale);
        }
    }
}
