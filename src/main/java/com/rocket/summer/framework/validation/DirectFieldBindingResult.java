package com.rocket.summer.framework.validation;

import com.rocket.summer.framework.beans.ConfigurablePropertyAccessor;
import com.rocket.summer.framework.beans.PropertyAccessorFactory;
import com.rocket.summer.framework.util.Assert;

/**
 * Special implementation of the Errors and BindingResult interfaces,
 * supporting registration and evaluation of binding errors on value objects.
 * Performs direct field access instead of going through JavaBean getters.
 *
 * <p>This implementation just supports fields in the actual target object.
 * It is not able to traverse nested fields.
 *
 * @author Juergen Hoeller
 * @since 2.0
 * @see DataBinder#getBindingResult()
 * @see DataBinder#initDirectFieldAccess()
 * @see BeanPropertyBindingResult
 */
@SuppressWarnings("serial")
public class DirectFieldBindingResult extends AbstractPropertyBindingResult {

    private final Object target;

    private transient ConfigurablePropertyAccessor directFieldAccessor;


    /**
     * Create a new DirectFieldBindingResult instance.
     * @param target the target object to bind onto
     * @param objectName the name of the target object
     */
    public DirectFieldBindingResult(Object target, String objectName) {
        super(objectName);
        this.target = target;
    }

    @Override
    public final Object getTarget() {
        return this.target;
    }

    /**
     * Returns the DirectFieldAccessor that this instance uses.
     * Creates a new one if none existed before.
     * @see #createDirectFieldAccessor()
     */
    @Override
    public final ConfigurablePropertyAccessor getPropertyAccessor() {
        if (this.directFieldAccessor == null) {
            this.directFieldAccessor = createDirectFieldAccessor();
            this.directFieldAccessor.setExtractOldValueForEditor(true);
        }
        return this.directFieldAccessor;
    }

    /**
     * Create a new DirectFieldAccessor for the underlying target object.
     * @see #getTarget()
     */
    protected ConfigurablePropertyAccessor createDirectFieldAccessor() {
        Assert.state(this.target != null, "Cannot access fields on null target instance '" + getObjectName() + "'!");
        return PropertyAccessorFactory.forDirectFieldAccess(this.target);
    }
}
