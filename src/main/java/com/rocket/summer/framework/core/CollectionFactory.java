package com.rocket.summer.framework.core;

import com.rocket.summer.framework.util.LinkedMultiValueMap;
import com.rocket.summer.framework.util.MultiValueMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

public abstract class CollectionFactory {

    private static Class navigableSetClass = null;

    private static Class navigableMapClass = null;

    private static final Set<Class> approximableCollectionTypes = new HashSet<Class>(10);

    private static final Set<Class> approximableMapTypes = new HashSet<Class>(6);

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
     * Create the most appropriate collection for the given collection type.
     * <p>Creates an ArrayList, TreeSet or linked Set for a List, SortedSet
     * or Set, respectively.
     * @param collectionType the desired type of the target Collection
     * @param initialCapacity the initial capacity
     * @return the new Collection instance
     * @see java.util.ArrayList
     * @see java.util.TreeSet
     * @see java.util.LinkedHashSet
     */
    public static Collection createCollection(Class<?> collectionType, int initialCapacity) {
        if (collectionType.isInterface()) {
            if (List.class.equals(collectionType)) {
                return new ArrayList(initialCapacity);
            }
            else if (SortedSet.class.equals(collectionType) || collectionType.equals(navigableSetClass)) {
                return new TreeSet();
            }
            else if (Set.class.equals(collectionType) || Collection.class.equals(collectionType)) {
                return new LinkedHashSet(initialCapacity);
            }
            else {
                throw new IllegalArgumentException("Unsupported Collection interface: " + collectionType.getName());
            }
        }
        else {
            if (!Collection.class.isAssignableFrom(collectionType)) {
                throw new IllegalArgumentException("Unsupported Collection type: " + collectionType.getName());
            }
            try {
                return (Collection) collectionType.newInstance();
            }
            catch (Exception ex) {
                throw new IllegalArgumentException("Could not instantiate Collection type: " +
                        collectionType.getName(), ex);
            }
        }
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

    /**
     * Create the most approximate map for the given map.
     * <p>Creates a TreeMap or linked Map for a SortedMap or Map, respectively.
     * @param mapType the desired type of the target Map
     * @param initialCapacity the initial capacity
     * @return the new Map instance
     * @see java.util.TreeMap
     * @see java.util.LinkedHashMap
     */
    public static Map createMap(Class<?> mapType, int initialCapacity) {
        if (mapType.isInterface()) {
            if (Map.class.equals(mapType)) {
                return new LinkedHashMap(initialCapacity);
            }
            else if (SortedMap.class.equals(mapType) || mapType.equals(navigableMapClass)) {
                return new TreeMap();
            }
            else if (MultiValueMap.class.equals(mapType)) {
                return new LinkedMultiValueMap();
            }
            else {
                throw new IllegalArgumentException("Unsupported Map interface: " + mapType.getName());
            }
        }
        else {
            if (!Map.class.isAssignableFrom(mapType)) {
                throw new IllegalArgumentException("Unsupported Map type: " + mapType.getName());
            }
            try {
                return (Map) mapType.newInstance();
            }
            catch (Exception ex) {
                throw new IllegalArgumentException("Could not instantiate Map type: " +
                        mapType.getName(), ex);
            }
        }
    }
}
