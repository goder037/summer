package com.rocket.summer.framework.context.support;

import com.rocket.summer.framework.beans.factory.BeanDefinitionStoreException;
import com.rocket.summer.framework.beans.factory.NoSuchBeanDefinitionException;
import com.rocket.summer.framework.beans.factory.config.BeanDefinition;
import com.rocket.summer.framework.beans.factory.config.ConfigurableListableBeanFactory;
import com.rocket.summer.framework.beans.factory.support.BeanDefinitionRegistry;
import com.rocket.summer.framework.beans.factory.support.DefaultListableBeanFactory;
import com.rocket.summer.framework.context.ApplicationContext;
import com.rocket.summer.framework.core.ResolvableType;
import com.rocket.summer.framework.core.env.ConfigurableEnvironment;
import com.rocket.summer.framework.core.io.Resource;
import com.rocket.summer.framework.core.io.ResourceLoader;
import com.rocket.summer.framework.core.io.support.ResourcePatternResolver;
import com.rocket.summer.framework.util.Assert;

import java.io.IOException;

/**
 * Generic ApplicationContext implementation that holds a single internal
 * {@link com.rocket.summer.framework.beans.factory.support.DefaultListableBeanFactory}
 * instance and does not assume a specific bean definition format. Implements
 * the {@link com.rocket.summer.framework.beans.factory.support.BeanDefinitionRegistry}
 * interface in order to allow for applying any bean definition readers to it.
 *
 * <p>Typical usage is to register a variety of bean definitions via the
 * {@link com.rocket.summer.framework.beans.factory.support.BeanDefinitionRegistry}
 * interface and then call {@link #refresh()} to initialize those beans
 * with application context semantics (handling
 * {@link com.rocket.summer.framework.context.ApplicationContextAware}, auto-detecting
 * {@link com.rocket.summer.framework.beans.factory.config.BeanFactoryPostProcessor BeanFactoryPostProcessors},
 * etc).
 *
 * <p>In contrast to other ApplicationContext implementations that create a new
 * internal BeanFactory instance for each refresh, the internal BeanFactory of
 * this context is available right from the start, to be able to register bean
 * definitions on it. {@link #refresh()} may only be called once.
 *
 * <p>Usage example:
 *
 * <pre>
 * GenericApplicationContext ctx = new GenericApplicationContext();
 * XmlBeanDefinitionReader xmlReader = new XmlBeanDefinitionReader(ctx);
 * xmlReader.loadBeanDefinitions(new ClassPathResource("applicationContext.xml"));
 * PropertiesBeanDefinitionReader propReader = new PropertiesBeanDefinitionReader(ctx);
 * propReader.loadBeanDefinitions(new ClassPathResource("otherBeans.properties"));
 * ctx.refresh();
 *
 * MyBean myBean = (MyBean) ctx.getBean("myBean");
 * ...</pre>
 *
 * For the typical case of XML bean definitions, simply use
 * {@link ClassPathXmlApplicationContext} or {@link FileSystemXmlApplicationContext},
 * which are easier to set up - but less flexible, since you can just use standard
 * resource locations for XML bean definitions, rather than mixing arbitrary bean
 * definition formats. The equivalent in a web environment is
 * {@link com.rocket.summer.framework.web.context.support.XmlWebApplicationContext}.
 *
 * <p>For custom application context implementations that are supposed to read
 * special bean definition formats in a refreshable manner, consider deriving
 * from the {@link AbstractRefreshableApplicationContext} base class.
 *
 * @author Juergen Hoeller
 * @since 1.1.2
 * @see #registerBeanDefinition
 * @see #refresh()
 * @see com.rocket.summer.framework.beans.factory.support.PropertiesBeanDefinitionReader
 */
public class GenericApplicationContext extends AbstractApplicationContext implements BeanDefinitionRegistry {

    private final DefaultListableBeanFactory beanFactory;

    private ResourceLoader resourceLoader;

    private boolean refreshed = false;


    /**
     * Create a new GenericApplicationContext.
     * @see #registerBeanDefinition
     * @see #refresh
     */
    public GenericApplicationContext() {
        this.beanFactory = new DefaultListableBeanFactory();
    }

    /**
     * Create a new GenericApplicationContext with the given DefaultListableBeanFactory.
     * @param beanFactory the DefaultListableBeanFactory instance to use for this context
     * @see #registerBeanDefinition
     * @see #refresh
     */
    public GenericApplicationContext(DefaultListableBeanFactory beanFactory) {
        Assert.notNull(beanFactory, "BeanFactory must not be null");
        this.beanFactory = beanFactory;
    }

    /**
     * Create a new GenericApplicationContext with the given parent.
     * @param parent the parent application context
     * @see #registerBeanDefinition
     * @see #refresh
     */
    public GenericApplicationContext(ApplicationContext parent) {
        this();
        setParent(parent);
    }

