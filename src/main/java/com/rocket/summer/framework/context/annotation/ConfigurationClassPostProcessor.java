package com.rocket.summer.framework.context.annotation;

import com.rocket.summer.framework.aop.framework.autoproxy.AutoProxyUtils;
import com.rocket.summer.framework.beans.factory.BeanClassLoaderAware;
import com.rocket.summer.framework.beans.factory.BeanDefinitionStoreException;
import com.rocket.summer.framework.beans.factory.config.BeanDefinition;
import com.rocket.summer.framework.beans.factory.config.BeanDefinitionHolder;
import com.rocket.summer.framework.beans.factory.config.ConfigurableListableBeanFactory;
import com.rocket.summer.framework.beans.factory.config.SingletonBeanRegistry;
import com.rocket.summer.framework.beans.factory.parsing.FailFastProblemReporter;
import com.rocket.summer.framework.beans.factory.parsing.PassThroughSourceExtractor;
import com.rocket.summer.framework.beans.factory.parsing.ProblemReporter;
import com.rocket.summer.framework.beans.factory.parsing.SourceExtractor;
import com.rocket.summer.framework.beans.factory.support.AbstractBeanDefinition;
import com.rocket.summer.framework.beans.factory.support.BeanDefinitionRegistry;
import com.rocket.summer.framework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import com.rocket.summer.framework.beans.factory.support.BeanNameGenerator;
import com.rocket.summer.framework.core.Ordered;
import com.rocket.summer.framework.core.env.Environment;
import com.rocket.summer.framework.core.io.DefaultResourceLoader;
import com.rocket.summer.framework.core.io.ResourceLoader;
import com.rocket.summer.framework.core.type.classreading.CachingMetadataReaderFactory;
import com.rocket.summer.framework.core.type.classreading.MetadataReaderFactory;
import com.rocket.summer.framework.util.Assert;
import com.rocket.summer.framework.util.ClassUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.util.*;

import static com.rocket.summer.framework.context.annotation.AnnotationConfigUtils.CONFIGURATION_BEAN_NAME_GENERATOR;

/**
 * {@link BeanFactoryPostProcessor} used for bootstrapping processing of
 * {@link Configuration @Configuration} classes.
 *
 * <p>Registered by default when using {@literal <context:annotation-config/>} or
 * {@literal <context:component-scan/>}. Otherwise, may be declared manually as
 * with any other BeanFactoryPostProcessor.
 *
 * <p>This post processor is {@link Ordered#HIGHEST_PRECEDENCE} as it is important
 * that any {@link Bean} methods declared in Configuration classes have their
 * respective bean definitions registered before any other BeanFactoryPostProcessor
 * executes.
 *
 * @author Chris Beams
 * @author Juergen Hoeller
 * @since 3.0
 */
public class ConfigurationClassPostProcessor implements BeanDefinitionRegistryPostProcessor, BeanClassLoaderAware {

    /** Whether the CGLIB2 library is present on the classpath */
    private static final boolean cglibAvailable = ClassUtils.isPresent(
            "net.sf.cglib.proxy.Enhancer", ConfigurationClassPostProcessor.class.getClassLoader());

    private static final String IMPORT_REGISTRY_BEAN_NAME =
            ConfigurationClassPostProcessor.class.getName() + ".importRegistry";

    private final Log logger = LogFactory.getLog(getClass());

    private SourceExtractor sourceExtractor = new PassThroughSourceExtractor();

    private ProblemReporter problemReporter = new FailFastProblemReporter();

    private ClassLoader beanClassLoader = ClassUtils.getDefaultClassLoader();

    private MetadataReaderFactory metadataReaderFactory = new CachingMetadataReaderFactory();

    private boolean setMetadataReaderFactoryCalled = false;

    private final Set<Integer> registriesPostProcessed = new HashSet<Integer>();

    private final Set<Integer> factoriesPostProcessed = new HashSet<>();

    private ConfigurationClassBeanDefinitionReader reader;

    private boolean localBeanNameGeneratorSet = false;

    private Environment environment;

    private ResourceLoader resourceLoader = new DefaultResourceLoader();

    /* Using short class names as default bean names */
    private BeanNameGenerator componentScanBeanNameGenerator = new AnnotationBeanNameGenerator();

    /* Using fully qualified class names as default bean names */
    private BeanNameGenerator importBeanNameGenerator = new AnnotationBeanNameGenerator() {
        @Override
        protected String buildDefaultBeanName(BeanDefinition definition) {
            return definition.getBeanClassName();
        }
    };

    /**
     * Set the {@link SourceExtractor} to use for generated bean definitions
     * that correspond to {@link Bean} factory methods.
     */
    public void setSourceExtractor(SourceExtractor sourceExtractor) {
        this.sourceExtractor = (sourceExtractor != null ? sourceExtractor : new PassThroughSourceExtractor());
    }

    /**
     * Set the {@link ProblemReporter} to use.
     * <p>Used to register any problems detected with {@link Configuration} or {@link Bean}
     * declarations. For instance, an @Bean method marked as {@literal final} is illegal
     * and would be reported as a problem. Defaults to {@link FailFastProblemReporter}.
     */
    public void setProblemReporter(ProblemReporter problemReporter) {
        this.problemReporter = (problemReporter != null ? problemReporter : new FailFastProblemReporter());
    }

