package com.rocket.summer.framework.beans.factory.config;

import com.rocket.summer.framework.beans.factory.BeanDefinitionStoreException;
import com.rocket.summer.framework.beans.factory.BeanFactory;
import com.rocket.summer.framework.beans.factory.BeanFactoryAware;
import com.rocket.summer.framework.beans.factory.BeanNameAware;
import com.rocket.summer.framework.util.StringValueResolver;

/**
 * Abstract base class for property resource configurers that resolve placeholders
 * in bean definition property values. Implementations <em>pull</em> values from a
 * properties file or other {@linkplain com.rocket.summer.framework.core.env.PropertySource
 * property source} into bean definitions.
 *
 * <p>The default placeholder syntax follows the Ant / Log4J / JSP EL style:
 *
 * <pre class="code">${...}</pre>
 *
 * Example XML bean definition:
 *
 * <pre class="code">
 * <bean id="dataSource" class="com.rocket.summer.framework.jdbc.datasource.DriverManagerDataSource"/>
 *   <property name="driverClassName" value="${driver}"/>
 *   <property name="url" value="jdbc:${dbname}"/>
 * </bean>
 * </pre>
 *
 * Example properties file:
 *
 * <pre class="code">driver=com.mysql.jdbc.Driver
 * dbname=mysql:mydb</pre>
 *
 * Annotated bean definitions may take advantage of property replacement using
 * the {@link com.rocket.summer.framework.beans.factory.annotation.Value @Value} annotation:
 *
 * <pre class="code">@Value("${person.age}")</pre>
 *
 * Implementations check simple property values, lists, maps, props, and bean names
 * in bean references. Furthermore, placeholder values can also cross-reference
 * other placeholders, like:
 *
 * <pre class="code">rootPath=myrootdir
 * subPath=${rootPath}/subdir</pre>
 *
 * In contrast to {@link PropertyOverrideConfigurer}, subclasses of this type allow
 * filling in of explicit placeholders in bean definitions.
 *
 * <p>If a configurer cannot resolve a placeholder, a {@link BeanDefinitionStoreException}
 * will be thrown. If you want to check against multiple properties files, specify multiple
 * resources via the {@link #setLocations locations} property. You can also define multiple
 * configurers, each with its <em>own</em> placeholder syntax. Use {@link
 * #ignoreUnresolvablePlaceholders} to intentionally suppress throwing an exception if a
 * placeholder cannot be resolved.
 *
 * <p>Default property values can be defined globally for each configurer instance
 * via the {@link #setProperties properties} property, or on a property-by-property basis
 * using the default value separator which is {@code ":"} by default and
 * customizable via {@link #setValueSeparator(String)}.
 *
 * <p>Example XML property with default value:
 *
 * <pre class="code">
 *   <property name="url" value="jdbc:${dbname:defaultdb}"/>
 * </pre>
 *
 * @author Chris Beams
 * @author Juergen Hoeller
 * @since 3.1
 * @see PropertyPlaceholderConfigurer
 * @see com.rocket.summer.framework.context.support.PropertySourcesPlaceholderConfigurer
 */
