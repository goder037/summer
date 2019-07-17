package com.rocket.summer.framework.beans.factory.support;

import com.rocket.summer.framework.beans.MutablePropertyValues;
import com.rocket.summer.framework.beans.factory.config.BeanDefinition;
import com.rocket.summer.framework.beans.factory.config.ConstructorArgumentValues;
import com.rocket.summer.framework.util.Assert;

import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * A root bean definition represents the merged bean definition that backs
 * a specific bean in a Spring BeanFactory at runtime. It might have been created
 * from multiple original bean definitions that inherit from each other,
 * typically registered as {@link GenericBeanDefinition GenericBeanDefinitions}.
 * A root bean definition is essentially the 'unified' bean definition view at runtime.
 *
 * <p>Root bean definitions may also be used for registering individual bean definitions
 * in the configuration phase. However, since Spring 2.5, the preferred way to register
 * bean definitions programmatically is the {@link GenericBeanDefinition} class.
 * GenericBeanDefinition has the advantage that it allows to dynamically define
 * parent dependencies, not 'hard-coding' the role as a root bean definition.
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @see GenericBeanDefinition
 * @see ChildBeanDefinition
 */
public class RootBeanDefinition extends AbstractBeanDefinition {

    private final Set<Member> externallyManagedConfigMembers = Collections.synchronizedSet(new HashSet<Member>(0));

    private final Set<String> externallyManagedInitMethods = Collections.synchronizedSet(new HashSet<String>(0));

    private final Set<String> externallyManagedDestroyMethods = Collections.synchronizedSet(new HashSet<String>(0));

    /** Package-visible field for caching the resolved constructor or factory method */
    volatile Object resolvedConstructorOrFactoryMethod;

    /** Package-visible field for caching fully resolved constructor arguments */
    volatile Object[] resolvedConstructorArguments;

    /** Package-visible field for caching partly prepared constructor arguments */
    volatile Object[] preparedConstructorArguments;

    /** Package-visible field that marks the constructor arguments as resolved */
    volatile boolean constructorArgumentsResolved = false;

    boolean isFactoryMethodUnique;

    /** Package-visible field that indicates a before-instantiation post-processor having kicked in */
    volatile Boolean beforeInstantiationResolved;

    /** Package-visible field that indicates MergedBeanDefinitionPostProcessor having been applied */
    boolean postProcessed = false;

    final Object postProcessingLock = new Object();


    /**
     * Create a new RootBeanDefinition, to be configured through its bean
     * properties and configuration methods.
     * @see #setBeanClass
     * @see #setBeanClassName
     * @see #setScope
     * @see #setAutowireMode
     * @see #setDependencyCheck
     * @see #setConstructorArgumentValues
     * @see #setPropertyValues
     */
    public RootBeanDefinition() {
        super();
    }

    /**
     * Create a new RootBeanDefinition for a singleton.
     * @param beanClass the class of the bean to instantiate
     */
    public RootBeanDefinition(Class beanClass) {
        super();
        setBeanClass(beanClass);
    }

    /**
     * Create a new RootBeanDefinition with the given singleton status.
     * @param beanClass the class of the bean to instantiate
     * @param singleton the singleton status of the bean
     * @deprecated since Spring 2.5, in favor of {@link #setScope}
     */
    public RootBeanDefinition(Class beanClass, boolean singleton) {
        super();
        setBeanClass(beanClass);
        setSingleton(singleton);
    }

    /**
     * Create a new RootBeanDefinition for a singleton,
     * using the given autowire mode.
     * @param beanClass the class of the bean to instantiate
     * @param autowireMode by name or type, using the constants in this interface
     */
    public RootBeanDefinition(Class beanClass, int autowireMode) {
        super();
        setBeanClass(beanClass);
        setAutowireMode(autowireMode);
    }

    /**
     * Create a new RootBeanDefinition for a singleton,
     * using the given autowire mode.
     * @param beanClass the class of the bean to instantiate
     * @param autowireMode by name or type, using the constants in this interface
     * @param dependencyCheck whether to perform a dependency check for objects
     * (not applicable to autowiring a constructor, thus ignored there)
     */
    public RootBeanDefinition(Class beanClass, int autowireMode, boolean dependencyCheck) {
        super();
        setBeanClass(beanClass);
        setAutowireMode(autowireMode);
        if (dependencyCheck && getResolvedAutowireMode() != AUTOWIRE_CONSTRUCTOR) {
            setDependencyCheck(RootBeanDefinition.DEPENDENCY_CHECK_OBJECTS);
        }
    }

    /**
     * Create a new RootBeanDefinition for a singleton,
     * providing property values.
     * @param beanClass the class of the bean to instantiate
     * @param pvs the property values to apply
     */
    public RootBeanDefinition(Class beanClass, MutablePropertyValues pvs) {
        super(null, pvs);
        setBeanClass(beanClass);
    }

    /**
     * Create a new RootBeanDefinition with the given singleton status,
     * providing property values.
     * @param beanClass the class of the bean to instantiate
     * @param pvs the property values to apply
     * @param singleton the singleton status of the bean
     * @deprecated since Spring 2.5, in favor of {@link #setScope}
     */
    public RootBeanDefinition(Class beanClass, MutablePropertyValues pvs, boolean singleton) {
        super(null, pvs);
        setBeanClass(beanClass);
        setSingleton(singleton);
    }

    /**
     * Create a new RootBeanDefinition for a singleton,
     * providing constructor arguments and property values.
     * @param beanClass the class of the bean to instantiate
     * @param cargs the constructor argument values to apply
     * @param pvs the property values to apply
     */
    public RootBeanDefinition(Class beanClass, ConstructorArgumentValues cargs, MutablePropertyValues pvs) {
        super(cargs, pvs);
        setBeanClass(beanClass);
    }

