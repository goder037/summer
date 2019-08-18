package com.rocket.summer.framework.aop.aspectj.autoproxy;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.aopalliance.aop.Advice;
import org.aspectj.util.PartialOrder;
import org.aspectj.util.PartialOrder.PartialComparable;

import com.rocket.summer.framework.aop.Advisor;
import com.rocket.summer.framework.aop.aspectj.AbstractAspectJAdvice;
import com.rocket.summer.framework.aop.aspectj.AspectJPointcutAdvisor;
import com.rocket.summer.framework.aop.aspectj.AspectJProxyUtils;
import com.rocket.summer.framework.aop.framework.autoproxy.AbstractAdvisorAutoProxyCreator;
import com.rocket.summer.framework.aop.interceptor.ExposeInvocationInterceptor;
import com.rocket.summer.framework.core.Ordered;
import com.rocket.summer.framework.util.ClassUtils;

/**
 * {@link com.rocket.summer.framework.aop.framework.autoproxy.AbstractAdvisorAutoProxyCreator}
 * subclass that exposes AspectJ's invocation context and understands AspectJ's rules
 * for advice precedence when multiple pieces of advice come from the same aspect.
 *
 * @author Adrian Colyer
 * @author Juergen Hoeller
 * @author Ramnivas Laddad
 * @since 2.0
 */
@SuppressWarnings("serial")
public class AspectJAwareAdvisorAutoProxyCreator extends AbstractAdvisorAutoProxyCreator {

    private static final Comparator<Advisor> DEFAULT_PRECEDENCE_COMPARATOR = new AspectJPrecedenceComparator();


    /**
     * Sort the rest by AspectJ precedence. If two pieces of advice have
     * come from the same aspect they will have the same order.
     * Advice from the same aspect is then further ordered according to the
     * following rules:
     * <ul>
     * <li>if either of the pair is after advice, then the advice declared
     * last gets highest precedence (runs last)</li>
     * <li>otherwise the advice declared first gets highest precedence (runs first)</li>
     * </ul>
     * <p><b>Important:</b> Advisors are sorted in precedence order, from highest
     * precedence to lowest. "On the way in" to a join point, the highest precedence
     * advisor should run first. "On the way out" of a join point, the highest precedence
     * advisor should run last.
     */
    @Override
    @SuppressWarnings("unchecked")
    protected List<Advisor> sortAdvisors(List<Advisor> advisors) {
        List<PartiallyComparableAdvisorHolder> partiallyComparableAdvisors =
                new ArrayList<PartiallyComparableAdvisorHolder>(advisors.size());
        for (Advisor element : advisors) {
            partiallyComparableAdvisors.add(
                    new PartiallyComparableAdvisorHolder(element, DEFAULT_PRECEDENCE_COMPARATOR));
        }
        List<PartiallyComparableAdvisorHolder> sorted =
                PartialOrder.sort(partiallyComparableAdvisors);
        if (sorted != null) {
            List<Advisor> result = new ArrayList<Advisor>(advisors.size());
            for (PartiallyComparableAdvisorHolder pcAdvisor : sorted) {
                result.add(pcAdvisor.getAdvisor());
            }
            return result;
        }
        else {
            return super.sortAdvisors(advisors);
        }
    }

    /**
     * Adds an {@link ExposeInvocationInterceptor} to the beginning of the advice chain.
     * These additional advices are needed when using AspectJ expression pointcuts
     * and when using AspectJ-style advice.
     */
    @Override
    protected void extendAdvisors(List<Advisor> candidateAdvisors) {
        AspectJProxyUtils.makeAdvisorChainAspectJCapableIfNecessary(candidateAdvisors);
    }

    @Override
    protected boolean shouldSkip(Class<?> beanClass, String beanName) {
        // TODO: Consider optimization by caching the list of the aspect names
        List<Advisor> candidateAdvisors = findCandidateAdvisors();
        for (Advisor advisor : candidateAdvisors) {
            if (advisor instanceof AspectJPointcutAdvisor) {
                if (((AbstractAspectJAdvice) advisor.getAdvice()).getAspectName().equals(beanName)) {
                    return true;
                }
            }
        }
        return super.shouldSkip(beanClass, beanName);
    }


    /**
     * Implements AspectJ PartialComparable interface for defining partial orderings.
     */
    private static class PartiallyComparableAdvisorHolder implements PartialComparable {

        private final Advisor advisor;

        private final Comparator<Advisor> comparator;

        public PartiallyComparableAdvisorHolder(Advisor advisor, Comparator<Advisor> comparator) {
            this.advisor = advisor;
            this.comparator = comparator;
        }

        @Override
        public int compareTo(Object obj) {
            Advisor otherAdvisor = ((PartiallyComparableAdvisorHolder) obj).advisor;
            return this.comparator.compare(this.advisor, otherAdvisor);
        }

        @Override
        public int fallbackCompareTo(Object obj) {
            return 0;
        }

        public Advisor getAdvisor() {
            return this.advisor;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            Advice advice = this.advisor.getAdvice();
            sb.append(ClassUtils.getShortName(advice.getClass()));
            sb.append(": ");
            if (this.advisor instanceof Ordered) {
                sb.append("order ").append(((Ordered) this.advisor).getOrder()).append(", ");
            }
            if (advice instanceof AbstractAspectJAdvice) {
                AbstractAspectJAdvice ajAdvice = (AbstractAspectJAdvice) advice;
                sb.append(ajAdvice.getAspectName());
                sb.append(", declaration order ");
                sb.append(ajAdvice.getDeclarationOrder());
            }
            return sb.toString();
        }
    }

}

