package com.rocket.summer.framework.context.annotation;

import com.rocket.summer.framework.beans.factory.BeanDefinitionStoreException;
import com.rocket.summer.framework.beans.factory.annotation.AnnotatedBeanDefinition;
import com.rocket.summer.framework.beans.factory.config.BeanDefinition;
import com.rocket.summer.framework.context.ResourceLoaderAware;
import com.rocket.summer.framework.core.env.Environment;
import com.rocket.summer.framework.core.env.StandardEnvironment;
import com.rocket.summer.framework.core.io.Resource;
import com.rocket.summer.framework.core.io.ResourceLoader;
import com.rocket.summer.framework.core.io.support.PathMatchingResourcePatternResolver;
import com.rocket.summer.framework.core.io.support.ResourcePatternResolver;
import com.rocket.summer.framework.core.io.support.ResourcePatternUtils;
import com.rocket.summer.framework.core.type.classreading.CachingMetadataReaderFactory;
import com.rocket.summer.framework.core.type.classreading.MetadataReader;
import com.rocket.summer.framework.core.type.classreading.MetadataReaderFactory;
import com.rocket.summer.framework.core.type.filter.AnnotationTypeFilter;
import com.rocket.summer.framework.core.type.filter.TypeFilter;
import com.rocket.summer.framework.stereotype.Component;
import com.rocket.summer.framework.stereotype.Controller;
import com.rocket.summer.framework.stereotype.Repository;
import com.rocket.summer.framework.stereotype.Service;
import com.rocket.summer.framework.util.Assert;
import com.rocket.summer.framework.util.ClassUtils;
import com.rocket.summer.framework.util.SystemPropertyUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * A component provider that scans the classpath from a base package. It then
 * applies exclude and include filters to the resulting classes to find candidates.
 *
 * <p>This implementation is based on Spring's
 * {@link com.rocket.summer.framework.core.type.classreading.MetadataReader MetadataReader}
 * facility, backed by an ASM {@link org.objectweb.asm.ClassReader ClassReader}.
 *
 * @author Mark Fisher
 * @author Juergen Hoeller
 * @author Ramnivas Laddad
 * @since 2.5
 * @see com.rocket.summer.framework.core.type.classreading.MetadataReaderFactory
 * @see com.rocket.summer.framework.core.type.AnnotationMetadata
 * @see ScannedGenericBeanDefinition
 */
public class ClassPathScanningCandidateComponentProvider implements ResourceLoaderAware {

    protected static final String DEFAULT_RESOURCE_PATTERN = "**/*.class";


    protected final Log logger = LogFactory.getLog(getClass());

    private ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();

    private MetadataReaderFactory metadataReaderFactory = new CachingMetadataReaderFactory(this.resourcePatternResolver);

    private String resourcePattern = DEFAULT_RESOURCE_PATTERN;

    private Environment environment;

    private ConditionEvaluator conditionEvaluator;

    private final List<TypeFilter> includeFilters = new LinkedList<TypeFilter>();

    private final List<TypeFilter> excludeFilters = new LinkedList<TypeFilter>();


    /**
     * Protected constructor for flexible subclass initialization.
     * @since 4.3.6
     */
    protected ClassPathScanningCandidateComponentProvider() {
    }

    /**
     * Create a ClassPathScanningCandidateComponentProvider.
     * @param useDefaultFilters whether to register the default filters for the
     * {@link Component @Component}, {@link Repository @Repository},
     * {@link Service @Service}, and {@link Controller @Controller}
     * stereotype annotations
     * @see #registerDefaultFilters()
     */
    public ClassPathScanningCandidateComponentProvider(boolean useDefaultFilters) {
        if (useDefaultFilters) {
            registerDefaultFilters();
        }
    }

    /**
     * Set the Environment to use when resolving placeholders and evaluating
     * {@link Conditional @Conditional}-annotated component classes.
     * <p>The default is a {@link StandardEnvironment}.
     * @param environment the Environment to use
     */
    public void setEnvironment(Environment environment) {
        Assert.notNull(environment, "Environment must not be null");
        this.environment = environment;
        this.conditionEvaluator = null;
    }

