package com.rocket.summer.framework.expression.spel.ast;

import org.objectweb.asm.MethodVisitor;
import com.rocket.summer.framework.expression.TypedValue;
import com.rocket.summer.framework.expression.spel.CodeFlow;

/**
 * Expression language AST node that represents a float literal.
 *
 * @author Satyapal Reddy
 * @author Andy Clement
 * @since 3.2
 */
public class FloatLiteral extends Literal {

	private final TypedValue value;


	public FloatLiteral(String payload, int pos, float value) {
		super(payload, pos);
		this.value = new TypedValue(value);
		this.exitTypeDescriptor = "F";
	}


	@Override
	public TypedValue getLiteralValue() {
		return this.value;
	}

	@Override
	public boolean isCompilable() {
		return true;
	}
	
	@Override
	public void generateCode(MethodVisitor mv, CodeFlow cf) {
		mv.visitLdcInsn(this.value.getValue());
		cf.pushDescriptor(this.exitTypeDescriptor);
	}

}
