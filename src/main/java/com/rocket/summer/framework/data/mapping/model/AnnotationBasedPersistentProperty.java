package com.rocket.summer.framework.data.mapping.model;

import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.rocket.summer.framework.beans.factory.annotation.Autowired;
import com.rocket.summer.framework.beans.factory.annotation.Value;
import com.rocket.summer.framework.core.annotation.AnnotatedElementUtils;
import com.rocket.summer.framework.data.annotation.AccessType;
import com.rocket.summer.framework.data.annotation.AccessType.Type;
import com.rocket.summer.framework.data.annotation.Id;
import com.rocket.summer.framework.data.annotation.ReadOnlyProperty;
import com.rocket.summer.framework.data.annotation.Reference;
import com.rocket.summer.framework.data.annotation.Transient;
import com.rocket.summer.framework.data.annotation.Version;
import com.rocket.summer.framework.data.mapping.Association;
import com.rocket.summer.framework.data.mapping.PersistentEntity;
import com.rocket.summer.framework.data.mapping.PersistentProperty;
import com.rocket.summer.framework.util.Assert;

/**
 * Special {@link PersistentProperty} that takes annotations at a property into account.
 *
 * @author Oliver Gierke
 * @author Christoph Strobl
 * @author Mark Paluch
 */
public abstract class AnnotationBasedPersistentProperty<P extends PersistentProperty<P>>
        extends AbstractPersistentProperty<P> {

    private static final String SPRING_DATA_PACKAGE = "com.rocket.summer.framework.data";

    private final Value value;
    private final Map<Class<? extends Annotation>, CachedValue<? extends Annotation>> annotationCache = //
            new ConcurrentHashMap<Class<? extends Annotation>, CachedValue<? extends Annotation>>();

    private Boolean isTransient;
    private boolean usePropertyAccess;

    /**
     * Creates a new {@link AnnotationBasedPersistentProperty}.
     *
     * @param field must not be {@literal null}.
     * @param propertyDescriptor can be {@literal null}.
     * @param owner must not be {@literal null}.
     */
    public AnnotationBasedPersistentProperty(Field field, PropertyDescriptor propertyDescriptor,
                                             PersistentEntity<?, P> owner, SimpleTypeHolder simpleTypeHolder) {

        super(field, propertyDescriptor, owner, simpleTypeHolder);

        populateAnnotationCache(field);

        AccessType accessType = findPropertyOrOwnerAnnotation(AccessType.class);
        this.usePropertyAccess = accessType == null ? false : Type.PROPERTY.equals(accessType.value());
        this.value = findAnnotation(Value.class);
    }

    /**
     * Populates the annotation cache by eagerly accessing the annotations directly annotated to the accessors (if
     * available) and the backing field. Annotations override annotations found on field.
     *
     * @param field
     * @throws MappingException in case we find an ambiguous mapping on the accessor methods
     */
    private final void populateAnnotationCache(Field field) {

        for (Method method : Arrays.asList(getGetter(), getSetter())) {

            if (method == null) {
                continue;
            }

            for (Annotation annotation : method.getAnnotations()) {

                Class<? extends Annotation> annotationType = annotation.annotationType();

                validateAnnotation(annotation,
                        "Ambiguous mapping! Annotation %s configured "
                                + "multiple times on accessor methods of property %s in class %s!",
                        annotationType.getSimpleName(), getName(), getOwner().getType().getSimpleName());

                cacheAndReturn(annotationType, AnnotatedElementUtils.findMergedAnnotation(method, annotationType));
            }
        }

        if (field == null) {
            return;
        }

        for (Annotation annotation : field.getAnnotations()) {

            Class<? extends Annotation> annotationType = annotation.annotationType();

            validateAnnotation(annotation,
                    "Ambiguous mapping! Annotation %s configured " + "on field %s and one of its accessor methods in class %s!",
                    annotationType.getSimpleName(), field.getName(), getOwner().getType().getSimpleName());

            cacheAndReturn(annotationType, AnnotatedElementUtils.findMergedAnnotation(field, annotationType));
        }
    }

    /**
     * Verifies the given annotation candidate detected. Will be rejected if it's a Spring Data annotation and we already
     * found another one with a different configuration setup (i.e. other attribute values).
     *
     * @param candidate must not be {@literal null}.
     * @param message must not be {@literal null}.
     * @param arguments must not be {@literal null}.
     */
    private void validateAnnotation(Annotation candidate, String message, Object... arguments) {

        Class<? extends Annotation> annotationType = candidate.annotationType();

        if (!annotationType.getName().startsWith(SPRING_DATA_PACKAGE)) {
            return;
        }

        CachedValue<? extends Annotation> cachedValue = annotationCache.get(annotationType);

        if (cachedValue != null && !annotationCache.get(annotationType).value.equals(candidate)) {
            throw new MappingException(String.format(message, arguments));
        }
    }

    /**
     * Inspects a potentially available {@link Value} annotation at the property and returns the {@link String} value of
     * it.
     *
     * @see com.rocket.summer.framework.data.mapping.model.AbstractPersistentProperty#getSpelExpression()
     */
    @Override
    public String getSpelExpression() {
        return value == null ? null : value.value();
    }

    /**
     * Considers plain transient fields, fields annotated with {@link Transient}, {@link Value} or {@link Autowired} as
     * transient.
     *
     * @see com.rocket.summer.framework.data.mapping.BasicPersistentProperty#isTransient()
     */
    @Override
    public boolean isTransient() {

        if (this.isTransient == null) {
            boolean potentiallyTransient = super.isTransient() || isAnnotationPresent(Transient.class);
            this.isTransient = potentiallyTransient || isAnnotationPresent(Value.class)
                    || isAnnotationPresent(Autowired.class);
        }

        return this.isTransient;
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.mapping.PersistentProperty#isIdProperty()
     */
    public boolean isIdProperty() {
        return isAnnotationPresent(Id.class);
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.mapping.PersistentProperty#isVersionProperty()
     */
    public boolean isVersionProperty() {
        return isAnnotationPresent(Version.class);
    }

    /**
     * Considers the property an {@link Association} if it is annotated with {@link Reference}.
     */
    @Override
    public boolean isAssociation() {
        return !isTransient() && isAnnotationPresent(Reference.class);
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.mapping.model.AbstractPersistentProperty#isWritable()
     */
    @Override
    public boolean isWritable() {
        return !isTransient() && !isAnnotationPresent(ReadOnlyProperty.class);
    }

    /**
     * Returns the annotation found for the current {@link AnnotationBasedPersistentProperty}. Will prefer getters or
     * setters annotations over ones found at the backing field as the former can be used to reconfigure the metadata in
     * subclasses.
     *
     * @param annotationType must not be {@literal null}.
     * @return
     */
    @SuppressWarnings("unchecked")
    public <A extends Annotation> A findAnnotation(Class<A> annotationType) {

        Assert.notNull(annotationType, "Annotation type must not be null!");

        CachedValue<? extends Annotation> cachedAnnotation = annotationCache == null ? null
                : annotationCache.get(annotationType);

        if (cachedAnnotation != null) {
            return (A) cachedAnnotation.getValue();
        }

        for (Method method : Arrays.asList(getGetter(), getSetter())) {

            if (method == null) {
                continue;
            }

            A annotation = AnnotatedElementUtils.findMergedAnnotation(method, annotationType);

            if (annotation != null) {
                return cacheAndReturn(annotationType, annotation);
            }
        }

        return cacheAndReturn(annotationType,
                field == null ? null : AnnotatedElementUtils.findMergedAnnotation(field, annotationType));
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.mapping.PersistentProperty#findPropertyOrOwnerAnnotation(java.lang.Class)
     */
    @Override
    public <A extends Annotation> A findPropertyOrOwnerAnnotation(Class<A> annotationType) {

        A annotation = findAnnotation(annotationType);
        return annotation == null ? owner.findAnnotation(annotationType) : annotation;
    }

    /**
     * Puts the given annotation into the local cache and returns it.
     *
     * @param annotation
     * @return
     */
    private <A extends Annotation> A cacheAndReturn(Class<? extends A> type, A annotation) {

        if (annotationCache != null) {
            annotationCache.put(type, CachedValue.of(annotation));
        }

        return annotation;
    }

    /**
     * Returns whether the property carries the an annotation of the given type.
     *
     * @param annotationType the annotation type to look up.
     * @return
     */
    public boolean isAnnotationPresent(Class<? extends Annotation> annotationType) {
        return findAnnotation(annotationType) != null;
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.mapping.model.AbstractPersistentProperty#usePropertyAccess()
     */
    @Override
    public boolean usePropertyAccess() {
        return super.usePropertyAccess() || usePropertyAccess;
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.mapping.model.AbstractPersistentProperty#toString()
     */
    @Override
    public String toString() {

        if (annotationCache.isEmpty()) {
            populateAnnotationCache(field);
        }

        StringBuilder builder = new StringBuilder();

        for (CachedValue<? extends Annotation> annotation : annotationCache.values()) {
            if (annotation.value != null) {
                builder.append(annotation.value.toString()).append(" ");
            }
        }

        return builder.toString() + super.toString();
    }

    @lombok.Value(staticConstructor = "of")
    static class CachedValue<T> {
        T value;
    }
}

