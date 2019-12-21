package com.rocket.summer.framework.expression.spel;

import com.rocket.summer.framework.expression.ParseException;

/**
 * Root exception for Spring EL related exceptions. Rather than holding a hard coded
 * string indicating the problem, it records a message key and the inserts for the
 * message. See {@link SpelMessage} for the list of all possible messages that can occur.
 *
 * @author Andy Clement
 * @author Juergen Hoeller
 * @since 3.0
 */
@SuppressWarnings("serial")
public class SpelParseException extends ParseException {

    private final SpelMessage message;

    private final Object[] inserts;


    public SpelParseException(String expressionString, int position, SpelMessage message, Object... inserts) {
        super(expressionString, position, message.formatMessage(inserts));
        this.message = message;
        this.inserts = inserts;
    }

    public SpelParseException(int position, SpelMessage message, Object... inserts) {
        super(position, message.formatMessage(inserts));
        this.message = message;
        this.inserts = inserts;
    }

    public SpelParseException(int position, Throwable cause, SpelMessage message, Object... inserts) {
        super(position, message.formatMessage(inserts), cause);
        this.message = message;
        this.inserts = inserts;
    }


    /**
     * Return the message code.
     */
    public SpelMessage getMessageCode() {
        return this.message;
    }

    /**
     * Return the message inserts.
     */
    public Object[] getInserts() {
        return this.inserts;
    }

}

