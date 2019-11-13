package com.rocket.summer.framework.data.redis.connection.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

public class ByteArraySet implements Set<ByteArrayWrapper> {

    LinkedHashSet<ByteArrayWrapper> delegate;

    public ByteArraySet() {
        this.delegate = new LinkedHashSet<ByteArrayWrapper>();
    }

    public ByteArraySet(Collection<byte[]> values) {
        this();
        addAll(values);
    }

    public int size() {
        return delegate.size();
    }

    public boolean contains(Object o) {

        if (o instanceof byte[]) {
            return delegate.contains(new ByteArrayWrapper((byte[]) o));
        }
        return delegate.contains(o);
    }

    public boolean add(ByteArrayWrapper e) {
        return delegate.add(e);
    }

    public boolean add(byte[] e) {
        return delegate.add(new ByteArrayWrapper(e));
    }

    public boolean containsAll(Collection<?> c) {

        for (Object o : c) {
            if (o instanceof byte[]) {
                if (!contains(new ByteArrayWrapper((byte[]) o))) {
                    return false;
                }
            } else {
                if (!contains(o)) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean addAll(Collection<? extends ByteArrayWrapper> c) {
        return delegate.addAll(c);
    }

    public boolean addAll(Iterable<byte[]> c) {

        for (byte[] o : c) {
            add(o);
        }
        return true;
    }

    @Override
    public boolean isEmpty() {
        return delegate.isEmpty();
    }

    @Override
    public Iterator<ByteArrayWrapper> iterator() {
        return delegate.iterator();
    }

    @Override
    public Object[] toArray() {
        return delegate.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return delegate.toArray(a);
    }

    @Override
    public boolean remove(Object o) {

        if (o instanceof byte[]) {
            delegate.remove(new ByteArrayWrapper((byte[]) o));
        }
        return delegate.remove(o);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return delegate.retainAll(c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {

        for (Object o : c) {
            remove(o);
        }
        return true;
    }

    @Override
    public void clear() {
        delegate.clear();
    }

    public Set<byte[]> asRawSet() {

        Set<byte[]> result = new LinkedHashSet<byte[]>();
        for (ByteArrayWrapper wrapper : delegate) {
            result.add(wrapper.getArray());
        }
        return result;
    }

}

