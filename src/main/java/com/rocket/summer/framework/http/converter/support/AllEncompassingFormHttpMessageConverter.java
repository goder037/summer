package com.rocket.summer.framework.http.converter.support;

import com.rocket.summer.framework.http.converter.FormHttpMessageConverter;
import com.rocket.summer.framework.http.converter.json.MappingJackson2HttpMessageConverter;
import com.rocket.summer.framework.http.converter.xml.Jaxb2RootElementHttpMessageConverter;
import com.rocket.summer.framework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;
import com.rocket.summer.framework.http.converter.xml.SourceHttpMessageConverter;
import com.rocket.summer.framework.util.ClassUtils;

import javax.xml.transform.Source;

/**
 * Extension of {@link com.rocket.summer.framework.http.converter.FormHttpMessageConverter},
 * adding support for XML and JSON-based parts.
 *
 * @author Rossen Stoyanchev
 * @author Juergen Hoeller
 * @since 3.2
 */
public class AllEncompassingFormHttpMessageConverter extends FormHttpMessageConverter {

    private static final boolean jaxb2Present =
            ClassUtils.isPresent("javax.xml.bind.Binder",
                    AllEncompassingFormHttpMessageConverter.class.getClassLoader());

    private static final boolean jackson2Present =
            ClassUtils.isPresent("com.fasterxml.jackson.databind.ObjectMapper",
                    AllEncompassingFormHttpMessageConverter.class.getClassLoader()) &&
                    ClassUtils.isPresent("com.fasterxml.jackson.core.JsonGenerator",
                            AllEncompassingFormHttpMessageConverter.class.getClassLoader());

    private static final boolean jackson2XmlPresent =
            ClassUtils.isPresent("com.fasterxml.jackson.dataformat.xml.XmlMapper",
                    AllEncompassingFormHttpMessageConverter.class.getClassLoader());



    public AllEncompassingFormHttpMessageConverter() {
        addPartConverter(new SourceHttpMessageConverter<Source>());

        if (jaxb2Present && !jackson2XmlPresent) {
            addPartConverter(new Jaxb2RootElementHttpMessageConverter());
        }

        if (jackson2Present) {
            addPartConverter(new MappingJackson2HttpMessageConverter());
        }

        if (jackson2XmlPresent) {
            addPartConverter(new MappingJackson2XmlHttpMessageConverter());
        }
    }

}