    /**
     * Set the ResourceLoader to use for resource locations.
     * This will typically be a ResourcePatternResolver implementation.
     * <p>Default is PathMatchingResourcePatternResolver, also capable of
     * resource pattern resolving through the ResourcePatternResolver interface.
     * @see com.rocket.summer.framework.core.io.support.ResourcePatternResolver
     * @see com.rocket.summer.framework.core.io.support.PathMatchingResourcePatternResolver
     */
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourcePatternResolver = ResourcePatternUtils.getResourcePatternResolver(resourceLoader);
        this.metadataReaderFactory = new CachingMetadataReaderFactory(resourceLoader);
    }

    /**
     * Return the ResourceLoader that this component provider uses.
     */
    public final ResourceLoader getResourceLoader() {
        return this.resourcePatternResolver;
    }

    /**
     * Set the resource pattern to use when scanning the classpath.
     * This value will be appended to each base package name.
     * @see #findCandidateComponents(String)
     * @see #DEFAULT_RESOURCE_PATTERN
     */
    public void setResourcePattern(String resourcePattern) {
        Assert.notNull(resourcePattern, "'resourcePattern' must not be null");
        this.resourcePattern = resourcePattern;
    }

    /**
     * Add an include type filter to the <i>end</i> of the inclusion list.
     */
    public void addIncludeFilter(TypeFilter includeFilter) {
        this.includeFilters.add(includeFilter);
    }

    /**
     * Add an exclude type filter to the <i>front</i> of the exclusion list.
     */
    public void addExcludeFilter(TypeFilter excludeFilter) {
        this.excludeFilters.add(0, excludeFilter);
    }

    /**
     * Reset the configured type filters.
     * @param useDefaultFilters whether to re-register the default filters for
     * the {@link Component @Component}, {@link Repository @Repository},
     * {@link Service @Service}, and {@link Controller @Controller}
     * stereotype annotations
     * @see #registerDefaultFilters()
     */
    public void resetFilters(boolean useDefaultFilters) {
        this.includeFilters.clear();
        this.excludeFilters.clear();
        if (useDefaultFilters) {
            registerDefaultFilters();
        }
    }

    /**
     * Register the default filter for {@link Component @Component}.
     * This will implicitly register all annotations that have the
     * {@link Component @Component} meta-annotation including the
     * {@link Repository @Repository}, {@link Service @Service}, and
     * {@link Controller @Controller} stereotype annotations.
     */
    protected void registerDefaultFilters() {
        this.includeFilters.add(new AnnotationTypeFilter(Component.class));
    }


    /**
     * Scan the class path for candidate components.
     * @param basePackage the package to check for annotated classes
     * @return a corresponding Set of autodetected bean definitions
     */
    public Set<BeanDefinition> findCandidateComponents(String basePackage) {
        Set<BeanDefinition> candidates = new LinkedHashSet<BeanDefinition>();
        try {
            String packageSearchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX +
                    resolveBasePackage(basePackage) + "/" + this.resourcePattern;
            Resource[] resources = this.resourcePatternResolver.getResources(packageSearchPath);
            boolean traceEnabled = logger.isTraceEnabled();
            boolean debugEnabled = logger.isDebugEnabled();
            for (int i = 0; i < resources.length; i++) {
                Resource resource = resources[i];
                if (traceEnabled) {
                    logger.trace("Scanning " + resource);
                }
                if (resource.isReadable()) {
                    MetadataReader metadataReader = this.metadataReaderFactory.getMetadataReader(resource);
                    if (isCandidateComponent(metadataReader)) {
                        ScannedGenericBeanDefinition sbd = new ScannedGenericBeanDefinition(metadataReader);
                        sbd.setResource(resource);
                        sbd.setSource(resource);
                        if (isCandidateComponent(sbd)) {
                            if (debugEnabled) {
                                logger.debug("Identified candidate component class: " + resource);
                            }
                            candidates.add(sbd);
                        }
                        else {
                            if (debugEnabled) {
                                logger.debug("Ignored because not a concrete top-level class: " + resource);
                            }
                        }
                    }
                    else {
                        if (traceEnabled) {
                            logger.trace("Ignored because not matching any filter: " + resource);
                        }
                    }
                }
                else {
                    if (traceEnabled) {
                        logger.trace("Ignored because not readable: " + resource);
                    }
                }
            }
        }
        catch (IOException ex) {
            throw new BeanDefinitionStoreException("I/O failure during classpath scanning", ex);
        }
        return candidates;
    }

    /**
     * Resolve the specified base package into a pattern specification for
     * the package search path.
     * <p>The default implementation resolves placeholders against system properties,
     * and converts a "."-based package path to a "/"-based resource path.
     * @param basePackage the base package as specified by the user
     * @return the pattern specification to be used for package searching
     */
    protected String resolveBasePackage(String basePackage) {
        return ClassUtils.convertClassNameToResourcePath(SystemPropertyUtils.resolvePlaceholders(basePackage));
    }

    /**
     * Determine whether the given class does not match any exclude filter
     * and does match at least one include filter.
     * @param metadataReader the ASM ClassReader for the class
     * @return whether the class qualifies as a candidate component
     */
    protected boolean isCandidateComponent(MetadataReader metadataReader) throws IOException {
        for (TypeFilter tf : this.excludeFilters) {
            if (tf.match(metadataReader, this.metadataReaderFactory)) {
                return false;
            }
        }
        for (TypeFilter tf : this.includeFilters) {
            if (tf.match(metadataReader, this.metadataReaderFactory)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Determine whether the given bean definition qualifies as candidate.
     * <p>The default implementation checks whether the class is concrete
     * (i.e. not abstract and not an interface). Can be overridden in subclasses.
     * @param beanDefinition the bean definition to check
     * @return whether the bean definition qualifies as a candidate component
     */
    protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
        return (beanDefinition.getMetadata().isConcrete() && beanDefinition.getMetadata().isIndependent());
    }

    /**
     * Clear the underlying metadata cache, removing all cached class metadata.
     */
    public void clearCache() {
        if (this.metadataReaderFactory instanceof CachingMetadataReaderFactory) {
            ((CachingMetadataReaderFactory) this.metadataReaderFactory).clearCache();
        }
    }

}

