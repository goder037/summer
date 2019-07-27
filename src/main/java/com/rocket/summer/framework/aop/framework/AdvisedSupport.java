package com.rocket.summer.framework.aop.framework;

import com.rocket.summer.framework.aop.*;
import com.rocket.summer.framework.aop.support.DefaultIntroductionAdvisor;
import com.rocket.summer.framework.aop.support.DefaultPointcutAdvisor;
import com.rocket.summer.framework.aop.target.EmptyTargetSource;
import com.rocket.summer.framework.aop.target.SingletonTargetSource;
import com.rocket.summer.framework.core.CollectionFactory;
import com.rocket.summer.framework.util.Assert;
import com.rocket.summer.framework.util.ClassUtils;
import com.rocket.summer.framework.util.ObjectUtils;
import org.aopalliance.aop.Advice;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Base class for AOP proxy configuration managers.
 * These are not themselves AOP proxies, but subclasses of this class are
 * normally factories from which AOP proxy instances are obtained directly.
 *
 * <p>This class frees subclasses of the housekeeping of Advices
 * and Advisors, but doesn't actually implement proxy creation
 * methods, which are provided by subclasses.
 *
 * <p>This class is serializable; subclasses need not be.
 * This class is used to hold snapshots of proxies.
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @see com.rocket.summer.framework.aop.framework.AopProxy
 */
public class AdvisedSupport extends ProxyConfig implements Advised {

    /** use serialVersionUID from Spring 2.0 for interoperability */
    private static final long serialVersionUID = 2651364800145442165L;


    /**
     * Canonical TargetSource when there's no target, and behavior is
     * supplied by the advisors.
     */
    public static final TargetSource EMPTY_TARGET_SOURCE = EmptyTargetSource.INSTANCE;


    /** Package-protected to allow direct access for efficiency */
    TargetSource targetSource = EMPTY_TARGET_SOURCE;

    /** Whether the Advisors are already filtered for the specific target class */
    private boolean preFiltered = false;

    /** The AdvisorChainFactory to use */
    AdvisorChainFactory advisorChainFactory = new DefaultAdvisorChainFactory();

    /** Cache with Method as key and advisor chain List as value */
    private transient Map methodCache;

    /**
     * Interfaces to be implemented by the proxy. Held in List to keep the order
     * of registration, to create JDK proxy with specified order of interfaces.
     */
    private List interfaces = new ArrayList();

    /**
     * List of Advisors. If an Advice is added, it will be wrapped
     * in an Advisor before being added to this List.
     */
    private List advisors = new LinkedList();

    /**
     * Array updated on changes to the advisors list, which is easier
     * to manipulate internally.
     */
    private Advisor[] advisorArray = new Advisor[0];


    /**
     * No-arg constructor for use as a JavaBean.
     */
    public AdvisedSupport() {
        initMethodCache();
    }

    /**
     * Create a AdvisedSupport instance with the given parameters.
     * @param interfaces the proxied interfaces
     */
    public AdvisedSupport(Class[] interfaces) {
        this();
        setInterfaces(interfaces);
    }

    /**
     * Initialize the method cache.
     */
    private void initMethodCache() {
        this.methodCache = CollectionFactory.createConcurrentMapIfPossible(32);
    }


    /**
     * Set the given object as target.
     * Will create a SingletonTargetSource for the object.
     * @see #setTargetSource
     * @see com.rocket.summer.framework.aop.target.SingletonTargetSource
     */
    public void setTarget(Object target) {
        setTargetSource(new SingletonTargetSource(target));
    }

    public void setTargetSource(TargetSource targetSource) {
        this.targetSource = (targetSource != null ? targetSource : EMPTY_TARGET_SOURCE);
    }

    public TargetSource getTargetSource() {
        return this.targetSource;
    }

    /**
     * Set a target class to be proxied, indicating that the proxy
     * should be castable to the given class.
     * <p>Internally, an {@link com.rocket.summer.framework.aop.target.EmptyTargetSource}
     * for the given target class will be used. The kind of proxy needed
     * will be determined on actual creation of the proxy.
     * <p>This is a replacement for setting a "targetSource" or "target",
     * for the case where we want a proxy based on a target class
     * (which can be an interface or a concrete class) without having
     * a fully capable TargetSource available.
     * @see #setTargetSource
     * @see #setTarget
     */
    public void setTargetClass(Class targetClass) {
        this.targetSource = EmptyTargetSource.forClass(targetClass);
    }

