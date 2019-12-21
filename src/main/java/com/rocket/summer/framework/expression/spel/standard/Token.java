package com.rocket.summer.framework.expression.spel.standard;

/**
 * Holder for a kind of token, the associated data and its position in the input data
 * stream (start/end).
 *
 * @author Andy Clement
 * @since 3.0
 */
class Token {

    TokenKind kind;

    String data;

    int startPos;  // index of first character

    int endPos;  // index of char after the last character


    /**
     * Constructor for use when there is no particular data for the token
     * (e.g. TRUE or '+')
     * @param startPos the exact start
     * @param endPos the index to the last character
     */
    Token(TokenKind tokenKind, int startPos, int endPos) {
        this.kind = tokenKind;
        this.startPos = startPos;
        this.endPos = endPos;
    }

    Token(TokenKind tokenKind, char[] tokenData, int startPos, int endPos) {
        this(tokenKind, startPos, endPos);
        this.data = new String(tokenData);
    }


    public TokenKind getKind() {
        return this.kind;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append("[").append(this.kind.toString());
        if (this.kind.hasPayload()) {
            s.append(":").append(this.data);
        }
        s.append("]");
        s.append("(").append(this.startPos).append(",").append(this.endPos).append(")");
        return s.toString();
    }

    public boolean isIdentifier() {
        return (this.kind == TokenKind.IDENTIFIER);
    }

    public boolean isNumericRelationalOperator() {
        return (this.kind == TokenKind.GT || this.kind == TokenKind.GE || this.kind == TokenKind.LT ||
                this.kind == TokenKind.LE || this.kind==TokenKind.EQ || this.kind==TokenKind.NE);
    }

    public String stringValue() {
        return this.data;
    }

    public Token asInstanceOfToken() {
        return new Token(TokenKind.INSTANCEOF, this.startPos, this.endPos);
    }

    public Token asMatchesToken() {
        return new Token(TokenKind.MATCHES, this.startPos, this.endPos);
    }

    public Token asBetweenToken() {
        return new Token(TokenKind.BETWEEN, this.startPos, this.endPos);
    }

}

