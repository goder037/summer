package com.rocket.summer.framework.beans.factory.config;

import com.rocket.summer.framework.beans.PropertyEditorRegistrar;
import com.rocket.summer.framework.beans.TypeConverter;
import com.rocket.summer.framework.beans.factory.HierarchicalBeanFactory;
import com.rocket.summer.framework.beans.factory.NoSuchBeanDefinitionException;

import java.beans.PropertyEditor;

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
     * Register the given scope, backed by the given Scope implementation.
     * @param scopeName the scope identifier
     * @param scope the backing Scope implementation
     */
    void registerScope(String scopeName, Scope scope);

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
     * {@link org.springframework.core.Ordered} interface will be ignored. Note
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
