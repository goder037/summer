package com.rocket.summer.framework.aop.support;

import com.rocket.summer.framework.aop.*;
import com.rocket.summer.framework.core.BridgeMethodResolver;
import com.rocket.summer.framework.core.JdkVersion;
import com.rocket.summer.framework.util.Assert;
import com.rocket.summer.framework.util.ClassUtils;
import com.rocket.summer.framework.util.ReflectionUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.*;

/**
 * Utility methods for AOP support code.
 * Mainly for internal use within Spring's AOP support.
 *
 * <p>See {@link org.springframework.aop.framework.AopProxyUtils} for a
 * collection of framework-specific AOP utility methods which depend
 * on internals of Spring's AOP framework implementation.
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @author Rob Harrop
 * @see org.springframework.aop.framework.AopProxyUtils
 */
public abstract class AopUtils {

    /**
     * Check whether the given object is a JDK dynamic proxy or a CGLIB proxy.
     * @param object the object to check
     * @see #isJdkDynamicProxy
     * @see #isCglibProxy
     */
    public static boolean isAopProxy(Object object) {
        return (object instanceof SpringProxy &&
                (Proxy.isProxyClass(object.getClass()) || isCglibProxyClass(object.getClass())));
    }

    /**
     * Check whether the given object is a JDK dynamic proxy.
     * @param object the object to check
     * @see java.lang.reflect.Proxy#isProxyClass
     */
    public static boolean isJdkDynamicProxy(Object object) {
        return (object instanceof SpringProxy && Proxy.isProxyClass(object.getClass()));
    }

    /**
     * Check whether the given object is a CGLIB proxy.
     * @param object the object to check
     */
    public static boolean isCglibProxy(Object object) {
        return (object instanceof SpringProxy && isCglibProxyClass(object.getClass()));
    }

    /**
     * Check whether the specified class is a CGLIB-generated class.
     * @param clazz the class to check
     */
    public static boolean isCglibProxyClass(Class clazz) {
        return (clazz != null && clazz.getName().indexOf(ClassUtils.CGLIB_CLASS_SEPARATOR) != -1);
    }

    /**
     * Determine the target class of the given bean instance,
     * which might be an AOP proxy.
     * <p>Returns the target class for an AOP proxy and the plain class else.
     * @param candidate the instance to check (might be an AOP proxy)
     * @return the target class (or the plain class of the given object as fallback)
     * @see org.springframework.aop.TargetClassAware#getTargetClass()
     */
    public static Class getTargetClass(Object candidate) {
        Assert.notNull(candidate, "Candidate object must not be null");
        if (candidate instanceof TargetClassAware) {
            return ((TargetClassAware) candidate).getTargetClass();
        }
        if (isCglibProxyClass(candidate.getClass())) {
            return candidate.getClass().getSuperclass();
        }
        return candidate.getClass();
    }

    /**
     * Determine whether the given method is an "equals" method.
     * @see java.lang.Object#equals
     */
    public static boolean isEqualsMethod(Method method) {
        return ReflectionUtils.isEqualsMethod(method);
    }

    /**
     * Determine whether the given method is a "hashCode" method.
     * @see java.lang.Object#hashCode
     */
    public static boolean isHashCodeMethod(Method method) {
        return ReflectionUtils.isHashCodeMethod(method);
    }

    /**
     * Determine whether the given method is a "toString" method.
     * @see java.lang.Object#toString()
     */
    public static boolean isToStringMethod(Method method) {
        return ReflectionUtils.isToStringMethod(method);
    }

    /**
     * Determine whether the given method is a "finalize" method.
     * @see java.lang.Object#finalize()
     */
    public static boolean isFinalizeMethod(Method method) {
        return (method != null && method.getName().equals("finalize") &&
                method.getParameterTypes().length == 0);
    }

    /**
     * Given a method, which may come from an interface, and a target class used
     * in the current AOP invocation, find the corresponding target method if there
     * is one. E.g. the method may be <code>IFoo.bar()</code> and the target class
     * may be <code>DefaultFoo</code>. In this case, the method may be
     * <code>DefaultFoo.bar()</code>. This enables attributes on that method to be found.
     * <p><b>NOTE:</b> In contrast to {@link org.springframework.util.ClassUtils#getMostSpecificMethod},
     * this method resolves Java 5 bridge methods in order to retrieve attributes
     * from the <i>original</i> method definition.
     * @param method the method to be invoked, which may come from an interface
     * @param targetClass the target class for the current invocation.
     * May be <code>null</code> or may not even implement the method.
     * @return the specific target method, or the original method if the
     * <code>targetClass</code> doesn't implement it or is <code>null</code>
     * @see org.springframework.util.ClassUtils#getMostSpecificMethod
     */
    public static Method getMostSpecificMethod(Method method, Class targetClass) {
        Method resolvedMethod = ClassUtils.getMostSpecificMethod(method, targetClass);
        // If we are dealing with method with generic parameters, find the original method.
        if (JdkVersion.isAtLeastJava15()) {
            resolvedMethod = BridgeMethodResolver.findBridgedMethod(resolvedMethod);
        }
        return resolvedMethod;
    }


