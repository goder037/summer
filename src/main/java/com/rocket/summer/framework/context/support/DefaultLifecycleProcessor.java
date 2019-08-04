package com.rocket.summer.framework.context.support;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.rocket.summer.framework.beans.factory.BeanFactory;
import com.rocket.summer.framework.beans.factory.BeanFactoryAware;
import com.rocket.summer.framework.beans.factory.BeanFactoryUtils;
import com.rocket.summer.framework.beans.factory.config.ConfigurableListableBeanFactory;
import com.rocket.summer.framework.context.ApplicationContextException;
import com.rocket.summer.framework.context.Lifecycle;
import com.rocket.summer.framework.context.LifecycleProcessor;
import com.rocket.summer.framework.context.Phased;
import com.rocket.summer.framework.context.SmartLifecycle;

/**
 * Default implementation of the {@link LifecycleProcessor} strategy.
 *
 * @author Mark Fisher
 * @author Juergen Hoeller
 * @since 3.0
 */
public class DefaultLifecycleProcessor implements LifecycleProcessor, BeanFactoryAware {

    private final Log logger = LogFactory.getLog(getClass());

    private volatile long timeoutPerShutdownPhase = 30000;

    private volatile boolean running;

    private volatile ConfigurableListableBeanFactory beanFactory;


    /**
     * Specify the maximum time allotted in milliseconds for the shutdown of
     * any phase (group of SmartLifecycle beans with the same 'phase' value).
     * <p>The default value is 30 seconds.
     */
    public void setTimeoutPerShutdownPhase(long timeoutPerShutdownPhase) {
        this.timeoutPerShutdownPhase = timeoutPerShutdownPhase;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) {
        if (!(beanFactory instanceof ConfigurableListableBeanFactory)) {
            throw new IllegalArgumentException(
                    "DefaultLifecycleProcessor requires a ConfigurableListableBeanFactory: " + beanFactory);
        }
        this.beanFactory = (ConfigurableListableBeanFactory) beanFactory;
    }


    // Lifecycle implementation

    /**
     * Start all registered beans that implement {@link Lifecycle} and are <i>not</i>
     * already running. Any bean that implements {@link SmartLifecycle} will be
     * started within its 'phase', and all phases will be ordered from lowest to
     * highest value. All beans that do not implement {@link SmartLifecycle} will be
     * started in the default phase 0. A bean declared as a dependency of another bean
     * will be started before the dependent bean regardless of the declared phase.
     */
    @Override
    public void start() {
        startBeans(false);
        this.running = true;
    }

    /**
     * Stop all registered beans that implement {@link Lifecycle} and <i>are</i>
     * currently running. Any bean that implements {@link SmartLifecycle} will be
     * stopped within its 'phase', and all phases will be ordered from highest to
     * lowest value. All beans that do not implement {@link SmartLifecycle} will be
     * stopped in the default phase 0. A bean declared as dependent on another bean
     * will be stopped before the dependency bean regardless of the declared phase.
     */
    @Override
    public void stop() {
        stopBeans();
        this.running = false;
    }

    @Override
    public void onRefresh() {
        startBeans(true);
        this.running = true;
    }

    @Override
    public void onClose() {
        stopBeans();
        this.running = false;
    }

    @Override
    public boolean isRunning() {
        return this.running;
    }


    // Internal helpers

    private void startBeans(boolean autoStartupOnly) {
        Map<String, Lifecycle> lifecycleBeans = getLifecycleBeans();
        Map<Integer, LifecycleGroup> phases = new HashMap<Integer, LifecycleGroup>();
        for (Map.Entry<String, ? extends Lifecycle> entry : lifecycleBeans.entrySet()) {
            Lifecycle bean = entry.getValue();
            if (!autoStartupOnly || (bean instanceof SmartLifecycle && ((SmartLifecycle) bean).isAutoStartup())) {
                int phase = getPhase(bean);
                LifecycleGroup group = phases.get(phase);
                if (group == null) {
                    group = new LifecycleGroup(phase, this.timeoutPerShutdownPhase, lifecycleBeans, autoStartupOnly);
                    phases.put(phase, group);
                }
                group.add(entry.getKey(), bean);
            }
        }
        if (!phases.isEmpty()) {
            List<Integer> keys = new ArrayList<Integer>(phases.keySet());
            Collections.sort(keys);
            for (Integer key : keys) {
                phases.get(key).start();
            }
        }
    }

