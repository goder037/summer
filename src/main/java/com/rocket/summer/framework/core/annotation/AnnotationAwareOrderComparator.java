package com.rocket.summer.framework.core.annotation;

import com.rocket.summer.framework.core.OrderComparator;
import com.rocket.summer.framework.core.Ordered;

/**
 * {@link java.util.Comparator} implementation that checks
 * {@link com.rocket.summer.framework.core.Ordered} as well as the
 * {@link Order} annotation, with an order value provided by an
 * <code>Ordered</code> instance overriding a statically defined
 * annotation value (if any).
 *
 * @author Juergen Hoeller
 * @since 2.0.1
 * @see com.rocket.summer.framework.core.Ordered
 * @see Order
 */
public class AnnotationAwareOrderComparator extends OrderComparator {

    @Override
    protected int getOrder(Object obj) {
        if (obj instanceof Ordered) {
            return ((Ordered) obj).getOrder();
        }
        if (obj != null) {
            Order order = obj.getClass().getAnnotation(Order.class);
            if (order != null) {
                return order.value();
            }
        }
        return Ordered.LOWEST_PRECEDENCE;
    }

}