    /**
     * Can the given pointcut apply at all on the given class?
     * <p>This is an important test as it can be used to optimize
     * out a pointcut for a class.
     * @param pc the static or dynamic pointcut to check
     * @param targetClass the class to test
     * @return whether the pointcut can apply on any method
     */
    public static boolean canApply(Pointcut pc, Class targetClass) {
        return canApply(pc, targetClass, false);
    }

    /**
     * Can the given pointcut apply at all on the given class?
     * <p>This is an important test as it can be used to optimize
     * out a pointcut for a class.
     * @param pc the static or dynamic pointcut to check
     * @param targetClass the class to test
     * @param hasIntroductions whether or not the advisor chain
     * for this bean includes any introductions
     * @return whether the pointcut can apply on any method
     */
    public static boolean canApply(Pointcut pc, Class targetClass, boolean hasIntroductions) {
        if (!pc.getClassFilter().matches(targetClass)) {
            return false;
        }

        MethodMatcher methodMatcher = pc.getMethodMatcher();
        IntroductionAwareMethodMatcher introductionAwareMethodMatcher = null;
        if (methodMatcher instanceof IntroductionAwareMethodMatcher) {
            introductionAwareMethodMatcher = (IntroductionAwareMethodMatcher) methodMatcher;
        }

        Set classes = new HashSet(ClassUtils.getAllInterfacesForClassAsSet(targetClass));
        classes.add(targetClass);
        for (Iterator it = classes.iterator(); it.hasNext();) {
            Class clazz = (Class) it.next();
            Method[] methods = clazz.getMethods();
            for (int j = 0; j < methods.length; j++) {
                if ((introductionAwareMethodMatcher != null &&
                        introductionAwareMethodMatcher.matches(methods[j], targetClass, hasIntroductions)) ||
                        methodMatcher.matches(methods[j], targetClass)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Can the given advisor apply at all on the given class?
     * This is an important test as it can be used to optimize
     * out a advisor for a class.
     * @param advisor the advisor to check
     * @param targetClass class we're testing
     * @return whether the pointcut can apply on any method
     */
    public static boolean canApply(Advisor advisor, Class targetClass) {
        return canApply(advisor, targetClass, false);
    }

    /**
     * Can the given advisor apply at all on the given class?
     * <p>This is an important test as it can be used to optimize out a advisor for a class.
     * This version also takes into account introductions (for IntroductionAwareMethodMatchers).
     * @param advisor the advisor to check
     * @param targetClass class we're testing
     * @param hasIntroductions whether or not the advisor chain for this bean includes
     * any introductions
     * @return whether the pointcut can apply on any method
     */
    public static boolean canApply(Advisor advisor, Class targetClass, boolean hasIntroductions) {
        if (advisor instanceof IntroductionAdvisor) {
            return ((IntroductionAdvisor) advisor).getClassFilter().matches(targetClass);
        }
        else if (advisor instanceof PointcutAdvisor) {
            PointcutAdvisor pca = (PointcutAdvisor) advisor;
            return canApply(pca.getPointcut(), targetClass, hasIntroductions);
        }
        else {
            // It doesn't have a pointcut so we assume it applies.
            return true;
        }
    }

    /**
     * Determine the sublist of the <code>candidateAdvisors</code> list
     * that is applicable to the given class.
     * @param candidateAdvisors the Advisors to evaluate
     * @param clazz the target class
     * @return sublist of Advisors that can apply to an object of the given class
     * (may be the incoming List as-is)
     */
    public static List findAdvisorsThatCanApply(List candidateAdvisors, Class clazz) {
        if (candidateAdvisors.isEmpty()) {
            return candidateAdvisors;
        }
        List eligibleAdvisors = new LinkedList();
        for (Iterator it = candidateAdvisors.iterator(); it.hasNext();) {
            Advisor candidate = (Advisor) it.next();
            if (candidate instanceof IntroductionAdvisor && canApply(candidate, clazz)) {
                eligibleAdvisors.add(candidate);
            }
        }
        boolean hasIntroductions = !eligibleAdvisors.isEmpty();
        for (Iterator it = candidateAdvisors.iterator(); it.hasNext();) {
            Advisor candidate = (Advisor) it.next();
            if (candidate instanceof IntroductionAdvisor) {
                // already processed
                continue;
            }
            if (canApply(candidate, clazz, hasIntroductions)) {
                eligibleAdvisors.add(candidate);
            }
        }
        return eligibleAdvisors;
    }


    /**
     * Invoke the given target via reflection, as part of an AOP method invocation.
     * @param target the target object
     * @param method the method to invoke
     * @param args the arguments for the method
     * @return the invocation result, if any
     * @throws Throwable if thrown by the target method
     * @throws org.springframework.aop.AopInvocationException in case of a reflection error
     */
    public static Object invokeJoinpointUsingReflection(Object target, Method method, Object[] args)
            throws Throwable {

        // Use reflection to invoke the method.
        try {
            ReflectionUtils.makeAccessible(method);
            return method.invoke(target, args);
        }
        catch (InvocationTargetException ex) {
            // Invoked method threw a checked exception.
            // We must rethrow it. The client won't see the interceptor.
            throw ex.getTargetException();
        }
        catch (IllegalArgumentException ex) {
            throw new AopInvocationException("AOP configuration seems to be invalid: tried calling method [" +
                    method + "] on target [" + target + "]", ex);
        }
        catch (IllegalAccessException ex) {
            throw new AopInvocationException("Could not access method [" + method + "]", ex);
        }
    }

}

