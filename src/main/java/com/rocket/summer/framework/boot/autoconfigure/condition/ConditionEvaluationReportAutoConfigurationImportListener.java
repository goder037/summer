package com.rocket.summer.framework.boot.autoconfigure.condition;

import com.rocket.summer.framework.beans.factory.BeanFactory;
import com.rocket.summer.framework.beans.factory.BeanFactoryAware;
import com.rocket.summer.framework.beans.factory.config.ConfigurableListableBeanFactory;
import com.rocket.summer.framework.boot.autoconfigure.AutoConfigurationImportEvent;
import com.rocket.summer.framework.boot.autoconfigure.AutoConfigurationImportListener;
import com.rocket.summer.framework.context.BeansException;

/**
 * {@link AutoConfigurationImportListener} to record results with the
 * {@link ConditionEvaluationReport}.
 *
 * @author Phillip Webb
 */
class ConditionEvaluationReportAutoConfigurationImportListener
        implements AutoConfigurationImportListener, BeanFactoryAware {

    private ConfigurableListableBeanFactory beanFactory;

    @Override
    public void onAutoConfigurationImportEvent(AutoConfigurationImportEvent event) {
        if (this.beanFactory != null) {
            ConditionEvaluationReport report = ConditionEvaluationReport
                    .get(this.beanFactory);
            report.recordEvaluationCandidates(event.getCandidateConfigurations());
            report.recordExclusions(event.getExclusions());
        }
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = (beanFactory instanceof ConfigurableListableBeanFactory)
                ? (ConfigurableListableBeanFactory) beanFactory : null;
    }

}
