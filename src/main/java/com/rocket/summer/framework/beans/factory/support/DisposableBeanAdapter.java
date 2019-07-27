package com.rocket.summer.framework.beans.factory.support;

import com.rocket.summer.framework.beans.BeanUtils;
import com.rocket.summer.framework.beans.factory.DisposableBean;
import com.rocket.summer.framework.beans.factory.config.DestructionAwareBeanPostProcessor;
import com.rocket.summer.framework.util.Assert;
import com.rocket.summer.framework.util.ReflectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Adapter that implements the {@link DisposableBean} interface
 * performing various destruction steps on a given bean instance:
 * <ul>
 * <li>DestructionAwareBeanPostProcessors
 * <li>the bean implementing DisposableBean itself
 * <li>a custom destroy method specified on the bean definition
 * </ul>
 *
 * @author Juergen Hoeller
 * @since 2.0
 * @see AbstractBeanFactory
 * @see com.rocket.summer.framework.beans.factory.DisposableBean
 * @see com.rocket.summer.framework.beans.factory.config.DestructionAwareBeanPostProcessor
 * @see AbstractBeanDefinition#getDestroyMethodName()
 */
class DisposableBeanAdapter implements DisposableBean, Runnable, Serializable {

    private static final Log logger = LogFactory.getLog(DisposableBeanAdapter.class);

    private final Object bean;

    private final String beanName;

    private final boolean invokeDisposableBean;

    private final String destroyMethodName;

    private final boolean enforceDestroyMethod;

    private List beanPostProcessors;


    /**
     * Create a new DisposableBeanAdapter for the given bean.
     * @param bean the bean instance (never <code>null</code>)
     * @param beanName the name of the bean
     * @param beanDefinition the merged bean definition
     * @param postProcessors the List of BeanPostProcessors
     * (potentially DestructionAwareBeanPostProcessor), if any
     */
    public DisposableBeanAdapter(
            Object bean, String beanName, RootBeanDefinition beanDefinition, List postProcessors) {

        Assert.notNull(bean, "Bean must not be null");
        this.bean = bean;
        this.beanName = beanName;
        this.invokeDisposableBean = !beanDefinition.isExternallyManagedDestroyMethod("destroy");
        this.destroyMethodName =
                (!beanDefinition.isExternallyManagedDestroyMethod(beanDefinition.getDestroyMethodName()) ?
                        beanDefinition.getDestroyMethodName() : null);
        this.enforceDestroyMethod = beanDefinition.isEnforceDestroyMethod();
        this.beanPostProcessors = filterPostProcessors(postProcessors);
    }

    /**
     * Create a new DisposableBeanAdapter for the given bean.
     * @param bean the bean instance (never <code>null</code>)
     * @param beanName the name of the bean
     * @param invokeDisposableBean whether to actually invoke
     * DisposableBean's destroy method here
     * @param destroyMethodName the name of the custom destroy method
     * (<code>null</code> if there is none)
     * @param enforceDestroyMethod whether to the specified custom
     * destroy method (if any) has to be present on the bean object
     * @param postProcessors the List of DestructionAwareBeanPostProcessors, if any
     */
    private DisposableBeanAdapter(Object bean, String beanName, boolean invokeDisposableBean,
                                  String destroyMethodName, boolean enforceDestroyMethod, List postProcessors) {

        this.bean = bean;
        this.beanName = beanName;
        this.invokeDisposableBean = invokeDisposableBean;
        this.destroyMethodName = destroyMethodName;
        this.enforceDestroyMethod = enforceDestroyMethod;
        this.beanPostProcessors = postProcessors;
    }

    /**
     * Search for all DestructionAwareBeanPostProcessors in the List.
     * @param postProcessors the List to search
     * @return the filtered List of DestructionAwareBeanPostProcessors
     */
    private List filterPostProcessors(List postProcessors) {
        List filteredPostProcessors = null;
        if (postProcessors != null && !postProcessors.isEmpty()) {
            filteredPostProcessors = new ArrayList(postProcessors.size());
            for (Object postProcessor : postProcessors) {
                if (postProcessor instanceof DestructionAwareBeanPostProcessor) {
                    filteredPostProcessors.add(postProcessor);
                }
            }
        }
        return filteredPostProcessors;
    }


    public void run() {
        destroy();
    }

