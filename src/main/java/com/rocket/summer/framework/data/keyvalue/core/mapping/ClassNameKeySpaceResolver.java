package com.rocket.summer.framework.data.keyvalue.core.mapping;

import com.rocket.summer.framework.util.Assert;
import com.rocket.summer.framework.util.ClassUtils;

/**
 * Most trivial implementation of {@link KeySpaceResolver} returning the {@link Class#getName()}.
 *
 * @author Christoph Strobl
 * @author Oliver Gierke
 */
enum ClassNameKeySpaceResolver implements KeySpaceResolver {

    INSTANCE;

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.keyvalue.core.KeySpaceResolver#resolveKeySpace(java.lang.Class)
     */
    @Override
    public String resolveKeySpace(Class<?> type) {

        Assert.notNull(type, "Type must not be null!");
        return ClassUtils.getUserClass(type).getName();
    }
}
