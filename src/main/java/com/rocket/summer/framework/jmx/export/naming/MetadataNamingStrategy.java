package com.rocket.summer.framework.jmx.export.naming;

import java.util.Hashtable;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import com.rocket.summer.framework.aop.support.AopUtils;
import com.rocket.summer.framework.beans.factory.InitializingBean;
import com.rocket.summer.framework.jmx.export.metadata.JmxAttributeSource;
import com.rocket.summer.framework.jmx.export.metadata.ManagedResource;
import com.rocket.summer.framework.jmx.support.ObjectNameManager;
import com.rocket.summer.framework.util.Assert;
import com.rocket.summer.framework.util.ClassUtils;
import com.rocket.summer.framework.util.StringUtils;

/**
 * An implementation of the {@link ObjectNamingStrategy} interface
 * that reads the {@code ObjectName} from the source-level metadata.
 * Falls back to the bean key (bean name) if no {@code ObjectName}
 * can be found in source-level metadata.
 *
 * <p>Uses the {@link JmxAttributeSource} strategy interface, so that
 * metadata can be read using any supported implementation. Out of the box,
 * {@link com.rocket.summer.framework.jmx.export.annotation.AnnotationJmxAttributeSource}
 * introspects a well-defined set of Java 5 annotations that come with Spring.
 *
 * @author Rob Harrop
 * @author Juergen Hoeller
 * @since 1.2
 * @see ObjectNamingStrategy
 * @see com.rocket.summer.framework.jmx.export.annotation.AnnotationJmxAttributeSource
 */
public class MetadataNamingStrategy implements ObjectNamingStrategy, InitializingBean {

    /**
     * The {@code JmxAttributeSource} implementation to use for reading metadata.
     */
    private JmxAttributeSource attributeSource;

    private String defaultDomain;


    /**
     * Create a new {@code MetadataNamingStrategy} which needs to be
     * configured through the {@link #setAttributeSource} method.
     */
    public MetadataNamingStrategy() {
    }

    /**
     * Create a new {@code MetadataNamingStrategy} for the given
     * {@code JmxAttributeSource}.
     * @param attributeSource the JmxAttributeSource to use
     */
    public MetadataNamingStrategy(JmxAttributeSource attributeSource) {
        Assert.notNull(attributeSource, "JmxAttributeSource must not be null");
        this.attributeSource = attributeSource;
    }


    /**
     * Set the implementation of the {@code JmxAttributeSource} interface to use
     * when reading the source-level metadata.
     */
    public void setAttributeSource(JmxAttributeSource attributeSource) {
        Assert.notNull(attributeSource, "JmxAttributeSource must not be null");
        this.attributeSource = attributeSource;
    }

    /**
     * Specify the default domain to be used for generating ObjectNames
     * when no source-level metadata has been specified.
     * <p>The default is to use the domain specified in the bean name
     * (if the bean name follows the JMX ObjectName syntax); else,
     * the package name of the managed bean class.
     */
    public void setDefaultDomain(String defaultDomain) {
        this.defaultDomain = defaultDomain;
    }

    @Override
    public void afterPropertiesSet() {
        if (this.attributeSource == null) {
            throw new IllegalArgumentException("Property 'attributeSource' is required");
        }
    }


    /**
     * Reads the {@code ObjectName} from the source-level metadata associated
     * with the managed resource's {@code Class}.
     */
    @Override
    public ObjectName getObjectName(Object managedBean, String beanKey) throws MalformedObjectNameException {
        Class<?> managedClass = AopUtils.getTargetClass(managedBean);
        ManagedResource mr = this.attributeSource.getManagedResource(managedClass);

        // Check that an object name has been specified.
        if (mr != null && StringUtils.hasText(mr.getObjectName())) {
            return ObjectNameManager.getInstance(mr.getObjectName());
        }
        else {
            try {
                return ObjectNameManager.getInstance(beanKey);
            }
            catch (MalformedObjectNameException ex) {
                String domain = this.defaultDomain;
                if (domain == null) {
                    domain = ClassUtils.getPackageName(managedClass);
                }
                Hashtable<String, String> properties = new Hashtable<String, String>();
                properties.put("type", ClassUtils.getShortName(managedClass));
                properties.put("name", beanKey);
                return ObjectNameManager.getInstance(domain, properties);
            }
        }
    }

}

