package com.rocket.summer.framework.aop.target;

/**
 * Simple {@link org.springframework.aop.TargetSource} implementation,
 * freshly obtaining the specified target bean from its containing
 * Spring {@link org.springframework.beans.factory.BeanFactory}.
 *
 * <p>Can obtain any kind of target bean: singleton, scoped, or prototype.
 * Typically used for scoped beans.
 *
 * @author Juergen Hoeller
 * @since 2.0.3
 */
public class SimpleBeanTargetSource extends AbstractBeanFactoryBasedTargetSource {

    public Object getTarget() throws Exception {
        return getBeanFactory().getBean(getTargetBeanName());
    }

}

