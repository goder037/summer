package com.rocket.summer.framework.aop.framework.autoproxy.target;

import com.rocket.summer.framework.aop.target.AbstractBeanFactoryBasedTargetSource;
import com.rocket.summer.framework.aop.target.PrototypeTargetSource;
import com.rocket.summer.framework.aop.target.ThreadLocalTargetSource;

/**
 * Convenient TargetSourceCreator using bean name prefixes to create one of three
 * well-known TargetSource types:
 * <li>: CommonsPool2TargetSource
 * <li>% ThreadLocalTargetSource
 * <li>! PrototypeTargetSource
 *
 * @author Rod Johnson
 * @author Stephane Nicoll
 * @see com.rocket.summer.framework.aop.target.ThreadLocalTargetSource
 * @see com.rocket.summer.framework.aop.target.PrototypeTargetSource
 */
public class QuickTargetSourceCreator extends AbstractBeanFactoryBasedTargetSourceCreator {

    public static final String PREFIX_THREAD_LOCAL = "%";
    public static final String PREFIX_PROTOTYPE = "!";

    @Override
    protected final AbstractBeanFactoryBasedTargetSource createBeanFactoryBasedTargetSource(
            Class<?> beanClass, String beanName) {

        if (beanName.startsWith(PREFIX_THREAD_LOCAL)) {
            return new ThreadLocalTargetSource();
        }
        else if (beanName.startsWith(PREFIX_PROTOTYPE)) {
            return new PrototypeTargetSource();
        }
        else {
            // No match. Don't create a custom target source.
            return null;
        }
    }

}

