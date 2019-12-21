package com.rocket.summer.framework.boot.autoconfigure;

import java.io.IOException;
import java.util.List;

import com.rocket.summer.framework.beans.factory.BeanClassLoaderAware;
import com.rocket.summer.framework.context.annotation.Configuration;
import com.rocket.summer.framework.core.io.support.SpringFactoriesLoader;
import com.rocket.summer.framework.core.type.classreading.MetadataReader;
import com.rocket.summer.framework.core.type.classreading.MetadataReaderFactory;
import com.rocket.summer.framework.core.type.filter.TypeFilter;

/**
 * A {@link TypeFilter} implementation that matches registered auto-configuration classes.
 *
 * @author Stephane Nicoll
 * @since 1.5.0
 */
public class AutoConfigurationExcludeFilter implements TypeFilter, BeanClassLoaderAware {

    private ClassLoader beanClassLoader;

    private volatile List<String> autoConfigurations;

    @Override
    public void setBeanClassLoader(ClassLoader beanClassLoader) {
        this.beanClassLoader = beanClassLoader;
    }

    @Override
    public boolean match(MetadataReader metadataReader,
                         MetadataReaderFactory metadataReaderFactory) throws IOException {
        return isConfiguration(metadataReader) && isAutoConfiguration(metadataReader);
    }

    private boolean isConfiguration(MetadataReader metadataReader) {
        return metadataReader.getAnnotationMetadata()
                .isAnnotated(Configuration.class.getName());
    }

    private boolean isAutoConfiguration(MetadataReader metadataReader) {
        return getAutoConfigurations()
                .contains(metadataReader.getClassMetadata().getClassName());
    }

    protected List<String> getAutoConfigurations() {
        if (this.autoConfigurations == null) {
            this.autoConfigurations = SpringFactoriesLoader.loadFactoryNames(
                    EnableAutoConfiguration.class, this.beanClassLoader);
        }
        return this.autoConfigurations;
    }

}

