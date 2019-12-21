package com.rocket.summer.framework.boot.autoconfigure.jmx;

import java.util.Hashtable;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import com.rocket.summer.framework.context.BeansException;
import com.rocket.summer.framework.context.ApplicationContext;
import com.rocket.summer.framework.context.ApplicationContextAware;
import com.rocket.summer.framework.jmx.export.metadata.JmxAttributeSource;
import com.rocket.summer.framework.jmx.export.naming.MetadataNamingStrategy;
import com.rocket.summer.framework.jmx.support.ObjectNameManager;
import com.rocket.summer.framework.util.ObjectUtils;

/**
 * Extension of {@link MetadataNamingStrategy} that supports a parent
 * {@link ApplicationContext}.
 *
 * @author Dave Syer
 * @since 1.1.1
 */
public class ParentAwareNamingStrategy extends MetadataNamingStrategy
        implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    private boolean ensureUniqueRuntimeObjectNames;

    public ParentAwareNamingStrategy(JmxAttributeSource attributeSource) {
        super(attributeSource);
    }

    /**
     * Set if unique runtime object names should be ensured.
     * @param ensureUniqueRuntimeObjectNames {@code true} if unique names should ensured.
     */
    public void setEnsureUniqueRuntimeObjectNames(
            boolean ensureUniqueRuntimeObjectNames) {
        this.ensureUniqueRuntimeObjectNames = ensureUniqueRuntimeObjectNames;
    }

    @Override
    public ObjectName getObjectName(Object managedBean, String beanKey)
            throws MalformedObjectNameException {
        ObjectName name = super.getObjectName(managedBean, beanKey);
        Hashtable<String, String> properties = new Hashtable<String, String>();
        properties.putAll(name.getKeyPropertyList());
        if (this.ensureUniqueRuntimeObjectNames) {
            properties.put("identity", ObjectUtils.getIdentityHexString(managedBean));
        }
        else if (parentContextContainsSameBean(this.applicationContext, beanKey)) {
            properties.put("context",
                    ObjectUtils.getIdentityHexString(this.applicationContext));
        }
        return ObjectNameManager.getInstance(name.getDomain(), properties);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext)
            throws BeansException {
        this.applicationContext = applicationContext;
    }

    private boolean parentContextContainsSameBean(ApplicationContext context,
                                                  String beanKey) {
        if (context.getParent() == null) {
            return false;
        }
        try {
            this.applicationContext.getParent().getBean(beanKey);
            return true;
        }
        catch (BeansException ex) {
            return parentContextContainsSameBean(context.getParent(), beanKey);
        }
    }

}

