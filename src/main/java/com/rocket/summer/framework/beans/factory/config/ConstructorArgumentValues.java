package com.rocket.summer.framework.beans.factory.config;

import com.rocket.summer.framework.beans.BeanMetadataElement;
import com.rocket.summer.framework.beans.Mergeable;
import com.rocket.summer.framework.util.Assert;
import com.rocket.summer.framework.util.ClassUtils;
import com.rocket.summer.framework.util.ObjectUtils;

import java.util.*;

/**
 * Holder for constructor argument values, typically as part of a bean definition.
 *
 * <p>Supports values for a specific index in the constructor argument list
 * as well as for generic argument matches by type.
 *
 * @author Juergen Hoeller
 * @since 09.11.2003
 * @see BeanDefinition#getConstructorArgumentValues
 */
public class ConstructorArgumentValues {

    private final Map indexedArgumentValues = new HashMap();

    private final List genericArgumentValues = new LinkedList();


    /**
     * Create a new empty ConstructorArgumentValues object.
     */
    public ConstructorArgumentValues() {
    }

    /**
     * Deep copy constructor.
     * @param original the ConstructorArgumentValues to copy
     */
    public ConstructorArgumentValues(ConstructorArgumentValues original) {
        addArgumentValues(original);
    }

    /**
     * Copy all given argument values into this object, using separate holder
     * instances to keep the values independent from the original object.
     * <p>Note: Identical ValueHolder instances will only be registered once,
     * to allow for merging and re-merging of argument value definitions. Distinct
     * ValueHolder instances carrying the same content are of course allowed.
     */
    public void addArgumentValues(ConstructorArgumentValues other) {
        if (other != null) {
            for (Iterator it = other.indexedArgumentValues.entrySet().iterator(); it.hasNext();) {
                Map.Entry entry = (Map.Entry) it.next();
                ValueHolder valueHolder = (ValueHolder) entry.getValue();
                addOrMergeIndexedArgumentValue(entry.getKey(), valueHolder.copy());
            }
            for (Iterator it = other.genericArgumentValues.iterator(); it.hasNext();) {
                ValueHolder valueHolder = (ValueHolder) it.next();
                if (!this.genericArgumentValues.contains(valueHolder)) {
                    this.genericArgumentValues.add(valueHolder.copy());
                }
            }
        }
    }


    /**
     * Add argument value for the given index in the constructor argument list.
     * @param index the index in the constructor argument list
     * @param value the argument value
     */
    public void addIndexedArgumentValue(int index, Object value) {
        addIndexedArgumentValue(index, new ValueHolder(value));
    }

    /**
     * Add argument value for the given index in the constructor argument list.
     * @param index the index in the constructor argument list
     * @param value the argument value
     * @param type the type of the constructor argument
     */
    public void addIndexedArgumentValue(int index, Object value, String type) {
        addIndexedArgumentValue(index, new ValueHolder(value, type));
    }

    /**
     * Add argument value for the given index in the constructor argument list.
     * @param index the index in the constructor argument list
     * @param newValue the argument value in the form of a ValueHolder
     */
    public void addIndexedArgumentValue(int index, ValueHolder newValue) {
        Assert.isTrue(index >= 0, "Index must not be negative");
        Assert.notNull(newValue, "ValueHolder must not be null");
        addOrMergeIndexedArgumentValue(new Integer(index), newValue);
    }

    /**
     * Add argument value for the given index in the constructor argument list,
     * merging the new value (typically a collection) with the current value
     * if demanded: see {@link com.rocket.summer.framework.beans.Mergeable}.
     * @param key the index in the constructor argument list
     * @param newValue the argument value in the form of a ValueHolder
     */
    private void addOrMergeIndexedArgumentValue(Object key, ValueHolder newValue) {
        ValueHolder currentValue = (ValueHolder) this.indexedArgumentValues.get(key);
        if (currentValue != null && newValue.getValue() instanceof Mergeable) {
            Mergeable mergeable = (Mergeable) newValue.getValue();
            if (mergeable.isMergeEnabled()) {
                newValue.setValue(mergeable.merge(currentValue.getValue()));
            }
        }
        this.indexedArgumentValues.put(key, newValue);
    }

