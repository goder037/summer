package com.rocket.summer.framework.context.annotation;

import com.rocket.summer.framework.beans.factory.BeanFactory;
import com.rocket.summer.framework.beans.factory.BeanFactoryAware;
import com.rocket.summer.framework.beans.factory.config.BeanDefinition;
import com.rocket.summer.framework.context.EnvironmentAware;
import com.rocket.summer.framework.core.annotation.AnnotationAttributes;
import com.rocket.summer.framework.core.env.Environment;
import com.rocket.summer.framework.core.type.AnnotationMetadata;
import com.rocket.summer.framework.jmx.export.annotation.AnnotationMBeanExporter;
import com.rocket.summer.framework.jmx.support.RegistrationPolicy;
import com.rocket.summer.framework.util.StringUtils;

import javax.management.MBeanServer;
import java.util.Map;

/**
 * {@code @Configuration} class that registers a {@link AnnotationMBeanExporter} bean.
 *
 * <p>This configuration class is automatically imported when using the
 * {@link EnableMBeanExport} annotation. See its javadoc for complete usage details.
 *
 * @author Phillip Webb
 * @author Chris Beams
 * @since 3.2
 * @see EnableMBeanExport
 */
@Configuration
public class MBeanExportConfiguration implements ImportAware, EnvironmentAware, BeanFactoryAware {

    private static final String MBEAN_EXPORTER_BEAN_NAME = "mbeanExporter";

    private AnnotationAttributes enableMBeanExport;

    private Environment environment;

    private BeanFactory beanFactory;


    @Override
    public void setImportMetadata(AnnotationMetadata importMetadata) {
        Map<String, Object> map = importMetadata.getAnnotationAttributes(EnableMBeanExport.class.getName());
        this.enableMBeanExport = AnnotationAttributes.fromMap(map);
        if (this.enableMBeanExport == null) {
            throw new IllegalArgumentException(
                    "@EnableMBeanExport is not present on importing class " + importMetadata.getClassName());
        }
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }


    @Bean(name = MBEAN_EXPORTER_BEAN_NAME)
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public AnnotationMBeanExporter mbeanExporter() {
        AnnotationMBeanExporter exporter = new AnnotationMBeanExporter();
        setupDomain(exporter);
        setupServer(exporter);
        setupRegistrationPolicy(exporter);
        return exporter;
    }

    private void setupDomain(AnnotationMBeanExporter exporter) {
        String defaultDomain = this.enableMBeanExport.getString("defaultDomain");
        if (defaultDomain != null && this.environment != null) {
            defaultDomain = this.environment.resolvePlaceholders(defaultDomain);
        }
        if (StringUtils.hasText(defaultDomain)) {
            exporter.setDefaultDomain(defaultDomain);
        }
    }

    private void setupServer(AnnotationMBeanExporter exporter) {
        String server = this.enableMBeanExport.getString("server");
        if (server != null && this.environment != null) {
            server = this.environment.resolvePlaceholders(server);
        }
        if (StringUtils.hasText(server)) {
            exporter.setServer(this.beanFactory.getBean(server, MBeanServer.class));
        }
        else {
            throw new RuntimeException();
        }
    }

    private void setupRegistrationPolicy(AnnotationMBeanExporter exporter) {
        RegistrationPolicy registrationPolicy = this.enableMBeanExport.getEnum("registration");
        exporter.setRegistrationPolicy(registrationPolicy);
    }




}

