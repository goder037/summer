package com.rocket.summer.framework.data.redis.connection;

import com.rocket.summer.framework.data.redis.connection.RedisZSetCommands.Tuple;
import com.rocket.summer.framework.data.redis.connection.StringRedisConnection.StringTuple;

/**
 * Default implementation for {@link StringTuple} interface.
 *
 * @author Costin Leau
 */
public class DefaultStringTuple extends DefaultTuple implements StringTuple {

    private final String valueAsString;

    /**
     * Constructs a new <code>DefaultStringTuple</code> instance.
     *
     * @param value
     * @param score
     */
    public DefaultStringTuple(byte[] value, String valueAsString, Double score) {
        super(value, score);
        this.valueAsString = valueAsString;

    }

    /**
     * Constructs a new <code>DefaultStringTuple</code> instance.
     *
     * @param tuple
     * @param valueAsString
     */
    public DefaultStringTuple(Tuple tuple, String valueAsString) {
        super(tuple.getValue(), tuple.getScore());
        this.valueAsString = valueAsString;
    }

    public String getValueAsString() {
        return valueAsString;
    }

    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((valueAsString == null) ? 0 : valueAsString.hashCode());
        return result;
    }

    public boolean equals(Object obj) {
        if (super.equals(obj)) {
            if (!(obj instanceof DefaultStringTuple))
                return false;
            DefaultStringTuple other = (DefaultStringTuple) obj;
            if (valueAsString == null) {
                if (other.valueAsString != null)
                    return false;
            } else if (!valueAsString.equals(other.valueAsString))
                return false;
            return true;
        }
        return false;
    }

    public String toString() {
        return "DefaultStringTuple[value=" + getValueAsString() + ", score=" + getScore() + "]";
    }
}

