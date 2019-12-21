package com.rocket.summer.framework.context.annotation;

import java.lang.annotation.*;

/**
 * Annotation providing a convenient and declarative mechanism for adding a
 * {@link com.rocket.summer.framework.core.env.PropertySource PropertySource} to Spring's
 * {@link com.rocket.summer.framework.core.env.Environment Environment}. To be used in
 * conjunction with @{@link Configuration} classes.
 *
 * <h3>Example usage</h3>
 * <p>Given a file {@code app.properties} containing the key/value pair
 * {@code testbean.name=myTestBean}, the following {@code @Configuration} class
 * uses {@code @PropertySource} to contribute {@code app.properties} to the
 * {@code Environment}'s set of {@code PropertySources}.
 *
 * <pre class="code">
 * &#064;Configuration
 * &#064;PropertySource("classpath:/com/myco/app.properties")
 * public class AppConfig {
 *     &#064;Autowired
 *     Environment env;
 *
 *     &#064;Bean
 *     public TestBean testBean() {
 *         TestBean testBean = new TestBean();
 *         testBean.setName(env.getProperty("testbean.name"));
 *         return testBean;
 *     }
 * }</pre>
 *
 * Notice that the {@code Environment} object is @{@link
 * com.rocket.summer.framework.beans.factory.annotation.Autowired Autowired} into the
 * configuration class and then used when populating the {@code TestBean} object. Given
 * the configuration above, a call to {@code testBean.getName()} will return "myTestBean".
 *
 * <h3>Resolving ${...} placeholders in {@code <bean>} and {@code @Value} annotations</h3>
 * In order to resolve ${...} placeholders in {@code <bean>} definitions or {@code @Value}
 * annotations using properties from a {@code PropertySource}, one must register
 * a {@code PropertySourcesPlaceholderConfigurer}. This happens automatically when using
 * {@code <context:property-placeholder>} in XML, but must be explicitly registered using
 * a {@code static} {@code @Bean} method when using {@code @Configuration} classes. See
 * the "Working with externalized values" section of @{@link Configuration} Javadoc and
 * "a note on BeanFactoryPostProcessor-returning @Bean methods" of @{@link Bean} Javadoc
 * for details and examples.
 *
 * <h3>Resolving ${...} placeholders within {@code @PropertySource} resource locations</h3>
 * Any ${...} placeholders present in a {@code @PropertySource} {@linkplain #value()
 * resource location} will be resolved against the set of property sources already
 * registered against the environment.  For example:
 * <pre class="code">
 * &#064;Configuration
 * &#064;PropertySource("classpath:/com/${my.placeholder:default/path}/app.properties")
 * public class AppConfig {
 *     &#064;Autowired
 *     Environment env;
 *
 *     &#064;Bean
 *     public TestBean testBean() {
 *         TestBean testBean = new TestBean();
 *         testBean.setName(env.getProperty("testbean.name"));
 *         return testBean;
 *     }
 * }</pre>
 *
 * Assuming that "my.placeholder" is present in one of the property sources already
 * registered, e.g. system properties or environment variables, the placeholder will
 * be resolved to the corresponding value. If not, then "default/path" will be used as a
 * default. Expressing a default value (delimited by colon ":") is optional.  If no
 * default is specified and a property cannot be resolved, an {@code
 * IllegalArgumentException} will be thrown.
 *
 * <h3>A note on property overriding with @PropertySource</h3>
 * In cases where a given property key exists in more than one {@code .properties}
 * file, the last {@code @PropertySource} annotation processed will 'win' and override.
 *
 * For example, given two properties files {@code a.properties} and
 * {@code b.properties}, consider the following two configuration classes
 * that reference them with {@code @PropertySource} annotations:
 *
 * <pre class="code">
 * &#064;Configuration
 * &#064;PropertySource("classpath:/com/myco/a.properties")
 * public class ConfigA { }
 *
 * &#064;Configuration
 * &#064;PropertySource("classpath:/com/myco/b.properties")
 * public class ConfigB { }
 * </pre>
 *
 * The override ordering depends on the order in which these classes are registered
 * with the application context.
 * <pre class="code">
 * AnnotationConfigApplicationContext ctx =
 *     new AnnotationConfigApplicationContext();
 * ctx.register(ConfigA.class);
 * ctx.register(ConfigB.class);
 * ctx.refresh();
 * </pre>
 *
 * In the scenario above, the properties in {@code b.properties} will override any
 * duplicates that exist in {@code a.properties}, because {@code ConfigB} was registered
 * last.
 *
 * <p>In certain situations, it may not be possible or practical to tightly control
 * property source ordering when using {@code @ProperySource} annotations. For example,
 * if the {@code @Configuration} classes above were registered via component-scanning,
 * the ordering is difficult to predict.  In such cases - and if overriding is important -
 * it is recommended that the user fall back to using the programmatic PropertySource API.
 * See {@link com.rocket.summer.framework.core.env.ConfigurableEnvironment ConfigurableEnvironment}
 * and {@link com.rocket.summer.framework.core.env.MutablePropertySources MutablePropertySources}
 * Javadoc for details.
 *
 * @author Chris Beams
 * @since 3.1
 * @see Configuration
 * @see com.rocket.summer.framework.core.env.PropertySource
 * @see com.rocket.summer.framework.core.env.ConfigurableEnvironment#getPropertySources()
 * @see com.rocket.summer.framework.core.env.MutablePropertySources
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PropertySource {

    /**
     * Indicate the name of this property source. If omitted, a name
     * will be generated based on the description of the underlying
     * resource.
     * @see com.rocket.summer.framework.core.env.PropertySource#getName()
     * @see com.rocket.summer.framework.core.io.Resource#getDescription()
     */
    String name() default "";

    /**
     * Indicate the resource location(s) of the properties file to be loaded.
     * For example, {@code "classpath:/com/myco/app.properties"} or
     * {@code "file:/path/to/file"}.
     * <p>Resource location wildcards (e.g. *&#42;/*.properties) are not permitted; each
     * location must evaluate to exactly one {@code .properties} resource.
     * <p>${...} placeholders will be resolved against any/all property sources already
     * registered with the {@code Environment}. See {@linkplain PropertySource above} for
     * examples.
     * <p>Each location will be added to the enclosing {@code Environment} as its own
     * property source, and in the order declared.
     */
    String[] value();

}
