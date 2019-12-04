package com.rocket.summer.framework.cglib.beans;

import java.util.*;

/* need it for class loading  */
public class FixedKeySet extends AbstractSet {
    private Set set;
    private int size;

    public FixedKeySet(String[] keys) {
        size = keys.length;
        set = Collections.unmodifiableSet(new HashSet(Arrays.asList(keys)));
    }

    public Iterator iterator() {
        return set.iterator();
    }

    public int size() {
        return size;
    }
}

