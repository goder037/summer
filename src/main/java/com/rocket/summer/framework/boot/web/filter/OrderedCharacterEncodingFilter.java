package com.rocket.summer.framework.boot.web.filter;

import com.rocket.summer.framework.core.Ordered;
import com.rocket.summer.framework.web.filter.CharacterEncodingFilter;

/**
 * {@link CharacterEncodingFilter} that also implements {@link Ordered}.
 *
 * @author Phillip Webb
 * @since 1.4.0
 */
public class OrderedCharacterEncodingFilter extends CharacterEncodingFilter
        implements Ordered {

    private int order = Ordered.HIGHEST_PRECEDENCE;

    @Override
    public int getOrder() {
        return this.order;
    }

    /**
     * Set the order for this filter.
     * @param order the order to set
     */
    public void setOrder(int order) {
        this.order = order;
    }

}