    public Class getTargetClass() {
        return this.targetSource.getTargetClass();
    }

    public void setPreFiltered(boolean preFiltered) {
        this.preFiltered = preFiltered;
    }

    public boolean isPreFiltered() {
        return this.preFiltered;
    }

    /**
     * Set the advisor chain factory to use.
     * <p>Default is a {@link DefaultAdvisorChainFactory}.
     */
    public void setAdvisorChainFactory(AdvisorChainFactory advisorChainFactory) {
        Assert.notNull(advisorChainFactory, "AdvisorChainFactory must not be null");
        this.advisorChainFactory = advisorChainFactory;
    }

    /**
     * Return the advisor chain factory to use (never <code>null</code>).
     */
    public AdvisorChainFactory getAdvisorChainFactory() {
        return this.advisorChainFactory;
    }


    /**
     * Set the interfaces to be proxied.
     */
    public void setInterfaces(Class[] interfaces) {
        Assert.notNull(interfaces, "Interfaces must not be null");
        this.interfaces.clear();
        for (int i = 0; i < interfaces.length; i++) {
            addInterface(interfaces[i]);
        }
    }

    /**
     * Add a new proxied interface.
     * @param intf the additional interface to proxy
     */
    public void addInterface(Class intf) {
        Assert.notNull(intf, "Interface must not be null");
        if (!intf.isInterface()) {
            throw new IllegalArgumentException("[" + intf.getName() + "] is not an interface");
        }
        if (!this.interfaces.contains(intf)) {
            this.interfaces.add(intf);
            adviceChanged();
        }
    }

    /**
     * Remove a proxied interface.
     * <p>Does nothing if the given interface isn't proxied.
     * @param intf the interface to remove from the proxy
     * @return <code>true</code> if the interface was removed; <code>false</code>
     * if the interface was not found and hence could not be removed
     */
    public boolean removeInterface(Class intf) {
        return this.interfaces.remove(intf);
    }

    public Class[] getProxiedInterfaces() {
        return (Class[]) this.interfaces.toArray(new Class[this.interfaces.size()]);
    }

    public boolean isInterfaceProxied(Class intf) {
        for (Iterator it = this.interfaces.iterator(); it.hasNext();) {
            Class proxyIntf = (Class) it.next();
            if (intf.isAssignableFrom(proxyIntf)) {
                return true;
            }
        }
        return false;
    }


    public final Advisor[] getAdvisors() {
        return this.advisorArray;
    }

    public void addAdvisor(Advisor advisor) {
        int pos = this.advisors.size();
        addAdvisor(pos, advisor);
    }

    public void addAdvisor(int pos, Advisor advisor) throws AopConfigException {
        if (advisor instanceof IntroductionAdvisor) {
            validateIntroductionAdvisor((IntroductionAdvisor) advisor);
        }
        addAdvisorInternal(pos, advisor);
    }

    public boolean removeAdvisor(Advisor advisor) {
        int index = indexOf(advisor);
        if (index == -1) {
            return false;
        }
        else {
            removeAdvisor(index);
            return true;
        }
    }

    public void removeAdvisor(int index) throws AopConfigException {
        if (isFrozen()) {
            throw new AopConfigException("Cannot remove Advisor: Configuration is frozen.");
        }
        if (index < 0 || index > this.advisors.size() - 1) {
            throw new AopConfigException("Advisor index " + index + " is out of bounds: " +
                    "This configuration only has " + this.advisors.size() + " advisors.");
        }

        Advisor advisor = (Advisor) this.advisors.get(index);
        if (advisor instanceof IntroductionAdvisor) {
            IntroductionAdvisor ia = (IntroductionAdvisor) advisor;
            // We need to remove introduction interfaces.
            for (int j = 0; j < ia.getInterfaces().length; j++) {
                removeInterface(ia.getInterfaces()[j]);
            }
        }

        this.advisors.remove(index);
        updateAdvisorArray();
        adviceChanged();
    }

    public int indexOf(Advisor advisor) {
        Assert.notNull(advisor, "Advisor must not be null");
        return this.advisors.indexOf(advisor);
    }

    public boolean replaceAdvisor(Advisor a, Advisor b) throws AopConfigException {
        Assert.notNull(a, "Advisor a must not be null");
        Assert.notNull(b, "Advisor b must not be null");
        int index = indexOf(a);
        if (index == -1) {
            return false;
        }
        removeAdvisor(index);
        addAdvisor(index, b);
        return true;
    }

