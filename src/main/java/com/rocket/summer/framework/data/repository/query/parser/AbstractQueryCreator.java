package com.rocket.summer.framework.data.repository.query.parser;

import java.util.Iterator;

import com.rocket.summer.framework.data.domain.Sort;
import com.rocket.summer.framework.data.repository.query.ParameterAccessor;
import com.rocket.summer.framework.data.repository.query.ParametersParameterAccessor;
import com.rocket.summer.framework.data.repository.query.parser.PartTree.OrPart;
import com.rocket.summer.framework.util.Assert;

/**
 * Base class for query creators that create criteria based queries from a {@link PartTree}.
 *
 * @param T the actual query type to be created
 * @param S the intermediate criteria type
 * @author Oliver Gierke
 */
public abstract class AbstractQueryCreator<T, S> {

    private final ParameterAccessor parameters;
    private final PartTree tree;

    /**
     * Creates a new {@link AbstractQueryCreator} for the given {@link PartTree} and {@link ParametersParameterAccessor}.
     * The latter is used to hand actual parameter values into the callback methods as well as to apply dynamic sorting
     * via a {@link Sort} parameter.
     *
     * @param tree must not be {@literal null}.
     * @param parameters can be {@literal null}.
     */
    public AbstractQueryCreator(PartTree tree, ParameterAccessor parameters) {

        Assert.notNull(tree, "PartTree must not be null");

        this.tree = tree;
        this.parameters = parameters;
    }

    /**
     * Creates a new {@link AbstractQueryCreator} for the given {@link PartTree}. This will cause {@literal null} be
     * handed for the {@link Iterator} in the callback methods.
     *
     * @param tree must not be {@literal null}.
     */
    public AbstractQueryCreator(PartTree tree) {

        this(tree, null);
    }

    /**
     * Creates the actual query object.
     *
     * @return
     */
    public T createQuery() {

        Sort dynamicSort = parameters != null ? parameters.getSort() : null;
        return createQuery(dynamicSort);
    }

    /**
     * Creates the actual query object applying the given {@link Sort} parameter. Use this method in case you haven't
     * provided a {@link ParameterAccessor} in the first place but want to apply dynamic sorting nevertheless.
     *
     * @param dynamicSort
     * @return
     */
    public T createQuery(Sort dynamicSort) {

        Sort staticSort = tree.getSort();
        Sort sort = staticSort != null ? staticSort.and(dynamicSort) : dynamicSort;

        return complete(createCriteria(tree), sort);
    }

    /**
     * Actual query building logic. Traverses the {@link PartTree} and invokes callback methods to delegate actual
     * criteria creation and concatenation.
     *
     * @param tree
     * @return
     */
    private S createCriteria(PartTree tree) {

        S base = null;
        Iterator<Object> iterator = parameters == null ? null : parameters.iterator();

        for (OrPart node : tree) {

            S criteria = null;

            for (Part part : node) {

                criteria = criteria == null ? create(part, iterator) : and(part, criteria, iterator);
            }

            base = base == null ? criteria : or(base, criteria);
        }

        return base;
    }

    /**
     * Creates a new atomic instance of the criteria object.
     *
     * @param part
     * @param iterator
     * @return
     */
    protected abstract S create(Part part, Iterator<Object> iterator);

    /**
     * Creates a new criteria object from the given part and and-concatenates it to the given base criteria.
     *
     * @param part
     * @param base will never be {@literal null}.
     * @param iterator
     * @return
     */
    protected abstract S and(Part part, S base, Iterator<Object> iterator);

    /**
     * Or-concatenates the given base criteria to the given new criteria.
     *
     * @param base
     * @param criteria
     * @return
     */
    protected abstract S or(S base, S criteria);

    /**
     * Actually creates the query object applying the given criteria object and {@link Sort} definition.
     *
     * @param criteria will never be {@literal null}.
     * @param sort might be {@literal null}.
     * @return
     */
    protected abstract T complete(S criteria, Sort sort);
}

