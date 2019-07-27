package com.rocket.summer.framework.aop.support;

import com.rocket.summer.framework.aop.ClassFilter;
import com.rocket.summer.framework.aop.DynamicIntroductionAdvice;
import com.rocket.summer.framework.aop.IntroductionAdvisor;
import com.rocket.summer.framework.aop.IntroductionInfo;
import com.rocket.summer.framework.core.Ordered;
import com.rocket.summer.framework.util.Assert;
import com.rocket.summer.framework.util.ClassUtils;
import org.aopalliance.aop.Advice;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Simple {@link com.rocket.summer.framework.aop.IntroductionAdvisor} implementation
 * that by default applies to any class.
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @since 11.11.2003
 */
public class DefaultIntroductionAdvisor implements IntroductionAdvisor, ClassFilter, Ordered, Serializable {

    private final Advice advice;

    private final Set interfaces = new HashSet();

    private int order = Integer.MAX_VALUE;


    /**
     * Create a DefaultIntroductionAdvisor for the given advice.
     * @param advice the Advice to apply (may implement the
     * {@link com.rocket.summer.framework.aop.IntroductionInfo} interface)
     * @see #addInterface
     */
    public DefaultIntroductionAdvisor(Advice advice) {
        this(advice, (advice instanceof IntroductionInfo ? (IntroductionInfo) advice : null));
    }

    /**
     * Create a DefaultIntroductionAdvisor for the given advice.
     * @param advice the Advice to apply
     * @param introductionInfo the IntroductionInfo that describes
     * the interface to introduce (may be <code>null</code>)
     */
    public DefaultIntroductionAdvisor(Advice advice, IntroductionInfo introductionInfo) {
        Assert.notNull(advice, "Advice must not be null");
        this.advice = advice;
        if (introductionInfo != null) {
            Class[] introducedInterfaces = introductionInfo.getInterfaces();
            if (introducedInterfaces.length == 0) {
                throw new IllegalArgumentException("IntroductionAdviceSupport implements no interfaces");
            }
            for (int i = 0; i < introducedInterfaces.length; i++) {
                addInterface(introducedInterfaces[i]);
            }
        }
    }

    /**
     * Create a DefaultIntroductionAdvisor for the given advice.
     * @param advice the Advice to apply
     * @param intf the interface to introduce
     */
    public DefaultIntroductionAdvisor(DynamicIntroductionAdvice advice, Class intf) {
        Assert.notNull(advice, "Advice must not be null");
        this.advice = advice;
        addInterface(intf);
    }


    /**
     * Add the specified interface to the list of interfaces to introduce.
     * @param intf the interface to introduce
     */
    public void addInterface(Class intf) {
        Assert.notNull(intf, "Interface must not be null");
        if (!intf.isInterface()) {
            throw new IllegalArgumentException("Specified class [" + intf.getName() + "] must be an interface");
        }
        this.interfaces.add(intf);
    }

    public Class[] getInterfaces() {
        return (Class[]) this.interfaces.toArray(new Class[this.interfaces.size()]);
    }

    public void validateInterfaces() throws IllegalArgumentException {
        for (Iterator it = this.interfaces.iterator(); it.hasNext();) {
            Class ifc = (Class) it.next();
            if (this.advice instanceof DynamicIntroductionAdvice &&
                    !((DynamicIntroductionAdvice) this.advice).implementsInterface(ifc)) {
                throw new IllegalArgumentException("DynamicIntroductionAdvice [" + this.advice + "] " +
                        "does not implement interface [" + ifc.getName() + "] specified for introduction");
            }
        }
    }


    public void setOrder(int order) {
        this.order = order;
    }

    public int getOrder() {
        return this.order;
    }


    public Advice getAdvice() {
        return this.advice;
    }

    public boolean isPerInstance() {
        return true;
    }

    public ClassFilter getClassFilter() {
        return this;
    }

    public boolean matches(Class clazz) {
        return true;
    }


    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof DefaultIntroductionAdvisor)) {
            return false;
        }
        DefaultIntroductionAdvisor otherAdvisor = (DefaultIntroductionAdvisor) other;
        return (this.advice.equals(otherAdvisor.advice) && this.interfaces.equals(otherAdvisor.interfaces));
    }

    public int hashCode() {
        return this.advice.hashCode() * 13 + this.interfaces.hashCode();
    }

    public String toString() {
        return ClassUtils.getShortName(getClass()) + ": advice [" + this.advice + "]; interfaces " +
                ClassUtils.classNamesToString(this.interfaces);
    }

}

