package com.rocket.summer.framework.data.redis.core.mapping;

import com.rocket.summer.framework.data.annotation.Id;
import com.rocket.summer.framework.data.keyvalue.core.mapping.BasicKeyValuePersistentEntity;
import com.rocket.summer.framework.data.keyvalue.core.mapping.KeySpaceResolver;
import com.rocket.summer.framework.data.keyvalue.core.mapping.KeyValuePersistentProperty;
import com.rocket.summer.framework.data.mapping.model.MappingException;
import com.rocket.summer.framework.data.redis.core.TimeToLive;
import com.rocket.summer.framework.data.redis.core.TimeToLiveAccessor;
import com.rocket.summer.framework.data.util.TypeInformation;
import com.rocket.summer.framework.util.Assert;

/**
 * {@link RedisPersistentEntity} implementation.
 *
 * @author Christoph Strobl
 * @param <T>
 */
public class BasicRedisPersistentEntity<T> extends BasicKeyValuePersistentEntity<T>
        implements RedisPersistentEntity<T> {

    private TimeToLiveAccessor timeToLiveAccessor;

    /**
     * Creates new {@link BasicRedisPersistentEntity}.
     *
     * @param information must not be {@literal null}.
     * @param fallbackKeySpaceResolver can be {@literal null}.
     * @param timeToLiveResolver can be {@literal null}.
     */
    public BasicRedisPersistentEntity(TypeInformation<T> information, KeySpaceResolver fallbackKeySpaceResolver,
                                      TimeToLiveAccessor timeToLiveAccessor) {
        super(information, fallbackKeySpaceResolver);

        Assert.notNull(timeToLiveAccessor, "TimeToLiveAccessor must not be null");
        this.timeToLiveAccessor = timeToLiveAccessor;
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.redis.core.mapping.RedisPersistentEntity#getTimeToLiveAccessor()
     */
    @Override
    public TimeToLiveAccessor getTimeToLiveAccessor() {
        return this.timeToLiveAccessor;
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.redis.core.mapping.RedisPersistentEntity#hasExplictTimeToLiveProperty()
     */
    @Override
    public boolean hasExplictTimeToLiveProperty() {
        return getExplicitTimeToLiveProperty() != null;
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.redis.core.mapping.RedisPersistentEntity#getExplicitTimeToLiveProperty()
     */
    @Override
    public RedisPersistentProperty getExplicitTimeToLiveProperty() {
        return (RedisPersistentProperty) this.getPersistentProperty(TimeToLive.class);
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.mapping.model.BasicPersistentEntity#returnPropertyIfBetterIdPropertyCandidateOrNull(com.rocket.summer.framework.data.mapping.PersistentProperty)
     */
    @Override
    protected KeyValuePersistentProperty returnPropertyIfBetterIdPropertyCandidateOrNull(
            KeyValuePersistentProperty property) {

        Assert.notNull(property, "Property must not be null!");

        if (!property.isIdProperty()) {
            return null;
        }

        KeyValuePersistentProperty currentIdProperty = getIdProperty();
        boolean currentIdPropertyIsSet = currentIdProperty != null;

        if (!currentIdPropertyIsSet) {
            return property;
        }

        boolean currentIdPropertyIsExplicit = currentIdProperty.isAnnotationPresent(Id.class);
        boolean newIdPropertyIsExplicit = property.isAnnotationPresent(Id.class);

        if (currentIdPropertyIsExplicit && newIdPropertyIsExplicit) {
            throw new MappingException(String.format(
                    "Attempt to add explicit id property %s but already have an property %s registered "
                            + "as explicit id. Check your mapping configuration!",
                    property.getField(), currentIdProperty.getField()));
        }

        if (!currentIdPropertyIsExplicit && !newIdPropertyIsExplicit) {
            throw new MappingException(
                    String.format("Attempt to add id property %s but already have an property %s registered "
                            + "as id. Check your mapping configuration!", property.getField(), currentIdProperty.getField()));
        }

        if (newIdPropertyIsExplicit) {
            return property;
        }

        return null;
    }
}
