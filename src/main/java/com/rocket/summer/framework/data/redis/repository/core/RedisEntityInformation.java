package com.rocket.summer.framework.data.redis.repository.core;

import java.io.Serializable;

import com.rocket.summer.framework.data.repository.core.EntityInformation;

/**
 * @author Christoph Strobl
 * @param <T>
 * @param <ID>
 */
public interface RedisEntityInformation<T, ID extends Serializable> extends EntityInformation<T, ID> {

}
