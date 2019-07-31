package com.rocket.summer.framework.beans.factory.config;

import com.rocket.summer.framework.beans.PropertyEditorRegistrar;
import com.rocket.summer.framework.beans.PropertyEditorRegistry;
import com.rocket.summer.framework.beans.TypeConverter;
import com.rocket.summer.framework.beans.factory.BeanFactory;
import com.rocket.summer.framework.beans.factory.HierarchicalBeanFactory;
import com.rocket.summer.framework.beans.factory.NoSuchBeanDefinitionException;
import com.rocket.summer.framework.core.convert.ConversionService;
import com.rocket.summer.framework.util.StringValueResolver;

import java.beans.PropertyEditor;
import java.security.AccessControlContext;

public interface ConfigurableBeanFactory extends HierarchicalBeanFactory, SingletonBeanRegistry {

    /**
     * Scope identifier for the standard singleton scope: "singleton".
     * Custom scopes can be added via <code>registerScope</code>.
     * @see #registerScope
     */
    String SCOPE_SINGLETON = "singleton";

    /**
     * Scope identifier for the standard prototype scope: "prototype".
     * Custom scopes can be added via <code>registerScope</code>.
     * @see #registerScope
     */
    String SCOPE_PROTOTYPE = "prototype";

    /**
     * Explicitly control the current in-creation status of the specified bean.
     * For container-internal use only.
     * @param beanName the name of the bean
     * @param inCreation whether the bean is currently in creation
     * @since 3.1
     */
    void setCurrentlyInCreation(String beanName, boolean inCreation);

    /**
     * Register the given scope, backed by the given Scope implementation.
     * @param scopeName the scope identifier
     * @param scope the backing Scope implementation
     */
    void registerScope(String scopeName, Scope scope);

    /**
     * Return the resolution strategy for expressions in bean definition values.
     * @since 3.0
     */
    BeanExpressionResolver getBeanExpressionResolver();

    /**
     * Specify the resolution strategy for expressions in bean definition values.
     * <p>There is no expression support active in a BeanFactory by default.
     * An ApplicationContext will typically set a standard expression strategy
     * here, supporting "#{...}" expressions in a Unified EL compatible style.
     * @since 3.0
     */
    void setBeanExpressionResolver(BeanExpressionResolver resolver);

    /**
     * Set a custom type converter that this BeanFactory should use for converting
     * bean property values, constructor argument values, etc.
     * <p>This will override the default PropertyEditor mechanism and hence make
     * any custom editors or custom editor registrars irrelevant.
     * @see #addPropertyEditorRegistrar
     * @see #registerCustomEditor
     * @since 2.5
     */
    void setTypeConverter(TypeConverter typeConverter);

    /**
     * Set whether to cache bean metadata such as given bean definitions
     * (in merged fashion) and resolved bean classes. Default is on.
     * <p>Turn this flag off to enable hot-refreshing of bean definition objects
     * and in particular bean classes. If this flag is off, any creation of a bean
     * instance will re-query the bean class loader for newly resolved classes.
     */
    void setCacheBeanMetadata(boolean cacheBeanMetadata);

    /**
     * Determine whether an embedded value resolver has been registered with this
     * bean factory, to be applied through {@link #resolveEmbeddedValue(String)}.
     * @since 4.3
     */
    boolean hasEmbeddedValueResolver();

    /**
     * Add a String resolver for embedded values such as annotation attributes.
     * @param valueResolver the String resolver to apply to embedded values
     * @since 3.0
     */
    void addEmbeddedValueResolver(StringValueResolver valueResolver);

    /**
     * Return the names of all currently registered scopes.
     * <p>This will only return the names of explicitly registered scopes.
     * Built-in scopes such as "singleton" and "prototype" won't be exposed.
     * @return the array of scope names, or an empty array if none
     * @see #registerScope
     */
    String[] getRegisteredScopeNames();

    /**
     * Return the Scope implementation for the given scope name, if any.
     * <p>This will only return explicitly registered scopes.
     * Built-in scopes such as "singleton" and "prototype" won't be exposed.
     * @param scopeName the name of the scope
     * @return the registered Scope implementation, or {@code null} if none
     * @see #registerScope
     */
    Scope getRegisteredScope(String scopeName);

