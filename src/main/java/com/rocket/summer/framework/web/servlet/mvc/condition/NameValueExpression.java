package com.rocket.summer.framework.web.servlet.mvc.condition;

import com.rocket.summer.framework.web.bind.annotation.RequestMapping;

/**
 * A contract for {@code "name!=value"} style expression used to specify request
 * parameters and request header conditions in {@code @RequestMapping}.
 *
 * @author Rossen Stoyanchev
 * @since 3.1
 *
 * @see RequestMapping#params()
 * @see RequestMapping#headers()
 */
public interface NameValueExpression<T> {

    String getName();

    T getValue();

    boolean isNegated();

}
