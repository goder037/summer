package com.rocket.summer.framework.aop.aspectj.autoproxy;

import java.util.ArrayList;
import java.util.List;

import com.rocket.summer.framework.aop.TargetSource;
import com.rocket.summer.framework.beans.factory.BeanFactory;
import com.rocket.summer.framework.beans.factory.FactoryBean;
import com.rocket.summer.framework.util.Assert;
import com.rocket.summer.framework.util.PatternMatchUtils;
import com.rocket.summer.framework.util.StringUtils;

/**
 * Auto proxy creator that identifies beans to proxy via a list of names.
 * Checks for direct, "xxx*", and "*xxx" matches.
 *
 * <p>For configuration details, see the javadoc of the parent class
 * AbstractAutoProxyCreator. Typically, you will specify a list of
 * interceptor names to apply to all identified beans, via the
 * "interceptorNames" property.
 *
 * @author Juergen Hoeller
 * @since 10.10.2003
 * @see #setBeanNames
 * @see #isMatch
 * @see #setInterceptorNames
 * @see AbstractAutoProxyCreator
 */
public class BeanNameAutoProxyCreator extends AbstractAutoProxyCreator {

    private List<String> beanNames;


    /**
     * Set the names of the beans that should automatically get wrapped with proxies.
     * A name can specify a prefix to match by ending with "*", e.g. "myBean,tx*"
     * will match the bean named "myBean" and all beans whose name start with "tx".
     * <p><b>NOTE:</b> In case of a FactoryBean, only the objects created by the
     * FactoryBean will get proxied. This default behavior applies as of Spring 2.0.
     * If you intend to proxy a FactoryBean instance itself (a rare use case, but
     * Spring 1.2's default behavior), specify the bean name of the FactoryBean
     * including the factory-bean prefix "&": e.g. "&myFactoryBean".
     * @see com.rocket.summer.framework.beans.factory.FactoryBean
     * @see com.rocket.summer.framework.beans.factory.BeanFactory#FACTORY_BEAN_PREFIX
     */
    public void setBeanNames(String... beanNames) {
        Assert.notEmpty(beanNames, "'beanNames' must not be empty");
        this.beanNames = new ArrayList<String>(beanNames.length);
        for (String mappedName : beanNames) {
            this.beanNames.add(StringUtils.trimWhitespace(mappedName));
        }
    }


    /**
     * Identify as bean to proxy if the bean name is in the configured list of names.
     */
    @Override
    protected Object[] getAdvicesAndAdvisorsForBean(Class<?> beanClass, String beanName, TargetSource targetSource) {
        if (this.beanNames != null) {
            for (String mappedName : this.beanNames) {
                if (FactoryBean.class.isAssignableFrom(beanClass)) {
                    if (!mappedName.startsWith(BeanFactory.FACTORY_BEAN_PREFIX)) {
                        continue;
                    }
                    mappedName = mappedName.substring(BeanFactory.FACTORY_BEAN_PREFIX.length());
                }
                if (isMatch(beanName, mappedName)) {
                    return PROXY_WITHOUT_ADDITIONAL_INTERCEPTORS;
                }
                BeanFactory beanFactory = getBeanFactory();
                if (beanFactory != null) {
                    String[] aliases = beanFactory.getAliases(beanName);
                    for (String alias : aliases) {
                        if (isMatch(alias, mappedName)) {
                            return PROXY_WITHOUT_ADDITIONAL_INTERCEPTORS;
                        }
                    }
                }
            }
        }
        return DO_NOT_PROXY;
    }

    /**
     * Return if the given bean name matches the mapped name.
     * <p>The default implementation checks for "xxx*", "*xxx" and "*xxx*" matches,
     * as well as direct equality. Can be overridden in subclasses.
     * @param beanName the bean name to check
     * @param mappedName the name in the configured list of names
     * @return if the names match
     * @see com.rocket.summer.framework.util.PatternMatchUtils#simpleMatch(String, String)
     */
    protected boolean isMatch(String beanName, String mappedName) {
        return PatternMatchUtils.simpleMatch(mappedName, beanName);
    }

}