    /**
     * Get argument value for the given index in the constructor argument list.
     * @param index the index in the constructor argument list
     * @param requiredType the type to match (can be <code>null</code> to match
     * untyped values only)
     * @return the ValueHolder for the argument, or <code>null</code> if none set
     */
    public ValueHolder getIndexedArgumentValue(int index, Class requiredType) {
        Assert.isTrue(index >= 0, "Index must not be negative");
        ValueHolder valueHolder = (ValueHolder) this.indexedArgumentValues.get(new Integer(index));
        if (valueHolder != null) {
            if (valueHolder.getType() == null ||
                    (requiredType != null && requiredType.getName().equals(valueHolder.getType()))) {
                return valueHolder;
            }
        }
        return null;
    }

    /**
     * Return the map of indexed argument values.
     * @return unmodifiable Map with Integer index as key and ValueHolder as value
     * @see ValueHolder
     */
    public Map getIndexedArgumentValues() {
        return Collections.unmodifiableMap(this.indexedArgumentValues);
    }


    /**
     * Add generic argument value to be matched by type.
     * <p>Note: A single generic argument value will just be used once,
     * rather than matched multiple times (as of Spring 1.1).
     * @param value the argument value
     */
    public void addGenericArgumentValue(Object value) {
        this.genericArgumentValues.add(new ValueHolder(value));
    }

    /**
     * Add generic argument value to be matched by type.
     * <p>Note: A single generic argument value will just be used once,
     * rather than matched multiple times (as of Spring 1.1).
     * @param value the argument value
     * @param type the type of the constructor argument
     */
    public void addGenericArgumentValue(Object value, String type) {
        this.genericArgumentValues.add(new ValueHolder(value, type));
    }

    /**
     * Add generic argument value to be matched by type.
     * <p>Note: A single generic argument value will just be used once,
     * rather than matched multiple times (as of Spring 1.1).
     * @param newValue the argument value in the form of a ValueHolder
     * <p>Note: Identical ValueHolder instances will only be registered once,
     * to allow for merging and re-merging of argument value definitions. Distinct
     * ValueHolder instances carrying the same content are of course allowed.
     */
    public void addGenericArgumentValue(ValueHolder newValue) {
        Assert.notNull(newValue, "ValueHolder must not be null");
        if (!this.genericArgumentValues.contains(newValue)) {
            this.genericArgumentValues.add(newValue);
        }
    }

    /**
     * Look for a generic argument value that matches the given type.
     * @param requiredType the type to match (can be <code>null</code> to find
     * an arbitrary next generic argument value)
     * @return the ValueHolder for the argument, or <code>null</code> if none set
     */
    public ValueHolder getGenericArgumentValue(Class requiredType) {
        return getGenericArgumentValue(requiredType, null);
    }

