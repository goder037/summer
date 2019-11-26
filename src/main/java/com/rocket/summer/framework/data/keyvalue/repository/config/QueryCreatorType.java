package com.rocket.summer.framework.data.keyvalue.repository.config;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.rocket.summer.framework.data.keyvalue.repository.query.KeyValuePartTreeQuery;
import com.rocket.summer.framework.data.repository.query.RepositoryQuery;
import com.rocket.summer.framework.data.repository.query.parser.AbstractQueryCreator;

/**
 * Annotation to customize the query creator type to be used for a specific store.
 *
 * @author Oliver Gierke
 * @author Christoph Strobl
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface QueryCreatorType {

    Class<? extends AbstractQueryCreator<?, ?>> value();

    /**
     * The {@link RepositoryQuery} type to be created by the {@link QueryCreatorType#value()}.
     *
     * @return
     * @since 1.1
     */
    Class<? extends RepositoryQuery> repositoryQueryType() default KeyValuePartTreeQuery.class;
}
