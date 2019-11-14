package com.rocket.summer.framework.data.redis.serializer;

import java.nio.charset.Charset;

import com.rocket.summer.framework.context.BeansException;
import com.rocket.summer.framework.beans.TypeConverter;
import com.rocket.summer.framework.beans.factory.BeanFactory;
import com.rocket.summer.framework.beans.factory.BeanFactoryAware;
import com.rocket.summer.framework.beans.factory.config.ConfigurableBeanFactory;
import com.rocket.summer.framework.core.convert.ConversionService;
import com.rocket.summer.framework.core.convert.support.DefaultConversionService;
import com.rocket.summer.framework.util.Assert;

/**
 * Generic String to byte[] (and back) serializer. Relies on the Spring {@link ConversionService} to transform objects
 * into String and vice versa. The Strings are convert into bytes and vice-versa using the specified charset (by default
 * UTF-8). <b>Note:</b> The conversion service initialization happens automatically if the class is defined as a Spring
 * bean. <b>Note:</b> Does not handle nulls in any special way delegating everything to the container.
 *
 * @author Costin Leau
 * @author Christoph Strobl
 */
public class GenericToStringSerializer<T> implements RedisSerializer<T>, BeanFactoryAware {

    private final Charset charset;
    private Converter converter = new Converter(new DefaultConversionService());
    private Class<T> type;

    public GenericToStringSerializer(Class<T> type) {
        this(type, Charset.forName("UTF8"));
    }

    public GenericToStringSerializer(Class<T> type, Charset charset) {
        Assert.notNull(type, "tyoe must not be null!");
        this.type = type;
        this.charset = charset;
    }

    public void setConversionService(ConversionService conversionService) {
        Assert.notNull(conversionService, "non null conversion service required");
        converter = new Converter(conversionService);
    }

    public void setTypeConverter(TypeConverter typeConverter) {
        Assert.notNull(typeConverter, "non null type converter required");
        converter = new Converter(typeConverter);
    }

    public T deserialize(byte[] bytes) {
        if (bytes == null) {
            return null;
        }

        String string = new String(bytes, charset);
        return converter.convert(string, type);
    }

    public byte[] serialize(T object) {
        if (object == null) {
            return null;
        }
        String string = converter.convert(object, String.class);
        return string.getBytes(charset);
    }

    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        if (converter == null && beanFactory instanceof ConfigurableBeanFactory) {
            ConfigurableBeanFactory cFB = (ConfigurableBeanFactory) beanFactory;
            ConversionService conversionService = cFB.getConversionService();

            converter = (conversionService != null ? new Converter(conversionService) : new Converter(cFB.getTypeConverter()));
        }
    }

    private class Converter {
        private final ConversionService conversionService;
        private final TypeConverter typeConverter;

        public Converter(ConversionService conversionService) {
            this.conversionService = conversionService;
            this.typeConverter = null;
        }

        public Converter(TypeConverter typeConverter) {
            this.conversionService = null;
            this.typeConverter = typeConverter;
        }

        <E> E convert(Object value, Class<E> targetType) {
            if (conversionService != null) {
                return conversionService.convert(value, targetType);
            }
            return typeConverter.convertIfNecessary(value, targetType);
        }
    }
}

