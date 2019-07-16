package com.rocket.summer.framework.aop.support;

import com.rocket.summer.framework.aop.IntroductionInfo;
import com.rocket.summer.framework.util.ClassUtils;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.*;

/**
 * Support for implementations of {@link org.springframework.aop.IntroductionInfo}.
 *
 * <p>Allows subclasses to conveniently add all interfaces from a given object,
 * and to suppress interfaces that should not be added. Also allows for querying
 * all introduced interfaces.
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 */
public class IntroductionInfoSupport implements IntroductionInfo, Serializable {

    protected transient Log logger = LogFactory.getLog(getClass());

    /** Set of interface Classes */
    protected Set publishedInterfaces = new HashSet();

    /**
     * Methods that we know we should implement here: key is Method, value is Boolean.
     **/
    private transient Map rememberedMethods = createRememberedMethodMap();


    /**
     * Suppress the specified interface, which may have been autodetected
     * due to the delegate implementing it. Call this method to exclude
     * internal interfaces from being visible at the proxy level.
     * <p>Does nothing if the interface is not implemented by the delegate.
     * @param intf the interface to suppress
     */
    public void suppressInterface(Class intf) {
        this.publishedInterfaces.remove(intf);
    }

    public Class[] getInterfaces() {
        return (Class[]) this.publishedInterfaces.toArray(new Class[this.publishedInterfaces.size()]);
    }

    /**
     * Check whether the specified interfaces is a published introduction interface.
     * @param intf the interface to check
     * @return whether the interface is part of this introduction
     */
    public boolean implementsInterface(Class intf) {
        for (Iterator it = this.publishedInterfaces.iterator(); it.hasNext();) {
            Class pubIntf = (Class) it.next();
            if (intf.isInterface() && intf.isAssignableFrom(pubIntf)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Publish all interfaces that the given delegate implements at the proxy level.
     * @param delegate the delegate object
     */
    protected void implementInterfacesOnObject(Object delegate) {
        this.publishedInterfaces.addAll(ClassUtils.getAllInterfacesAsSet(delegate));
    }

    private Map createRememberedMethodMap() {
        return new IdentityHashMap(32);
    }

    /**
     * Is this method on an introduced interface?
     * @param mi the method invocation
     * @return whether the invoked method is on an introduced interface
     */
    protected final boolean isMethodOnIntroducedInterface(MethodInvocation mi) {
        Boolean rememberedResult = (Boolean) this.rememberedMethods.get(mi.getMethod());
        if (rememberedResult != null) {
            return rememberedResult.booleanValue();
        }
        else {
            // Work it out and cache it.
            boolean result = implementsInterface(mi.getMethod().getDeclaringClass());
            this.rememberedMethods.put(mi.getMethod(), (result ? Boolean.TRUE : Boolean.FALSE));
            return result;
        }
    }


    //---------------------------------------------------------------------
    // Serialization support
    //---------------------------------------------------------------------

    /**
     * This method is implemented only to restore the logger.
     * We don't make the logger static as that would mean that subclasses
     * would use this class's log category.
     */
    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        // Rely on default serialization; just initialize state after deserialization.
        ois.defaultReadObject();

        // Initialize transient fields.
        this.logger = LogFactory.getLog(getClass());
        this.rememberedMethods = createRememberedMethodMap();
    }

}

