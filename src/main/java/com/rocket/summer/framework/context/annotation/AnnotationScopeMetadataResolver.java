package com.rocket.summer.framework.context.annotation;

import com.rocket.summer.framework.beans.factory.annotation.AnnotatedBeanDefinition;
import com.rocket.summer.framework.beans.factory.config.BeanDefinition;
import com.rocket.summer.framework.util.Assert;

import java.lang.annotation.Annotation;
import java.util.Map;

/**
 * A {@link ScopeMetadataResolver} implementation that (by default) checks for
 * the presence of the {@link Scope} annotation on the bean class.
 *
 * <p>The exact type of annotation that is checked for is configurable via the
 * {@link #setScopeAnnotationType(Class)} property.
 *
 * @author Mark Fisher
 * @author Juergen Hoeller
 * @since 2.5
 * @see Scope
 */
public class AnnotationScopeMetadataResolver implements ScopeMetadataResolver {

    private Class<? extends Annotation> scopeAnnotationType = Scope.class;

    private ScopedProxyMode scopedProxyMode;


    /**
     * Create a new instance of the <code>AnnotationScopeMetadataResolver</code> class.
     * @see #AnnotationScopeMetadataResolver(ScopedProxyMode)
     * @see ScopedProxyMode#NO
     */
    public AnnotationScopeMetadataResolver() {
        this(ScopedProxyMode.NO);
    }

    /**
     * Create a new instance of the <code>AnnotationScopeMetadataResolver</code> class.
     * @param scopedProxyMode the desired scoped-proxy mode
     */
    public AnnotationScopeMetadataResolver(ScopedProxyMode scopedProxyMode) {
        Assert.notNull(scopedProxyMode, "'scopedProxyMode' must not be null");
        this.scopedProxyMode = scopedProxyMode;
    }


    /**
     * Set the type of annotation that is checked for by this
     * {@link AnnotationScopeMetadataResolver}.
     * @param scopeAnnotationType the target annotation type
     */
    public void setScopeAnnotationType(Class<? extends Annotation> scopeAnnotationType) {
        Assert.notNull(scopeAnnotationType, "'scopeAnnotationType' must not be null");
        this.scopeAnnotationType = scopeAnnotationType;
    }


    public ScopeMetadata resolveScopeMetadata(BeanDefinition definition) {
        ScopeMetadata metadata = new ScopeMetadata();
        if (definition instanceof AnnotatedBeanDefinition) {
            AnnotatedBeanDefinition annDef = (AnnotatedBeanDefinition) definition;
            Map<String, Object> attributes =
                    annDef.getMetadata().getAnnotationAttributes(this.scopeAnnotationType.getName());
            if (attributes != null) {
                metadata.setScopeName((String) attributes.get("value"));
            }
            if (!metadata.getScopeName().equals(BeanDefinition.SCOPE_SINGLETON)) {
                metadata.setScopedProxyMode(this.scopedProxyMode);
            }
        }
        return metadata;
    }

}
