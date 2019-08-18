package com.rocket.summer.framework.aop.framework.autoproxy;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.rocket.summer.framework.aop.Advisor;
import com.rocket.summer.framework.beans.factory.BeanCreationException;
import com.rocket.summer.framework.beans.factory.BeanCurrentlyInCreationException;
import com.rocket.summer.framework.beans.factory.BeanFactoryUtils;
import com.rocket.summer.framework.beans.factory.config.ConfigurableListableBeanFactory;
import com.rocket.summer.framework.util.Assert;

/**
 * Helper for retrieving standard Spring Advisors from a BeanFactory,
 * for use with auto-proxying.
 *
 * @author Juergen Hoeller
 * @since 2.0.2
 * @see AbstractAdvisorAutoProxyCreator
 */
public class BeanFactoryAdvisorRetrievalHelper {

    private static final Log logger = LogFactory.getLog(BeanFactoryAdvisorRetrievalHelper.class);

    private final ConfigurableListableBeanFactory beanFactory;

    private volatile String[] cachedAdvisorBeanNames;


    /**
     * Create a new BeanFactoryAdvisorRetrievalHelper for the given BeanFactory.
     * @param beanFactory the ListableBeanFactory to scan
     */
    public BeanFactoryAdvisorRetrievalHelper(ConfigurableListableBeanFactory beanFactory) {
        Assert.notNull(beanFactory, "ListableBeanFactory must not be null");
        this.beanFactory = beanFactory;
    }


    /**
     * Find all eligible Advisor beans in the current bean factory,
     * ignoring FactoryBeans and excluding beans that are currently in creation.
     * @return the list of {@link com.rocket.summer.framework.aop.Advisor} beans
     * @see #isEligibleBean
     */
    public List<Advisor> findAdvisorBeans() {
        // Determine list of advisor bean names, if not cached already.
        String[] advisorNames = this.cachedAdvisorBeanNames;
        if (advisorNames == null) {
            // Do not initialize FactoryBeans here: We need to leave all regular beans
            // uninitialized to let the auto-proxy creator apply to them!
            advisorNames = BeanFactoryUtils.beanNamesForTypeIncludingAncestors(
                    this.beanFactory, Advisor.class, true, false);
            this.cachedAdvisorBeanNames = advisorNames;
        }
        if (advisorNames.length == 0) {
            return new ArrayList<Advisor>();
        }

        List<Advisor> advisors = new ArrayList<Advisor>();
        for (String name : advisorNames) {
            if (isEligibleBean(name)) {
                if (this.beanFactory.isCurrentlyInCreation(name)) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Skipping currently created advisor '" + name + "'");
                    }
                }
                else {
                    try {
                        advisors.add(this.beanFactory.getBean(name, Advisor.class));
                    }
                    catch (BeanCreationException ex) {
                        Throwable rootCause = ex.getMostSpecificCause();
                        if (rootCause instanceof BeanCurrentlyInCreationException) {
                            BeanCreationException bce = (BeanCreationException) rootCause;
                            if (this.beanFactory.isCurrentlyInCreation(bce.getBeanName())) {
                                if (logger.isDebugEnabled()) {
                                    logger.debug("Skipping advisor '" + name +
                                            "' with dependency on currently created bean: " + ex.getMessage());
                                }
                                // Ignore: indicates a reference back to the bean we're trying to advise.
                                // We want to find advisors other than the currently created bean itself.
                                continue;
                            }
                        }
                        throw ex;
                    }
                }
            }
        }
        return advisors;
    }

    /**
     * Determine whether the aspect bean with the given name is eligible.
     * <p>The default implementation always returns {@code true}.
     * @param beanName the name of the aspect bean
     * @return whether the bean is eligible
     */
    protected boolean isEligibleBean(String beanName) {
        return true;
    }

}

