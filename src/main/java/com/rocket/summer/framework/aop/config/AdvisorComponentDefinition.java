package com.rocket.summer.framework.aop.config;

import com.rocket.summer.framework.beans.MutablePropertyValues;
import com.rocket.summer.framework.beans.factory.config.BeanDefinition;
import com.rocket.summer.framework.beans.factory.config.BeanReference;
import com.rocket.summer.framework.beans.factory.parsing.AbstractComponentDefinition;
import com.rocket.summer.framework.util.Assert;

/**
 * {@link com.rocket.summer.framework.beans.factory.parsing.ComponentDefinition}
 * that bridges the gap between the advisor bean definition configured
 * by the {@code <aop:advisor>} tag and the component definition
 * infrastructure.
 *
 * @author Rob Harrop
 * @author Juergen Hoeller
 * @since 2.0
 */
public class AdvisorComponentDefinition extends AbstractComponentDefinition {

    private final String advisorBeanName;

    private final BeanDefinition advisorDefinition;

    private String description;

    private BeanReference[] beanReferences;

    private BeanDefinition[] beanDefinitions;


    public AdvisorComponentDefinition(String advisorBeanName, BeanDefinition advisorDefinition) {
        this(advisorBeanName, advisorDefinition, null);
    }

    public AdvisorComponentDefinition(
            String advisorBeanName, BeanDefinition advisorDefinition, BeanDefinition pointcutDefinition) {

        Assert.notNull(advisorBeanName, "'advisorBeanName' must not be null");
        Assert.notNull(advisorDefinition, "'advisorDefinition' must not be null");
        this.advisorBeanName = advisorBeanName;
        this.advisorDefinition = advisorDefinition;
        unwrapDefinitions(advisorDefinition, pointcutDefinition);
    }


    private void unwrapDefinitions(BeanDefinition advisorDefinition, BeanDefinition pointcutDefinition) {
        MutablePropertyValues pvs = advisorDefinition.getPropertyValues();
        BeanReference adviceReference = (BeanReference) pvs.getPropertyValue("adviceBeanName").getValue();

        if (pointcutDefinition != null) {
            this.beanReferences = new BeanReference[] {adviceReference};
            this.beanDefinitions = new BeanDefinition[] {advisorDefinition, pointcutDefinition};
            this.description = buildDescription(adviceReference, pointcutDefinition);
        }
        else {
            BeanReference pointcutReference = (BeanReference) pvs.getPropertyValue("pointcut").getValue();
            this.beanReferences = new BeanReference[] {adviceReference, pointcutReference};
            this.beanDefinitions = new BeanDefinition[] {advisorDefinition};
            this.description = buildDescription(adviceReference, pointcutReference);
        }
    }

    private String buildDescription(BeanReference adviceReference, BeanDefinition pointcutDefinition) {
        return new StringBuilder("Advisor <advice(ref)='").
                append(adviceReference.getBeanName()).append("', pointcut(expression)=[").
                append(pointcutDefinition.getPropertyValues().getPropertyValue("expression").getValue()).
                append("]>").toString();
    }

    private String buildDescription(BeanReference adviceReference, BeanReference pointcutReference) {
        return new StringBuilder("Advisor <advice(ref)='").
                append(adviceReference.getBeanName()).append("', pointcut(ref)='").
                append(pointcutReference.getBeanName()).append("'>").toString();
    }


    @Override
    public String getName() {
        return this.advisorBeanName;
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    @Override
    public BeanDefinition[] getBeanDefinitions() {
        return this.beanDefinitions;
    }

    @Override
    public BeanReference[] getBeanReferences() {
        return this.beanReferences;
    }

    @Override
    public Object getSource() {
        return this.advisorDefinition.getSource();
    }

}