    /**
     * Look for the next generic argument value that matches the given type,
     * ignoring argument values that have already been used in the current
     * resolution process.
     * @param requiredType the type to match (can be <code>null</code> to find
     * an arbitrary next generic argument value)
     * @param usedValueHolders a Set of ValueHolder objects that have already been used
     * in the current resolution process and should therefore not be returned again
     * @return the ValueHolder for the argument, or <code>null</code> if none found
     */
    public ValueHolder getGenericArgumentValue(Class requiredType, Set usedValueHolders) {
        for (Iterator it = this.genericArgumentValues.iterator(); it.hasNext();) {
            ValueHolder valueHolder = (ValueHolder) it.next();
            if (usedValueHolders == null || !usedValueHolders.contains(valueHolder)) {
                if (requiredType != null) {
                    // Check matching type.
                    if (valueHolder.getType() != null) {
                        if (valueHolder.getType().equals(requiredType.getName())) {
                            return valueHolder;
                        }
                    }
                    else if (ClassUtils.isAssignableValue(requiredType, valueHolder.getValue())) {
                        return valueHolder;
                    }
                }
                else {
                    // No required type specified -> consider untyped values only.
                    if (valueHolder.getType() == null) {
                        return valueHolder;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Return the list of generic argument values.
     * @return unmodifiable List of ValueHolders
     * @see ValueHolder
     */
    public List getGenericArgumentValues() {
        return Collections.unmodifiableList(this.genericArgumentValues);
    }


    /**
     * Look for an argument value that either corresponds to the given index
     * in the constructor argument list or generically matches by type.
     * @param index the index in the constructor argument list
     * @param requiredType the type to match
     * @return the ValueHolder for the argument, or <code>null</code> if none set
     */
    public ValueHolder getArgumentValue(int index, Class requiredType) {
        return getArgumentValue(index, requiredType, null);
    }

    /**
     * Look for an argument value that either corresponds to the given index
     * in the constructor argument list or generically matches by type.
     * @param index the index in the constructor argument list
     * @param requiredType the type to match (can be <code>null</code> to find
     * an untyped argument value)
     * @param usedValueHolders a Set of ValueHolder objects that have already
     * been used in the current resolution process and should therefore not
     * be returned again (allowing to return the next generic argument match
     * in case of multiple generic argument values of the same type)
     * @return the ValueHolder for the argument, or <code>null</code> if none set
     */
    public ValueHolder getArgumentValue(int index, Class requiredType, Set usedValueHolders) {
        Assert.isTrue(index >= 0, "Index must not be negative");
        ValueHolder valueHolder = getIndexedArgumentValue(index, requiredType);
        if (valueHolder == null) {
            valueHolder = getGenericArgumentValue(requiredType, usedValueHolders);
        }
        return valueHolder;
    }

    /**
     * Return the number of argument values held in this instance,
     * counting both indexed and generic argument values.
     */
    public int getArgumentCount() {
        return (this.indexedArgumentValues.size() + this.genericArgumentValues.size());
    }

    /**
     * Return if this holder does not contain any argument values,
     * neither indexed ones nor generic ones.
     */
    public boolean isEmpty() {
        return (this.indexedArgumentValues.isEmpty() && this.genericArgumentValues.isEmpty());
    }

    /**
     * Clear this holder, removing all argument values.
     */
    public void clear() {
        this.indexedArgumentValues.clear();
        this.genericArgumentValues.clear();
    }


    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof ConstructorArgumentValues)) {
            return false;
        }
        ConstructorArgumentValues that = (ConstructorArgumentValues) other;
        if (this.genericArgumentValues.size() != that.genericArgumentValues.size() ||
                this.indexedArgumentValues.size() != that.indexedArgumentValues.size()) {
            return false;
        }
        Iterator it1 = this.genericArgumentValues.iterator();
        Iterator it2 = that.genericArgumentValues.iterator();
        while (it1.hasNext() && it2.hasNext()) {
            ValueHolder vh1 = (ValueHolder) it1.next();
            ValueHolder vh2 = (ValueHolder) it2.next();
            if (!vh1.contentEquals(vh2)) {
                return false;
            }
        }
        for (Iterator it = this.indexedArgumentValues.entrySet().iterator(); it.hasNext();) {
            Map.Entry entry = (Map.Entry) it.next();
            ValueHolder vh1 = (ValueHolder) entry.getValue();
            ValueHolder vh2 = (ValueHolder) that.indexedArgumentValues.get(entry.getKey());
            if (!vh1.contentEquals(vh2)) {
                return false;
            }
        }
        return true;
    }

    public int hashCode() {
        int hashCode = 7;
        for (Iterator it = this.genericArgumentValues.iterator(); it.hasNext();) {
            ValueHolder valueHolder = (ValueHolder) it.next();
            hashCode = 31 * hashCode + valueHolder.contentHashCode();
        }
        hashCode = 29 * hashCode;
        for (Iterator it = this.indexedArgumentValues.entrySet().iterator(); it.hasNext();) {
            Map.Entry entry = (Map.Entry) it.next();
            Integer key = (Integer) entry.getKey();
            ValueHolder value = (ValueHolder) entry.getValue();
            hashCode = 31 * hashCode + (value.contentHashCode() ^ key.hashCode());
        }
        return hashCode;
    }


    /**
     * Holder for a constructor argument value, with an optional type
     * attribute indicating the target type of the actual constructor argument.
     */
    public static class ValueHolder implements BeanMetadataElement {

        private Object value;

        private String type;

        private Object source;

        private boolean converted = false;

        private Object convertedValue;

        /**
         * Create a new ValueHolder for the given value.
         * @param value the argument value
         */
        public ValueHolder(Object value) {
            this.value = value;
        }

        /**
         * Create a new ValueHolder for the given value and type.
         * @param value the argument value
         * @param type the type of the constructor argument
         */
        public ValueHolder(Object value, String type) {
            this.value = value;
            this.type = type;
        }

        /**
         * Set the value for the constructor argument.
         * Only necessary for manipulating a registered value,
         * for example in BeanFactoryPostProcessors.
         * @see PropertyPlaceholderConfigurer
         */
        public void setValue(Object value) {
            this.value = value;
        }

        /**
         * Return the value for the constructor argument.
         */
        public Object getValue() {
            return this.value;
        }

        /**
         * Set the type of the constructor argument.
         * Only necessary for manipulating a registered value,
         * for example in BeanFactoryPostProcessors.
         * @see PropertyPlaceholderConfigurer
         */
        public void setType(String type) {
            this.type = type;
        }

        /**
         * Return the type of the constructor argument.
         */
        public String getType() {
            return this.type;
        }

        /**
         * Set the configuration source <code>Object</code> for this metadata element.
         * <p>The exact type of the object will depend on the configuration mechanism used.
         */
        public void setSource(Object source) {
            this.source = source;
        }

        public Object getSource() {
            return this.source;
        }

        /**
         * Return whether this holder contains a converted value already (<code>true</code>),
         * or whether the value still needs to be converted (<code>false</code>).
         */
        public synchronized boolean isConverted() {
            return this.converted;
        }

        /**
         * Set the converted value of the constructor argument,
         * after processed type conversion.
         */
        public synchronized void setConvertedValue(Object value) {
            this.converted = true;
            this.convertedValue = value;
        }

        /**
         * Return the converted value of the constructor argument,
         * after processed type conversion.
         */
        public synchronized Object getConvertedValue() {
            return this.convertedValue;
        }

        /**
         * Determine whether the content of this ValueHolder is equal
         * to the content of the given other ValueHolder.
         * <p>Note that ValueHolder does not implement <code>equals</code>
         * directly, to allow for multiple ValueHolder instances with the
         * same content to reside in the same Set.
         */
        private boolean contentEquals(ValueHolder other) {
            return (this == other ||
                    (ObjectUtils.nullSafeEquals(this.value, other.value) && ObjectUtils.nullSafeEquals(this.type, other.type)));
        }

        /**
         * Determine whether the hash code of the content of this ValueHolder.
         * <p>Note that ValueHolder does not implement <code>hashCode</code>
         * directly, to allow for multiple ValueHolder instances with the
         * same content to reside in the same Set.
         */
        private int contentHashCode() {
            return ObjectUtils.nullSafeHashCode(this.value) * 29 + ObjectUtils.nullSafeHashCode(this.type);
        }

        /**
         * Create a copy of this ValueHolder: that is, an independent
         * ValueHolder instance with the same contents.
         */
        public ValueHolder copy() {
            ValueHolder copy = new ValueHolder(this.value, this.type);
            copy.setSource(this.source);
            return copy;
        }
    }

}
