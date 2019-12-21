package com.rocket.summer.framework.expression;

/**
 * A property accessor is able to read from (and possibly write to) an object's properties.
 * This interface places no restrictions, and so implementors are free to access properties
 * directly as fields or through getters or in any other way they see as appropriate.
 *
 * <p>A resolver can optionally specify an array of target classes for which it should be
 * called. However, if it returns {@code null} from {@link #getSpecificTargetClasses()},
 * it will be called for all property references and given a chance to determine if it
 * can read or write them.
 *
 * <p>Property resolvers are considered to be ordered and each will be called in turn.
 * The only rule that affects the call order is that any naming the target class directly
 * in {@link #getSpecificTargetClasses()} will be called first, before the general resolvers.
 *
 * @author Andy Clement
 * @since 3.0
 */
public interface PropertyAccessor {

    /**
     * Return an array of classes for which this resolver should be called.
     * <p>>Returning {@code null} indicates this is a general resolver that
     * can be called in an attempt to resolve a property on any type.
     * @return an array of classes that this resolver is suitable for
     * (or {@code null} if a general resolver)
     */
    Class<?>[] getSpecificTargetClasses();

    /**
     * Called to determine if a resolver instance is able to access a specified property
     * on a specified target object.
     * @param context the evaluation context in which the access is being attempted
     * @param target the target object upon which the property is being accessed
     * @param name the name of the property being accessed
     * @return true if this resolver is able to read the property
     * @throws AccessException if there is any problem determining whether the property can be read
     */
    boolean canRead(EvaluationContext context, Object target, String name) throws AccessException;

    /**
     * Called to read a property from a specified target object.
     * Should only succeed if {@link #canRead} also returns {@code true}.
     * @param context the evaluation context in which the access is being attempted
     * @param target the target object upon which the property is being accessed
     * @param name the name of the property being accessed
     * @return a TypedValue object wrapping the property value read and a type descriptor for it
     * @throws AccessException if there is any problem accessing the property value
     */
    TypedValue read(EvaluationContext context, Object target, String name) throws AccessException;

    /**
     * Called to determine if a resolver instance is able to write to a specified
     * property on a specified target object.
     * @param context the evaluation context in which the access is being attempted
     * @param target the target object upon which the property is being accessed
     * @param name the name of the property being accessed
     * @return true if this resolver is able to write to the property
     * @throws AccessException if there is any problem determining whether the
     * property can be written to
     */
    boolean canWrite(EvaluationContext context, Object target, String name) throws AccessException;

    /**
     * Called to write to a property on a specified target object.
     * Should only succeed if {@link #canWrite} also returns {@code true}.
     * @param context the evaluation context in which the access is being attempted
     * @param target the target object upon which the property is being accessed
     * @param name the name of the property being accessed
     * @param newValue the new value for the property
     * @throws AccessException if there is any problem writing to the property value
     */
    void write(EvaluationContext context, Object target, String name, Object newValue) throws AccessException;

}

