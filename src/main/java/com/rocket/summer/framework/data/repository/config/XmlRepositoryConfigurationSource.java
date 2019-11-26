package com.rocket.summer.framework.data.repository.config;

import java.util.Arrays;
import java.util.Collection;

import com.rocket.summer.framework.beans.factory.xml.ParserContext;
import com.rocket.summer.framework.core.env.Environment;
import com.rocket.summer.framework.core.type.filter.TypeFilter;
import com.rocket.summer.framework.data.config.TypeFilterParser;
import com.rocket.summer.framework.data.config.TypeFilterParser.Type;
import com.rocket.summer.framework.data.repository.query.QueryLookupStrategy;
import com.rocket.summer.framework.data.util.ParsingUtils;
import com.rocket.summer.framework.util.Assert;
import com.rocket.summer.framework.util.StringUtils;
import org.w3c.dom.Element;

/**
 * XML based {@link RepositoryConfigurationSource}. Uses configuration defined on {@link Element} attributes.
 *
 * @author Oliver Gierke
 * @author Thomas Darimont
 * @author Peter Rietzler
 */
public class XmlRepositoryConfigurationSource extends RepositoryConfigurationSourceSupport {

    private static final String QUERY_LOOKUP_STRATEGY = "query-lookup-strategy";
    private static final String BASE_PACKAGE = "base-package";
    private static final String NAMED_QUERIES_LOCATION = "named-queries-location";
    private static final String REPOSITORY_IMPL_POSTFIX = "repository-impl-postfix";
    private static final String REPOSITORY_FACTORY_BEAN_CLASS_NAME = "factory-class";
    private static final String REPOSITORY_BASE_CLASS_NAME = "base-class";
    private static final String CONSIDER_NESTED_REPOSITORIES = "consider-nested-repositories";

    private final Element element;
    private final ParserContext context;

    private final Collection<TypeFilter> includeFilters;
    private final Collection<TypeFilter> excludeFilters;

    /**
     * Creates a new {@link XmlRepositoryConfigurationSource} using the given {@link Element} and {@link ParserContext}.
     *
     * @param element must not be {@literal null}.
     * @param context must not be {@literal null}.
     * @param environment must not be {@literal null}.
     */
    public XmlRepositoryConfigurationSource(Element element, ParserContext context, Environment environment) {

        super(environment, context.getRegistry());

        Assert.notNull(element, "Element must not be null!");

        this.element = element;
        this.context = context;

        TypeFilterParser parser = new TypeFilterParser(context.getReaderContext());
        this.includeFilters = parser.parseTypeFilters(element, Type.INCLUDE);
        this.excludeFilters = parser.parseTypeFilters(element, Type.EXCLUDE);
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.repository.config.RepositoryConfigurationSource#getSource()
     */
    public Object getSource() {
        return context.extractSource(element);
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.repository.config.RepositoryConfigurationSource#getBasePackages()
     */
    public Iterable<String> getBasePackages() {

        String attribute = element.getAttribute(BASE_PACKAGE);
        return Arrays.asList(StringUtils.delimitedListToStringArray(attribute, ",", " "));
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.repository.config.RepositoryConfigurationSource#getQueryLookupStrategyKey()
     */
    public Object getQueryLookupStrategyKey() {
        return QueryLookupStrategy.Key.create(getNullDefaultedAttribute(element, QUERY_LOOKUP_STRATEGY));
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.repository.config.RepositoryConfigurationSource#getNamedQueryLocation()
     */
    public String getNamedQueryLocation() {
        return getNullDefaultedAttribute(element, NAMED_QUERIES_LOCATION);
    }

    /**
     * Returns the XML element backing the configuration.
     *
     * @return the element
     */
    public Element getElement() {
        return element;
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.repository.config.RepositoryConfigurationSourceSupport#getExcludeFilters()
     */
    @Override
    public Iterable<TypeFilter> getExcludeFilters() {
        return excludeFilters;
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.repository.config.RepositoryConfigurationSourceSupport#getIncludeFilters()
     */
    @Override
    protected Iterable<TypeFilter> getIncludeFilters() {
        return includeFilters;
    }

    /* (non-Javadoc)
     * @see com.rocket.summer.framework.data.repository.config.RepositoryConfigurationSource#getRepositoryImplementationPostfix()
     */
    public String getRepositoryImplementationPostfix() {
        return getNullDefaultedAttribute(element, REPOSITORY_IMPL_POSTFIX);
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.repository.config.RepositoryConfigurationSource#getRepositoryFactoryBeanName()
     */
    public String getRepositoryFactoryBeanName() {
        return getNullDefaultedAttribute(element, REPOSITORY_FACTORY_BEAN_CLASS_NAME);
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.repository.config.RepositoryConfigurationSource#getRepositoryBaseClassName()
     */
    @Override
    public String getRepositoryBaseClassName() {
        return getNullDefaultedAttribute(element, REPOSITORY_BASE_CLASS_NAME);
    }

    private String getNullDefaultedAttribute(Element element, String attributeName) {
        String attribute = element.getAttribute(attributeName);
        return StringUtils.hasText(attribute) ? attribute : null;
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.repository.config.RepositoryConfigurationSourceSupport#isConsideringNestedRepositoriesEnabled()
     */
    @Override
    public boolean shouldConsiderNestedRepositories() {

        String attribute = getNullDefaultedAttribute(element, CONSIDER_NESTED_REPOSITORIES);
        return attribute != null && Boolean.parseBoolean(attribute);
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.repository.config.RepositoryConfigurationSource#getAttribute(java.lang.String)
     */
    @Override
    public String getAttribute(String name) {

        String xmlAttributeName = ParsingUtils.reconcatenateCamelCase(name, "-");
        String attribute = element.getAttribute(xmlAttributeName);

        return StringUtils.hasText(attribute) ? attribute : null;
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.repository.config.RepositoryConfigurationSource#usesExplicitFilters()
     */
    @Override
    public boolean usesExplicitFilters() {
        return !(this.includeFilters.isEmpty() && this.excludeFilters.isEmpty());
    }
}
