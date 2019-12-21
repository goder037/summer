package com.rocket.summer.framework.expression;

import java.util.List;

/**
 * Expressions are executed in an evaluation context. It is in this context that
 * references are resolved when encountered during expression evaluation.
 *
 * <p>There is a default implementation of this EvaluationContext interface:
 * {@link com.rocket.summer.framework.expression.spel.support.StandardEvaluationContext}
 * which can be extended, rather than having to implement everything manually.
 *
 * @author Andy Clement
 * @author Juergen Hoeller
 * @since 3.0
 */
public interface EvaluationContext {

    /**
     * Return the default root context object against which unqualified
     * properties/methods/etc should be resolved. This can be overridden
     * when evaluating an expression.
     */
    TypedValue getRootObject();

    /**
     * Return a list of accessors that will be asked in turn to read/write a property.
     */
    List<PropertyAccessor> getPropertyAccessors();

    /**
     * Return a list of resolvers that will be asked in turn to locate a constructor.
     */
    List<ConstructorResolver> getConstructorResolvers();

    /**
     * Return a list of resolvers that will be asked in turn to locate a method.
     */
    List<MethodResolver> getMethodResolvers();

    /**
     * Return a bean resolver that can look up beans by name.
     */
    BeanResolver getBeanResolver();

    /**
     * Return a type locator that can be used to find types, either by short or
     * fully qualified name.
     */
    TypeLocator getTypeLocator();

    /**
     * Return a type converter that can convert (or coerce) a value from one type to another.
     */
    TypeConverter getTypeConverter();

    /**
     * Return a type comparator for comparing pairs of objects for equality.
     */
    TypeComparator getTypeComparator();

    /**
     * Return an operator overloader that may support mathematical operations
     * between more than the standard set of types.
     */
    OperatorOverloader getOperatorOverloader();

    /**
     * Set a named variable within this evaluation context to a specified value.
     * @param name variable to set
     * @param value value to be placed in the variable
     */
    void setVariable(String name, Object value);

    /**
     * Look up a named variable within this evaluation context.
     * @param name variable to lookup
     * @return the value of the variable, or {@code null} if not found
     */
    Object lookupVariable(String name);

}

