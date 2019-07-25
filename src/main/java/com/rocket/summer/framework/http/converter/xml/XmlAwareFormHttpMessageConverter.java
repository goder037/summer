package com.rocket.summer.framework.http.converter.xml;

import com.rocket.summer.framework.http.converter.FormHttpMessageConverter;

/**
 * Extension of {@link org.springframework.http.converter.FormHttpMessageConverter},
 * adding support for XML-based parts through a {@link SourceHttpMessageConverter}.
 *
 * @author Juergen Hoeller
 * @since 3.0.3
 */
public class XmlAwareFormHttpMessageConverter extends FormHttpMessageConverter {

    public XmlAwareFormHttpMessageConverter() {
        super();
        addPartConverter(new SourceHttpMessageConverter());
    }

}
