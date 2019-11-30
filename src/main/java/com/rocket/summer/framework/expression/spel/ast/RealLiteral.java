package com.rocket.summer.framework.expression.spel.ast;

import com.rocket.summer.framework.asm.MethodVisitor;
import com.rocket.summer.framework.expression.TypedValue;
import com.rocket.summer.framework.expression.spel.CodeFlow;

/**
 * Expression language AST node that represents a real literal.
 *
 * @author Andy Clement
 * @since 3.0
 */
public class RealLiteral extends Literal {

	private final TypedValue value;


	public RealLiteral(String payload, int pos, double value) {
		super(payload, pos);
		this.value = new TypedValue(value);
		this.exitTypeDescriptor = "D";
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
