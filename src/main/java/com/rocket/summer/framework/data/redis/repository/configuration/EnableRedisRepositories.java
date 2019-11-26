package com.rocket.summer.framework.data.redis.repository.configuration;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.rocket.summer.framework.beans.factory.FactoryBean;
import com.rocket.summer.framework.context.annotation.ComponentScan.Filter;
import com.rocket.summer.framework.context.annotation.Import;
import com.rocket.summer.framework.data.keyvalue.core.KeyValueOperations;
import com.rocket.summer.framework.data.keyvalue.repository.config.QueryCreatorType;
import com.rocket.summer.framework.data.redis.core.RedisKeyValueAdapter.EnableKeyspaceEvents;
import com.rocket.summer.framework.data.redis.core.RedisOperations;
import com.rocket.summer.framework.data.redis.core.convert.KeyspaceConfiguration;
import com.rocket.summer.framework.data.redis.core.index.IndexConfiguration;
import com.rocket.summer.framework.data.redis.listener.KeyExpirationEventMessageListener;
import com.rocket.summer.framework.data.redis.repository.query.RedisQueryCreator;
import com.rocket.summer.framework.data.redis.repository.support.RedisRepositoryFactoryBean;
import com.rocket.summer.framework.data.repository.config.DefaultRepositoryBaseClass;
import com.rocket.summer.framework.data.repository.query.QueryLookupStrategy;
import com.rocket.summer.framework.data.repository.query.QueryLookupStrategy.Key;

/**
 * Annotation to activate Redis repositories. If no base package is configured through either {@link #value()},
 * {@link #basePackages()} or {@link #basePackageClasses()} it will trigger scanning of the package of annotated class.
 *
 * @author Christoph Strobl
 * @author Mark Paluch
 * @since 1.7
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import(RedisRepositoriesRegistrar.class)
@QueryCreatorType(RedisQueryCreator.class)
public @interface EnableRedisRepositories {

    /**
     * Alias for the {@link #basePackages()} attribute. Allows for more concise annotation declarations e.g.:
     * {@code @EnableRedisRepositories("org.my.pkg")} instead of
     * {@code @EnableRedisRepositories(basePackages="org.my.pkg")}.
     */
    String[] value() default {};

    /**
     * Base packages to scan for annotated components. {@link #value()} is an alias for (and mutually exclusive with) this
     * attribute. Use {@link #basePackageClasses()} for a type-safe alternative to String-based package names.
     */
    String[] basePackages() default {};

    /**
     * Type-safe alternative to {@link #basePackages()} for specifying the packages to scan for annotated components. The
     * package of each class specified will be scanned. Consider creating a special no-op marker class or interface in
     * each package that serves no purpose other than being referenced by this attribute.
     */
    Class<?>[] basePackageClasses() default {};

    /**
     * Specifies which types are not eligible for component scanning.
     */
    Filter[] excludeFilters() default {};

    /**
     * Specifies which types are eligible for component scanning. Further narrows the set of candidate components from
     * everything in {@link #basePackages()} to everything in the base packages that matches the given filter or filters.
     */
    Filter[] includeFilters() default {};

    /**
     * Returns the postfix to be used when looking up custom repository implementations. Defaults to {@literal Impl}. So
     * for a repository named {@code PersonRepository} the corresponding implementation class will be looked up scanning
     * for {@code PersonRepositoryImpl}.
     *
     * @return
     */
    String repositoryImplementationPostfix() default "Impl";

    /**
     * Configures the location of where to find the Spring Data named queries properties file.
     *
     * @return
     */
    String namedQueriesLocation() default "";

    /**
     * Returns the key of the {@link QueryLookupStrategy} to be used for lookup queries for query methods. Defaults to
     * {@link Key#CREATE_IF_NOT_FOUND}.
     *
     * @return
     */
    Key queryLookupStrategy() default Key.CREATE_IF_NOT_FOUND;

    /**
     * Returns the {@link FactoryBean} class to be used for each repository instance. Defaults to
     * {@link RedisRepositoryFactoryBean}.
     *
     * @return
     */
    Class<?> repositoryFactoryBeanClass() default RedisRepositoryFactoryBean.class;

    /**
     * Configure the repository base class to be used to create repository proxies for this particular configuration.
     *
     * @return
     */
    Class<?> repositoryBaseClass() default DefaultRepositoryBaseClass.class;

    /**
     * Configures the name of the {@link KeyValueOperations} bean to be used with the repositories detected.
     *
     * @return
     */
    String keyValueTemplateRef() default "redisKeyValueTemplate";

    /**
     * Configures whether nested repository-interfaces (e.g. defined as inner classes) should be discovered by the
     * repositories infrastructure.
     */
    boolean considerNestedRepositories() default false;

    /**
     * Configures the bean name of the {@link RedisOperations} to be used. Defaulted to {@literal redisTemplate}.
     *
     * @return
     */
    String redisTemplateRef() default "redisTemplate";

    /**
     * Set up index patterns using simple configuration class.
     *
     * @return
     */
    Class<? extends IndexConfiguration> indexConfiguration() default IndexConfiguration.class;

    /**
     * Set up keyspaces for specific types.
     *
     * @return
     */
    Class<? extends KeyspaceConfiguration> keyspaceConfiguration() default KeyspaceConfiguration.class;

    /**
     * Configure usage of {@link KeyExpirationEventMessageListener}.
     *
     * @return
     * @since 1.8
     */
    EnableKeyspaceEvents enableKeyspaceEvents() default EnableKeyspaceEvents.OFF;

    /**
     * Configure the {@literal notify-keyspace-events} property if not already set. <br />
     * Use an empty {@link String} to keep <b>not</b> alter existing server configuration.
     *
     * @return {@literal Ex} by default.
     * @since 1.8
     */
    String keyspaceNotificationsConfigParameter() default "Ex";

}

