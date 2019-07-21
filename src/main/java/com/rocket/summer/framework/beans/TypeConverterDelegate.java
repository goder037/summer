package com.rocket.summer.framework.beans;

import com.rocket.summer.framework.core.CollectionFactory;
import com.rocket.summer.framework.core.GenericCollectionTypeResolver;
import com.rocket.summer.framework.core.JdkVersion;
import com.rocket.summer.framework.core.MethodParameter;
import com.rocket.summer.framework.core.convert.ConversionService;
import com.rocket.summer.framework.core.convert.TypeDescriptor;
import com.rocket.summer.framework.core.convert.support.PropertyTypeDescriptor;
import com.rocket.summer.framework.util.ClassUtils;
import com.rocket.summer.framework.util.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.beans.PropertyDescriptor;
import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * Internal helper class for converting property values to target types.
 *
 * <p>Works on a given {@link PropertyEditorRegistrySupport} instance.
 * Used as a delegate by {@link BeanWrapperImpl} and {@link SimpleTypeConverter}.
 *
 * @author Juergen Hoeller
 * @author Rob Harrop
 * @since 2.0
 * @see BeanWrapperImpl
 * @see SimpleTypeConverter
 */
class TypeConverterDelegate {

    private static final Log logger = LogFactory.getLog(TypeConverterDelegate.class);

    private static final Map unknownEditorTypes = Collections.synchronizedMap(new WeakHashMap());

    private final PropertyEditorRegistrySupport propertyEditorRegistry;

    private final Object targetObject;


    /**
     * Create a new TypeConverterDelegate for the given editor registry.
     * @param propertyEditorRegistry the editor registry to use
     */
    public TypeConverterDelegate(PropertyEditorRegistrySupport propertyEditorRegistry) {
        this(propertyEditorRegistry, null);
    }

    /**
     * Create a new TypeConverterDelegate for the given editor registry and bean instance.
     * @param propertyEditorRegistry the editor registry to use
     * @param targetObject the target object to work on (as context that can be passed to editors)
     */
    public TypeConverterDelegate(PropertyEditorRegistrySupport propertyEditorRegistry, Object targetObject) {
        this.propertyEditorRegistry = propertyEditorRegistry;
        this.targetObject = targetObject;
    }


    /**
     * Convert the value to the specified required type.
     * @param newValue the proposed new value
     * @param requiredType the type we must convert to
     * (or <code>null</code> if not known, for example in case of a collection element)
     * @return the new value, possibly the result of type conversion
     * @throws IllegalArgumentException if type conversion failed
     */
    public Object convertIfNecessary(Object newValue, Class requiredType) throws IllegalArgumentException {
        return convertIfNecessary(null, null, newValue, requiredType, null, null);
    }

