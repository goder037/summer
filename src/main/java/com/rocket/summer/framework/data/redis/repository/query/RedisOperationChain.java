package com.rocket.summer.framework.data.redis.repository.query;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import com.rocket.summer.framework.data.geo.Distance;
import com.rocket.summer.framework.data.geo.Point;
import com.rocket.summer.framework.util.ObjectUtils;

/**
 * Simple set of operations required to run queries against Redis.
 *
 * @author Christoph Strobl
 * @since 1.7
 */
public class RedisOperationChain {

    private Set<PathAndValue> sismember = new LinkedHashSet<PathAndValue>();
    private Set<PathAndValue> orSismember = new LinkedHashSet<PathAndValue>();
    private NearPath near;

    public void sismember(String path, Object value) {
        sismember(new PathAndValue(path, value));
    }

    public void sismember(PathAndValue pathAndValue) {
        sismember.add(pathAndValue);
    }

    public Set<PathAndValue> getSismember() {
        return sismember;
    }

    public void orSismember(String path, Object value) {
        orSismember(new PathAndValue(path, value));
    }

    public void orSismember(PathAndValue pathAndValue) {
        orSismember.add(pathAndValue);
    }

    public void orSismember(Collection<PathAndValue> next) {
        orSismember.addAll(next);
    }

    public Set<PathAndValue> getOrSismember() {
        return orSismember;
    }

    public void near(NearPath near) {
        this.near = near;
    }

    public NearPath getNear() {
        return near;
    }

    public static class PathAndValue {

        private final String path;
        private final Collection<Object> values;

        public PathAndValue(String path, Object singleValue) {

            this.path = path;
            this.values = Collections.singleton(singleValue);
        }

        public PathAndValue(String path, Collection<Object> values) {

            this.path = path;
            this.values = values != null ? values : Collections.emptySet();
        }

        public boolean isSingleValue() {
            return values.size() == 1;
        }

        public String getPath() {
            return path;
        }

        public Collection<Object> values() {
            return values;
        }

        public Object getFirstValue() {
            return values.isEmpty() ? null : values.iterator().next();
        }

        @Override
        public String toString() {
            return path + ":" + (isSingleValue() ? getFirstValue() : values);
        }

        @Override
        public int hashCode() {

            int result = ObjectUtils.nullSafeHashCode(path);
            result += ObjectUtils.nullSafeHashCode(values);
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (!(obj instanceof PathAndValue)) {
                return false;
            }
            PathAndValue that = (PathAndValue) obj;
            if (!ObjectUtils.nullSafeEquals(this.path, that.path)) {
                return false;
            }

            return ObjectUtils.nullSafeEquals(this.values, that.values);
        }

    }

    /**
     * @since 1.8
     * @author Christoph Strobl
     */
    public static class NearPath extends PathAndValue {

        public NearPath(String path, Point point, Distance distance) {
            super(path, Arrays.<Object> asList(point, distance));
        }

        public Point getPoint() {
            return (Point) getFirstValue();
        }

        public Distance getDistance() {

            Iterator<Object> it = values().iterator();
            it.next();
            return (Distance) it.next();
        }
    }
}
