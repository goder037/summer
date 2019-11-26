package com.rocket.summer.framework.data.keyvalue.repository.support;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.rocket.summer.framework.data.domain.Page;
import com.rocket.summer.framework.data.domain.PageImpl;
import com.rocket.summer.framework.data.domain.Pageable;
import com.rocket.summer.framework.data.domain.Sort;
import com.rocket.summer.framework.data.keyvalue.core.IterableConverter;
import com.rocket.summer.framework.data.keyvalue.core.KeyValueOperations;
import com.rocket.summer.framework.data.keyvalue.repository.KeyValueRepository;
import com.rocket.summer.framework.data.repository.core.EntityInformation;
import com.rocket.summer.framework.util.Assert;

/**
 * @author Christoph Strobl
 * @author Oliver Gierke
 * @param <T>
 * @param <ID>
 */
public class SimpleKeyValueRepository<T, ID extends Serializable> implements KeyValueRepository<T, ID> {

    private final KeyValueOperations operations;
    private final EntityInformation<T, ID> entityInformation;

    /**
     * Creates a new {@link SimpleKeyValueRepository} for the given {@link EntityInformation} and
     * {@link KeyValueOperations}.
     *
     * @param metadata must not be {@literal null}.
     * @param operations must not be {@literal null}.
     */
    public SimpleKeyValueRepository(EntityInformation<T, ID> metadata, KeyValueOperations operations) {

        Assert.notNull(metadata, "EntityInformation must not be null!");
        Assert.notNull(operations, "KeyValueOperations must not be null!");

        this.entityInformation = metadata;
        this.operations = operations;
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.repository.PagingAndSortingRepository#findAll(com.rocket.summer.framework.data.domain.Sort)
     */
    @Override
    public Iterable<T> findAll(Sort sort) {
        return operations.findAll(sort, entityInformation.getJavaType());
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.repository.PagingAndSortingRepository#findAll(com.rocket.summer.framework.data.domain.Pageable)
     */
    @Override
    public Page<T> findAll(Pageable pageable) {

        if (pageable == null) {
            List<T> result = findAll();
            return new PageImpl<T>(result, null, result.size());
        }

        Iterable<T> content = operations.findInRange(pageable.getOffset(), pageable.getPageSize(), pageable.getSort(),
                entityInformation.getJavaType());

        return new PageImpl<T>(IterableConverter.toList(content), pageable, this.operations.count(entityInformation
                .getJavaType()));
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.repository.CrudRepository#save(java.lang.Object)
     */
    @Override
    public <S extends T> S save(S entity) {

        Assert.notNull(entity, "Entity must not be null!");

        if (entityInformation.isNew(entity)) {
            operations.insert(entity);
        } else {
            operations.update(entityInformation.getId(entity), entity);
        }
        return entity;
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.repository.CrudRepository#save(java.lang.Iterable)
     */
    @Override
    public <S extends T> Iterable<S> save(Iterable<S> entities) {

        for (S entity : entities) {
            save(entity);
        }

        return entities;
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.repository.CrudRepository#findOne(java.io.Serializable)
     */
    @Override
    public T findOne(ID id) {
        return operations.findById(id, entityInformation.getJavaType());
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.repository.CrudRepository#exists(java.io.Serializable)
     */
    @Override
    public boolean exists(ID id) {
        return findOne(id) != null;
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.repository.CrudRepository#findAll()
     */
    @Override
    public List<T> findAll() {
        return IterableConverter.toList(operations.findAll(entityInformation.getJavaType()));
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.repository.CrudRepository#findAll(java.lang.Iterable)
     */
    @Override
    public Iterable<T> findAll(Iterable<ID> ids) {

        List<T> result = new ArrayList<T>();

        for (ID id : ids) {

            T candidate = findOne(id);

            if (candidate != null) {
                result.add(candidate);
            }
        }

        return result;
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.repository.CrudRepository#count()
     */
    @Override
    public long count() {
        return operations.count(entityInformation.getJavaType());
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.repository.CrudRepository#delete(java.io.Serializable)
     */
    @Override
    public void delete(ID id) {
        operations.delete(id, entityInformation.getJavaType());
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.repository.CrudRepository#delete(java.lang.Object)
     */
    @Override
    public void delete(T entity) {
        delete(entityInformation.getId(entity));
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.repository.CrudRepository#delete(java.lang.Iterable)
     */
    @Override
    public void delete(Iterable<? extends T> entities) {

        for (T entity : entities) {
            delete(entity);
        }
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.repository.CrudRepository#deleteAll()
     */
    @Override
    public void deleteAll() {
        operations.delete(entityInformation.getJavaType());
    }
}

