package com.rocket.summer.framework.aop.aspectj;

import org.aopalliance.aop.Advice;

import com.rocket.summer.framework.aop.ClassFilter;
import com.rocket.summer.framework.aop.IntroductionAdvisor;
import com.rocket.summer.framework.aop.IntroductionInterceptor;
import com.rocket.summer.framework.aop.support.ClassFilters;
import com.rocket.summer.framework.aop.support.DelegatePerTargetObjectIntroductionInterceptor;
import com.rocket.summer.framework.aop.support.DelegatingIntroductionInterceptor;

/**
 * Introduction advisor delegating to the given object.
 * Implements AspectJ annotation-style behavior for the DeclareParents annotation.
 *
 * @author Rod Johnson
 * @author Ramnivas Laddad
 * @since 2.0
 */
public class DeclareParentsAdvisor implements IntroductionAdvisor {

    private final Advice advice;

    private final Class<?> introducedInterface;

    private final ClassFilter typePatternClassFilter;


    /**
     * Create a new advisor for this DeclareParents field.
     * @param interfaceType static field defining the introduction
     * @param typePattern type pattern the introduction is restricted to
     * @param defaultImpl the default implementation class
     */
    public DeclareParentsAdvisor(Class<?> interfaceType, String typePattern, Class<?> defaultImpl) {
        this(interfaceType, typePattern,
                new DelegatePerTargetObjectIntroductionInterceptor(defaultImpl, interfaceType));
    }

    /**
     * Create a new advisor for this DeclareParents field.
     * @param interfaceType static field defining the introduction
     * @param typePattern type pattern the introduction is restricted to
     * @param delegateRef the delegate implementation object
     */
    public DeclareParentsAdvisor(Class<?> interfaceType, String typePattern, Object delegateRef) {
        this(interfaceType, typePattern, new DelegatingIntroductionInterceptor(delegateRef));
    }

    /**
     * Private constructor to share common code between impl-based delegate and reference-based delegate
     * (cannot use method such as init() to share common code, due the use of final fields)
     * @param interfaceType static field defining the introduction
     * @param typePattern type pattern the introduction is restricted to
     * @param interceptor the delegation advice as {@link IntroductionInterceptor}
     */
    private DeclareParentsAdvisor(Class<?> interfaceType, String typePattern, IntroductionInterceptor interceptor) {
        this.advice = interceptor;
        this.introducedInterface = interfaceType;

        // Excludes methods implemented.
        ClassFilter typePatternFilter = new TypePatternClassFilter(typePattern);
        ClassFilter exclusion = new ClassFilter() {
            @Override
            public boolean matches(Class<?> clazz) {
                return !introducedInterface.isAssignableFrom(clazz);
            }
        };
        this.typePatternClassFilter = ClassFilters.intersection(typePatternFilter, exclusion);
    }


    @Override
    public ClassFilter getClassFilter() {
        return this.typePatternClassFilter;
    }

    @Override
    public void validateInterfaces() throws IllegalArgumentException {
        // Do nothing
    }

    @Override
    public boolean isPerInstance() {
        return true;
    }

    @Override
    public Advice getAdvice() {
        return this.advice;
    }

    @Override
    public Class<?>[] getInterfaces() {
        return new Class<?>[] {this.introducedInterface};
    }

}

