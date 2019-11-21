package com.rocket.summer.framework.dao.annotation;

import java.lang.annotation.Annotation;

import com.rocket.summer.framework.aop.framework.autoproxy.AbstractBeanFactoryAwareAdvisingPostProcessor;
import com.rocket.summer.framework.beans.factory.BeanFactory;
import com.rocket.summer.framework.beans.factory.ListableBeanFactory;
import com.rocket.summer.framework.stereotype.Repository;
import com.rocket.summer.framework.util.Assert;

/**
 * Bean post-processor that automatically applies persistence exception translation to any
 * bean marked with Spring's @{@link com.rocket.summer.framework.stereotype.Repository Repository}
 * annotation, adding a corresponding {@link PersistenceExceptionTranslationAdvisor} to
 * the exposed proxy (either an existing AOP proxy or a newly generated proxy that
 * implements all of the target's interfaces).
 *
 * <p>Translates native resource exceptions to Spring's
 * {@link com.rocket.summer.framework.dao.DataAccessException DataAccessException} hierarchy.
 * Autodetects beans that implement the
 * {@link com.rocket.summer.framework.dao.support.PersistenceExceptionTranslator
 * PersistenceExceptionTranslator} interface, which are subsequently asked to translate
 * candidate exceptions.
 *

 * <p>All of Spring's applicable resource factories (e.g.
 * {@link com.rocket.summer.framework.orm.jpa.LocalContainerEntityManagerFactoryBean})
 * implement the {@code PersistenceExceptionTranslator} interface out of the box.
 * As a consequence, all that is usually needed to enable automatic exception
 * translation is marking all affected beans (such as Repositories or DAOs)
 * with the {@code @Repository} annotation, along with defining this post-processor
 * as a bean in the application context.
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @since 2.0
 * @see PersistenceExceptionTranslationAdvisor
 * @see com.rocket.summer.framework.stereotype.Repository
 * @see com.rocket.summer.framework.dao.DataAccessException
 * @see com.rocket.summer.framework.dao.support.PersistenceExceptionTranslator
 */
@SuppressWarnings("serial")
public class PersistenceExceptionTranslationPostProcessor extends AbstractBeanFactoryAwareAdvisingPostProcessor {

    private Class<? extends Annotation> repositoryAnnotationType = Repository.class;


    /**
     * Set the 'repository' annotation type.
     * The default repository annotation type is the {@link Repository} annotation.
     * <p>This setter property exists so that developers can provide their own
     * (non-Spring-specific) annotation type to indicate that a class has a
     * repository role.
     * @param repositoryAnnotationType the desired annotation type
     */
    public void setRepositoryAnnotationType(Class<? extends Annotation> repositoryAnnotationType) {
        Assert.notNull(repositoryAnnotationType, "'repositoryAnnotationType' must not be null");
        this.repositoryAnnotationType = repositoryAnnotationType;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) {
        super.setBeanFactory(beanFactory);

        if (!(beanFactory instanceof ListableBeanFactory)) {
            throw new IllegalArgumentException(
                    "Cannot use PersistenceExceptionTranslator autodetection without ListableBeanFactory");
        }
        this.advisor = new PersistenceExceptionTranslationAdvisor(
                (ListableBeanFactory) beanFactory, this.repositoryAnnotationType);
    }

}

