package com.rocket.summer.framework.aop.framework;

import com.rocket.summer.framework.objenesis.SpringObjenesis;
import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.Factory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Objenesis-based extension of {@link CglibAopProxy} to create proxy instances
 * without invoking the constructor of the class. Used by default as of Spring 4.
 *
 * @author Oliver Gierke
 * @author Juergen Hoeller
 * @since 4.0
 */
@SuppressWarnings("serial")
class ObjenesisCglibAopProxy extends CglibAopProxy {

    private static final Log logger = LogFactory.getLog(ObjenesisCglibAopProxy.class);

    private static final SpringObjenesis objenesis = new SpringObjenesis();


    /**
     * Create a new ObjenesisCglibAopProxy for the given AOP configuration.
     * @param config the AOP configuration as AdvisedSupport object
     */
    public ObjenesisCglibAopProxy(AdvisedSupport config) {
        super(config);
    }


    @Override
    protected Object createProxyClassAndInstance(Enhancer enhancer, Callback[] callbacks) {
        Class<?> proxyClass = enhancer.createClass();
        Object proxyInstance = null;

        if (objenesis.isWorthTrying()) {
            try {
                proxyInstance = objenesis.newInstance(proxyClass, enhancer.getUseCache());
            }
            catch (Throwable ex) {
                logger.debug("Unable to instantiate proxy using Objenesis, " +
                        "falling back to regular proxy construction", ex);
            }
        }

        if (proxyInstance == null) {
            // Regular instantiation via default constructor...
            try {
                proxyInstance = (this.constructorArgs != null ?
                        proxyClass.getConstructor(this.constructorArgTypes).newInstance(this.constructorArgs) :
                        proxyClass.newInstance());
            }
            catch (Throwable ex) {
                throw new AopConfigException("Unable to instantiate proxy using Objenesis, " +
                        "and regular proxy instantiation via default constructor fails as well", ex);
            }
        }

        ((Factory) proxyInstance).setCallbacks(callbacks);
        return proxyInstance;
    }

}