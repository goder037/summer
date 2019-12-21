package com.rocket.summer.framework.context.expression;

import com.rocket.summer.framework.context.BeansException;
import com.rocket.summer.framework.beans.factory.BeanFactory;
import com.rocket.summer.framework.expression.AccessException;
import com.rocket.summer.framework.expression.BeanResolver;
import com.rocket.summer.framework.expression.EvaluationContext;
import com.rocket.summer.framework.util.Assert;
/**
 * EL bean resolver that operates against a Spring
 * {@link com.rocket.summer.framework.beans.factory.BeanFactory}.
 *
 * @author Juergen Hoeller
 * @since 3.0.4
 */
public class BeanFactoryResolver implements BeanResolver {

    private final BeanFactory beanFactory;


    /**
     * Create a new {@link BeanFactoryResolver} for the given factory.
     * @param beanFactory the {@link BeanFactory} to resolve bean names against
     */
    public BeanFactoryResolver(BeanFactory beanFactory) {
        Assert.notNull(beanFactory, "BeanFactory must not be null");
        this.beanFactory = beanFactory;
    }


    @Override
    public Object resolve(EvaluationContext context, String beanName) throws AccessException {
        try {
            return this.beanFactory.getBean(beanName);
        }
        catch (BeansException ex) {
            throw new AccessException("Could not resolve bean reference against BeanFactory", ex);
        }
    }

}