    /**
     * Copy all relevant configuration from the given other factory.
     * <p>Should include all standard configuration settings as well as
     * BeanPostProcessors, Scopes, and factory-specific internal settings.
     * Should not include any metadata of actual bean definitions,
     * such as BeanDefinition objects and bean name aliases.
     * @param otherFactory the other BeanFactory to copy from
     */
    void copyConfigurationFrom(ConfigurableBeanFactory otherFactory);

    /**
     * Initialize the given PropertyEditorRegistry with the custom editors
     * that have been registered with this BeanFactory.
     * @param registry the PropertyEditorRegistry to initialize
     */
    void copyRegisteredEditorsTo(PropertyEditorRegistry registry);

    /**
     * Provides a security access control context relevant to this factory.
     * @return the applicable AccessControlContext (never {@code null})
     * @since 3.0
     */
    AccessControlContext getAccessControlContext();

    /**
     * Return the associated ConversionService, if any.
     * @since 3.0
     */
    ConversionService getConversionService();

    /**
     * Specify a Spring 3.0 ConversionService to use for converting
     * property values, as an alternative to JavaBeans PropertyEditors.
     * @since 3.0
     */
    void setConversionService(ConversionService conversionService);

    /**
     * Resolve the given embedded value, e.g. an annotation attribute.
     * @param value the value to resolve
     * @return the resolved value (may be the original value as-is)
     * @since 3.0
     */
    String resolveEmbeddedValue(String value);

    /**
     * Destroy the given bean instance (usually a prototype instance
     * obtained from this factory) according to its bean definition.
     * <p>Any exception that arises during destruction should be caught
     * and logged instead of propagated to the caller of this method.
     * @param beanName the name of the bean definition
     * @param beanInstance the bean instance to destroy
     */
    void destroyBean(String beanName, Object beanInstance);

    /**
     * Destroy all singleton beans in this factory, including inner beans that have
     * been registered as disposable. To be called on shutdown of a factory.
     * <p>Any exception that arises during destruction should be caught
     * and logged instead of propagated to the caller of this method.
     */
    void destroySingletons();

    /**
     * Specify a temporary ClassLoader to use for type matching purposes.
     * Default is none, simply using the standard bean ClassLoader.
     * <p>A temporary ClassLoader is usually just specified if
     * <i>load-time weaving</i> is involved, to make sure that actual bean
     * classes are loaded as lazily as possible. The temporary loader is
     * then removed once the BeanFactory completes its bootstrap phase.
     */
    void setTempClassLoader(ClassLoader tempClassLoader);

    /**
     * Return the temporary ClassLoader to use for type matching purposes,
     * if any.
     */
    ClassLoader getTempClassLoader();

    /**
     * Set the class loader to use for loading bean classes.
     * Default is the thread context class loader.
     * <p>Note that this class loader will only apply to bean definitions
     * that do not carry a resolved bean class yet. This is the case as of
     * Spring 2.0 by default: Bean definitions only carry bean class names,
     * to be resolved once the factory processes the bean definition.
     * @param beanClassLoader the class loader to use,
     * or <code>null</code> to suggest the default class loader
     */
    void setBeanClassLoader(ClassLoader beanClassLoader);

    /**
     * Return the names of all beans that the specified bean depends on, if any.
     * @param beanName the name of the bean
     * @return the array of names of beans which the bean depends on,
     * or an empty array if none
     */
    String[] getDependenciesForBean(String beanName);

    /**
     * Set the parent of this bean factory.
     * <p>Note that the parent cannot be changed: It should only be set outside
     * a constructor if it isn't available at the time of factory instantiation.
     * @param parentBeanFactory the parent BeanFactory
     * @throws IllegalStateException if this factory is already associated with
     * a parent BeanFactory
     * @see #getParentBeanFactory()
     */
    void setParentBeanFactory(BeanFactory parentBeanFactory) throws IllegalStateException;