public abstract class PlaceholderConfigurerSupport extends PropertyResourceConfigurer
        implements BeanNameAware, BeanFactoryAware {

    /** Default placeholder prefix: {@value} */
    public static final String DEFAULT_PLACEHOLDER_PREFIX = "${";

    /** Default placeholder suffix: {@value} */
    public static final String DEFAULT_PLACEHOLDER_SUFFIX = "}";

    /** Default value separator: {@value} */
    public static final String DEFAULT_VALUE_SEPARATOR = ":";


    /** Defaults to {@value #DEFAULT_PLACEHOLDER_PREFIX} */
    protected String placeholderPrefix = DEFAULT_PLACEHOLDER_PREFIX;

    /** Defaults to {@value #DEFAULT_PLACEHOLDER_SUFFIX} */
    protected String placeholderSuffix = DEFAULT_PLACEHOLDER_SUFFIX;

    /** Defaults to {@value #DEFAULT_VALUE_SEPARATOR} */
    protected String valueSeparator = DEFAULT_VALUE_SEPARATOR;

    protected boolean trimValues = false;

    protected String nullValue;

    protected boolean ignoreUnresolvablePlaceholders = false;

    private String beanName;

    private BeanFactory beanFactory;


    /**
     * Set the prefix that a placeholder string starts with.
     * The default is {@value #DEFAULT_PLACEHOLDER_PREFIX}.
     */
    public void setPlaceholderPrefix(String placeholderPrefix) {
        this.placeholderPrefix = placeholderPrefix;
    }

    /**
     * Set the suffix that a placeholder string ends with.
     * The default is {@value #DEFAULT_PLACEHOLDER_SUFFIX}.
     */
    public void setPlaceholderSuffix(String placeholderSuffix) {
        this.placeholderSuffix = placeholderSuffix;
    }

    /**
     * Specify the separating character between the placeholder variable
     * and the associated default value, or {@code null} if no such
     * special character should be processed as a value separator.
     * The default is {@value #DEFAULT_VALUE_SEPARATOR}.
     */
    public void setValueSeparator(String valueSeparator) {
        this.valueSeparator = valueSeparator;
    }

    /**
     * Specify whether to trim resolved values before applying them,
     * removing superfluous whitespace from the beginning and end.
     * <p>Default is {@code false}.
     * @since 4.3
     */
    public void setTrimValues(boolean trimValues) {
        this.trimValues = trimValues;
    }

    /**
     * Set a value that should be treated as {@code null} when resolved
     * as a placeholder value: e.g. "" (empty String) or "null".
     * <p>Note that this will only apply to full property values,
     * not to parts of concatenated values.
     * <p>By default, no such null value is defined. This means that
     * there is no way to express {@code null} as a property value
     * unless you explicitly map a corresponding value here.
     */
    public void setNullValue(String nullValue) {
        this.nullValue = nullValue;
    }

    /**
     * Set whether to ignore unresolvable placeholders.
     * <p>Default is "false": An exception will be thrown if a placeholder fails
     * to resolve. Switch this flag to "true" in order to preserve the placeholder
     * String as-is in such a case, leaving it up to other placeholder configurers
     * to resolve it.
     */
    public void setIgnoreUnresolvablePlaceholders(boolean ignoreUnresolvablePlaceholders) {
        this.ignoreUnresolvablePlaceholders = ignoreUnresolvablePlaceholders;
    }

    /**
     * Only necessary to check that we're not parsing our own bean definition,
     * to avoid failing on unresolvable placeholders in properties file locations.
     * The latter case can happen with placeholders for system properties in
     * resource locations.
     * @see #setLocations
     * @see com.rocket.summer.framework.core.io.ResourceEditor
     */
    @Override
    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    /**
     * Only necessary to check that we're not parsing our own bean definition,
     * to avoid failing on unresolvable placeholders in properties file locations.
     * The latter case can happen with placeholders for system properties in
     * resource locations.
     * @see #setLocations
     * @see com.rocket.summer.framework.core.io.ResourceEditor
     */
    @Override
    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }


    protected void doProcessProperties(ConfigurableListableBeanFactory beanFactoryToProcess,
                                       StringValueResolver valueResolver) {

        BeanDefinitionVisitor visitor = new BeanDefinitionVisitor(valueResolver);

        String[] beanNames = beanFactoryToProcess.getBeanDefinitionNames();
        for (String curName : beanNames) {
            // Check that we're not parsing our own bean definition,
            // to avoid failing on unresolvable placeholders in properties file locations.
            if (!(curName.equals(this.beanName) && beanFactoryToProcess.equals(this.beanFactory))) {
                BeanDefinition bd = beanFactoryToProcess.getBeanDefinition(curName);
                try {
                    visitor.visitBeanDefinition(bd);
                }
                catch (Exception ex) {
                    throw new BeanDefinitionStoreException(bd.getResourceDescription(), curName, ex.getMessage(), ex);
                }
            }
        }

        // New in Spring 2.5: resolve placeholders in alias target names and aliases as well.
        beanFactoryToProcess.resolveAliases(valueResolver);

        // New in Spring 3.0: resolve placeholders in embedded values such as annotation attributes.
        beanFactoryToProcess.addEmbeddedValueResolver(valueResolver);
    }

}

