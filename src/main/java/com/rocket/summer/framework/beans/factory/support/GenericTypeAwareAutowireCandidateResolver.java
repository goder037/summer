package com.rocket.summer.framework.beans.factory.support;

import com.rocket.summer.framework.beans.factory.BeanFactory;
import com.rocket.summer.framework.beans.factory.BeanFactoryAware;
import com.rocket.summer.framework.beans.factory.FactoryBean;
import com.rocket.summer.framework.beans.factory.config.BeanDefinition;
import com.rocket.summer.framework.beans.factory.config.BeanDefinitionHolder;
import com.rocket.summer.framework.beans.factory.config.ConfigurableListableBeanFactory;
import com.rocket.summer.framework.beans.factory.config.DependencyDescriptor;
import com.rocket.summer.framework.core.ResolvableType;
import com.rocket.summer.framework.util.ClassUtils;

import java.lang.reflect.Method;

/**
 * Basic {@link AutowireCandidateResolver} that performs a full generic type
 * match with the candidate's type if the dependency is declared as a generic type
 * (e.g. Repository&lt;Customer&gt;).
 *
 * <p>This is the base class for
 * {@link com.rocket.summer.framework.beans.factory.annotation.QualifierAnnotationAutowireCandidateResolver},
 * providing an implementation all non-annotation-based resolution steps at this level.
 *
 * @author Juergen Hoeller
 * @since 4.0
 */
public class GenericTypeAwareAutowireCandidateResolver extends SimpleAutowireCandidateResolver
        implements BeanFactoryAware {

    private BeanFactory beanFactory;


    @Override
    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    protected final BeanFactory getBeanFactory() {
        return this.beanFactory;
    }


    @Override
    public boolean isAutowireCandidate(BeanDefinitionHolder bdHolder, DependencyDescriptor descriptor) {
        if (!super.isAutowireCandidate(bdHolder, descriptor)) {
            // If explicitly false, do not proceed with any other checks...
            return false;
        }
        return (descriptor == null || checkGenericTypeMatch(bdHolder, descriptor));
    }

    /**
     * Match the given dependency type with its generic type information against the given
     * candidate bean definition.
     */
    protected boolean checkGenericTypeMatch(BeanDefinitionHolder bdHolder, DependencyDescriptor descriptor) {
        ResolvableType dependencyType = descriptor.getResolvableType();
        if (dependencyType.getType() instanceof Class) {
            // No generic type -> we know it's a Class type-match, so no need to check again.
            return true;
        }

        ResolvableType targetType = null;
        boolean cacheType = false;
        RootBeanDefinition rbd = null;
        if (bdHolder.getBeanDefinition() instanceof RootBeanDefinition) {
            rbd = (RootBeanDefinition) bdHolder.getBeanDefinition();
        }
        if (rbd != null) {
            targetType = rbd.targetType;
            if (targetType == null) {
                cacheType = true;
                // First, check factory method return type, if applicable
                targetType = getReturnTypeForFactoryMethod(rbd, descriptor);
                if (targetType == null) {
                    RootBeanDefinition dbd = getResolvedDecoratedDefinition(rbd);
                    if (dbd != null) {
                        targetType = dbd.targetType;
                        if (targetType == null) {
                            targetType = getReturnTypeForFactoryMethod(dbd, descriptor);
                        }
                    }
                }
            }
        }

        if (targetType == null) {
            // Regular case: straight bean instance, with BeanFactory available.
            if (this.beanFactory != null) {
                Class<?> beanType = this.beanFactory.getType(bdHolder.getBeanName());
                if (beanType != null) {
                    targetType = ResolvableType.forClass(ClassUtils.getUserClass(beanType));
                }
            }
            // Fallback: no BeanFactory set, or no type resolvable through it
            // -> best-effort match against the target class if applicable.
            if (targetType == null && rbd != null && rbd.hasBeanClass() && rbd.getFactoryMethodName() == null) {
                Class<?> beanClass = rbd.getBeanClass();
                if (!FactoryBean.class.isAssignableFrom(beanClass)) {
                    targetType = ResolvableType.forClass(ClassUtils.getUserClass(beanClass));
                }
            }
        }

        if (targetType == null) {
            return true;
        }
        if (cacheType) {
            rbd.targetType = targetType;
        }
        if (descriptor.fallbackMatchAllowed() && targetType.hasUnresolvableGenerics()) {
            return true;
        }
        // Full check for complex generic type match...
        return dependencyType.isAssignableFrom(targetType);
    }

    protected RootBeanDefinition getResolvedDecoratedDefinition(RootBeanDefinition rbd) {
        BeanDefinitionHolder decDef = rbd.getDecoratedDefinition();
        if (decDef != null && this.beanFactory instanceof ConfigurableListableBeanFactory) {
            ConfigurableListableBeanFactory clbf = (ConfigurableListableBeanFactory) this.beanFactory;
            if (clbf.containsBeanDefinition(decDef.getBeanName())) {
                BeanDefinition dbd = clbf.getMergedBeanDefinition(decDef.getBeanName());
                if (dbd instanceof RootBeanDefinition) {
                    return (RootBeanDefinition) dbd;
                }
            }
        }
        return null;
    }

    protected ResolvableType getReturnTypeForFactoryMethod(RootBeanDefinition rbd, DependencyDescriptor descriptor) {
        // Should typically be set for any kind of factory method, since the BeanFactory
        // pre-resolves them before reaching out to the AutowireCandidateResolver...
        ResolvableType returnType = rbd.factoryMethodReturnType;
        if (returnType == null) {
            Method factoryMethod = rbd.getResolvedFactoryMethod();
            if (factoryMethod != null) {
                returnType = ResolvableType.forMethodReturnType(factoryMethod);
            }
        }
        if (returnType != null) {
            Class<?> resolvedClass = returnType.resolve();
            if (resolvedClass != null && descriptor.getDependencyType().isAssignableFrom(resolvedClass)) {
                // Only use factory method metadata if the return type is actually expressive enough
                // for our dependency. Otherwise, the returned instance type may have matched instead
                // in case of a singleton instance having been registered with the container already.
                return returnType;
            }
        }
        return null;
    }

}

