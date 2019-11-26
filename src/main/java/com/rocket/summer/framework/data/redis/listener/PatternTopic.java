package com.rocket.summer.framework.data.redis.listener;

import com.rocket.summer.framework.util.Assert;

/**
 * Pattern topic (matching multiple channels).
 *
 * @author Costin Leau
 */
public class PatternTopic implements Topic {

    private final String channelPattern;

    public PatternTopic(String pattern) {
        Assert.notNull(pattern, "a valid topic is required");
        this.channelPattern = pattern;
    }

    public String getTopic() {
        return channelPattern;
    }

    @Override
    public int hashCode() {
        return channelPattern.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof PatternTopic)) {
            return false;
        }
        PatternTopic other = (PatternTopic) obj;
        if (channelPattern == null) {
            if (other.channelPattern != null) {
                return false;
            }
        } else if (!channelPattern.equals(other.channelPattern)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return channelPattern;
    }
}