    /**
     * Register a dependent bean for the given bean,
     * to be destroyed before the given bean is destroyed.
     * @param beanName the name of the bean
     * @param dependentBeanName the name of the dependent bean
     */
    void registerDependentBean(String beanName, String dependentBeanName);

    /**
     * Obtain a type converter as used by this BeanFactory. This may be a fresh
     * instance for each call, since TypeConverters are usually <i>not</i> thread-safe.
     * <p>If the default PropertyEditor mechanism is active, the returned
     * TypeConverter will be aware of all custom editors that have been registered.
     */
    TypeConverter getTypeConverter();

    /**
     * Destroy the specified scoped bean in the current target scope, if any.
     * <p>Any exception that arises during destruction should be caught
     * and logged instead of propagated to the caller of this method.
     * @param beanName the name of the scoped bean
     */
    void destroyScopedBean(String beanName);

    /**
     * Determine whether the specified bean is currently in creation.
     * @param beanName the name of the bean
     * @return whether the bean is currently in creation
     * @since 2.5
     */
    boolean isCurrentlyInCreation(String beanName);

    /**
     * Add a PropertyEditorRegistrar to be applied to all bean creation processes.
     * <p>Such a registrar creates new PropertyEditor instances and registers them
     * on the given registry, fresh for each bean creation attempt. This avoids
     * the need for synchronization on custom editors; hence, it is generally
     * preferable to use this method instead of {@link #registerCustomEditor}.
     * @param registrar the PropertyEditorRegistrar to register
     */
    void addPropertyEditorRegistrar(PropertyEditorRegistrar registrar);
    /**
     * Register the given custom property editor for all properties of the
     * given type. To be invoked during factory configuration.
     * <p>Note that this method will register a shared custom editor instance;
     * access to that instance will be synchronized for thread-safety. It is
     * generally preferable to use {@link #addPropertyEditorRegistrar} instead
     * of this method, to avoid for the need for synchronization on custom editors.
     * @param requiredType type of the property
     * @param propertyEditorClass the {@link PropertyEditor} class to register
     */
    void registerCustomEditor(Class<?> requiredType, Class<? extends PropertyEditor> propertyEditorClass);

    /**
     * Add a new BeanPostProcessor that will get applied to beans created
     * by this factory. To be invoked during factory configuration.
     * <p>Note: Post-processors submitted here will be applied in the order of
     * registration; any ordering semantics expressed through implementing the
     * {@link com.rocket.summer.framework.core.Ordered} interface will be ignored. Note
     * that autodetected post-processors (e.g. as beans in an ApplicationContext)
     * will always be applied after programmatically registered ones.
     * @param beanPostProcessor the post-processor to register
     */
    void addBeanPostProcessor(BeanPostProcessor beanPostProcessor);

    /**
     * Return this factory's class loader for loading bean classes.
     */
    ClassLoader getBeanClassLoader();

    /**
     * Return the current number of registered BeanPostProcessors, if any.
     */
    int getBeanPostProcessorCount();

    /**
     * Return the names of all beans which depend on the specified bean, if any.
     * @param beanName the name of the bean
     * @return the array of dependent bean names, or an empty array if none
     */
    String[] getDependentBeans(String beanName);

    /**
     * Return a merged BeanDefinition for the given bean name,
     * merging a child bean definition with its parent if necessary.
     * Considers bean definitions in ancestor factories as well.
     * @param beanName the name of the bean to retrieve the merged definition for
     * @return a (potentially merged) BeanDefinition for the given bean
     * @throws NoSuchBeanDefinitionException if there is no bean definition with the given name
     */
    BeanDefinition getMergedBeanDefinition(String beanName) throws NoSuchBeanDefinitionException;

    /**
     * Determine whether the bean with the given name is a FactoryBean.
     * @param name the name of the bean to check
     * @return whether the bean is a FactoryBean
     * (<code>false</code> means the bean exists but is not a FactoryBean)
     * @throws NoSuchBeanDefinitionException if there is no bean with the given name
     */
    boolean isFactoryBean(String name) throws NoSuchBeanDefinitionException;

    /**
     * Return whether to cache bean metadata such as given bean definitions
     * (in merged fashion) and resolved bean classes.
     */
    boolean isCacheBeanMetadata();
}
