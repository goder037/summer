package com.rocket.summer.framework.jmx.export.metadata;

import com.rocket.summer.framework.jmx.JmxException;

/**
 * Thrown by the {@code JmxAttributeSource} when it encounters
 * incorrect metadata on a managed resource or one of its methods.
 *
 * @author Rob Harrop
 * @since 1.2
 * @see JmxAttributeSource
 * @see com.rocket.summer.framework.jmx.export.assembler.MetadataMBeanInfoAssembler
 */
@SuppressWarnings("serial")
public class InvalidMetadataException extends JmxException {

    /**
     * Create a new {@code InvalidMetadataException} with the supplied
     * error message.
     * @param msg the detail message
     */
    public InvalidMetadataException(String msg) {
        super(msg);
    }

}

