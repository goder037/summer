package com.rocket.summer.framework.data.querydsl;

import com.rocket.summer.framework.data.domain.Page;
import com.rocket.summer.framework.data.domain.Pageable;
import com.rocket.summer.framework.data.domain.Sort;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;

/**
 * Interface to allow execution of QueryDsl {@link Predicate} instances.
 *
 * @author Oliver Gierke
 * @author Thomas Darimont
 */
public interface QueryDslPredicateExecutor<T> {

    /**
     * Returns a single entity matching the given {@link Predicate} or {@literal null} if none was found.
     *
     * @param predicate can be {@literal null}.
     * @return a single entity matching the given {@link Predicate} or {@literal null} if none was found.
     * @throws com.rocket.summer.framework.dao.IncorrectResultSizeDataAccessException if the predicate yields more than one
     *           result.
     */
    T findOne(Predicate predicate);

    /**
     * Returns all entities matching the given {@link Predicate}. In case no match could be found an empty
     * {@link Iterable} is returned.
     *
     * @param predicate can be {@literal null}.
     * @return all entities matching the given {@link Predicate}.
     */
    Iterable<T> findAll(Predicate predicate);

    /**
     * Returns all entities matching the given {@link Predicate} applying the given {@link Sort}. In case no match could
     * be found an empty {@link Iterable} is returned.
     *
     * @param predicate can be {@literal null}.
     * @param sort the {@link Sort} specification to sort the results by, must not be {@literal null}.
     * @return all entities matching the given {@link Predicate}.
     * @since 1.10
     */
    Iterable<T> findAll(Predicate predicate, Sort sort);

    /**
     * Returns all entities matching the given {@link Predicate} applying the given {@link OrderSpecifier}s. In case no
     * match could be found an empty {@link Iterable} is returned.
     *
     * @param predicate can be {@literal null}.
     * @param orders the {@link OrderSpecifier}s to sort the results by
     * @return all entities matching the given {@link Predicate} applying the given {@link OrderSpecifier}s.
     */
    Iterable<T> findAll(Predicate predicate, OrderSpecifier<?>... orders);

    /**
     * Returns all entities ordered by the given {@link OrderSpecifier}s.
     *
     * @param orders the {@link OrderSpecifier}s to sort the results by.
     * @return all entities ordered by the given {@link OrderSpecifier}s.
     */
    Iterable<T> findAll(OrderSpecifier<?>... orders);

    /**
     * Returns a {@link Page} of entities matching the given {@link Predicate}. In case no match could be found, an empty
     * {@link Page} is returned.
     *
     * @param predicate can be {@literal null}.
     * @param pageable can be {@literal null}.
     * @return a {@link Page} of entities matching the given {@link Predicate}.
     */
    Page<T> findAll(Predicate predicate, Pageable pageable);

    /**
     * Returns the number of instances matching the given {@link Predicate}.
     *
     * @param predicate the {@link Predicate} to count instances for, can be {@literal null}.
     * @return the number of instances matching the {@link Predicate}.
     */
    long count(Predicate predicate);

    /**
     * Checks whether the data store contains elements that match the given {@link Predicate}.
     *
     * @param predicate the {@link Predicate} to use for the existence check, can be {@literal null}.
     * @return {@literal true} if the data store contains elements that match the given {@link Predicate}.
     */
    boolean exists(Predicate predicate);
}

