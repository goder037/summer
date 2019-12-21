package com.rocket.summer.framework.context.annotation;

import com.rocket.summer.framework.beans.factory.Aware;
import com.rocket.summer.framework.core.type.AnnotationMetadata;

/**
 * Interface to be implemented by any @{@link Configuration} class that wishes
 * to be injected with the {@link AnnotationMetadata} of the @{@code Configuration}
 * class that imported it. Useful in conjunction with annotations that
 * use @{@link Import} as a meta-annotation.
 *
 * @author Chris Beams
 * @since 3.1
 */
public interface ImportAware extends Aware {

    /**
     * Set the annotation metadata of the importing @{@code Configuration} class.
     */
    void setImportMetadata(AnnotationMetadata importMetadata);

}
