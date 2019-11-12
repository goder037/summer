package com.rocket.summer.framework.context.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.rocket.summer.framework.jmx.export.annotation.AnnotationMBeanExporter;
import com.rocket.summer.framework.jmx.support.RegistrationPolicy;

/**
 * Enables default exporting of all standard {@code MBean}s from the Spring context, as
 * well as well all {@code @ManagedResource} annotated beans.
 *
 * <p>The resulting {@link com.rocket.summer.framework.jmx.export.MBeanExporter MBeanExporter}
 * bean is defined under the name "mbeanExporter". Alternatively, consider defining a
 * custom {@link AnnotationMBeanExporter} bean explicitly.
 *
 * <p>This annotation is modeled after and functionally equivalent to Spring XML's
 * {@code <context:mbean-export/>} element.
 *
 * @author Phillip Webb
 * @since 3.2
 * @see MBeanExportConfiguration
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(MBeanExportConfiguration.class)
public @interface EnableMBeanExport {

    /**
     * The default domain to use when generating JMX ObjectNames.
     */
    String defaultDomain() default "";

    /**
     * The bean name of the MBeanServer to which MBeans should be exported. Default is to
     * use the platform's default MBeanServer.
     */
    String server() default "";

    /**
     * The policy to use when attempting to register an MBean under an
     * {@link javax.management.ObjectName} that already exists. Defaults to
     * {@link RegistrationPolicy#FAIL_ON_EXISTING}.
     */
    RegistrationPolicy registration() default RegistrationPolicy.FAIL_ON_EXISTING;
}