    /**
     * Create a new GenericApplicationContext with the given DefaultListableBeanFactory.
     * @param beanFactory the DefaultListableBeanFactory instance to use for this context
     * @param parent the parent application context
     * @see #registerBeanDefinition
     * @see #refresh
     */
    public GenericApplicationContext(DefaultListableBeanFactory beanFactory, ApplicationContext parent) {
        this(beanFactory);
        setParent(parent);
    }

    /**
     * Set the parent of this application context, also setting
     * the parent of the internal BeanFactory accordingly.
     * @see com.rocket.summer.framework.beans.factory.config.ConfigurableBeanFactory#setParentBeanFactory
     */
    public void setParent(ApplicationContext parent) {
        super.setParent(parent);
        this.beanFactory.setParentBeanFactory(getInternalParentBeanFactory());
    }

    /**
     * Set a ResourceLoader to use for this context. If set, the context will
     * delegate all <code>getResource</code> calls to the given ResourceLoader.
     * If not set, default resource loading will apply.
     * <p>The main reason to specify a custom ResourceLoader is to resolve
     * resource paths (withour URL prefix) in a specific fashion.
     * The default behavior is to resolve such paths as class path locations.
     * To resolve resource paths as file system locations, specify a
     * FileSystemResourceLoader here.
     * <p>You can also pass in a full ResourcePatternResolver, which will
     * be autodetected by the context and used for <code>getResources</code>
     * calls as well. Else, default resource pattern matching will apply.
     * @see #getResource
     * @see com.rocket.summer.framework.core.io.DefaultResourceLoader
     * @see com.rocket.summer.framework.core.io.FileSystemResourceLoader
     * @see com.rocket.summer.framework.core.io.support.ResourcePatternResolver
     * @see #getResources
     */
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }


    /**
     * This implementation delegates to this context's ResourceLoader if set,
     * falling back to the default superclass behavior else.
     * @see #setResourceLoader
     */
    public Resource getResource(String location) {
        if (this.resourceLoader != null) {
            return this.resourceLoader.getResource(location);
        }
        return super.getResource(location);
    }

    /**
     * This implementation delegates to this context's ResourceLoader if it
     * implements the ResourcePatternResolver interface, falling back to the
     * default superclass behavior else.
     * @see #setResourceLoader
     */
    public Resource[] getResources(String locationPattern) throws IOException {
        if (this.resourceLoader instanceof ResourcePatternResolver) {
            return ((ResourcePatternResolver) this.resourceLoader).getResources(locationPattern);
        }
        return super.getResources(locationPattern);
    }


    //---------------------------------------------------------------------
    // Implementations of AbstractApplicationContext's template methods
    //---------------------------------------------------------------------

    /**
     * Do nothing: We hold a single internal BeanFactory and rely on callers
     * to register beans through our public methods (or the BeanFactory's).
     * @see #registerBeanDefinition
     */
    protected final void refreshBeanFactory() throws IllegalStateException {
        if (this.refreshed) {
            throw new IllegalStateException(
                    "GenericApplicationContext does not support multiple refresh attempts: just call 'refresh' once");
        }
        this.refreshed = true;
    }

    /**
     * Do nothing: We hold a single internal BeanFactory that will never
     * get released.
     */
    protected final void closeBeanFactory() {
    }

    /**
     * Return the single internal BeanFactory held by this context
     * (as ConfigurableListableBeanFactory).
     */
    public final ConfigurableListableBeanFactory getBeanFactory() {
        return this.beanFactory;
    }

    /**
     * Return the underlying bean factory of this context,
     * available for registering bean definitions.
     * <p><b>NOTE:</b> You need to call {@link #refresh()} to initialize the
     * bean factory and its contained beans with application context semantics
     * (autodetecting BeanFactoryPostProcessors, etc).
     * @return the internal bean factory (as DefaultListableBeanFactory)
     */
    public final DefaultListableBeanFactory getDefaultListableBeanFactory() {
        return this.beanFactory;
    }


    //---------------------------------------------------------------------
    // Implementation of BeanDefinitionRegistry
    //---------------------------------------------------------------------

    public void registerBeanDefinition(String beanName, BeanDefinition beanDefinition)
            throws BeanDefinitionStoreException {

        this.beanFactory.registerBeanDefinition(beanName, beanDefinition);
    }

    public void removeBeanDefinition(String beanName) throws NoSuchBeanDefinitionException {
        this.beanFactory.removeBeanDefinition(beanName);
    }

    public BeanDefinition getBeanDefinition(String beanName) throws NoSuchBeanDefinitionException {
        return this.beanFactory.getBeanDefinition(beanName);
    }

    public boolean isBeanNameInUse(String beanName) {
        return this.beanFactory.isBeanNameInUse(beanName);
    }

    public void registerAlias(String beanName, String alias) {
        this.beanFactory.registerAlias(beanName, alias);
    }

    public void removeAlias(String alias) {
        this.beanFactory.removeAlias(alias);
    }

    public boolean isAlias(String beanName) {
        return this.beanFactory.isAlias(beanName);
    }

    @Override
    public boolean isTypeMatch(String name, ResolvableType typeToMatch) throws NoSuchBeanDefinitionException {
        return false;
    }
}

