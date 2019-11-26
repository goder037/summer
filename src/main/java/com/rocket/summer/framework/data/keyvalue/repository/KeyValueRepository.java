package com.rocket.summer.framework.data.keyvalue.repository;

import java.io.Serializable;

import com.rocket.summer.framework.data.repository.PagingAndSortingRepository;

/**
 * @author Christoph Strobl
 * @param <T>
 * @param <ID>
 */
public interface KeyValueRepository<T, ID extends Serializable> extends PagingAndSortingRepository<T, ID> {

}

