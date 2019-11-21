package com.rocket.summer.framework.data.mapping.model;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.rocket.summer.framework.core.annotation.AnnotatedElementUtils;
import com.rocket.summer.framework.data.annotation.TypeAlias;
import com.rocket.summer.framework.data.domain.Persistable;
import com.rocket.summer.framework.data.mapping.Association;
import com.rocket.summer.framework.data.mapping.AssociationHandler;
import com.rocket.summer.framework.data.mapping.IdentifierAccessor;
import com.rocket.summer.framework.data.mapping.PersistentEntity;
import com.rocket.summer.framework.data.mapping.PersistentProperty;
import com.rocket.summer.framework.data.mapping.PersistentPropertyAccessor;
import com.rocket.summer.framework.data.mapping.PreferredConstructor;
import com.rocket.summer.framework.data.mapping.PropertyHandler;
import com.rocket.summer.framework.data.mapping.SimpleAssociationHandler;
import com.rocket.summer.framework.data.mapping.SimplePropertyHandler;
import com.rocket.summer.framework.data.util.TypeInformation;
import com.rocket.summer.framework.util.Assert;
import com.rocket.summer.framework.util.StringUtils;

/**
 * Simple value object to capture information of {@link PersistentEntity}s.
 *
 * @author Oliver Gierke
 * @author Jon Brisbin
 * @author Patryk Wasik
 * @author Thomas Darimont
 * @author Christoph Strobl
 * @author Mark Paluch
 */
public class BasicPersistentEntity<T, P extends PersistentProperty<P>> implements MutablePersistentEntity<T, P> {

    private static final Logger LOGGER = LoggerFactory.getLogger(BasicPersistentEntity.class);
    private static final String TYPE_MISMATCH = "Target bean of type %s is not of type of the persistent entity (%s)!";
    private static final String NULL_ASSOCIATION = "%s.addAssociation(…) was called with a null association! Usually indicates a problem in a Spring Data MappingContext implementation. Be sure to file a bug at https://jira.spring.io!";

    private final PreferredConstructor<T, P> constructor;
    private final TypeInformation<T> information;
    private final List<P> properties;
    private final Comparator<P> comparator;
    private final Set<Association<P>> associations;

    private final Map<String, P> propertyCache;
    private final Map<Class<? extends Annotation>, Annotation> annotationCache;

    private P idProperty;
    private P versionProperty;
    private PersistentPropertyAccessorFactory propertyAccessorFactory;

    /**
     * Creates a new {@link BasicPersistentEntity} from the given {@link TypeInformation}.
     *
     * @param information must not be {@literal null}.
     */
    public BasicPersistentEntity(TypeInformation<T> information) {
        this(information, null);
    }

