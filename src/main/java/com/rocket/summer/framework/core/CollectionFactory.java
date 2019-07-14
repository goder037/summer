package com.rocket.summer.framework.core;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

public abstract class CollectionFactory {

    private static final Log logger = LogFactory.getLog(CollectionFactory.class);

    private static final Set approximableCollectionTypes = new HashSet(10);

    private static final Set approximableMapTypes = new HashSet(6);

    /**
     * Create a concurrent Map if possible: that is, if running on JDK >= 1.5
     * or if the backport-concurrent library is available. Prefers a JDK 1.5+
     * ConcurrentHashMap to its backport-concurrent equivalent. Falls back
     * to a plain synchronized HashMap if no concurrent Map is available.
     * @param initialCapacity the initial capacity of the Map
     * @return the new Map instance
     * @see java.util.concurrent.ConcurrentHashMap
     */
    public static Map createConcurrentMapIfPossible(int initialCapacity) {
        return new ConcurrentHashMap(initialCapacity);
    }

    /**
     * Create a copy-on-write Set (allowing for synchronization-less iteration),
     * requiring JDK >= 1.5 or the backport-concurrent library on the classpath.
     * Prefers a JDK 1.5+ CopyOnWriteArraySet to its backport-concurrent equivalent.
     * Throws an IllegalStateException if no copy-on-write Set is available.
     * @return the new Set instance
     * @throws IllegalStateException if no copy-on-write Set is available
     * @see java.util.concurrent.ConcurrentHashMap
     */
    public static Set createCopyOnWriteSet() {
        return new CopyOnWriteArraySet();
    }

    /**
     * Determine whether the given collection type is an approximable type,
     * i.e. a type that {@link #createApproximateCollection} can approximate.
     * @param collectionType the collection type to check
     * @return <code>true</code> if the type is approximable,
     * <code>false</code> if it is not
     */
    public static boolean isApproximableCollectionType(Class collectionType) {
        return (collectionType != null && approximableCollectionTypes.contains(collectionType));
    }

    /**
     * Determine whether the given map type is an approximable type,
     * i.e. a type that {@link #createApproximateMap} can approximate.
     * @param mapType the map type to check
     * @return <code>true</code> if the type is approximable,
     * <code>false</code> if it is not
     */
    public static boolean isApproximableMapType(Class mapType) {
        return (mapType != null && approximableMapTypes.contains(mapType));
    }

    /**
     * Create the most approximate map for the given map.
     * <p>Creates a TreeMap or linked Map for a SortedMap or Map, respectively.
     * @param map the original map object
     * @param initialCapacity the initial capacity
     * @return the new collection instance
     * @see java.util.TreeMap
     * @see java.util.LinkedHashMap
     */
    public static Map createApproximateMap(Object map, int initialCapacity) {
        if (map instanceof SortedMap) {
            return new TreeMap(((SortedMap) map).comparator());
        }
        else {
            return new LinkedHashMap(initialCapacity);
        }
    }

    /**
     * Create the most approximate collection for the given collection.
     * <p>Creates an ArrayList, TreeSet or linked Set for a List, SortedSet
     * or Set, respectively.
     * @param collection the original collection object
     * @param initialCapacity the initial capacity
     * @return the new collection instance
     * @see java.util.ArrayList
     * @see java.util.TreeSet
     * @see java.util.LinkedHashSet
     */
    public static Collection createApproximateCollection(Object collection, int initialCapacity) {
        if (collection instanceof LinkedList) {
            return new LinkedList();
        }
        else if (collection instanceof List) {
            return new ArrayList(initialCapacity);
        }
        else if (collection instanceof SortedSet) {
            return new TreeSet(((SortedSet) collection).comparator());
        }
        else {
            return new LinkedHashSet(initialCapacity);
        }
    }
}
