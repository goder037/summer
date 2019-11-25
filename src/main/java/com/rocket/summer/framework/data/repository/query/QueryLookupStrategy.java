package com.rocket.summer.framework.data.repository.query;

import java.lang.reflect.Method;
import java.util.Locale;

import com.rocket.summer.framework.data.projection.ProjectionFactory;
import com.rocket.summer.framework.data.repository.core.NamedQueries;
import com.rocket.summer.framework.data.repository.core.RepositoryMetadata;
import com.rocket.summer.framework.util.StringUtils;

/**
 * Strategy interface for which way to lookup {@link RepositoryQuery}s.
 *
 * @author Oliver Gierke
 */
public interface QueryLookupStrategy {

    public static enum Key {

        CREATE, USE_DECLARED_QUERY, CREATE_IF_NOT_FOUND;

        /**
         * Returns a strategy key from the given XML value.
         *
         * @param xml
         * @return a strategy key from the given XML value
         */
        public static Key create(String xml) {

            if (!StringUtils.hasText(xml)) {
                return null;
            }

            return valueOf(xml.toUpperCase(Locale.US).replace("-", "_"));
        }
    }

    /**
     * Resolves a {@link RepositoryQuery} from the given {@link QueryMethod} that can be executed afterwards.
     *
     * @param method will never be {@literal null}.
     * @param metadata will never be {@literal null}.
     * @param factory will never be {@literal null}.
     * @param namedQueries will never be {@literal null}.
     * @return
     */
    RepositoryQuery resolveQuery(Method method, RepositoryMetadata metadata, ProjectionFactory factory,
                                 NamedQueries namedQueries);
}

