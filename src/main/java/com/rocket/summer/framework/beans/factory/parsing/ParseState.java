package com.rocket.summer.framework.beans.factory.parsing;

import java.util.Stack;

/**
 * Simple {@link Stack}-based structure for tracking the logical position during
 * a parsing process. {@link Entry entries} are added to the stack at
 * each point during the parse phase in a reader-specific manner.
 *
 * <p>Calling {@link #toString()} will render a tree-style view of the current logical
 * position in the parse phase. This representation is intended for use in
 * error messages.
 *
 * @author Rob Harrop
 * @since 2.0
 */
public final class ParseState {

    /**
     * Tab character used when rendering the tree-style representation.
     */
    private static final char TAB = '\t';

    /**
     * Internal {@link Stack} storage.
     */
    private final Stack state;


    /**
     * Create a new <code>ParseState</code> with an empty {@link Stack}.
     */
    public ParseState() {
        this.state = new Stack();
    }

    /**
     * Create a new <code>ParseState</code> whose {@link Stack} is a {@link Object#clone clone}
     * of that of the passed in <code>ParseState</code>.
     */
    private ParseState(ParseState other) {
        this.state = (Stack) other.state.clone();
    }


    /**
     * Add a new {@link Entry} to the {@link Stack}.
     */
    public void push(Entry entry) {
        this.state.push(entry);
    }

    /**
     * Remove an {@link Entry} from the {@link Stack}.
     */
    public void pop() {
        this.state.pop();
    }

    /**
     * Return the {@link Entry} currently at the top of the {@link Stack} or
     * <code>null</code> if the {@link Stack} is empty.
     */
    public Entry peek() {
        return (Entry) (this.state.empty() ? null : this.state.peek());
    }

    /**
     * Create a new instance of {@link ParseState} which is an independent snapshot
     * of this instance.
     */
    public ParseState snapshot() {
        return new ParseState(this);
    }


    /**
     * Returns a tree-style representation of the current <code>ParseState</code>.
     */
    public String toString() {
        StringBuffer sb = new StringBuffer();
        for (int x = 0; x < this.state.size(); x++) {
            if (x > 0) {
                sb.append('\n');
                for (int y = 0; y < x; y++) {
                    sb.append(TAB);
                }
                sb.append("-> ");
            }
            sb.append(this.state.get(x));
        }
        return sb.toString();
    }


    /**
     * Marker interface for entries into the {@link ParseState}.
     */
    public interface Entry {

    }

}