package com.rocket.summer.framework.context.expression;

import java.util.Map;

import com.rocket.summer.framework.expression.AccessException;
import com.rocket.summer.framework.expression.EvaluationContext;
import com.rocket.summer.framework.expression.TypedValue;
import com.rocket.summer.framework.expression.spel.CodeFlow;
import com.rocket.summer.framework.expression.spel.CompilablePropertyAccessor;
import org.objectweb.asm.MethodVisitor;

/**
 * EL property accessor that knows how to traverse the keys
 * of a standard {@link java.util.Map}.
 *
 * @author Juergen Hoeller
 * @author Andy Clement
 * @since 3.0
 */
public class MapAccessor implements CompilablePropertyAccessor {

    @Override
    public Class<?>[] getSpecificTargetClasses() {
        return new Class<?>[] {Map.class};
    }

    @Override
    public boolean canRead(EvaluationContext context, Object target, String name) throws AccessException {
        Map<?, ?> map = (Map<?, ?>) target;
        return map.containsKey(name);
    }

    @Override
    public TypedValue read(EvaluationContext context, Object target, String name) throws AccessException {
        Map<?, ?> map = (Map<?, ?>) target;
        Object value = map.get(name);
        if (value == null && !map.containsKey(name)) {
            throw new MapAccessException(name);
        }
        return new TypedValue(value);
    }

    @Override
    public boolean canWrite(EvaluationContext context, Object target, String name) throws AccessException {
        return true;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void write(EvaluationContext context, Object target, String name, Object newValue) throws AccessException {
        Map<Object, Object> map = (Map<Object, Object>) target;
        map.put(name, newValue);
    }

    @Override
    public boolean isCompilable() {
        return true;
    }

    @Override
    public Class<?> getPropertyType() {
        return Object.class;
    }

    @Override
    public void generateCode(String propertyName, MethodVisitor mv, CodeFlow cf) {
        String descriptor = cf.lastDescriptor();
        if (descriptor == null || !descriptor.equals("Ljava/util/Map")) {
            if (descriptor == null) {
                cf.loadTarget(mv);
            }
            CodeFlow.insertCheckCast(mv, "Ljava/util/Map");
        }
        mv.visitLdcInsn(propertyName);
        mv.visitMethodInsn(INVOKEINTERFACE, "java/util/Map", "get","(Ljava/lang/Object;)Ljava/lang/Object;",true);
    }


    /**
     * Exception thrown from {@code read} in order to reset a cached
     * PropertyAccessor, allowing other accessors to have a try.
     */
    @SuppressWarnings("serial")
    private static class MapAccessException extends AccessException {

        private final String key;

        public MapAccessException(String key) {
            super(null);
            this.key = key;
        }

        @Override
        public String getMessage() {
            return "Map does not contain a value for key '" + this.key + "'";
        }
    }

}