    /**
     * Add all of the given advisors to this proxy configuration.
     * @param advisors the advisors to register
     */
    public void addAllAdvisors(Advisor[] advisors) {
        if (isFrozen()) {
            throw new AopConfigException("Cannot add advisor: Configuration is frozen.");
        }
        if (!ObjectUtils.isEmpty(advisors)) {
            for (int i = 0; i < advisors.length; i++) {
                Advisor advisor = advisors[i];
                if (advisor instanceof IntroductionAdvisor) {
                    validateIntroductionAdvisor((IntroductionAdvisor) advisor);
                }
                Assert.notNull(advisor, "Advisor must not be null");
                this.advisors.add(advisor);
            }
            updateAdvisorArray();
            adviceChanged();
        }
    }

    private void validateIntroductionAdvisor(IntroductionAdvisor advisor) {
        advisor.validateInterfaces();
        // If the advisor passed validation, we can make the change.
        Class[] ifcs = advisor.getInterfaces();
        for (int i = 0; i < ifcs.length; i++) {
            addInterface(ifcs[i]);
        }
    }

    private void addAdvisorInternal(int pos, Advisor advisor) throws AopConfigException {
        Assert.notNull(advisor, "Advisor must not be null");
        if (isFrozen()) {
            throw new AopConfigException("Cannot add advisor: Configuration is frozen.");
        }
        if (pos > this.advisors.size()) {
            throw new IllegalArgumentException(
                    "Illegal position " + pos + " in advisor list with size " + this.advisors.size());
        }
        this.advisors.add(pos, advisor);
        updateAdvisorArray();
        adviceChanged();
    }

    /**
     * Bring the array up to date with the list.
     */
    protected final void updateAdvisorArray() {
        this.advisorArray = (Advisor[]) this.advisors.toArray(new Advisor[this.advisors.size()]);
    }

    /**
     * Allows uncontrolled access to the {@link List} of {@link Advisor Advisors}.
     * <p>Use with care, and remember to {@link #updateAdvisorArray() refresh the advisor array}
     * and {@link #adviceChanged() fire advice changed events} when making any modifications.
     */
    protected final List getAdvisorsInternal() {
        return this.advisors;
    }


    public void addAdvice(Advice advice) throws AopConfigException {
        int pos = this.advisors.size();
        addAdvice(pos, advice);
    }

    /**
     * Cannot add introductions this way unless the advice implements IntroductionInfo.
     */
    public void addAdvice(int pos, Advice advice) throws AopConfigException {
        Assert.notNull(advice, "Advice must not be null");
        if (advice instanceof IntroductionInfo) {
            // We don't need an IntroductionAdvisor for this kind of introduction:
            // It's fully self-describing.
            addAdvisor(pos, new DefaultIntroductionAdvisor(advice, (IntroductionInfo) advice));
        }
        else if (advice instanceof DynamicIntroductionAdvice) {
            // We need an IntroductionAdvisor for this kind of introduction.
            throw new AopConfigException("DynamicIntroductionAdvice may only be added as part of IntroductionAdvisor");
        }
        else {
            addAdvisor(pos, new DefaultPointcutAdvisor(advice));
        }
    }

    public boolean removeAdvice(Advice advice) throws AopConfigException {
        int index = indexOf(advice);
        if (index == -1) {
            return false;
        }
        else {
            removeAdvisor(index);
            return true;
        }
    }

