package com.rocket.summer.framework.data.querydsl;

import com.querydsl.core.types.EntityPath;

/**
 * Strategy interface to abstract the ways to translate an plain domain class into a {@link EntityPath}.
 *
 * @author Oliver Gierke
 */
public interface EntityPathResolver {

    <T> EntityPath<T> createPath(Class<T> domainClass);
}