    @SuppressWarnings("unchecked")
    protected Collection convertToTypedCollection(
            Collection original, String propertyName, Class requiredType, TypeDescriptor typeDescriptor) {

        if (!Collection.class.isAssignableFrom(requiredType)) {
            return original;
        }

        boolean approximable = CollectionFactory.isApproximableCollectionType(requiredType);
        if (!approximable && !canCreateCopy(requiredType)) {
            if (logger.isDebugEnabled()) {
                logger.debug("Custom Collection type [" + original.getClass().getName() +
                        "] does not allow for creating a copy - injecting original Collection as-is");
            }
            return original;
        }

        boolean originalAllowed = requiredType.isInstance(original);
        MethodParameter methodParam = typeDescriptor.getMethodParameter();
        Class elementType = null;
        if (methodParam != null) {
            elementType = GenericCollectionTypeResolver.getCollectionParameterType(methodParam);
        }
        if (elementType == null && originalAllowed &&
                !this.propertyEditorRegistry.hasCustomEditorForElement(null, propertyName)) {
            return original;
        }

        Iterator it;
        try {
            it = original.iterator();
            if (it == null) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Collection of type [" + original.getClass().getName() +
                            "] returned null Iterator - injecting original Collection as-is");
                }
                return original;
            }
        }
        catch (Throwable ex) {
            if (logger.isDebugEnabled()) {
                logger.debug("Cannot access Collection of type [" + original.getClass().getName() +
                        "] - injecting original Collection as-is: " + ex);
            }
            return original;
        }

        Collection convertedCopy;
        try {
            if (approximable) {
                convertedCopy = CollectionFactory.createApproximateCollection(original, original.size());
            }
            else {
                convertedCopy = (Collection) requiredType.newInstance();
            }
        }
        catch (Throwable ex) {
            if (logger.isDebugEnabled()) {
                logger.debug("Cannot create copy of Collection type [" + original.getClass().getName() +
                        "] - injecting original Collection as-is: " + ex);
            }
            return original;
        }

        int i = 0;
        for (; it.hasNext(); i++) {
            Object element = it.next();
            String indexedPropertyName = buildIndexedPropertyName(propertyName, i);
            if (methodParam != null) {
                methodParam.increaseNestingLevel();
            }
            Object convertedElement = convertIfNecessary(
                    indexedPropertyName, null, element, elementType, typeDescriptor);
            if (methodParam != null) {
                methodParam.decreaseNestingLevel();
            }
            try {
                convertedCopy.add(convertedElement);
            }
            catch (Throwable ex) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Collection type [" + original.getClass().getName() +
                            "] seems to be read-only - injecting original Collection as-is: " + ex);
                }
                return original;
            }
            originalAllowed = originalAllowed && (element == convertedElement);
        }
        return (originalAllowed ? original : convertedCopy);
    }

    private Object attemptToConvertStringToEnum(Class<?> requiredType, String trimmedValue, Object currentConvertedValue) {
        Object convertedValue = currentConvertedValue;

        if (Enum.class.equals(requiredType)) {
            // target type is declared as raw enum, treat the trimmed value as <enum.fqn>.FIELD_NAME
            int index = trimmedValue.lastIndexOf(".");
            if (index > - 1) {
                String enumType = trimmedValue.substring(0, index);
                String fieldName = trimmedValue.substring(index + 1);
                ClassLoader loader = this.targetObject.getClass().getClassLoader();
                try {
                    Class<?> enumValueType = loader.loadClass(enumType);
                    Field enumField = enumValueType.getField(fieldName);
                    convertedValue = enumField.get(null);
                }
                catch (ClassNotFoundException ex) {
                    if(logger.isTraceEnabled()) {
                        logger.trace("Enum class [" + enumType + "] cannot be loaded from [" + loader + "]", ex);
                    }
                }
                catch (Throwable ex) {
                    if(logger.isTraceEnabled()) {
                        logger.trace("Field [" + fieldName + "] isn't an enum value for type [" + enumType + "]", ex);
                    }
                }
            }
        }

        if (convertedValue == currentConvertedValue) {
            // Try field lookup as fallback: for JDK 1.5 enum or custom enum
            // with values defined as static fields. Resulting value still needs
            // to be checked, hence we don't return it right away.
            try {
                Field enumField = requiredType.getField(trimmedValue);
                convertedValue = enumField.get(null);
            }
            catch (Throwable ex) {
                if (logger.isTraceEnabled()) {
                    logger.trace("Field [" + convertedValue + "] isn't an enum value", ex);

                }
            }
        }

        return convertedValue;
    }

    /**
     * Convert the value to the required type (if necessary from a String),
     * for the specified property.
     * @param propertyName name of the property
     * @param oldValue the previous value, if available (may be <code>null</code>)
     * @param newValue the proposed new value
     * @param requiredType the type we must convert to
     * (or <code>null</code> if not known, for example in case of a collection element)
     * @param typeDescriptor the descriptor for the target property or field
     * @return the new value, possibly the result of type conversion
     * @throws IllegalArgumentException if type conversion failed
     */
    @SuppressWarnings("unchecked")
    public <T> T convertIfNecessary(String propertyName, Object oldValue, Object newValue,
                                    Class<T> requiredType, TypeDescriptor typeDescriptor) throws IllegalArgumentException {

        Object convertedValue = newValue;

        // Custom editor for this type?
        PropertyEditor editor = this.propertyEditorRegistry.findCustomEditor(requiredType, propertyName);

        // No custom editor but custom ConversionService specified?
        ConversionService conversionService = this.propertyEditorRegistry.getConversionService();
        if (editor == null && conversionService != null && convertedValue != null) {
            TypeDescriptor sourceTypeDesc = TypeDescriptor.forObject(convertedValue);
            TypeDescriptor targetTypeDesc = typeDescriptor;
            if (requiredType != null && !requiredType.isAssignableFrom(typeDescriptor.getType())) {
                targetTypeDesc = typeDescriptor.forElementType(requiredType);
            }
            if (conversionService.canConvert(sourceTypeDesc, targetTypeDesc)) {
                return (T) conversionService.convert(convertedValue, sourceTypeDesc, targetTypeDesc);
            }
        }

        // Value not of required type?
        if (editor != null || (requiredType != null && !ClassUtils.isAssignableValue(requiredType, convertedValue))) {
            if (requiredType != null && Collection.class.isAssignableFrom(requiredType) &&
                    convertedValue instanceof String && typeDescriptor.getMethodParameter() != null) {
                Class elemType = GenericCollectionTypeResolver.getCollectionParameterType(typeDescriptor.getMethodParameter());
                if (elemType != null && Enum.class.isAssignableFrom(elemType)) {
                    convertedValue = StringUtils.commaDelimitedListToStringArray((String) convertedValue);
                }
            }
            if (editor == null) {
                editor = findDefaultEditor(requiredType, typeDescriptor);
            }
            convertedValue = doConvertValue(oldValue, convertedValue, requiredType, editor);
        }

        if (requiredType != null) {
            // Try to apply some standard type conversion rules if appropriate.

            if (convertedValue != null) {
                if (requiredType.isArray()) {
                    // Array required -> apply appropriate conversion of elements.
                    if (convertedValue instanceof String && Enum.class.isAssignableFrom(requiredType.getComponentType())) {
                        convertedValue = StringUtils.commaDelimitedListToStringArray((String) convertedValue);
                    }
                    return (T) convertToTypedArray(convertedValue, propertyName, requiredType.getComponentType());
                }
                else if (convertedValue instanceof Collection) {
                    // Convert elements to target type, if determined.
                    convertedValue = convertToTypedCollection(
                            (Collection) convertedValue, propertyName, requiredType, typeDescriptor);
                }
                else if (convertedValue instanceof Map) {
                    // Convert keys and values to respective target type, if determined.
                    convertedValue = convertToTypedMap(
                            (Map) convertedValue, propertyName, requiredType, typeDescriptor);
                }
                if (convertedValue.getClass().isArray() && Array.getLength(convertedValue) == 1) {
                    convertedValue = Array.get(convertedValue, 0);
                }
                if (String.class.equals(requiredType) && ClassUtils.isPrimitiveOrWrapper(convertedValue.getClass())) {
                    // We can stringify any primitive value...
                    return (T) convertedValue.toString();
                }
                else if (convertedValue instanceof String && !requiredType.isInstance(convertedValue)) {
                    if (!requiredType.isInterface() && !requiredType.isEnum()) {
                        try {
                            Constructor strCtor = requiredType.getConstructor(String.class);
                            return (T) BeanUtils.instantiateClass(strCtor, convertedValue);
                        }
                        catch (NoSuchMethodException ex) {
                            // proceed with field lookup
                            if (logger.isTraceEnabled()) {
                                logger.trace("No String constructor found on type [" + requiredType.getName() + "]", ex);
                            }
                        }
                        catch (Exception ex) {
                            if (logger.isDebugEnabled()) {
                                logger.debug("Construction via String failed for type [" + requiredType.getName() + "]", ex);
                            }
                        }
                    }
                    String trimmedValue = ((String) convertedValue).trim();
                    if (requiredType.isEnum() && "".equals(trimmedValue)) {
                        // It's an empty enum identifier: reset the enum value to null.
                        return null;
                    }

                    convertedValue = attemptToConvertStringToEnum(requiredType, trimmedValue, convertedValue);
                }
            }

            if (!ClassUtils.isAssignableValue(requiredType, convertedValue)) {
                // Definitely doesn't match: throw IllegalArgumentException/IllegalStateException
                StringBuilder msg = new StringBuilder();
                msg.append("Cannot convert value of type [").append(ClassUtils.getDescriptiveType(newValue));
                msg.append("] to required type [").append(ClassUtils.getQualifiedName(requiredType)).append("]");
                if (propertyName != null) {
                    msg.append(" for property '").append(propertyName).append("'");
                }
                if (editor != null) {
                    msg.append(": PropertyEditor [").append(editor.getClass().getName()).append(
                            "] returned inappropriate value of type [").append(
                            ClassUtils.getDescriptiveType(convertedValue)).append("]");
                    throw new IllegalArgumentException(msg.toString());
                }
                else {
                    msg.append(": no matching editors or conversion strategy found");
                    throw new IllegalStateException(msg.toString());
                }
            }
        }

        return (T) convertedValue;
    }

    protected Map convertToTypedMap(
            Map original, String propertyName, Class requiredType, TypeDescriptor typeDescriptor) {

        if (!Map.class.isAssignableFrom(requiredType)) {
            return original;
        }

        boolean approximable = CollectionFactory.isApproximableMapType(requiredType);
        if (!approximable && !canCreateCopy(requiredType)) {
            if (logger.isDebugEnabled()) {
                logger.debug("Custom Map type [" + original.getClass().getName() +
                        "] does not allow for creating a copy - injecting original Map as-is");
            }
            return original;
        }

        boolean originalAllowed = requiredType.isInstance(original);
        Class keyType = null;
        Class valueType = null;
        MethodParameter methodParam = typeDescriptor.getMethodParameter();
        if (methodParam != null) {
            keyType = GenericCollectionTypeResolver.getMapKeyParameterType(methodParam);
            valueType = GenericCollectionTypeResolver.getMapValueParameterType(methodParam);
        }
        if (keyType == null && valueType == null && originalAllowed &&
                !this.propertyEditorRegistry.hasCustomEditorForElement(null, propertyName)) {
            return original;
        }

        Iterator it;
        try {
            it = original.entrySet().iterator();
            if (it == null) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Map of type [" + original.getClass().getName() +
                            "] returned null Iterator - injecting original Map as-is");
                }
                return original;
            }
        }
        catch (Throwable ex) {
            if (logger.isDebugEnabled()) {
                logger.debug("Cannot access Map of type [" + original.getClass().getName() +
                        "] - injecting original Map as-is: " + ex);
            }
            return original;
        }

        Map convertedCopy;
        try {
            if (approximable) {
                convertedCopy = CollectionFactory.createApproximateMap(original, original.size());
            }
            else {
                convertedCopy = (Map) requiredType.newInstance();
            }
        }
        catch (Throwable ex) {
            if (logger.isDebugEnabled()) {
                logger.debug("Cannot create copy of Map type [" + original.getClass().getName() +
                        "] - injecting original Map as-is: " + ex);
            }
            return original;
        }

        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            Object key = entry.getKey();
            Object value = entry.getValue();
            String keyedPropertyName = buildKeyedPropertyName(propertyName, key);
            if (methodParam != null) {
                methodParam.increaseNestingLevel();
                methodParam.setTypeIndexForCurrentLevel(0);
            }
            Object convertedKey = convertIfNecessary(keyedPropertyName, null, key, keyType, typeDescriptor);
            if (methodParam != null) {
                methodParam.setTypeIndexForCurrentLevel(1);
            }
            Object convertedValue = convertIfNecessary(keyedPropertyName, null, value, valueType, typeDescriptor);
            if (methodParam != null) {
                methodParam.decreaseNestingLevel();
            }
            try {
                convertedCopy.put(convertedKey, convertedValue);
            }
            catch (Throwable ex) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Map type [" + original.getClass().getName() +
                            "] seems to be read-only - injecting original Map as-is: " + ex);
                }
                return original;
            }
            originalAllowed = originalAllowed && (key == convertedKey) && (value == convertedValue);
        }
        return (originalAllowed ? original : convertedCopy);
    }

    /**
     * Find a default editor for the given type.
     * @param requiredType the type to find an editor for
     * @param typeDescriptor the JavaBeans descriptor for the property
     * @return the corresponding editor, or <code>null</code> if none
     */
    protected PropertyEditor findDefaultEditor(Class requiredType, TypeDescriptor typeDescriptor) {
        PropertyEditor editor = null;
        if (typeDescriptor instanceof PropertyTypeDescriptor) {
            PropertyDescriptor pd = ((PropertyTypeDescriptor) typeDescriptor).getPropertyDescriptor();
            editor = pd.createPropertyEditor(this.targetObject);
        }
        if (editor == null && requiredType != null) {
            // No custom editor -> check BeanWrapperImpl's default editors.
            editor = this.propertyEditorRegistry.getDefaultEditor(requiredType);
            if (editor == null && !String.class.equals(requiredType)) {
                // No BeanWrapper default editor -> check standard JavaBean editor.
                editor = BeanUtils.findEditorByConvention(requiredType);
            }
        }
        return editor;
    }


    /**
     * Convert the value to the required type for the specified property.
     * @param propertyName name of the property
     * @param oldValue the previous value, if available (may be <code>null</code>)
     * @param newValue the proposed new value
     * @param requiredType the type we must convert to
     * (or <code>null</code> if not known, for example in case of a collection element)
     * @return the new value, possibly the result of type conversion
     * @throws IllegalArgumentException if type conversion failed
     */
    public Object convertIfNecessary(
            String propertyName, Object oldValue, Object newValue, Class requiredType)
            throws IllegalArgumentException {

        return convertIfNecessary(propertyName, oldValue, newValue, requiredType, null, null);
    }

    /**
     * Convert the value to the required type for the specified property.
     * @param oldValue the previous value, if available (may be <code>null</code>)
     * @param newValue the proposed new value
     * @param descriptor the JavaBeans descriptor for the property
     * @return the new value, possibly the result of type conversion
     * @throws IllegalArgumentException if type conversion failed
     */
    public Object convertIfNecessary(Object oldValue, Object newValue, PropertyDescriptor descriptor)
            throws IllegalArgumentException {

        return convertIfNecessary(
                descriptor.getName(), oldValue, newValue, descriptor.getPropertyType(), descriptor,
                BeanUtils.getWriteMethodParameter(descriptor));
    }


    /**
     * Convert the value to the required type (if necessary from a String),
     * for the specified property.
     * @param propertyName name of the property
     * @param oldValue the previous value, if available (may be <code>null</code>)
     * @param newValue the proposed new value
     * @param requiredType the type we must convert to
     * (or <code>null</code> if not known, for example in case of a collection element)
     * @param descriptor the JavaBeans descriptor for the property
     * @param methodParam the method parameter that is the target of the conversion
     * (may be <code>null</code>)
     * @return the new value, possibly the result of type conversion
     * @throws IllegalArgumentException if type conversion failed
     */
    protected Object convertIfNecessary(
            String propertyName, Object oldValue, Object newValue, Class requiredType,
            PropertyDescriptor descriptor, MethodParameter methodParam)
            throws IllegalArgumentException {

        Object convertedValue = newValue;

        // Custom editor for this type?
        PropertyEditor editor = this.propertyEditorRegistry.findCustomEditor(requiredType, propertyName);

        // Value not of required type?
        if (editor != null || (requiredType != null && !ClassUtils.isAssignableValue(requiredType, convertedValue))) {
            if (editor == null) {
                editor = findDefaultEditor(requiredType, descriptor);
            }
            convertedValue = doConvertValue(oldValue, convertedValue, requiredType, editor);
        }

        if (requiredType != null) {
            // Try to apply some standard type conversion rules if appropriate.

            if (convertedValue != null) {
                if (String.class.equals(requiredType) && ClassUtils.isPrimitiveOrWrapper(convertedValue.getClass())) {
                    // We can stringify any primitive value...
                    return convertedValue.toString();
                }
                else if (requiredType.isArray()) {
                    // Array required -> apply appropriate conversion of elements.
                    return convertToTypedArray(convertedValue, propertyName, requiredType.getComponentType());
                }
                else if (convertedValue instanceof Collection && CollectionFactory.isApproximableCollectionType(requiredType)) {
                    // Convert elements to target type, if determined.
                    convertedValue = convertToTypedCollection((Collection) convertedValue, propertyName, methodParam);
                }
                else if (convertedValue instanceof Map && CollectionFactory.isApproximableMapType(requiredType)) {
                    // Convert keys and values to respective target type, if determined.
                    convertedValue = convertToTypedMap((Map) convertedValue, propertyName, methodParam);
                }
                else if (convertedValue instanceof String && !requiredType.isInstance(convertedValue)) {
                    String strValue = ((String) convertedValue).trim();
                    if (JdkVersion.isAtLeastJava15() && requiredType.isEnum() && "".equals(strValue)) {
                        // It's an empty enum identifier: reset the enum value to null.
                        return null;
                    }
                    // Try field lookup as fallback: for JDK 1.5 enum or custom enum
                    // with values defined as static fields. Resulting value still needs
                    // to be checked, hence we don't return it right away.
                    try {
                        Field enumField = requiredType.getField(strValue);
                        convertedValue = enumField.get(null);
                    }
                    catch (Throwable ex) {
                        if (logger.isTraceEnabled()) {
                            logger.trace("Field [" + convertedValue + "] isn't an enum value", ex);
                        }
                    }
                }
            }

            if (!ClassUtils.isAssignableValue(requiredType, convertedValue)) {
                // Definitely doesn't match: throw IllegalArgumentException.
                StringBuffer msg = new StringBuffer();
                msg.append("Cannot convert value of type [").append(ClassUtils.getDescriptiveType(newValue));
                msg.append("] to required type [").append(ClassUtils.getQualifiedName(requiredType)).append("]");
                if (propertyName != null) {
                    msg.append(" for property '" + propertyName + "'");
                }
                if (editor != null) {
                    msg.append(": PropertyEditor [" + editor.getClass().getName() + "] returned inappropriate value");
                }
                else {
                    msg.append(": no matching editors or conversion strategy found");
                }
                throw new IllegalArgumentException(msg.toString());
            }
        }

        return convertedValue;
    }

    /**
     * Find a default editor for the given type.
     * @param requiredType the type to find an editor for
     * @param descriptor the JavaBeans descriptor for the property
     * @return the corresponding editor, or <code>null</code> if none
     */
    protected PropertyEditor findDefaultEditor(Class requiredType, PropertyDescriptor descriptor) {
        PropertyEditor editor = null;
        if (descriptor != null) {
            if (JdkVersion.isAtLeastJava15()) {
                editor = descriptor.createPropertyEditor(this.targetObject);
            }
            else {
                Class editorClass = descriptor.getPropertyEditorClass();
                if (editorClass != null) {
                    editor = (PropertyEditor) BeanUtils.instantiateClass(editorClass);
                }
            }
        }
        if (editor == null && requiredType != null) {
            // No custom editor -> check BeanWrapperImpl's default editors.
            editor = (PropertyEditor) this.propertyEditorRegistry.getDefaultEditor(requiredType);
            if (editor == null && !String.class.equals(requiredType)) {
                // No BeanWrapper default editor -> check standard JavaBean editor.
                editor = BeanUtils.findEditorByConvention(requiredType);
                if (editor == null && !unknownEditorTypes.containsKey(requiredType)) {
                    // Deprecated global PropertyEditorManager fallback...
                    editor = PropertyEditorManager.findEditor(requiredType);
                    if (editor == null) {
                        // Regular case as of Spring 2.5
                        unknownEditorTypes.put(requiredType, Boolean.TRUE);
                    }
                    else {
                        logger.warn("PropertyEditor [" + editor.getClass().getName() +
                                "] found through deprecated global PropertyEditorManager fallback - " +
                                "consider using a more isolated form of registration, e.g. on the BeanWrapper/BeanFactory!");
                    }
                }
            }
        }
        return editor;
    }

    /**
     * Convert the value to the required type (if necessary from a String),
     * using the given property editor.
     * @param oldValue the previous value, if available (may be <code>null</code>)
     * @param newValue the proposed new value
     * @param requiredType the type we must convert to
     * (or <code>null</code> if not known, for example in case of a collection element)
     * @param editor the PropertyEditor to use
     * @return the new value, possibly the result of type conversion
     * @throws IllegalArgumentException if type conversion failed
     */
    protected Object doConvertValue(Object oldValue, Object newValue, Class requiredType, PropertyEditor editor) {
        Object convertedValue = newValue;
        boolean sharedEditor = false;

        if (editor != null) {
            sharedEditor = this.propertyEditorRegistry.isSharedEditor(editor);
        }

        if (editor != null && !(convertedValue instanceof String)) {
            // Not a String -> use PropertyEditor's setValue.
            // With standard PropertyEditors, this will return the very same object;
            // we just want to allow special PropertyEditors to override setValue
            // for type conversion from non-String values to the required type.
            try {
                Object newConvertedValue = null;
                if (sharedEditor) {
                    // Synchronized access to shared editor instance.
                    synchronized (editor) {
                        editor.setValue(convertedValue);
                        newConvertedValue = editor.getValue();
                    }
                }
                else {
                    // Unsynchronized access to non-shared editor instance.
                    editor.setValue(convertedValue);
                    newConvertedValue = editor.getValue();
                }
                if (newConvertedValue != convertedValue) {
                    convertedValue = newConvertedValue;
                    // Reset PropertyEditor: It already did a proper conversion.
                    // Don't use it again for a setAsText call.
                    editor = null;
                }
            }
            catch (Exception ex) {
                if (logger.isDebugEnabled()) {
                    logger.debug("PropertyEditor [" + editor.getClass().getName() + "] does not support setValue call", ex);
                }
                // Swallow and proceed.
            }
        }

        if (requiredType != null && !requiredType.isArray() && convertedValue instanceof String[]) {
            // Convert String array to a comma-separated String.
            // Only applies if no PropertyEditor converted the String array before.
            // The CSV String will be passed into a PropertyEditor's setAsText method, if any.
            if (logger.isTraceEnabled()) {
                logger.trace("Converting String array to comma-delimited String [" + convertedValue + "]");
            }
            convertedValue = StringUtils.arrayToCommaDelimitedString((String[]) convertedValue);
        }

        if (editor != null && convertedValue instanceof String) {
            // Use PropertyEditor's setAsText in case of a String value.
            if (logger.isTraceEnabled()) {
                logger.trace("Converting String to [" + requiredType + "] using property editor [" + editor + "]");
            }
            String newTextValue = (String) convertedValue;
            if (sharedEditor) {
                // Synchronized access to shared editor instance.
                synchronized (editor) {
                    return doConvertTextValue(oldValue, newTextValue, editor);
                }
            }
            else {
                // Unsynchronized access to non-shared editor instance.
                return doConvertTextValue(oldValue, newTextValue, editor);
            }
        }

        return convertedValue;
    }

    /**
     * Convert the given text value using the given property editor.
     * @param oldValue the previous value, if available (may be <code>null</code>)
     * @param newTextValue the proposed text value
     * @param editor the PropertyEditor to use
     * @return the converted value
     */
    protected Object doConvertTextValue(Object oldValue, String newTextValue, PropertyEditor editor) {
        try {
            editor.setValue(oldValue);
        }
        catch (Exception ex) {
            if (logger.isDebugEnabled()) {
                logger.debug("PropertyEditor [" + editor.getClass().getName() + "] does not support setValue call", ex);
            }
            // Swallow and proceed.
        }
        editor.setAsText(newTextValue);
        return editor.getValue();
    }

    protected Object convertToTypedArray(Object input, String propertyName, Class componentType) {
        if (input instanceof Collection) {
            // Convert Collection elements to array elements.
            Collection coll = (Collection) input;
            Object result = Array.newInstance(componentType, coll.size());
            int i = 0;
            for (Iterator it = coll.iterator(); it.hasNext(); i++) {
                Object value = convertIfNecessary(
                        buildIndexedPropertyName(propertyName, i), null, it.next(), componentType);
                Array.set(result, i, value);
            }
            return result;
        }
        else if (input.getClass().isArray()) {
            // Convert array elements, if necessary.
            if (componentType.equals(input.getClass().getComponentType()) &&
                    !this.propertyEditorRegistry.hasCustomEditorForElement(componentType, propertyName)) {
                return input;
            }
            int arrayLength = Array.getLength(input);
            Object result = Array.newInstance(componentType, arrayLength);
            for (int i = 0; i < arrayLength; i++) {
                Object value = convertIfNecessary(
                        buildIndexedPropertyName(propertyName, i), null, Array.get(input, i), componentType);
                Array.set(result, i, value);
            }
            return result;
        }
        else {
            // A plain value: convert it to an array with a single component.
            Object result = Array.newInstance(componentType, 1);
            Object value = convertIfNecessary(
                    buildIndexedPropertyName(propertyName, 0), null, input, componentType);
            Array.set(result, 0, value);
            return result;
        }
    }

    protected Collection convertToTypedCollection(
            Collection original, String propertyName, MethodParameter methodParam) {

        Class elementType = null;
        if (methodParam != null && JdkVersion.isAtLeastJava15()) {
            elementType = GenericCollectionTypeResolver.getCollectionParameterType(methodParam);
        }
        if (elementType == null &&
                !this.propertyEditorRegistry.hasCustomEditorForElement(null, propertyName)) {
            return original;
        }

        Collection convertedCopy = null;
        Iterator it = null;
        try {
            it = original.iterator();
            if (it == null) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Collection of type [" + original.getClass().getName() +
                            "] returned null Iterator - injecting original Collection as-is");
                }
                return original;
            }
            convertedCopy = CollectionFactory.createApproximateCollection(original, original.size());
        }
        catch (Throwable ex) {
            if (logger.isDebugEnabled()) {
                logger.debug("Cannot access Collection of type [" + original.getClass().getName() +
                        "] - injecting original Collection as-is", ex);
            }
            return original;
        }
        boolean actuallyConverted = false;
        int i = 0;
        for (; it.hasNext(); i++) {
            Object element = it.next();
            String indexedPropertyName = buildIndexedPropertyName(propertyName, i);
            if (methodParam != null) {
                methodParam.increaseNestingLevel();
            }
            Object convertedElement =
                    convertIfNecessary(indexedPropertyName, null, element, elementType, null, methodParam);
            if (methodParam != null) {
                methodParam.decreaseNestingLevel();
            }
            convertedCopy.add(convertedElement);
            actuallyConverted = actuallyConverted || (element != convertedElement);
        }
        return (actuallyConverted ? convertedCopy : original);
    }

    protected Map convertToTypedMap(Map original, String propertyName, MethodParameter methodParam) {
        Class keyType = null;
        Class valueType = null;
        if (methodParam != null && JdkVersion.isAtLeastJava15()) {
            keyType = GenericCollectionTypeResolver.getMapKeyParameterType(methodParam);
            valueType = GenericCollectionTypeResolver.getMapValueParameterType(methodParam);
        }
        if (keyType == null && valueType == null &&
                !this.propertyEditorRegistry.hasCustomEditorForElement(null, propertyName)) {
            return original;
        }

        Map convertedCopy = null;
        Iterator it = null;
        try {
            it = original.entrySet().iterator();
            if (it == null) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Map of type [" + original.getClass().getName() +
                            "] returned null Iterator - injecting original Map as-is");
                }
            }
            convertedCopy = CollectionFactory.createApproximateMap(original, original.size());
        }
        catch (Throwable ex) {
            if (logger.isDebugEnabled()) {
                logger.debug("Cannot access Map of type [" + original.getClass().getName() +
                        "] - injecting original Map as-is", ex);
            }
            return original;
        }
        boolean actuallyConverted = false;
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            Object key = entry.getKey();
            Object value = entry.getValue();
            String keyedPropertyName = buildKeyedPropertyName(propertyName, key);
            if (methodParam != null) {
                methodParam.increaseNestingLevel();
                methodParam.setTypeIndexForCurrentLevel(0);
            }
            Object convertedKey = convertIfNecessary(keyedPropertyName, null, key, keyType, null, methodParam);
            if (methodParam != null) {
                methodParam.setTypeIndexForCurrentLevel(1);
            }
            Object convertedValue = convertIfNecessary(keyedPropertyName, null, value, valueType, null, methodParam);
            if (methodParam != null) {
                methodParam.decreaseNestingLevel();
            }
            convertedCopy.put(convertedKey, convertedValue);
            actuallyConverted = actuallyConverted || (key != convertedKey) || (value != convertedValue);
        }
        return (actuallyConverted ? convertedCopy : original);
    }

    private String buildIndexedPropertyName(String propertyName, int index) {
        return (propertyName != null ?
                propertyName + PropertyAccessor.PROPERTY_KEY_PREFIX + index + PropertyAccessor.PROPERTY_KEY_SUFFIX :
                null);
    }

    private String buildKeyedPropertyName(String propertyName, Object key) {
        return (propertyName != null ?
                propertyName + PropertyAccessor.PROPERTY_KEY_PREFIX + key + PropertyAccessor.PROPERTY_KEY_SUFFIX :
                null);
    }

    private boolean canCreateCopy(Class requiredType) {
        return (!requiredType.isInterface() && !Modifier.isAbstract(requiredType.getModifiers()) &&
                Modifier.isPublic(requiredType.getModifiers()) && ClassUtils.hasConstructor(requiredType));
    }

    /**
     * Convert the value to the specified required type.
     * @param newValue the proposed new value
     * @param requiredType the type we must convert to
     * (or <code>null</code> if not known, for example in case of a collection element)
     * @param methodParam the method parameter that is the target of the conversion
     * (may be <code>null</code>)
     * @return the new value, possibly the result of type conversion
     * @throws IllegalArgumentException if type conversion failed
     */
    public <T> T convertIfNecessary(Object newValue, Class<T> requiredType, MethodParameter methodParam)
            throws IllegalArgumentException {

        return convertIfNecessary(null, null, newValue, requiredType,
                (methodParam != null ? new TypeDescriptor(methodParam) : TypeDescriptor.valueOf(requiredType)));
    }

}
