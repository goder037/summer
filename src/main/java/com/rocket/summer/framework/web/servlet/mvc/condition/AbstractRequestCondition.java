package com.rocket.summer.framework.web.servlet.mvc.condition;

import java.util.Collection;
import java.util.Iterator;

/**
 * A base class for {@link RequestCondition} types providing implementations of
 * {@link #equals(Object)}, {@link #hashCode()}, and {@link #toString()}.
 *
 * @author Rossen Stoyanchev
 * @since 3.1
 */
public abstract class AbstractRequestCondition<T extends AbstractRequestCondition<T>> implements RequestCondition<T> {

    /**
     * Return the discrete items a request condition is composed of.
     * For example URL patterns, HTTP request methods, param expressions, etc.
     * @return a collection of objects, never {@code null}
     */
    protected abstract Collection<?> getContent();

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o != null && getClass().equals(o.getClass())) {
            AbstractRequestCondition<?> other = (AbstractRequestCondition<?>) o;
            return getContent().equals(other.getContent());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return getContent().hashCode();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("[");
        for (Iterator<?> iterator = getContent().iterator(); iterator.hasNext();) {
            Object expression = iterator.next();
            builder.append(expression.toString());
            if (iterator.hasNext()) {
                builder.append(getToStringInfix());
            }
        }
        builder.append("]");
        return builder.toString();
    }

    /**
     * The notation to use when printing discrete items of content.
     * For example " || " for URL patterns or " && " for param expressions.
     */
    protected abstract String getToStringInfix();

}