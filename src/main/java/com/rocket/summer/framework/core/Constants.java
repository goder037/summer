package com.rocket.summer.framework.core;

import com.rocket.summer.framework.util.Assert;
import com.rocket.summer.framework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.*;

/**
 * This class can be used to parse other classes containing constant definitions
 * in public static final members. The <code>asXXXX</code> methods of this class
 * allow these constant values to be accessed via their string names.
 *
 * <p>Consider class Foo containing <code>public final static int CONSTANT1 = 66;</code>
 * An instance of this class wrapping <code>Foo.class</code> will return the constant value
 * of 66 from its <code>asNumber</code> method given the argument <code>"CONSTANT1"</code>.
 *
 * <p>This class is ideal for use in PropertyEditors, enabling them to
 * recognize the same names as the constants themselves, and freeing them
 * from maintaining their own mapping.
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @since 16.03.2003
 */
public class Constants {

    /** The name of the introspected class */
    private final String className;

    /** Map from String field name to object value */
    private final Map fieldCache = new HashMap();


    /**
     * Create a new Constants converter class wrapping the given class.
     * <p>All <b>public</b> static final variables will be exposed, whatever their type.
     * @param clazz the class to analyze
     * @throws IllegalArgumentException if the supplied <code>clazz</code> is <code>null</code>
     */
    public Constants(Class clazz) {
        Assert.notNull(clazz);
        this.className = clazz.getName();
        Field[] fields = clazz.getFields();
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            if (ReflectionUtils.isPublicStaticFinal(field)) {
                String name = field.getName();
                try {
                    Object value = field.get(null);
                    this.fieldCache.put(name, value);
                }
                catch (IllegalAccessException ex) {
                    // just leave this field and continue
                }
            }
        }
    }


    /**
     * Return the name of the analyzed class.
     */
    public final String getClassName() {
        return this.className;
    }

    /**
     * Return the number of constants exposed.
     */
    public final int getSize() {
        return this.fieldCache.size();
    }

    /**
     * Exposes the field cache to subclasses:
     * a Map from String field name to object value.
     */
    protected final Map getFieldCache() {
        return this.fieldCache;
    }


    /**
     * Return a constant value cast to a Number.
     * @param code the name of the field (never <code>null</code>)
     * @return the Number value
     * @see #asObject
     * @throws ConstantException if the field name wasn't found
     * or if the type wasn't compatible with Number
     */
    public Number asNumber(String code) throws ConstantException {
        Object obj = asObject(code);
        if (!(obj instanceof Number)) {
            throw new ConstantException(this.className, code, "not a Number");
        }
        return (Number) obj;
    }

    /**
     * Return a constant value as a String.
     * @param code the name of the field (never <code>null</code>)
     * @return the String value
     * Works even if it's not a string (invokes <code>toString()</code>).
     * @see #asObject
     * @throws ConstantException if the field name wasn't found
     */
    public String asString(String code) throws ConstantException {
        return asObject(code).toString();
    }

    /**
     * Parse the given String (upper or lower case accepted) and return
     * the appropriate value if it's the name of a constant field in the
     * class that we're analysing.
     * @param code the name of the field (never <code>null</code>)
     * @return the Object value
     * @throws ConstantException if there's no such field
     */
    public Object asObject(String code) throws ConstantException {
        Assert.notNull(code, "Code must not be null");
        String codeToUse = code.toUpperCase(Locale.ENGLISH);
        Object val = this.fieldCache.get(codeToUse);
        if (val == null) {
            throw new ConstantException(this.className, codeToUse, "not found");
        }
        return val;
    }


    /**
     * Return all names of the given group of constants.
     * <p>Note that this method assumes that constants are named
     * in accordance with the standard Java convention for constant
     * values (i.e. all uppercase). The supplied <code>namePrefix</code>
     * will be uppercased (in a locale-insensitive fashion) prior to
     * the main logic of this method kicking in.
     * @param namePrefix prefix of the constant names to search (may be <code>null</code>)
     * @return the set of constant names
     */
    public Set getNames(String namePrefix) {
        String prefixToUse = (namePrefix != null ? namePrefix.trim().toUpperCase(Locale.ENGLISH) : "");
        Set names = new HashSet();
        for (Iterator it = this.fieldCache.keySet().iterator(); it.hasNext();) {
            String code = (String) it.next();
            if (code.startsWith(prefixToUse)) {
                names.add(code);
            }
        }
        return names;
    }

    /**
     * Return all names of the group of constants for the
     * given bean property name.
     * @param propertyName the name of the bean property
     * @return the set of values
     * @see #propertyToConstantNamePrefix
     */
    public Set getNamesForProperty(String propertyName) {
        return getNames(propertyToConstantNamePrefix(propertyName));
    }

    /**
     * Return all names of the given group of constants.
     * <p>Note that this method assumes that constants are named
     * in accordance with the standard Java convention for constant
     * values (i.e. all uppercase). The supplied <code>nameSuffix</code>
     * will be uppercased (in a locale-insensitive fashion) prior to
     * the main logic of this method kicking in.
     * @param nameSuffix suffix of the constant names to search (may be <code>null</code>)
     * @return the set of constant names
     */
    public Set getNamesForSuffix(String nameSuffix) {
        String suffixToUse = (nameSuffix != null ? nameSuffix.trim().toUpperCase(Locale.ENGLISH) : "");
        Set names = new HashSet();
        for (Iterator it = this.fieldCache.keySet().iterator(); it.hasNext();) {
            String code = (String) it.next();
            if (code.endsWith(suffixToUse)) {
                names.add(code);
            }
        }
        return names;
    }


    /**
     * Return all values of the given group of constants.
     * <p>Note that this method assumes that constants are named
     * in accordance with the standard Java convention for constant
     * values (i.e. all uppercase). The supplied <code>namePrefix</code>
     * will be uppercased (in a locale-insensitive fashion) prior to
     * the main logic of this method kicking in.
     * @param namePrefix prefix of the constant names to search (may be <code>null</code>)
     * @return the set of values
     */
    public Set getValues(String namePrefix) {
        String prefixToUse = (namePrefix != null ? namePrefix.trim().toUpperCase(Locale.ENGLISH) : "");
        Set values = new HashSet();
        for (Iterator it = this.fieldCache.keySet().iterator(); it.hasNext();) {
            String code = (String) it.next();
            if (code.startsWith(prefixToUse)) {
                values.add(this.fieldCache.get(code));
            }
        }
        return values;
    }

    /**
     * Return all values of the group of constants for the
     * given bean property name.
     * @param propertyName the name of the bean property
     * @return the set of values
     * @see #propertyToConstantNamePrefix
     */
    public Set getValuesForProperty(String propertyName) {
        return getValues(propertyToConstantNamePrefix(propertyName));
    }

    /**
     * Return all values of the given group of constants.
     * <p>Note that this method assumes that constants are named
     * in accordance with the standard Java convention for constant
     * values (i.e. all uppercase). The supplied <code>nameSuffix</code>
     * will be uppercased (in a locale-insensitive fashion) prior to
     * the main logic of this method kicking in.
     * @param nameSuffix suffix of the constant names to search (may be <code>null</code>)
     * @return the set of values
     */
    public Set getValuesForSuffix(String nameSuffix) {
        String suffixToUse = (nameSuffix != null ? nameSuffix.trim().toUpperCase(Locale.ENGLISH) : "");
        Set values = new HashSet();
        for (Iterator it = this.fieldCache.keySet().iterator(); it.hasNext();) {
            String code = (String) it.next();
            if (code.endsWith(suffixToUse)) {
                values.add(this.fieldCache.get(code));
            }
        }
        return values;
    }


    /**
     * Look up the given value within the given group of constants.
     * <p>Will return the first match.
     * @param value constant value to look up
     * @param namePrefix prefix of the constant names to search (may be <code>null</code>)
     * @return the name of the constant field
     * @throws ConstantException if the value wasn't found
     */
    public String toCode(Object value, String namePrefix) throws ConstantException {
        String prefixToUse = (namePrefix != null ? namePrefix.trim().toUpperCase(Locale.ENGLISH) : null);
        for (Iterator it = this.fieldCache.entrySet().iterator(); it.hasNext();) {
            Map.Entry entry = (Map.Entry) it.next();
            String key = (String) entry.getKey();
            if (key.startsWith(prefixToUse) && entry.getValue().equals(value)) {
                return key;
            }
        }
        throw new ConstantException(this.className, prefixToUse, value);
    }

    /**
     * Look up the given value within the group of constants for
     * the given bean property name. Will return the first match.
     * @param value constant value to look up
     * @param propertyName the name of the bean property
     * @return the name of the constant field
     * @throws ConstantException if the value wasn't found
     * @see #propertyToConstantNamePrefix
     */
    public String toCodeForProperty(Object value, String propertyName) throws ConstantException {
        return toCode(value, propertyToConstantNamePrefix(propertyName));
    }

    /**
     * Look up the given value within the given group of constants.
     * <p>Will return the first match.
     * @param value constant value to look up
     * @param nameSuffix suffix of the constant names to search (may be <code>null</code>)
     * @return the name of the constant field
     * @throws ConstantException if the value wasn't found
     */
    public String toCodeForSuffix(Object value, String nameSuffix) throws ConstantException {
        String suffixToUse = (nameSuffix != null ? nameSuffix.trim().toUpperCase(Locale.ENGLISH) : null);
        for (Iterator it = this.fieldCache.entrySet().iterator(); it.hasNext();) {
            Map.Entry entry = (Map.Entry) it.next();
            String key = (String) entry.getKey();
            if (key.endsWith(suffixToUse) && entry.getValue().equals(value)) {
                return key;
            }
        }
        throw new ConstantException(this.className, suffixToUse, value);
    }


    /**
     * Convert the given bean property name to a constant name prefix.
     * <p>Uses a common naming idiom: turning all lower case characters to
     * upper case, and prepending upper case characters with an underscore.
     * <p>Example: "imageSize" -> "IMAGE_SIZE"<br>
     * Example: "imagesize" -> "IMAGESIZE".<br>
     * Example: "ImageSize" -> "_IMAGE_SIZE".<br>
     * Example: "IMAGESIZE" -> "_I_M_A_G_E_S_I_Z_E"
     * @param propertyName the name of the bean property
     * @return the corresponding constant name prefix
     * @see #getValuesForProperty
     * @see #toCodeForProperty
     */
    public String propertyToConstantNamePrefix(String propertyName) {
        StringBuffer parsedPrefix = new StringBuffer();
        for(int i = 0; i < propertyName.length(); i++) {
            char c = propertyName.charAt(i);
            if (Character.isUpperCase(c)) {
                parsedPrefix.append("_");
                parsedPrefix.append(c);
            }
            else {
                parsedPrefix.append(Character.toUpperCase(c));
            }
        }
        return parsedPrefix.toString();
    }

}
