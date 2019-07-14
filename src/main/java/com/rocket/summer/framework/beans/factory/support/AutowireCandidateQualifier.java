package com.rocket.summer.framework.beans.factory.support;

import com.rocket.summer.framework.beans.BeanMetadataAttributeAccessor;
import com.rocket.summer.framework.util.Assert;

/**
 * Qualifier for resolving autowire candidates. A bean definition that
 * includes one or more such qualifiers enables fine-grained matching
 * against annotations on a field or parameter to be autowired.
 *
 * @author Mark Fisher
 * @author Juergen Hoeller
 * @since 2.5
 * @see org.springframework.beans.factory.annotation.Qualifier
 */
public class AutowireCandidateQualifier extends BeanMetadataAttributeAccessor {

    public static String VALUE_KEY = "value";

    private final String typeName;


    /**
     * Construct a qualifier to match against an annotation of the
     * given type.
     * @param type the annotation type
     */
    public AutowireCandidateQualifier(Class type) {
        this(type.getName());
    }

    /**
     * Construct a qualifier to match against an annotation of the
     * given type name.
     * <p>The type name may match the fully-qualified class name of
     * the annotation or the short class name (without the package).
     * @param typeName the name of the annotation type
     */
    public AutowireCandidateQualifier(String typeName) {
        Assert.notNull(typeName, "Type name must not be null");
        this.typeName = typeName;
    }

    /**
     * Construct a qualifier to match against an annotation of the
     * given type whose <code>value</code> attribute also matches
     * the specified value.
     * @param type the annotation type
     * @param value the annotation value to match
     */
    public AutowireCandidateQualifier(Class type, Object value) {
        this(type.getName(), value);
    }

    /**
     * Construct a qualifier to match against an annotation of the
     * given type name whose <code>value</code> attribute also matches
     * the specified value.
     * <p>The type name may match the fully-qualified class name of
     * the annotation or the short class name (without the package).
     * @param typeName the name of the annotation type
     * @param value the annotation value to match
     */
    public AutowireCandidateQualifier(String typeName, Object value) {
        Assert.notNull(typeName, "Type name must not be null");
        this.typeName = typeName;
        setAttribute(VALUE_KEY, value);
    }


    /**
     * Retrieve the type name. This value will be the same as the
     * type name provided to the constructor or the fully-qualified
     * class name if a Class instance was provided to the constructor.
     */
    public String getTypeName() {
        return this.typeName;
    }

}

