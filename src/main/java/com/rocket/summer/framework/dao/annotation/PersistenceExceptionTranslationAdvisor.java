package com.rocket.summer.framework.dao.annotation;

import java.lang.annotation.Annotation;

import org.aopalliance.aop.Advice;

import com.rocket.summer.framework.aop.Pointcut;
import com.rocket.summer.framework.aop.support.AbstractPointcutAdvisor;
import com.rocket.summer.framework.aop.support.annotation.AnnotationMatchingPointcut;
import com.rocket.summer.framework.beans.factory.ListableBeanFactory;
import com.rocket.summer.framework.dao.support.PersistenceExceptionTranslationInterceptor;
import com.rocket.summer.framework.dao.support.PersistenceExceptionTranslator;

/**
 * Spring AOP exception translation aspect for use at Repository or DAO layer level.
 * Translates native persistence exceptions into Spring's DataAccessException hierarchy,
 * based on a given PersistenceExceptionTranslator.
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @since 2.0
 * @see com.rocket.summer.framework.dao.DataAccessException
 * @see com.rocket.summer.framework.dao.support.PersistenceExceptionTranslator
 */
@SuppressWarnings("serial")
public class PersistenceExceptionTranslationAdvisor extends AbstractPointcutAdvisor {

    private final PersistenceExceptionTranslationInterceptor advice;

    private final AnnotationMatchingPointcut pointcut;


    /**
     * Create a new PersistenceExceptionTranslationAdvisor.
     * @param persistenceExceptionTranslator the PersistenceExceptionTranslator to use
     * @param repositoryAnnotationType the annotation type to check for
     */
    public PersistenceExceptionTranslationAdvisor(
            PersistenceExceptionTranslator persistenceExceptionTranslator,
            Class<? extends Annotation> repositoryAnnotationType) {

        this.advice = new PersistenceExceptionTranslationInterceptor(persistenceExceptionTranslator);
        this.pointcut = new AnnotationMatchingPointcut(repositoryAnnotationType, true);
    }

    /**
     * Create a new PersistenceExceptionTranslationAdvisor.
     * @param beanFactory the ListableBeanFactory to obtaining all
     * PersistenceExceptionTranslators from
     * @param repositoryAnnotationType the annotation type to check for
     */
    PersistenceExceptionTranslationAdvisor(
            ListableBeanFactory beanFactory, Class<? extends Annotation> repositoryAnnotationType) {

        this.advice = new PersistenceExceptionTranslationInterceptor(beanFactory);
        this.pointcut = new AnnotationMatchingPointcut(repositoryAnnotationType, true);
    }


    @Override
    public Advice getAdvice() {
        return this.advice;
    }

    @Override
    public Pointcut getPointcut() {
        return this.pointcut;
    }

}