    /**
     * Creates a new {@link BasicPersistentEntity} for the given {@link TypeInformation} and {@link Comparator}. The given
     * {@link Comparator} will be used to define the order of the {@link PersistentProperty} instances added to the
     * entity.
     *
     * @param information must not be {@literal null}.
     * @param comparator can be {@literal null}.
     */
    public BasicPersistentEntity(TypeInformation<T> information, Comparator<P> comparator) {

        Assert.notNull(information, "Information must not be null!");

        this.information = information;
        this.properties = new ArrayList<P>();
        this.comparator = comparator;
        this.constructor = new PreferredConstructorDiscoverer<T, P>(information, this).getConstructor();
        this.associations = comparator == null ? new HashSet<Association<P>>()
                : new TreeSet<Association<P>>(new AssociationComparator<P>(comparator));

        this.propertyCache = new HashMap<String, P>();
        this.annotationCache = new HashMap<Class<? extends Annotation>, Annotation>();
        this.propertyAccessorFactory = BeanWrapperPropertyAccessorFactory.INSTANCE;
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.mapping.PersistentEntity#getPersistenceConstructor()
     */
    public PreferredConstructor<T, P> getPersistenceConstructor() {
        return constructor;
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.mapping.PersistentEntity#isConstructorArgument(com.rocket.summer.framework.data.mapping.PersistentProperty)
     */
    public boolean isConstructorArgument(PersistentProperty<?> property) {
        return constructor == null ? false : constructor.isConstructorParameter(property);
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.mapping.PersistentEntity#isIdProperty(com.rocket.summer.framework.data.mapping.PersistentProperty)
     */
    public boolean isIdProperty(PersistentProperty<?> property) {
        return this.idProperty == null ? false : this.idProperty.equals(property);
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.mapping.PersistentEntity#isVersionProperty(com.rocket.summer.framework.data.mapping.PersistentProperty)
     */
    public boolean isVersionProperty(PersistentProperty<?> property) {
        return this.versionProperty == null ? false : this.versionProperty.equals(property);
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.mapping.PersistentEntity#getName()
     */
    public String getName() {
        return getType().getName();
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.mapping.PersistentEntity#getIdProperty()
     */
    public P getIdProperty() {
        return idProperty;
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.mapping.PersistentEntity#getVersionProperty()
     */
    public P getVersionProperty() {
        return versionProperty;
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.mapping.PersistentEntity#hasIdProperty()
     */
    public boolean hasIdProperty() {
        return idProperty != null;
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.mapping.PersistentEntity#hasVersionProperty()
     */
    public boolean hasVersionProperty() {
        return versionProperty != null;
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.mapping.MutablePersistentEntity#addPersistentProperty(P)
     */
    public void addPersistentProperty(P property) {

        Assert.notNull(property, "Property must not be null!");

        if (properties.contains(property)) {
            return;
        }

        properties.add(property);

        if (!propertyCache.containsKey(property.getName())) {
            propertyCache.put(property.getName(), property);
        }

        P candidate = returnPropertyIfBetterIdPropertyCandidateOrNull(property);

        if (candidate != null) {
            this.idProperty = candidate;
        }

        if (property.isVersionProperty()) {

            if (this.versionProperty != null) {
                throw new MappingException(
                        String.format(
                                "Attempt to add version property %s but already have property %s registered "
                                        + "as version. Check your mapping configuration!",
                                property.getField(), versionProperty.getField()));
            }

            this.versionProperty = property;
        }
    }

    /**
     * Returns the given property if it is a better candidate for the id property than the current id property.
     *
     * @param property the new id property candidate, will never be {@literal null}.
     * @return the given id property or {@literal null} if the given property is not an id property.
     */
    protected P returnPropertyIfBetterIdPropertyCandidateOrNull(P property) {

        if (!property.isIdProperty()) {
            return null;
        }

        if (this.idProperty != null) {
            throw new MappingException(String.format("Attempt to add id property %s but already have property %s registered "
                    + "as id. Check your mapping configuration!", property.getField(), idProperty.getField()));
        }

        return property;
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.mapping.MutablePersistentEntity#addAssociation(com.rocket.summer.framework.data.mapping.model.Association)
     */
    public void addAssociation(Association<P> association) {

        if (association == null) {
            LOGGER.warn(String.format(NULL_ASSOCIATION, this.getClass().getName()));
            return;
        }

        if (!associations.contains(association)) {
            associations.add(association);
        }
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.mapping.PersistentEntity#getPersistentProperty(java.lang.String)
     */
    public P getPersistentProperty(String name) {
        return propertyCache.get(name);
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.mapping.PersistentEntity#getPersistentProperty(java.lang.Class)
     */
    @Override
    public P getPersistentProperty(Class<? extends Annotation> annotationType) {

        Assert.notNull(annotationType, "Annotation type must not be null!");

        for (P property : properties) {
            if (property.isAnnotationPresent(annotationType)) {
                return property;
            }
        }

        for (Association<P> association : associations) {

            P property = association.getInverse();

            if (property.isAnnotationPresent(annotationType)) {
                return property;
            }
        }

        return null;
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.mapping.PersistentEntity#getType()
     */
    public Class<T> getType() {
        return information.getType();
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.mapping.PersistentEntity#getTypeAlias()
     */
    public Object getTypeAlias() {

        TypeAlias alias = AnnotatedElementUtils.findMergedAnnotation(getType(), TypeAlias.class);
        return alias == null ? null : StringUtils.hasText(alias.value()) ? alias.value() : null;
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.mapping.PersistentEntity#getTypeInformation()
     */
    public TypeInformation<T> getTypeInformation() {
        return information;
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.mapping.PersistentEntity#doWithProperties(com.rocket.summer.framework.data.mapping.PropertyHandler)
     */
    public void doWithProperties(PropertyHandler<P> handler) {

        Assert.notNull(handler, "Handler must not be null!");

        for (P property : properties) {
            if (!property.isTransient() && !property.isAssociation()) {
                handler.doWithPersistentProperty(property);
            }
        }
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.mapping.PersistentEntity#doWithProperties(com.rocket.summer.framework.data.mapping.PropertyHandler.Simple)
     */
    @Override
    public void doWithProperties(SimplePropertyHandler handler) {

        Assert.notNull(handler, "Handler must not be null!");

        for (PersistentProperty<?> property : properties) {
            if (!property.isTransient() && !property.isAssociation()) {
                handler.doWithPersistentProperty(property);
            }
        }
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.mapping.PersistentEntity#doWithAssociations(com.rocket.summer.framework.data.mapping.AssociationHandler)
     */
    public void doWithAssociations(AssociationHandler<P> handler) {

        Assert.notNull(handler, "Handler must not be null!");

        for (Association<P> association : associations) {
            handler.doWithAssociation(association);
        }
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.mapping.PersistentEntity#doWithAssociations(com.rocket.summer.framework.data.mapping.SimpleAssociationHandler)
     */
    public void doWithAssociations(SimpleAssociationHandler handler) {

        Assert.notNull(handler, "Handler must not be null!");

        for (Association<? extends PersistentProperty<?>> association : associations) {
            handler.doWithAssociation(association);
        }
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.mapping.PersistentEntity#findAnnotation(java.lang.Class)
     */
    @Override
    @SuppressWarnings("unchecked")
    public <A extends Annotation> A findAnnotation(Class<A> annotationType) {

        if (annotationCache.containsKey(annotationType)) {
            return (A) annotationCache.get(annotationType);
        }

        A annotation = AnnotatedElementUtils.findMergedAnnotation(getType(), annotationType);
        annotationCache.put(annotationType, annotation);

        return annotation;
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.mapping.MutablePersistentEntity#verify()
     */
    public void verify() {

        if (comparator != null) {
            Collections.sort(properties, comparator);
        }
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.mapping.model.MutablePersistentEntity#setPersistentPropertyAccessorFactory(com.rocket.summer.framework.data.mapping.model.PersistentPropertyAccessorFactory)
     */
    @Override
    public void setPersistentPropertyAccessorFactory(PersistentPropertyAccessorFactory factory) {
        this.propertyAccessorFactory = factory;
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.mapping.PersistentEntity#getPropertyAccessor(java.lang.Object)
     */
    @Override
    public PersistentPropertyAccessor getPropertyAccessor(Object bean) {

        Assert.notNull(bean, "Target bean must not be null!");
        assertBeanType(bean);

        return propertyAccessorFactory.getPropertyAccessor(this, bean);
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.mapping.PersistentEntity#getIdentifierAccessor(java.lang.Object)
     */
    @Override
    public IdentifierAccessor getIdentifierAccessor(Object bean) {

        Assert.notNull(bean, "Target bean must not be null!");
        assertBeanType(bean);

        if (Persistable.class.isAssignableFrom(getType())) {
            return new PersistableIdentifierAccessor((Persistable<?>) bean);
        }

        return hasIdProperty() ? new IdPropertyIdentifierAccessor(this, bean) : NullReturningIdentifierAccessor.INSTANCE;
    }

    private void assertBeanType(Object bean) {

        if (!getType().isInstance(bean)) {
            throw new IllegalArgumentException(String.format(TYPE_MISMATCH, bean.getClass().getName(), getType().getName()));
        }
    }

    /**
     * A null-object implementation of {@link IdentifierAccessor} to be able to return an accessor for entities that do
     * not have an identifier property.
     *
     * @author Oliver Gierke
     */
    private static enum NullReturningIdentifierAccessor implements IdentifierAccessor {

        INSTANCE;

        /*
         * (non-Javadoc)
         * @see com.rocket.summer.framework.data.mapping.IdentifierAccessor#getIdentifier()
         */
        @Override
        public Object getIdentifier() {
            return null;
        }
    }

    /**
     * Simple {@link Comparator} adaptor to delegate ordering to the inverse properties of the association.
     *
     * @author Oliver Gierke
     */
    @RequiredArgsConstructor
    private static final class AssociationComparator<P extends PersistentProperty<P>>
            implements Comparator<Association<P>>, Serializable {

        private static final long serialVersionUID = 4508054194886854513L;
        private final @NonNull Comparator<P> delegate;

        /*
         * (non-Javadoc)
         * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
         */
        public int compare(Association<P> left, Association<P> right) {
            return delegate.compare(left.getInverse(), right.getInverse());
        }
    }
}

