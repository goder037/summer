package com.rocket.summer.framework.expression.spel.ast;

import java.lang.reflect.Array;

import com.rocket.summer.framework.asm.MethodVisitor;
import com.rocket.summer.framework.asm.Type;
import com.rocket.summer.framework.expression.EvaluationException;
import com.rocket.summer.framework.expression.TypedValue;
import com.rocket.summer.framework.expression.spel.CodeFlow;
import com.rocket.summer.framework.expression.spel.ExpressionState;

/**
 * Represents a reference to a type, for example "T(String)" or "T(com.somewhere.Foo)"
 *
 * @author Andy Clement
 */
public class TypeReference extends SpelNodeImpl {

	private final int dimensions;

	private transient Class<?> type;


	public TypeReference(int pos, SpelNodeImpl qualifiedId) {
		this(pos, qualifiedId, 0);
	}

	public TypeReference(int pos, SpelNodeImpl qualifiedId, int dims) {
		super(pos, qualifiedId);
		this.dimensions = dims;
	}


	@Override
	public TypedValue getValueInternal(ExpressionState state) throws EvaluationException {
		// TODO possible optimization here if we cache the discovered type reference, but can we do that?
		String typeName = (String) this.children[0].getValueInternal(state).getValue();
		if (!typeName.contains(".") && Character.isLowerCase(typeName.charAt(0))) {
			TypeCode tc = TypeCode.valueOf(typeName.toUpperCase());
			if (tc != TypeCode.OBJECT) {
				// It is a primitive type
				Class<?> clazz = makeArrayIfNecessary(tc.getType());
				this.exitTypeDescriptor = "Ljava/lang/Class";
				this.type = clazz;
				return new TypedValue(clazz);
			}
		}
		Class<?> clazz = state.findType(typeName);
		clazz = makeArrayIfNecessary(clazz);
		this.exitTypeDescriptor = "Ljava/lang/Class";
		this.type = clazz;
		return new TypedValue(clazz);
	}

	private Class<?> makeArrayIfNecessary(Class<?> clazz) {
		if (this.dimensions != 0) {
			for (int i = 0; i < this.dimensions; i++) {
				Object array = Array.newInstance(clazz, 0);
				clazz = array.getClass();
			}
		}
		return clazz;
	}

	@Override
	public String toStringAST() {
		StringBuilder sb = new StringBuilder("T(");
		sb.append(getChild(0).toStringAST());
		for (int d = 0; d < this.dimensions; d++) {
			sb.append("[]");
		}
		sb.append(")");
		return sb.toString();
	}
	
	@Override
	public boolean isCompilable() {
		return (this.exitTypeDescriptor != null);
	}
	
	@Override
	public void generateCode(MethodVisitor mv, CodeFlow cf) {
		// TODO Future optimization - if followed by a static method call, skip generating code here
		if (this.type.isPrimitive()) {
			if (this.type == Boolean.TYPE) {
				mv.visitFieldInsn(GETSTATIC, "java/lang/Boolean", "TYPE", "Ljava/lang/Class;");
			}
			else if (this.type == Byte.TYPE) {
				mv.visitFieldInsn(GETSTATIC, "java/lang/Byte", "TYPE", "Ljava/lang/Class;");
			}
			else if (this.type == Character.TYPE) {
				mv.visitFieldInsn(GETSTATIC, "java/lang/Character", "TYPE", "Ljava/lang/Class;");
			}
			else if (this.type == Double.TYPE) {
				mv.visitFieldInsn(GETSTATIC, "java/lang/Double", "TYPE", "Ljava/lang/Class;");
			}
			else if (this.type == Float.TYPE) {
				mv.visitFieldInsn(GETSTATIC, "java/lang/Float", "TYPE", "Ljava/lang/Class;");
			}
			else if (this.type == Integer.TYPE) {
				mv.visitFieldInsn(GETSTATIC, "java/lang/Integer", "TYPE", "Ljava/lang/Class;");
			}
			else if (this.type == Long.TYPE) {
				mv.visitFieldInsn(GETSTATIC, "java/lang/Long", "TYPE", "Ljava/lang/Class;");
			}
			else if (this.type == Short.TYPE) {
				mv.visitFieldInsn(GETSTATIC, "java/lang/Short", "TYPE", "Ljava/lang/Class;");
			}
		}
		else {
			mv.visitLdcInsn(Type.getType(this.type));
		}
		cf.pushDescriptor(this.exitTypeDescriptor);
	}

}