    public int indexOf(Advice advice) {
        Assert.notNull(advice, "Advice must not be null");
        for (int i = 0; i < this.advisors.size(); i++) {
            Advisor advisor = (Advisor) this.advisors.get(i);
            if (advisor.getAdvice() == advice) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Is the given advice included in any advisor within this proxy configuration?
     * @param advice the advice to check inclusion of
     * @return whether this advice instance is included
     */
    public boolean adviceIncluded(Advice advice) {
        Assert.notNull(advice, "Advice must not be null");
        for (int i = 0; i < this.advisors.size(); i++) {
            Advisor advisor = (Advisor) this.advisors.get(i);
            if (advisor.getAdvice() == advice) {
                return true;
            }
        }
        return false;
    }

    /**
     * Count advices of the given class.
     * @param adviceClass the advice class to check
     * @return the count of the interceptors of this class or subclasses
     */
    public int countAdvicesOfType(Class adviceClass) {
        Assert.notNull(adviceClass, "Advice class must not be null");
        int count = 0;
        for (int i = 0; i < this.advisors.size(); i++) {
            Advisor advisor = (Advisor) this.advisors.get(i);
            if (advisor.getAdvice() != null &&
                    adviceClass.isAssignableFrom(advisor.getAdvice().getClass())) {
                count++;
            }
        }
        return count;
    }


    /**
     * Determine a list of {@link org.aopalliance.intercept.MethodInterceptor} objects
     * for the given method, based on this configuration.
     * @param method the proxied method
     * @param targetClass the target class
     * @return List of MethodInterceptors (may also include InterceptorAndDynamicMethodMatchers)
     */
    public List getInterceptorsAndDynamicInterceptionAdvice(Method method, Class targetClass) {
        MethodCacheKey cacheKey = new MethodCacheKey(method);
        List cached = (List) this.methodCache.get(cacheKey);
        if (cached == null) {
            cached = this.advisorChainFactory.getInterceptorsAndDynamicInterceptionAdvice(
                    this, method, targetClass);
            this.methodCache.put(cacheKey, cached);
        }
        return cached;
    }

    /**
     * Invoked when advice has changed.
     */
    protected void adviceChanged() {
        synchronized (this.methodCache) {
            this.methodCache.clear();
        }
    }

    /**
     * Call this method on a new instance created by the no-arg constructor
     * to create an independent copy of the configuration from the given object.
     * @param other the AdvisedSupport object to copy configuration from
     */
    protected void copyConfigurationFrom(AdvisedSupport other) {
        copyConfigurationFrom(other, other.targetSource, new ArrayList(other.advisors));
    }

    /**
     * Copy the AOP configuration from the given AdvisedSupport object,
     * but allow substitution of a fresh TargetSource and a given interceptor chain.
     * @param other the AdvisedSupport object to take proxy configuration from
     * @param targetSource the new TargetSource
     * @param advisors the Advisors for the chain
     */
    protected void copyConfigurationFrom(AdvisedSupport other, TargetSource targetSource, List advisors) {
        copyFrom(other);
        this.targetSource = targetSource;
        this.advisorChainFactory = other.advisorChainFactory;
        this.interfaces = new ArrayList(other.interfaces);
        for (Iterator it = advisors.iterator(); it.hasNext();) {
            Advisor advisor = (Advisor) it.next();
            if (advisor instanceof IntroductionAdvisor) {
                validateIntroductionAdvisor((IntroductionAdvisor) advisor);
            }
            Assert.notNull(advisor, "Advisor must not be null");
            this.advisors.add(advisor);
        }
        updateAdvisorArray();
        adviceChanged();
    }

    /**
     * Build a configuration-only copy of this AdvisedSupport,
     * replacing the TargetSource
     */
    AdvisedSupport getConfigurationOnlyCopy() {
        AdvisedSupport copy = new AdvisedSupport();
        copy.copyFrom(this);
        copy.targetSource = EmptyTargetSource.forClass(getTargetClass(), getTargetSource().isStatic());
        copy.advisorChainFactory = this.advisorChainFactory;
        copy.interfaces = this.interfaces;
        copy.advisors = this.advisors;
        copy.updateAdvisorArray();
        return copy;
    }


    //---------------------------------------------------------------------
    // Serialization support
    //---------------------------------------------------------------------

    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        // Rely on default serialization; just initialize state after deserialization.
        ois.defaultReadObject();

        // Initialize transient fields.
        initMethodCache();
    }


    public String toProxyConfigString() {
        return toString();
    }

    /**
     * For debugging/diagnostic use.
     */
    public String toString() {
        StringBuffer sb = new StringBuffer(getClass().getName() + ": ");
        sb.append(this.interfaces.size()).append(" interfaces ");
        sb.append(ClassUtils.classNamesToString(this.interfaces)).append("; ");
        sb.append(this.advisors.size()).append(" advisors ");
        sb.append(this.advisors).append("; ");
        sb.append("targetSource [").append(this.targetSource).append("]; ");
        sb.append(super.toString());
        return sb.toString();
    }


    /**
     * Simple wrapper class around a Method. Used as the key when
     * caching methods, for efficient equals and hashCode comparisons.
     */
    private static class MethodCacheKey {

        private final Method method;

        private final int hashCode;

        public MethodCacheKey(Method method) {
            this.method = method;
            this.hashCode = method.hashCode();
        }

        public boolean equals(Object other) {
            if (other == this) {
                return true;
            }
            MethodCacheKey otherKey = (MethodCacheKey) other;
            return (this.method == otherKey.method);
        }

        public int hashCode() {
            return this.hashCode;
        }
    }

}

