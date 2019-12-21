package com.rocket.summer.framework.expression;

/**
 * A bean resolver can be registered with the evaluation context and will kick in
 * for bean references: {@code @myBeanName} and {@code &myBeanName} expressions.
 * The <tt>&</tt> variant syntax allows access to the factory bean where relevant.
 *
 * @author Andy Clement
 * @since 3.0.3
 */
public interface BeanResolver {

    /**
     * Look up a bean by the given name and return a corresponding instance for it.
     * For attempting access to a factory bean, the name needs a <tt>&</tt> prefix.
     * @param context the current evaluation context
     * @param beanName the name of the bean to look up
     * @return an object representing the bean
     * @throws AccessException if there is an unexpected problem resolving the bean
     */
    Object resolve(EvaluationContext context, String beanName) throws AccessException;

}
