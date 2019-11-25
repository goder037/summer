package com.rocket.summer.framework.data.keyvalue.repository.query;

import java.lang.reflect.Constructor;

import com.rocket.summer.framework.beans.BeanUtils;
import com.rocket.summer.framework.data.domain.PageImpl;
import com.rocket.summer.framework.data.domain.Pageable;
import com.rocket.summer.framework.data.domain.Sort;
import com.rocket.summer.framework.data.keyvalue.core.IterableConverter;
import com.rocket.summer.framework.data.keyvalue.core.KeyValueOperations;
import com.rocket.summer.framework.data.keyvalue.core.SpelCriteria;
import com.rocket.summer.framework.data.keyvalue.core.query.KeyValueQuery;
import com.rocket.summer.framework.data.repository.query.EvaluationContextProvider;
import com.rocket.summer.framework.data.repository.query.ParameterAccessor;
import com.rocket.summer.framework.data.repository.query.ParametersParameterAccessor;
import com.rocket.summer.framework.data.repository.query.QueryMethod;
import com.rocket.summer.framework.data.repository.query.RepositoryQuery;
import com.rocket.summer.framework.data.repository.query.ResultProcessor;
import com.rocket.summer.framework.data.repository.query.parser.AbstractQueryCreator;
import com.rocket.summer.framework.data.repository.query.parser.PartTree;
import com.rocket.summer.framework.expression.EvaluationContext;
import com.rocket.summer.framework.expression.spel.standard.SpelExpression;
import com.rocket.summer.framework.util.Assert;
import com.rocket.summer.framework.util.ClassUtils;

/**
 * {@link RepositoryQuery} implementation deriving queries from {@link PartTree} using a predefined
 * {@link AbstractQueryCreator}.
 *
 * @author Christoph Strobl
 * @author Oliver Gierke
 * @author Mark Paluch
 */
public class KeyValuePartTreeQuery implements RepositoryQuery {

    private final EvaluationContextProvider evaluationContextProvider;
    private final QueryMethod queryMethod;
    private final KeyValueOperations keyValueOperations;
    private final Class<? extends AbstractQueryCreator<?, ?>> queryCreator;

    /**
     * Creates a new {@link KeyValuePartTreeQuery} for the given {@link QueryMethod}, {@link EvaluationContextProvider},
     * {@link KeyValueOperations} and query creator type.
     *
     * @param queryMethod must not be {@literal null}.
     * @param evaluationContextProvider must not be {@literal null}.
     * @param keyValueOperations must not be {@literal null}.
     * @param queryCreator must not be {@literal null}.
     */
    public KeyValuePartTreeQuery(QueryMethod queryMethod, EvaluationContextProvider evaluationContextProvider,
                                 KeyValueOperations keyValueOperations, Class<? extends AbstractQueryCreator<?, ?>> queryCreator) {

        Assert.notNull(queryMethod, "Query method must not be null!");
        Assert.notNull(evaluationContextProvider, "EvaluationContextprovider must not be null!");
        Assert.notNull(keyValueOperations, "KeyValueOperations must not be null!");
        Assert.notNull(queryCreator, "QueryCreator type must not be null!");

        this.queryMethod = queryMethod;
        this.keyValueOperations = keyValueOperations;
        this.evaluationContextProvider = evaluationContextProvider;
        this.queryCreator = queryCreator;
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.repository.query.RepositoryQuery#execute(java.lang.Object[])
     */
    @Override
    public Object execute(Object[] parameters) {

        ParameterAccessor accessor = new ParametersParameterAccessor(getQueryMethod().getParameters(), parameters);
        KeyValueQuery<?> query = prepareQuery(parameters);
        ResultProcessor processor = queryMethod.getResultProcessor().withDynamicProjection(accessor);

        return processor.processResult(doExecute(parameters, query));
    }

    /**
     * @param parameters
     * @param query
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    protected Object doExecute(Object[] parameters, KeyValueQuery<?> query) {

        if (queryMethod.isPageQuery() || queryMethod.isSliceQuery()) {

            Pageable page = (Pageable) parameters[queryMethod.getParameters().getPageableIndex()];
            query.setOffset(page.getOffset());
            query.setRows(page.getPageSize());

            Iterable<?> result = this.keyValueOperations.find(query, queryMethod.getEntityInformation().getJavaType());

            long count = queryMethod.isSliceQuery() ? 0
                    : keyValueOperations.count(query, queryMethod.getEntityInformation().getJavaType());

            return new PageImpl(IterableConverter.toList(result), page, count);

        } else if (queryMethod.isCollectionQuery()) {

            return this.keyValueOperations.find(query, queryMethod.getEntityInformation().getJavaType());

        } else if (queryMethod.isQueryForEntity()) {

            Iterable<?> result = this.keyValueOperations.find(query, queryMethod.getEntityInformation().getJavaType());
            return result.iterator().hasNext() ? result.iterator().next() : null;
        }

        throw new UnsupportedOperationException("Query method not supported.");
    }

    protected KeyValueQuery<?> prepareQuery(Object[] parameters) {

        return prepareQuery(createQuery(new ParametersParameterAccessor(getQueryMethod().getParameters(), parameters)),
                parameters);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected KeyValueQuery<?> prepareQuery(KeyValueQuery<?> instance, Object[] parameters) {

        ParametersParameterAccessor accessor = new ParametersParameterAccessor(getQueryMethod().getParameters(),
                parameters);

        Object criteria = instance.getCriteria();

        if (criteria instanceof SpelCriteria || criteria instanceof SpelExpression) {

            SpelExpression spelExpression = getSpelExpression(criteria);
            EvaluationContext context = this.evaluationContextProvider.getEvaluationContext(getQueryMethod().getParameters(),
                    parameters);
            criteria = new SpelCriteria(spelExpression, context);
        }

        KeyValueQuery<?> query = new KeyValueQuery(criteria);
        Pageable pageable = accessor.getPageable();
        Sort sort = accessor.getSort();

        query.setOffset(pageable == null ? -1 : pageable.getOffset());

        if (pageable != null) {
            query.setRows(pageable.getPageSize());
        } else if (instance.getRows() >= 0) {
            query.setRows(instance.getRows());
        }

        query.setSort(sort == null ? instance.getSort() : sort);

        return query;
    }

    private SpelExpression getSpelExpression(Object criteria) {

        if (criteria instanceof SpelExpression) {
            return (SpelExpression) criteria;
        }

        if (criteria instanceof SpelCriteria) {
            return getSpelExpression(((SpelCriteria) criteria).getExpression());
        }

        throw new IllegalStateException(String.format("Cannot retrieve SpelExpression from %s", criteria));
    }

    public KeyValueQuery<?> createQuery(ParameterAccessor accessor) {

        PartTree tree = new PartTree(getQueryMethod().getName(), getQueryMethod().getEntityInformation().getJavaType());

        Constructor<? extends AbstractQueryCreator<?, ?>> constructor = (Constructor<? extends AbstractQueryCreator<?, ?>>) ClassUtils
                .getConstructorIfAvailable(queryCreator, PartTree.class, ParameterAccessor.class);
        KeyValueQuery<?> query = (KeyValueQuery<?>) BeanUtils.instantiateClass(constructor, tree, accessor).createQuery();

        if (tree.isLimiting()) {
            query.setRows(tree.getMaxResults());
        }
        return query;
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.repository.query.RepositoryQuery#getQueryMethod()
     */
    @Override
    public QueryMethod getQueryMethod() {
        return queryMethod;
    }
}