    public void destroy() {
        if (this.beanPostProcessors != null && !this.beanPostProcessors.isEmpty()) {
            for (int i = this.beanPostProcessors.size() - 1; i >= 0; i--) {
                ((DestructionAwareBeanPostProcessor) this.beanPostProcessors.get(i)).postProcessBeforeDestruction(
                        this.bean, this.beanName);
            }
        }

        boolean isDisposableBean = (this.bean instanceof DisposableBean);
        if (isDisposableBean && this.invokeDisposableBean) {
            if (logger.isDebugEnabled()) {
                logger.debug("Invoking destroy() on bean with name '" + this.beanName + "'");
            }
            try {
                ((DisposableBean) this.bean).destroy();
            }
            catch (Throwable ex) {
                String msg = "Invocation of destroy method failed on bean with name '" + this.beanName + "'";
                if (logger.isDebugEnabled()) {
                    logger.warn(msg, ex);
                }
                else {
                    logger.warn(msg + ": " + ex);
                }
            }
        }

        if (this.destroyMethodName != null && !(isDisposableBean && "destroy".equals(this.destroyMethodName))) {
            invokeCustomDestroyMethod();
        }
    }

    /**
     * Invoke the specified custom destroy method on the given bean.
     * <p>This implementation invokes a no-arg method if found, else checking
     * for a method with a single boolean argument (passing in "true",
     * assuming a "force" parameter), else logging an error.
     */
    private void invokeCustomDestroyMethod() {
        try {
            Method destroyMethod =
                    BeanUtils.findMethodWithMinimalParameters(this.bean.getClass(), this.destroyMethodName);
            if (destroyMethod == null) {
                if (this.enforceDestroyMethod) {
                    logger.warn("Couldn't find a destroy method named '" + this.destroyMethodName +
                            "' on bean with name '" + this.beanName + "'");
                }
            }

            else {
                Class[] paramTypes = destroyMethod.getParameterTypes();
                if (paramTypes.length > 1) {
                    logger.error("Method '" + this.destroyMethodName + "' of bean '" + this.beanName +
                            "' has more than one parameter - not supported as destroy method");
                }
                else if (paramTypes.length == 1 && !paramTypes[0].equals(boolean.class)) {
                    logger.error("Method '" + this.destroyMethodName + "' of bean '" + this.beanName +
                            "' has a non-boolean parameter - not supported as destroy method");
                }

                else {
                    Object[] args = new Object[paramTypes.length];
                    if (paramTypes.length == 1) {
                        args[0] = Boolean.TRUE;
                    }
                    if (logger.isDebugEnabled()) {
                        logger.debug("Invoking destroy method '" + this.destroyMethodName +
                                "' on bean with name '" + this.beanName + "'");
                    }
                    ReflectionUtils.makeAccessible(destroyMethod);
                    try {
                        destroyMethod.invoke(this.bean, args);
                    }
                    catch (InvocationTargetException ex) {
                        String msg = "Invocation of destroy method '" + this.destroyMethodName +
                                "' failed on bean with name '" + this.beanName + "'";
                        if (logger.isDebugEnabled()) {
                            logger.warn(msg, ex.getTargetException());
                        }
                        else {
                            logger.warn(msg + ": " + ex.getTargetException());
                        }
                    }
                    catch (Throwable ex) {
                        logger.error("Couldn't invoke destroy method '" + this.destroyMethodName +
                                "' on bean with name '" + this.beanName + "'", ex);
                    }
                }
            }
        }
        catch (IllegalArgumentException ex) {
            // thrown from findMethodWithMinimalParameters
            logger.error("Couldn't find a unique destroy method on bean with name '" +
                    this.beanName + ": " + ex.getMessage());
        }
    }


    /**
     * Serializes a copy of the state of this class,
     * filtering out non-serializable BeanPostProcessors.
     */
    protected Object writeReplace() {
        List serializablePostProcessors = null;
        if (this.beanPostProcessors != null) {
            serializablePostProcessors = new ArrayList();
            for (Object postProcessor : this.beanPostProcessors) {
                if (postProcessor instanceof Serializable) {
                    serializablePostProcessors.add(postProcessor);
                }
            }
        }
        return new DisposableBeanAdapter(this.bean, this.beanName, this.invokeDisposableBean,
                this.destroyMethodName, this.enforceDestroyMethod, serializablePostProcessors);
    }

}
