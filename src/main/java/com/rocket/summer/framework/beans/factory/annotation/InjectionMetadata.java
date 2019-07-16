package com.rocket.summer.framework.beans.factory.annotation;

import com.rocket.summer.framework.beans.MutablePropertyValues;
import com.rocket.summer.framework.beans.PropertyValues;
import com.rocket.summer.framework.beans.factory.support.RootBeanDefinition;
import com.rocket.summer.framework.util.ReflectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Internal class for managing injection metadata.
 * Not intended for direct use in applications.
 *
 * <p>Used by {@link AutowiredAnnotationBeanPostProcessor},
 * {@link org.springframework.context.annotation.CommonAnnotationBeanPostProcessor} and
 * {@link org.springframework.orm.jpa.support.PersistenceAnnotationBeanPostProcessor}.
 *
 * @author Juergen Hoeller
 * @since 2.5
 */
public class InjectionMetadata {

    private final Log logger = LogFactory.getLog(InjectionMetadata.class);

    private String targetClassName;

    private final Set<InjectedElement> injectedFields = new LinkedHashSet<InjectedElement>();

    private final Set<InjectedElement> injectedMethods = new LinkedHashSet<InjectedElement>();


    public InjectionMetadata() {
    }

    public InjectionMetadata(Class targetClass) {
        this.targetClassName = targetClass.getName();
    }


    public void addInjectedField(InjectedElement element) {
        if (logger.isDebugEnabled()) {
            logger.debug("Found injected field on class [" + this.targetClassName + "]: " + element);
        }
        this.injectedFields.add(element);
    }

    public void addInjectedMethod(InjectedElement element) {
        if (logger.isDebugEnabled()) {
            logger.debug("Found injected method on class [" + this.targetClassName + "]: " + element);
        }
        this.injectedMethods.add(element);
    }

    public void checkConfigMembers(RootBeanDefinition beanDefinition) {
        doRegisterConfigMembers(beanDefinition, this.injectedFields);
        doRegisterConfigMembers(beanDefinition, this.injectedMethods);
    }

    private void doRegisterConfigMembers(RootBeanDefinition beanDefinition, Set<InjectedElement> members) {
        for (Iterator<InjectedElement> it = members.iterator(); it.hasNext();) {
            Member member = it.next().getMember();
            if (!beanDefinition.isExternallyManagedConfigMember(member)) {
                beanDefinition.registerExternallyManagedConfigMember(member);
            }
            else {
                it.remove();
            }
        }
    }

    public void injectFields(Object target, String beanName) throws Throwable {
        if (!this.injectedFields.isEmpty()) {
            boolean debug = logger.isDebugEnabled();
            for (InjectedElement element : this.injectedFields) {
                if (debug) {
                    logger.debug("Processing injected field of bean '" + beanName + "': " + element);
                }
                element.inject(target, beanName, null);
            }
        }
    }

    public void injectMethods(Object target, String beanName, PropertyValues pvs) throws Throwable {
        if (!this.injectedMethods.isEmpty()) {
            boolean debug = logger.isDebugEnabled();
            for (InjectedElement element : this.injectedMethods) {
                if (debug) {
                    logger.debug("Processing injected method of bean '" + beanName + "': " + element);
                }
                element.inject(target, beanName, pvs);
            }
        }
    }


    public static abstract class InjectedElement {

        protected final Member member;

        protected final boolean isField;

        protected final PropertyDescriptor pd;

        protected volatile Boolean skip;

        protected InjectedElement(Member member, PropertyDescriptor pd) {
            this.member = member;
            this.isField = (member instanceof Field);
            this.pd = pd;
        }

        public final Member getMember() {
            return this.member;
        }

        protected final Class getResourceType() {
            if (this.isField) {
                return ((Field) this.member).getType();
            }
            else if (this.pd != null) {
                return this.pd.getPropertyType();
            }
            else {
                return ((Method) this.member).getParameterTypes()[0];
            }
        }

        protected final void checkResourceType(Class resourceType) {
            if (this.isField) {
                Class fieldType = ((Field) this.member).getType();
                if (!(resourceType.isAssignableFrom(fieldType) || fieldType.isAssignableFrom(resourceType))) {
                    throw new IllegalStateException("Specified field type [" + fieldType +
                            "] is incompatible with resource type [" + resourceType.getName() + "]");
                }
            }
            else {
                Class paramType =
                        (this.pd != null ? this.pd.getPropertyType() : ((Method) this.member).getParameterTypes()[0]);
                if (!(resourceType.isAssignableFrom(paramType) || paramType.isAssignableFrom(resourceType))) {
                    throw new IllegalStateException("Specified parameter type [" + paramType +
                            "] is incompatible with resource type [" + resourceType.getName() + "]");
                }
            }
        }

        /**
         * Either this or {@link #getResourceToInject} needs to be overridden.
         */
        protected void inject(Object target, String requestingBeanName, PropertyValues pvs) throws Throwable {
            if (this.isField) {
                Field field = (Field) this.member;
                ReflectionUtils.makeAccessible(field);
                field.set(target, getResourceToInject(target, requestingBeanName));
            }
            else {
                if (this.skip == null) {
                    this.skip = Boolean.valueOf(checkPropertySkipping(pvs));
                }
                if (this.skip.booleanValue()) {
                    return;
                }
                try {
                    Method method = (Method) this.member;
                    ReflectionUtils.makeAccessible(method);
                    method.invoke(target, getResourceToInject(target, requestingBeanName));
                }
                catch (InvocationTargetException ex) {
                    throw ex.getTargetException();
                }
            }
        }

        /**
         * Checks whether this injector's property needs to be skipped due to
         * an explicit property value having been specified. Also marks the
         * affected property as processed for other processors to ignore it.
         */
        protected boolean checkPropertySkipping(PropertyValues pvs) {
            if (this.pd != null && pvs != null) {
                if (pvs.contains(this.pd.getName())) {
                    // Explicit value provided as part of the bean definition.
                    return true;
                }
                else if (pvs instanceof MutablePropertyValues) {
                    ((MutablePropertyValues) pvs).registerProcessedProperty(this.pd.getName());
                }
            }
            return false;
        }

        /**
         * Either this or {@link #inject} needs to be overridden.
         */
        protected Object getResourceToInject(Object target, String requestingBeanName) {
            return null;
        }

        public boolean equals(Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof InjectedElement)) {
                return false;
            }
            InjectedElement otherElement = (InjectedElement) other;
            if (this.isField) {
                return this.member.equals(otherElement.member);
            }
            else {
                return (otherElement.member instanceof Method &&
                        this.member.getName().equals(otherElement.member.getName()) &&
                        Arrays.equals(((Method) this.member).getParameterTypes(),
                                ((Method) otherElement.member).getParameterTypes()));
            }
        }

        public int hashCode() {
            return this.member.getClass().hashCode() * 29 + this.member.getName().hashCode();
        }

        public String toString() {
            return getClass().getSimpleName() + " for " + this.member;
        }
    }

}
