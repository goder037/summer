package com.rocket.summer.framework.aop.framework.autoproxy;

import java.util.List;

import com.rocket.summer.framework.aop.Advisor;
import com.rocket.summer.framework.aop.TargetSource;
import com.rocket.summer.framework.aop.support.AopUtils;
import com.rocket.summer.framework.beans.factory.BeanFactory;
import com.rocket.summer.framework.beans.factory.config.ConfigurableListableBeanFactory;
import com.rocket.summer.framework.core.annotation.AnnotationAwareOrderComparator;

/**
 * Generic auto proxy creator that builds AOP proxies for specific beans
 * based on detected Advisors for each bean.
 *
 * <p>Subclasses may override the {@link #findCandidateAdvisors()} method to
 * return a custom list of Advisors applying to any object. Subclasses can
 * also override the inherited {@link #shouldSkip} method to exclude certain
 * objects from auto-proxying.
 *
 * <p>Advisors or advices requiring ordering should implement the
 * {@link com.rocket.summer.framework.core.Ordered} interface. This class sorts
 * Advisors by Ordered order value. Advisors that don't implement the
 * Ordered interface will be considered as unordered; they will appear
 * at the end of the advisor chain in undefined order.
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @see #findCandidateAdvisors
 */
@SuppressWarnings("serial")
public abstract class AbstractAdvisorAutoProxyCreator extends AbstractAutoProxyCreator {

    private BeanFactoryAdvisorRetrievalHelper advisorRetrievalHelper;


    @Override
    public void setBeanFactory(BeanFactory beanFactory) {
        super.setBeanFactory(beanFactory);
        if (!(beanFactory instanceof ConfigurableListableBeanFactory)) {
            throw new IllegalArgumentException(
                    "AdvisorAutoProxyCreator requires a ConfigurableListableBeanFactory: " + beanFactory);
        }
        initBeanFactory((ConfigurableListableBeanFactory) beanFactory);
    }

    protected void initBeanFactory(ConfigurableListableBeanFactory beanFactory) {
        this.advisorRetrievalHelper = new BeanFactoryAdvisorRetrievalHelperAdapter(beanFactory);
    }


    @Override
    protected Object[] getAdvicesAndAdvisorsForBean(Class<?> beanClass, String beanName, TargetSource targetSource) {
        List<Advisor> advisors = findEligibleAdvisors(beanClass, beanName);
        if (advisors.isEmpty()) {
            return DO_NOT_PROXY;
        }
        return advisors.toArray();
    }

    /**
     * Find all eligible Advisors for auto-proxying this class.
     * @param beanClass the clazz to find advisors for
     * @param beanName the name of the currently proxied bean
     * @return the empty List, not {@code null},
     * if there are no pointcuts or interceptors
     * @see #findCandidateAdvisors
     * @see #sortAdvisors
     * @see #extendAdvisors
     */
    protected List<Advisor> findEligibleAdvisors(Class<?> beanClass, String beanName) {
        List<Advisor> candidateAdvisors = findCandidateAdvisors();
        List<Advisor> eligibleAdvisors = findAdvisorsThatCanApply(candidateAdvisors, beanClass, beanName);
        extendAdvisors(eligibleAdvisors);
        if (!eligibleAdvisors.isEmpty()) {
            eligibleAdvisors = sortAdvisors(eligibleAdvisors);
        }
        return eligibleAdvisors;
    }

    /**
     * Find all candidate Advisors to use in auto-proxying.
     * @return the List of candidate Advisors
     */
    protected List<Advisor> findCandidateAdvisors() {
        return this.advisorRetrievalHelper.findAdvisorBeans();
    }

    /**
     * Search the given candidate Advisors to find all Advisors that
     * can apply to the specified bean.
     * @param candidateAdvisors the candidate Advisors
     * @param beanClass the target's bean class
     * @param beanName the target's bean name
     * @return the List of applicable Advisors
     * @see ProxyCreationContext#getCurrentProxiedBeanName()
     */
    protected List<Advisor> findAdvisorsThatCanApply(
            List<Advisor> candidateAdvisors, Class<?> beanClass, String beanName) {

        ProxyCreationContext.setCurrentProxiedBeanName(beanName);
        try {
            return AopUtils.findAdvisorsThatCanApply(candidateAdvisors, beanClass);
        }
        finally {
            ProxyCreationContext.setCurrentProxiedBeanName(null);
        }
    }

    /**
     * Return whether the Advisor bean with the given name is eligible
     * for proxying in the first place.
     * @param beanName the name of the Advisor bean
     * @return whether the bean is eligible
     */
    protected boolean isEligibleAdvisorBean(String beanName) {
        return true;
    }

    /**
     * Sort advisors based on ordering. Subclasses may choose to override this
     * method to customize the sorting strategy.
     * @param advisors the source List of Advisors
     * @return the sorted List of Advisors
     * @see com.rocket.summer.framework.core.Ordered
     * @see com.rocket.summer.framework.core.annotation.Order
     * @see com.rocket.summer.framework.core.annotation.AnnotationAwareOrderComparator
     */
    protected List<Advisor> sortAdvisors(List<Advisor> advisors) {
        AnnotationAwareOrderComparator.sort(advisors);
        return advisors;
    }

    /**
     * Extension hook that subclasses can override to register additional Advisors,
     * given the sorted Advisors obtained to date.
     * <p>The default implementation is empty.
     * <p>Typically used to add Advisors that expose contextual information
     * required by some of the later advisors.
     * @param candidateAdvisors the Advisors that have already been identified as
     * applying to a given bean
     */
    protected void extendAdvisors(List<Advisor> candidateAdvisors) {
    }

    /**
     * This auto-proxy creator always returns pre-filtered Advisors.
     */
    @Override
    protected boolean advisorsPreFiltered() {
        return true;
    }


    /**
     * Subclass of BeanFactoryAdvisorRetrievalHelper that delegates to
     * surrounding AbstractAdvisorAutoProxyCreator facilities.
     */
    private class BeanFactoryAdvisorRetrievalHelperAdapter extends BeanFactoryAdvisorRetrievalHelper {

        public BeanFactoryAdvisorRetrievalHelperAdapter(ConfigurableListableBeanFactory beanFactory) {
            super(beanFactory);
        }

        @Override
        protected boolean isEligibleBean(String beanName) {
            return AbstractAdvisorAutoProxyCreator.this.isEligibleAdvisorBean(beanName);
        }
    }

}