    /**
     * Create a new RootBeanDefinition for a singleton,
     * providing constructor arguments and property values.
     * <p>Takes a bean class name to avoid eager loading of the bean class.
     * @param beanClassName the name of the class to instantiate
     * @param cargs the constructor argument values to apply
     * @param pvs the property values to apply
     */
    public RootBeanDefinition(String beanClassName, ConstructorArgumentValues cargs, MutablePropertyValues pvs) {
        super(cargs, pvs);
        setBeanClassName(beanClassName);
    }

    /**
     * Check whether the given candidate qualifies as a factory method.
     */
    public boolean isFactoryMethod(Method candidate) {
        return (candidate != null && candidate.getName().equals(getFactoryMethodName()));
    }

    /**
     * Specify a factory method name that refers to a non-overloaded method.
     */
    public void setUniqueFactoryMethodName(String name) {
        Assert.hasText(name, "Factory method name must not be empty");
        setFactoryMethodName(name);
        this.isFactoryMethodUnique = true;
    }

    /**
     * Create a new RootBeanDefinition as deep copy of the given
     * bean definition.
     * @param original the original bean definition to copy from
     */
    public RootBeanDefinition(RootBeanDefinition original) {
        super((BeanDefinition) original);
    }

    /**
     * Create a new RootBeanDefinition as deep copy of the given
     * bean definition.
     * @param original the original bean definition to copy from
     */
    RootBeanDefinition(BeanDefinition original) {
        super(original);
    }


    public String getParentName() {
        return null;
    }

    public void setParentName(String parentName) {
        if (parentName != null) {
            throw new IllegalArgumentException("Root bean cannot be changed into a child bean with parent reference");
        }
    }


    public void registerExternallyManagedConfigMember(Member configMember) {
        this.externallyManagedConfigMembers.add(configMember);
    }

    public boolean isExternallyManagedConfigMember(Member configMember) {
        return this.externallyManagedConfigMembers.contains(configMember);
    }

    public void registerExternallyManagedInitMethod(String initMethod) {
        this.externallyManagedInitMethods.add(initMethod);
    }

    public boolean isExternallyManagedInitMethod(String initMethod) {
        return this.externallyManagedInitMethods.contains(initMethod);
    }

    public void registerExternallyManagedDestroyMethod(String destroyMethod) {
        this.externallyManagedDestroyMethods.add(destroyMethod);
    }

    public boolean isExternallyManagedDestroyMethod(String destroyMethod) {
        return this.externallyManagedDestroyMethods.contains(destroyMethod);
    }


    public AbstractBeanDefinition cloneBeanDefinition() {
        return new RootBeanDefinition(this);
    }

    public boolean equals(Object other) {
        return (this == other || (other instanceof RootBeanDefinition && super.equals(other)));
    }

    public String toString() {
        return "Root bean: " + super.toString();
    }

    /**
     * Override settings in this bean definition (assumably a copied parent
     * from a parent-child inheritance relationship) from the given bean
     * definition (assumably the child).
     * <ul>
     * <li>Will override beanClass if specified in the given bean definition.
     * <li>Will always take <code>abstract</code>, <code>scope</code>,
     * <code>lazyInit</code>, <code>autowireMode</code>, <code>dependencyCheck</code>,
     * and <code>dependsOn</code> from the given bean definition.
     * <li>Will add <code>constructorArgumentValues</code>, <code>propertyValues</code>,
     * <code>methodOverrides</code> from the given bean definition to existing ones.
     * <li>Will override <code>factoryBeanName</code>, <code>factoryMethodName</code>,
     * <code>initMethodName</code>, and <code>destroyMethodName</code> if specified
     * in the given bean definition.
     * </ul>
     */
    public void overrideFrom(BeanDefinition other) {
        if (other.getBeanClassName() != null) {
            setBeanClassName(other.getBeanClassName());
        }
        if (other.getFactoryBeanName() != null) {
            setFactoryBeanName(other.getFactoryBeanName());
        }
        if (other.getFactoryMethodName() != null) {
            setFactoryMethodName(other.getFactoryMethodName());
        }
        setScope(other.getScope());
        setAbstract(other.isAbstract());
        setLazyInit(other.isLazyInit());
        setRole(other.getRole());
        getConstructorArgumentValues().addArgumentValues(other.getConstructorArgumentValues());
        getPropertyValues().addPropertyValues(other.getPropertyValues());
        setSource(other.getSource());
        copyAttributesFrom(other);

        if (other instanceof AbstractBeanDefinition) {
            AbstractBeanDefinition otherAbd = (AbstractBeanDefinition) other;
            if (otherAbd.hasBeanClass()) {
                setBeanClass(otherAbd.getBeanClass());
            }
            setAutowireCandidate(otherAbd.isAutowireCandidate());
            setAutowireMode(otherAbd.getAutowireMode());
            copyQualifiersFrom(otherAbd);
            setPrimary(otherAbd.isPrimary());
            setDependencyCheck(otherAbd.getDependencyCheck());
            setDependsOn(otherAbd.getDependsOn());
            if (otherAbd.getInitMethodName() != null) {
                setInitMethodName(otherAbd.getInitMethodName());
                setEnforceInitMethod(otherAbd.isEnforceInitMethod());
            }
            if (otherAbd.getDestroyMethodName() != null) {
                setDestroyMethodName(otherAbd.getDestroyMethodName());
                setEnforceDestroyMethod(otherAbd.isEnforceDestroyMethod());
            }
            getMethodOverrides().addOverrides(otherAbd.getMethodOverrides());
            setSynthetic(otherAbd.isSynthetic());
            setResource(otherAbd.getResource());
        }
        else {
            setResourceDescription(other.getResourceDescription());
        }
    }
}
