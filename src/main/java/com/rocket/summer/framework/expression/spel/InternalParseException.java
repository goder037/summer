package com.rocket.summer.framework.expression.spel;

/**
 * Wraps a real parse exception. This exception flows to the top parse method and then
 * the wrapped exception is thrown as the real problem.
 *
 * @author Andy Clement
 * @since 3.0
 */
public class InternalParseException extends RuntimeException {

    public InternalParseException(SpelParseException cause) {
        super(cause);
    }

    @Override
    public SpelParseException getCause() {
        return (SpelParseException) super.getCause();
    }

}
