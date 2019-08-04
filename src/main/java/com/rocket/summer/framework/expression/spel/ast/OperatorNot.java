package com.rocket.summer.framework.expression.spel.ast;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import com.rocket.summer.framework.expression.EvaluationException;
import com.rocket.summer.framework.expression.spel.CodeFlow;
import com.rocket.summer.framework.expression.spel.ExpressionState;
import com.rocket.summer.framework.expression.spel.SpelEvaluationException;
import com.rocket.summer.framework.expression.spel.SpelMessage;
import com.rocket.summer.framework.expression.spel.support.BooleanTypedValue;

/**
 * Represents a NOT operation.
 *
 * @author Andy Clement
 * @author Mark Fisher
 * @author Oliver Becker
 * @since 3.0
 */
public class OperatorNot extends SpelNodeImpl {  // Not is a unary operator so does not extend BinaryOperator

	public OperatorNot(int pos, SpelNodeImpl operand) {
		super(pos, operand);
		this.exitTypeDescriptor = "Z";
	}


	@Override
	public BooleanTypedValue getValueInternal(ExpressionState state) throws EvaluationException {
		try {
			Boolean value = this.children[0].getValue(state, Boolean.class);
			if (value == null) {
				throw new SpelEvaluationException(SpelMessage.TYPE_CONVERSION_ERROR, "null", "boolean");
			}
			return BooleanTypedValue.forValue(!value);
		}
		catch (SpelEvaluationException ex) {
			ex.setPosition(getChild(0).getStartPosition());
			throw ex;
		}
	}

	@Override
	public String toStringAST() {
		return "!" + getChild(0).toStringAST();
	}
	
	@Override
	public boolean isCompilable() {
		SpelNodeImpl child = this.children[0];
		return (child.isCompilable() && CodeFlow.isBooleanCompatible(child.exitTypeDescriptor));
	}
	
	@Override
	public void generateCode(MethodVisitor mv, CodeFlow cf) {
		this.children[0].generateCode(mv, cf);
		cf.unboxBooleanIfNecessary(mv);
		Label elseTarget = new Label();
		Label endOfIf = new Label();
		mv.visitJumpInsn(IFNE,elseTarget);		
		mv.visitInsn(ICONST_1); // TRUE
		mv.visitJumpInsn(GOTO,endOfIf);
		mv.visitLabel(elseTarget);
		mv.visitInsn(ICONST_0); // FALSE
		mv.visitLabel(endOfIf);
		cf.pushDescriptor(this.exitTypeDescriptor);
	}

}
