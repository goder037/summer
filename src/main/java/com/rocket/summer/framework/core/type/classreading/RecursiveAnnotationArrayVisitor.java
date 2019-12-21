package com.rocket.summer.framework.core.type.classreading;

import com.rocket.summer.framework.core.annotation.AnnotationAttributes;
import com.rocket.summer.framework.util.ObjectUtils;
import com.rocket.summer.framework.asm.AnnotationVisitor;
import com.rocket.summer.framework.asm.Type;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Chris Beams
 * @author Juergen Hoeller
 * @since 3.1.1
 */
class RecursiveAnnotationArrayVisitor extends AbstractRecursiveAnnotationVisitor {

    private final String attributeName;

    private final List<AnnotationAttributes> allNestedAttributes = new ArrayList<AnnotationAttributes>();


    public RecursiveAnnotationArrayVisitor(
            String attributeName, AnnotationAttributes attributes, ClassLoader classLoader) {

        super(classLoader, attributes);
        this.attributeName = attributeName;
    }


    @Override
    public void visit(String attributeName, Object attributeValue) {
        Object newValue = attributeValue;
        Object existingValue = this.attributes.get(this.attributeName);
        if (existingValue != null) {
            newValue = ObjectUtils.addObjectToArray((Object[]) existingValue, newValue);
        }
        else {
            Class<?> arrayClass = newValue.getClass();
            if (Enum.class.isAssignableFrom(arrayClass)) {
                while (arrayClass.getSuperclass() != null && !arrayClass.isEnum()) {
                    arrayClass = arrayClass.getSuperclass();
                }
            }
            Object[] newArray = (Object[]) Array.newInstance(arrayClass, 1);
            newArray[0] = newValue;
            newValue = newArray;
        }
        this.attributes.put(this.attributeName, newValue);
    }

    @Override
    public AnnotationVisitor visitAnnotation(String attributeName, String asmTypeDescriptor) {
        String annotationType = Type.getType(asmTypeDescriptor).getClassName();
        AnnotationAttributes nestedAttributes = new AnnotationAttributes(annotationType, this.classLoader);
        this.allNestedAttributes.add(nestedAttributes);
        return new RecursiveAnnotationAttributesVisitor(annotationType, nestedAttributes, this.classLoader);
    }

    @Override
    public void visitEnd() {
        if (!this.allNestedAttributes.isEmpty()) {
            this.attributes.put(this.attributeName,
                    this.allNestedAttributes.toArray(new AnnotationAttributes[this.allNestedAttributes.size()]));
        }
    }

}