    /**
     * Set the {@link MetadataReaderFactory} to use.
     * <p>Default is a {@link CachingMetadataReaderFactory} for the specified
     * {@link #setBeanClassLoader bean class loader}.
     */
    public void setMetadataReaderFactory(MetadataReaderFactory metadataReaderFactory) {
        Assert.notNull(metadataReaderFactory, "MetadataReaderFactory must not be null");
        this.metadataReaderFactory = metadataReaderFactory;
        this.setMetadataReaderFactoryCalled = true;
    }

    public void setBeanClassLoader(ClassLoader beanClassLoader) {
        this.beanClassLoader = beanClassLoader;
        if (!this.setMetadataReaderFactoryCalled) {
            this.metadataReaderFactory = new CachingMetadataReaderFactory(beanClassLoader);
        }
    }

    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }


    /**
     * Derive further bean definitions from the configuration classes in the registry.
     */
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) {
        int registryId = System.identityHashCode(registry);
        if (this.registriesPostProcessed.contains(registryId)) {
            throw new IllegalStateException(
                    "postProcessBeanDefinitionRegistry already called for this post-processor against " + registry);
        }
        if (this.factoriesPostProcessed.contains(registryId)) {
            throw new IllegalStateException(
                    "postProcessBeanFactory already called for this post-processor against " + registry);
        }
        this.registriesPostProcessed.add(registryId);
        processConfigBeanDefinitions(registry);
    }

    /**
     * Prepare the Configuration classes for servicing bean requests at runtime
     * by replacing them with CGLIB-enhanced subclasses.
     */
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) {
        int factoryId = System.identityHashCode(beanFactory);
        if (this.factoriesPostProcessed.contains(factoryId)) {
            throw new IllegalStateException(
                    "postProcessBeanFactory already called for this post-processor against " + beanFactory);
        }
        this.factoriesPostProcessed.add((factoryId));
        if (!this.registriesPostProcessed.contains((factoryId))) {
            // BeanDefinitionRegistryPostProcessor hook apparently not supported...
            // Simply call processConfigBeanDefinitions lazily at this point then.
            processConfigBeanDefinitions((BeanDefinitionRegistry) beanFactory);
        }
        enhanceConfigurationClasses(beanFactory);
    }


    /**
     * Build and validate a configuration model based on the registry of
     * {@link Configuration} classes.
     */
    public void processConfigBeanDefinitions(BeanDefinitionRegistry registry) {
        List<BeanDefinitionHolder> configCandidates = new ArrayList<BeanDefinitionHolder>();
        String[] candidateNames = registry.getBeanDefinitionNames();

        for (String beanName : candidateNames) {
            BeanDefinition beanDef = registry.getBeanDefinition(beanName);
            if (ConfigurationClassUtils.isFullConfigurationClass(beanDef) ||
                    ConfigurationClassUtils.isLiteConfigurationClass(beanDef)) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Bean definition has already been processed as a configuration class: " + beanDef);
                }
            }
            else if (ConfigurationClassUtils.checkConfigurationClassCandidate(beanDef, this.metadataReaderFactory)) {
                configCandidates.add(new BeanDefinitionHolder(beanDef, beanName));
            }
        }

        // Return immediately if no @Configuration classes were found
        if (configCandidates.isEmpty()) {
            return;
        }

        // Sort by previously determined @Order value, if applicable
        Collections.sort(configCandidates, new Comparator<BeanDefinitionHolder>() {
            @Override
            public int compare(BeanDefinitionHolder bd1, BeanDefinitionHolder bd2) {
                int i1 = ConfigurationClassUtils.getOrder(bd1.getBeanDefinition());
                int i2 = ConfigurationClassUtils.getOrder(bd2.getBeanDefinition());
                return (i1 < i2) ? -1 : (i1 > i2) ? 1 : 0;
            }
        });

        // Detect any custom bean name generation strategy supplied through the enclosing application context
        SingletonBeanRegistry sbr = null;
        if (registry instanceof SingletonBeanRegistry) {
            sbr = (SingletonBeanRegistry) registry;
            if (!this.localBeanNameGeneratorSet && sbr.containsSingleton(CONFIGURATION_BEAN_NAME_GENERATOR)) {
                BeanNameGenerator generator = (BeanNameGenerator) sbr.getSingleton(CONFIGURATION_BEAN_NAME_GENERATOR);
                this.componentScanBeanNameGenerator = generator;
                this.importBeanNameGenerator = generator;
            }
        }

        // Parse each @Configuration class
        ConfigurationClassParser parser = new ConfigurationClassParser(
                this.metadataReaderFactory, this.problemReporter, this.environment,
                this.resourceLoader, this.componentScanBeanNameGenerator, registry);

        Set<BeanDefinitionHolder> candidates = new LinkedHashSet<BeanDefinitionHolder>(configCandidates);
        Set<ConfigurationClass> alreadyParsed = new HashSet<ConfigurationClass>(configCandidates.size());
        do {
            parser.parse(candidates);
            parser.validate();

            Set<ConfigurationClass> configClasses = new LinkedHashSet<ConfigurationClass>(parser.getConfigurationClasses());
            configClasses.removeAll(alreadyParsed);

            // Read the model and create bean definitions based on its content
            if (this.reader == null) {
                this.reader = new ConfigurationClassBeanDefinitionReader(
                        registry, this.sourceExtractor, this.resourceLoader, this.environment,
                        this.importBeanNameGenerator, parser.getImportRegistry());
            }
            this.reader.loadBeanDefinitions(configClasses);
            alreadyParsed.addAll(configClasses);

            candidates.clear();
            if (registry.getBeanDefinitionCount() > candidateNames.length) {
                String[] newCandidateNames = registry.getBeanDefinitionNames();
                Set<String> oldCandidateNames = new HashSet<String>(Arrays.asList(candidateNames));
                Set<String> alreadyParsedClasses = new HashSet<String>();
                for (ConfigurationClass configurationClass : alreadyParsed) {
                    alreadyParsedClasses.add(configurationClass.getMetadata().getClassName());
                }
                for (String candidateName : newCandidateNames) {
                    if (!oldCandidateNames.contains(candidateName)) {
                        BeanDefinition bd = registry.getBeanDefinition(candidateName);
                        if (ConfigurationClassUtils.checkConfigurationClassCandidate(bd, this.metadataReaderFactory) &&
                                !alreadyParsedClasses.contains(bd.getBeanClassName())) {
                            candidates.add(new BeanDefinitionHolder(bd, candidateName));
                        }
                    }
                }
                candidateNames = newCandidateNames;
            }
        }
        while (!candidates.isEmpty());

        // Register the ImportRegistry as a bean in order to support ImportAware @Configuration classes
        if (sbr != null) {
            if (!sbr.containsSingleton(IMPORT_REGISTRY_BEAN_NAME)) {
                sbr.registerSingleton(IMPORT_REGISTRY_BEAN_NAME, parser.getImportRegistry());
            }
        }

        if (this.metadataReaderFactory instanceof CachingMetadataReaderFactory) {
            ((CachingMetadataReaderFactory) this.metadataReaderFactory).clearCache();
        }
    }

    /**
     * Post-processes a BeanFactory in search of Configuration class BeanDefinitions;
     * any candidates are then enhanced by a {@link ConfigurationClassEnhancer}.
     * Candidate status is determined by BeanDefinition attribute metadata.
     * @see ConfigurationClassEnhancer
     */
    public void enhanceConfigurationClasses(ConfigurableListableBeanFactory beanFactory) {
        Map<String, AbstractBeanDefinition> configBeanDefs = new LinkedHashMap<String, AbstractBeanDefinition>();
        for (String beanName : beanFactory.getBeanDefinitionNames()) {
            BeanDefinition beanDef = beanFactory.getBeanDefinition(beanName);
            if (ConfigurationClassUtils.isFullConfigurationClass(beanDef)) {
                if (!(beanDef instanceof AbstractBeanDefinition)) {
                    throw new BeanDefinitionStoreException("Cannot enhance @Configuration bean definition '" +
                            beanName + "' since it is not stored in an AbstractBeanDefinition subclass");
                }
                else if (logger.isWarnEnabled() && beanFactory.containsSingleton(beanName)) {
                    logger.warn("Cannot enhance @Configuration bean definition '" + beanName +
                            "' since its singleton instance has been created too early. The typical cause " +
                            "is a non-static @Bean method with a BeanDefinitionRegistryPostProcessor " +
                            "return type: Consider declaring such methods as 'static'.");
                }
                configBeanDefs.put(beanName, (AbstractBeanDefinition) beanDef);
            }
        }
        if (configBeanDefs.isEmpty()) {
            // nothing to enhance -> return immediately
            return;
        }

        ConfigurationClassEnhancer enhancer = new ConfigurationClassEnhancer();
        for (Map.Entry<String, AbstractBeanDefinition> entry : configBeanDefs.entrySet()) {
            AbstractBeanDefinition beanDef = entry.getValue();
            // If a @Configuration class gets proxied, always proxy the target class
            beanDef.setAttribute(AutoProxyUtils.PRESERVE_TARGET_CLASS_ATTRIBUTE, Boolean.TRUE);
            try {
                // Set enhanced subclass of the user-specified bean class
                Class<?> configClass = beanDef.resolveBeanClass(this.beanClassLoader);
                Class<?> enhancedClass = enhancer.enhance(configClass, this.beanClassLoader);
                if (configClass != enhancedClass) {
                    if (logger.isDebugEnabled()) {
                        logger.debug(String.format("Replacing bean definition '%s' existing class '%s' with " +
                                "enhanced class '%s'", entry.getKey(), configClass.getName(), enhancedClass.getName()));
                    }
                    beanDef.setBeanClass(enhancedClass);
                }
            }
            catch (Throwable ex) {
                throw new IllegalStateException("Cannot load configuration class: " + beanDef.getBeanClassName(), ex);
            }
        }
    }

}

