package com.rocket.summer.framework.boot.domain;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.rocket.summer.framework.context.annotation.Import;
import com.rocket.summer.framework.core.annotation.AliasFor;

/**
 * Configures the base packages used by auto-configuration when scanning for entity
 * classes.
 * <p>
 * Using {@code @EntityScan} will cause auto-configuration to:
 * <ul>
 * <li>Set the
 * {@link com.rocket.summer.framework.orm.jpa.LocalContainerEntityManagerFactoryBean#setPackagesToScan(String...)
 * packages scanned} for JPA entities.</li>
 * <li>Set the packages used with Neo4J's {@link org.neo4j.ogm.session.SessionFactory
 * SessionFactory}.</li>
 * <li>Set the
 * {@link com.rocket.summer.framework.data.mapping.context.AbstractMappingContext#setInitialEntitySet(java.util.Set)
 * initial entity set} used with Spring Data
 * {@link com.rocket.summer.framework.data.mongodb.core.mapping.MongoMappingContext MongoDB},
 * {@link com.rocket.summer.framework.data.cassandra.mapping.CassandraMappingContext Cassandra}
 * and {@link com.rocket.summer.framework.data.couchbase.core.mapping.CouchbaseMappingContext
 * Couchbase} mapping contexts.</li>
 * </ul>
 * <p>
 * One of {@link #basePackageClasses()}, {@link #basePackages()} or its alias
 * {@link #value()} may be specified to define specific packages to scan. If specific
 * packages are not defined scanning will occur from the package of the class with this
 * annotation.
 *
 * @author Phillip Webb
 * @since 1.4.0
 * @see EntityScanPackages
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(EntityScanPackages.Registrar.class)
public @interface EntityScan {

    /**
     * Alias for the {@link #basePackages()} attribute. Allows for more concise annotation
     * declarations e.g.: {@code @EntityScan("org.my.pkg")} instead of
     * {@code @EntityScan(basePackages="org.my.pkg")}.
     * @return the base packages to scan
     */
    @AliasFor("basePackages")
    String[] value() default {};

    /**
     * Base packages to scan for entities. {@link #value()} is an alias for (and mutually
     * exclusive with) this attribute.
     * <p>
     * Use {@link #basePackageClasses()} for a type-safe alternative to String-based
     * package names.
     * @return the base packages to scan
     */
    @AliasFor("value")
    String[] basePackages() default {};

    /**
     * Type-safe alternative to {@link #basePackages()} for specifying the packages to
     * scan for entities. The package of each class specified will be scanned.
     * <p>
     * Consider creating a special no-op marker class or interface in each package that
     * serves no purpose other than being referenced by this attribute.
     * @return classes from the base packages to scan
     */
    Class<?>[] basePackageClasses() default {};

}

