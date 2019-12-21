package com.rocket.summer.framework.cache.interceptor;

import com.rocket.summer.framework.aop.ClassFilter;
import com.rocket.summer.framework.aop.Pointcut;
import com.rocket.summer.framework.aop.support.AbstractBeanFactoryPointcutAdvisor;

/**
 * Advisor driven by a {@link CacheOperationSource}, used to include a
 * cache advice bean for methods that are cacheable.
 *
 * @author Costin Leau
 * @since 3.1
 */
@SuppressWarnings("serial")
public class BeanFactoryCacheOperationSourceAdvisor extends AbstractBeanFactoryPointcutAdvisor {

    private CacheOperationSource cacheOperationSource;

    private final CacheOperationSourcePointcut pointcut = new CacheOperationSourcePointcut() {
        @Override
        protected CacheOperationSource getCacheOperationSource() {
            return cacheOperationSource;
        }
    };


    /**
     * Set the cache operation attribute source which is used to find cache
     * attributes. This should usually be identical to the source reference
     * set on the cache interceptor itself.
     */
    public void setCacheOperationSource(CacheOperationSource cacheOperationSource) {
        this.cacheOperationSource = cacheOperationSource;
    }

    /**
     * Set the {@link ClassFilter} to use for this pointcut.
     * Default is {@link ClassFilter#TRUE}.
     */
    public void setClassFilter(ClassFilter classFilter) {
        this.pointcut.setClassFilter(classFilter);
    }

    @Override
    public Pointcut getPointcut() {
        return this.pointcut;
    }

}

