package com.rocket.summer.framework.core.type.classreading;

import com.rocket.summer.framework.core.annotation.AnnotationAttributes;
import com.rocket.summer.framework.util.ReflectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.lang.reflect.Field;
import java.security.AccessControlException;

/**
 * @author Chris Beams
 * @author Juergen Hoeller
 * @author Phillip Webb
 * @author Sam Brannen
 * @since 3.1.1
 */
abstract class AbstractRecursiveAnnotationVisitor extends AnnotationVisitor {

    protected final Log logger = LogFactory.getLog(getClass());

    protected final AnnotationAttributes attributes;

    protected final ClassLoader classLoader;


    public AbstractRecursiveAnnotationVisitor(ClassLoader classLoader, AnnotationAttributes attributes) {
        super(Opcodes.ASM7);
        this.classLoader = classLoader;
        this.attributes = attributes;
    }


    @Override
    public void visit(String attributeName, Object attributeValue) {
        this.attributes.put(attributeName, attributeValue);
    }

    @Override
    public AnnotationVisitor visitAnnotation(String attributeName, String asmTypeDescriptor) {
        String annotationType = Type.getType(asmTypeDescriptor).getClassName();
        AnnotationAttributes nestedAttributes = new AnnotationAttributes(annotationType, this.classLoader);
        this.attributes.put(attributeName, nestedAttributes);
        return new RecursiveAnnotationAttributesVisitor(annotationType, nestedAttributes, this.classLoader);
    }

    @Override
    public AnnotationVisitor visitArray(String attributeName) {
        return new RecursiveAnnotationArrayVisitor(attributeName, this.attributes, this.classLoader);
    }

    @Override
    public void visitEnum(String attributeName, String asmTypeDescriptor, String attributeValue) {
        Object newValue = getEnumValue(asmTypeDescriptor, attributeValue);
        visit(attributeName, newValue);
    }

    protected Object getEnumValue(String asmTypeDescriptor, String attributeValue) {
        Object valueToUse = attributeValue;
        try {
            Class<?> enumType = this.classLoader.loadClass(Type.getType(asmTypeDescriptor).getClassName());
            Field enumConstant = ReflectionUtils.findField(enumType, attributeValue);
            if (enumConstant != null) {
                ReflectionUtils.makeAccessible(enumConstant);
                valueToUse = enumConstant.get(null);
            }
        }
        catch (ClassNotFoundException ex) {
            logger.debug("Failed to classload enum type while reading annotation metadata", ex);
        }
        catch (NoClassDefFoundError ex) {
            logger.debug("Failed to classload enum type while reading annotation metadata", ex);
        }
        catch (IllegalAccessException ex) {
            logger.debug("Could not access enum value while reading annotation metadata", ex);
        }
        catch (AccessControlException ex) {
            logger.debug("Could not access enum value while reading annotation metadata", ex);
        }
        return valueToUse;
    }

}

