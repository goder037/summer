package com.rocket.summer.framework.aop;

/**
 * Filter that restricts matching of a pointcut or introduction to
 * a given set of target classes.
 *
 * <p>Can be used as part of a {@link Pointcut} or for the entire
 * targeting of an {@link IntroductionAdvisor}.
 *
 * @author Rod Johnson
 * @see Pointcut
 * @see MethodMatcher
 */
public interface ClassFilter {

    /**
     * Should the pointcut apply to the given interface or target class?
     * @param clazz the candidate target class
     * @return whether the advice should apply to the given target class
     */
    boolean matches(Class<?> clazz);


    /**
     * Canonical instance of a ClassFilter that matches all classes.
     */
    ClassFilter TRUE = TrueClassFilter.INSTANCE;

}