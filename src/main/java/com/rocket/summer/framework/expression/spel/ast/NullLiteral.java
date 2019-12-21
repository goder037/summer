package com.rocket.summer.framework.expression.spel.ast;

import com.rocket.summer.framework.asm.MethodVisitor;
import com.rocket.summer.framework.expression.TypedValue;
import com.rocket.summer.framework.expression.spel.CodeFlow;

/**
 * Expression language AST node that represents null.
 *
 * @author Andy Clement
 * @since 3.0
 */
public class NullLiteral extends Literal {

	public NullLiteral(int pos) {
		super(null, pos);
		this.exitTypeDescriptor = "Ljava/lang/Object";
	}


	@Override
	public TypedValue getLiteralValue() {
		return TypedValue.NULL;
	}

	@Override
	public String toString() {
		return "null";
	}

	@Override
	public boolean isCompilable() {
		return true;
	}
	
	@Override
	public void generateCode(MethodVisitor mv, CodeFlow cf) {
		mv.visitInsn(ACONST_NULL);
		cf.pushDescriptor(this.exitTypeDescriptor);
	}

}
