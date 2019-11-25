package com.rocket.summer.framework.data.repository.query;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.rocket.summer.framework.data.domain.Pageable;
import com.rocket.summer.framework.data.domain.Sort;
import com.rocket.summer.framework.data.repository.util.QueryExecutionConverters;
import com.rocket.summer.framework.util.Assert;

/**
 * {@link ParameterAccessor} implementation using a {@link Parameters} instance to find special parameters.
 *
 * @author Oliver Gierke
 */
public class ParametersParameterAccessor implements ParameterAccessor {

    private final Parameters<?, ?> parameters;
    private final List<Object> values;

    /**
     * Creates a new {@link ParametersParameterAccessor}.
     *
     * @param parameters must not be {@literal null}.
     * @param values must not be {@literal null}.
     */
    public ParametersParameterAccessor(Parameters<?, ?> parameters, Object[] values) {

        Assert.notNull(parameters, "Parameters must not be null!");
        Assert.notNull(values, "Values must not be null!");

        Assert.isTrue(parameters.getNumberOfParameters() == values.length, "Invalid number of parameters given!");

        this.parameters = parameters;

        List<Object> unwrapped = new ArrayList<Object>(values.length);

        for (Object element : values.clone()) {
            unwrapped.add(QueryExecutionConverters.unwrap(element));
        }

        this.values = unwrapped;
    }

    /**
     * Returns the {@link Parameters} instance backing the accessor.
     *
     * @return the parameters will never be {@literal null}.
     */
    public Parameters<?, ?> getParameters() {
        return parameters;
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.repository.query.ParameterAccessor#getPageable()
     */
    public Pageable getPageable() {

        if (!parameters.hasPageableParameter()) {
            return null;
        }

        return (Pageable) values.get(parameters.getPageableIndex());
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.repository.query.ParameterAccessor#getSort()
     */
    public Sort getSort() {

        if (parameters.hasSortParameter()) {
            return (Sort) values.get(parameters.getSortIndex());
        }

        if (parameters.hasPageableParameter() && getPageable() != null) {
            return getPageable().getSort();
        }

        return null;
    }

    /**
     * Returns the dynamic projection type if available, {@literal null} otherwise.
     *
     * @return
     */
    public Class<?> getDynamicProjection() {
        return parameters.hasDynamicProjection() ? (Class<?>) values.get(parameters.getDynamicProjectionIndex()) : null;
    }

    /**
     * Returns the value with the given index.
     *
     * @param index
     * @return
     */
    @SuppressWarnings("unchecked")
    protected <T> T getValue(int index) {
        return (T) values.get(index);
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.repository.query.ParameterAccessor#getBindableValue(int)
     */
    public Object getBindableValue(int index) {
        return values.get(parameters.getBindableParameter(index).getIndex());
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.repository.query.ParameterAccessor#hasBindableNullValue()
     */
    public boolean hasBindableNullValue() {

        for (Parameter parameter : parameters.getBindableParameters()) {
            if (values.get(parameter.getIndex()) == null) {
                return true;
            }
        }

        return false;
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.repository.query.ParameterAccessor#iterator()
     */
    public BindableParameterIterator iterator() {
        return new BindableParameterIterator(this);
    }

    /**
     * Iterator class to allow traversing all bindable parameters inside the accessor.
     *
     * @author Oliver Gierke
     */
    private static class BindableParameterIterator implements Iterator<Object> {

        private final int bindableParameterCount;
        private final ParameterAccessor accessor;

        private int currentIndex = 0;

        /**
         * Creates a new {@link BindableParameterIterator}.
         *
         * @param accessor must not be {@literal null}.
         */
        public BindableParameterIterator(ParametersParameterAccessor accessor) {

            Assert.notNull(accessor, "ParametersParameterAccessor must not be null!");

            this.accessor = accessor;
            this.bindableParameterCount = accessor.getParameters().getBindableParameters().getNumberOfParameters();
        }

        /**
         * Returns the next bindable parameter.
         *
         * @return
         */
        public Object next() {
            return accessor.getBindableValue(currentIndex++);
        }

        /*
         * (non-Javadoc)
         * @see java.util.Iterator#hasNext()
         */
        public boolean hasNext() {
            return bindableParameterCount > currentIndex;
        }

        /*
         * (non-Javadoc)
         * @see java.util.Iterator#remove()
         */
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}