    /**
     * Start the specified bean as part of the given set of Lifecycle beans,
     * making sure that any beans that it depends on are started first.
     * @param lifecycleBeans a Map with bean name as key and Lifecycle instance as value
     * @param beanName the name of the bean to start
     */
    private void doStart(Map<String, ? extends Lifecycle> lifecycleBeans, String beanName, boolean autoStartupOnly) {
        Lifecycle bean = lifecycleBeans.remove(beanName);
        if (bean != null && bean != this) {
            String[] dependenciesForBean = this.beanFactory.getDependenciesForBean(beanName);
            for (String dependency : dependenciesForBean) {
                doStart(lifecycleBeans, dependency, autoStartupOnly);
            }
            if (!bean.isRunning() &&
                    (!autoStartupOnly || !(bean instanceof SmartLifecycle) || ((SmartLifecycle) bean).isAutoStartup())) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Starting bean '" + beanName + "' of type [" + bean.getClass().getName() + "]");
                }
                try {
                    bean.start();
                }
                catch (Throwable ex) {
                    throw new ApplicationContextException("Failed to start bean '" + beanName + "'", ex);
                }
                if (logger.isDebugEnabled()) {
                    logger.debug("Successfully started bean '" + beanName + "'");
                }
            }
        }
    }

    private void stopBeans() {
        Map<String, Lifecycle> lifecycleBeans = getLifecycleBeans();
        Map<Integer, LifecycleGroup> phases = new HashMap<Integer, LifecycleGroup>();
        for (Map.Entry<String, Lifecycle> entry : lifecycleBeans.entrySet()) {
            Lifecycle bean = entry.getValue();
            int shutdownPhase = getPhase(bean);
            LifecycleGroup group = phases.get(shutdownPhase);
            if (group == null) {
                group = new LifecycleGroup(shutdownPhase, this.timeoutPerShutdownPhase, lifecycleBeans, false);
                phases.put(shutdownPhase, group);
            }
            group.add(entry.getKey(), bean);
        }
        if (!phases.isEmpty()) {
            List<Integer> keys = new ArrayList<Integer>(phases.keySet());
            Collections.sort(keys, Collections.reverseOrder());
            for (Integer key : keys) {
                phases.get(key).stop();
            }
        }
    }

    /**
     * Stop the specified bean as part of the given set of Lifecycle beans,
     * making sure that any beans that depends on it are stopped first.
     * @param lifecycleBeans a Map with bean name as key and Lifecycle instance as value
     * @param beanName the name of the bean to stop
     */
    private void doStop(Map<String, ? extends Lifecycle> lifecycleBeans, final String beanName,
                        final CountDownLatch latch, final Set<String> countDownBeanNames) {

        Lifecycle bean = lifecycleBeans.remove(beanName);
        if (bean != null) {
            String[] dependentBeans = this.beanFactory.getDependentBeans(beanName);
            for (String dependentBean : dependentBeans) {
                doStop(lifecycleBeans, dependentBean, latch, countDownBeanNames);
            }
            try {
                if (bean.isRunning()) {
                    if (bean instanceof SmartLifecycle) {
                        if (logger.isDebugEnabled()) {
                            logger.debug("Asking bean '" + beanName + "' of type [" +
                                    bean.getClass().getName() + "] to stop");
                        }
                        countDownBeanNames.add(beanName);
                        ((SmartLifecycle) bean).stop(new Runnable() {
                            @Override
                            public void run() {
                                latch.countDown();
                                countDownBeanNames.remove(beanName);
                                if (logger.isDebugEnabled()) {
                                    logger.debug("Bean '" + beanName + "' completed its stop procedure");
                                }
                            }
                        });
                    }
                    else {
                        if (logger.isDebugEnabled()) {
                            logger.debug("Stopping bean '" + beanName + "' of type [" +
                                    bean.getClass().getName() + "]");
                        }
                        bean.stop();
                        if (logger.isDebugEnabled()) {
                            logger.debug("Successfully stopped bean '" + beanName + "'");
                        }
                    }
                }
                else if (bean instanceof SmartLifecycle) {
                    // Don't wait for beans that aren't running...
                    latch.countDown();
                }
            }
            catch (Throwable ex) {
                if (logger.isWarnEnabled()) {
                    logger.warn("Failed to stop bean '" + beanName + "'", ex);
                }
            }
        }
    }


    // overridable hooks

    /**
     * Retrieve all applicable Lifecycle beans: all singletons that have already been created,
     * as well as all SmartLifecycle beans (even if they are marked as lazy-init).
     * @return the Map of applicable beans, with bean names as keys and bean instances as values
     */
    protected Map<String, Lifecycle> getLifecycleBeans() {
        Map<String, Lifecycle> beans = new LinkedHashMap<String, Lifecycle>();
        String[] beanNames = this.beanFactory.getBeanNamesForType(Lifecycle.class, false, false);
        for (String beanName : beanNames) {
            String beanNameToRegister = BeanFactoryUtils.transformedBeanName(beanName);
            boolean isFactoryBean = this.beanFactory.isFactoryBean(beanNameToRegister);
            String beanNameToCheck = (isFactoryBean ? BeanFactory.FACTORY_BEAN_PREFIX + beanName : beanName);
            if ((this.beanFactory.containsSingleton(beanNameToRegister) &&
                    (!isFactoryBean || Lifecycle.class.isAssignableFrom(this.beanFactory.getType(beanNameToCheck)))) ||
                    SmartLifecycle.class.isAssignableFrom(this.beanFactory.getType(beanNameToCheck))) {
                Lifecycle bean = this.beanFactory.getBean(beanNameToCheck, Lifecycle.class);
                if (bean != this) {
                    beans.put(beanNameToRegister, bean);
                }
            }
        }
        return beans;
    }

    /**
     * Determine the lifecycle phase of the given bean.
     * <p>The default implementation checks for the {@link Phased} interface, using
     * a default of 0 otherwise. Can be overridden to apply other/further policies.
     * @param bean the bean to introspect
     * @return the phase (an integer value)
     * @see Phased#getPhase()
     * @see SmartLifecycle
     */
    protected int getPhase(Lifecycle bean) {
        return (bean instanceof Phased ? ((Phased) bean).getPhase() : 0);
    }


    /**
     * Helper class for maintaining a group of Lifecycle beans that should be started
     * and stopped together based on their 'phase' value (or the default value of 0).
     */
    private class LifecycleGroup {

        private final int phase;

        private final long timeout;

        private final Map<String, ? extends Lifecycle> lifecycleBeans;

        private final boolean autoStartupOnly;

        private final List<LifecycleGroupMember> members = new ArrayList<LifecycleGroupMember>();

        private int smartMemberCount;

        public LifecycleGroup(
                int phase, long timeout, Map<String, ? extends Lifecycle> lifecycleBeans, boolean autoStartupOnly) {

            this.phase = phase;
            this.timeout = timeout;
            this.lifecycleBeans = lifecycleBeans;
            this.autoStartupOnly = autoStartupOnly;
        }

        public void add(String name, Lifecycle bean) {
            this.members.add(new LifecycleGroupMember(name, bean));
            if (bean instanceof SmartLifecycle) {
                this.smartMemberCount++;
            }
        }

        public void start() {
            if (this.members.isEmpty()) {
                return;
            }
            if (logger.isInfoEnabled()) {
                logger.info("Starting beans in phase " + this.phase);
            }
            Collections.sort(this.members);
            for (LifecycleGroupMember member : this.members) {
                if (this.lifecycleBeans.containsKey(member.name)) {
                    doStart(this.lifecycleBeans, member.name, this.autoStartupOnly);
                }
            }
        }

        public void stop() {
            if (this.members.isEmpty()) {
                return;
            }
            if (logger.isInfoEnabled()) {
                logger.info("Stopping beans in phase " + this.phase);
            }
            Collections.sort(this.members, Collections.reverseOrder());
            CountDownLatch latch = new CountDownLatch(this.smartMemberCount);
            Set<String> countDownBeanNames = Collections.synchronizedSet(new LinkedHashSet<String>());
            for (LifecycleGroupMember member : this.members) {
                if (this.lifecycleBeans.containsKey(member.name)) {
                    doStop(this.lifecycleBeans, member.name, latch, countDownBeanNames);
                }
                else if (member.bean instanceof SmartLifecycle) {
                    // Already removed: must have been a dependent bean from another phase
                    latch.countDown();
                }
            }
            try {
                latch.await(this.timeout, TimeUnit.MILLISECONDS);
                if (latch.getCount() > 0 && !countDownBeanNames.isEmpty() && logger.isWarnEnabled()) {
                    logger.warn("Failed to shut down " + countDownBeanNames.size() + " bean" +
                            (countDownBeanNames.size() > 1 ? "s" : "") + " with phase value " +
                            this.phase + " within timeout of " + this.timeout + ": " + countDownBeanNames);
                }
            }
            catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }
    }


    /**
     * Adapts the Comparable interface onto the lifecycle phase model.
     */
    private class LifecycleGroupMember implements Comparable<LifecycleGroupMember> {

        private final String name;

        private final Lifecycle bean;

        LifecycleGroupMember(String name, Lifecycle bean) {
            this.name = name;
            this.bean = bean;
        }

        @Override
        public int compareTo(LifecycleGroupMember other) {
            int thisPhase = getPhase(this.bean);
            int otherPhase = getPhase(other.bean);
            return (thisPhase == otherPhase ? 0 : (thisPhase < otherPhase) ? -1 : 1);
        }
    }

}

