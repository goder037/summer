package com.rocket.summer.framework.data.keyvalue.core;

import java.util.Comparator;

import com.rocket.summer.framework.expression.spel.standard.SpelExpression;
import com.rocket.summer.framework.expression.spel.standard.SpelExpressionParser;

/**
 * {@link Comparator} implementation using {@link SpelExpression}.
 *
 * @author Christoph Strobl
 * @author Oliver Gierke
 * @param <T>
 */
public class SpelPropertyComparator<T> implements Comparator<T> {

    private final String path;
    private final SpelExpressionParser parser;

    private boolean asc = true;
    private boolean nullsFirst = true;
    private SpelExpression expression;

    /**
     * Create new {@link SpelPropertyComparator} for the given property path an {@link SpelExpressionParser}..
     *
     * @param path must not be {@literal null} or empty.
     * @param parser must not be {@literal null}.
     */
    public SpelPropertyComparator(String path, SpelExpressionParser parser) {

        this.path = path;
        this.parser = parser;
    }

    /**
     * Sort {@literal ascending}.
     *
     * @return
     */
    public SpelPropertyComparator<T> asc() {
        this.asc = true;
        return this;
    }

    /**
     * Sort {@literal descending}.
     *
     * @return
     */
    public SpelPropertyComparator<T> desc() {
        this.asc = false;
        return this;
    }

    /**
     * Sort {@literal null} values first.
     *
     * @return
     */
    public SpelPropertyComparator<T> nullsFirst() {
        this.nullsFirst = true;
        return this;
    }

    /**
     * Sort {@literal null} values last.
     *
     * @return
     */
    public SpelPropertyComparator<T> nullsLast() {
        this.nullsFirst = false;
        return this;
    }

    /**
     * Parse values to {@link SpelExpression}
     *
     * @return
     */
    protected SpelExpression getExpression() {

        if (this.expression == null) {
            this.expression = parser.parseRaw(buildExpressionForPath());
        }

        return this.expression;
    }

    /**
     * Create the expression raw value.
     *
     * @return
     */
    protected String buildExpressionForPath() {

        StringBuilder rawExpression = new StringBuilder(
                "new com.rocket.summer.framework.util.comparator.NullSafeComparator(new com.rocket.summer.framework.util.comparator.ComparableComparator(), "
                        + Boolean.toString(this.nullsFirst) + ").compare(");

        rawExpression.append("#arg1?.");
        rawExpression.append(path != null ? path.replace(".", "?.") : "");
        rawExpression.append(",");
        rawExpression.append("#arg2?.");
        rawExpression.append(path != null ? path.replace(".", "?.") : "");
        rawExpression.append(")");

        return rawExpression.toString();
    }

    /*
     * (non-Javadoc)
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    @Override
    public int compare(T arg1, T arg2) {

        SpelExpression expressionToUse = getExpression();

        expressionToUse.getEvaluationContext().setVariable("arg1", arg1);
        expressionToUse.getEvaluationContext().setVariable("arg2", arg2);

        return expressionToUse.getValue(Integer.class) * (asc ? 1 : -1);
    }

    /**
     * Get dot path to property.
     *
     * @return
     */
    public String getPath() {
        return path;
    }
}

