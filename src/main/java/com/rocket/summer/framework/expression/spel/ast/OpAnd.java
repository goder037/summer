package com.rocket.summer.framework.expression.spel.ast;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import com.rocket.summer.framework.expression.EvaluationException;
import com.rocket.summer.framework.expression.TypedValue;
import com.rocket.summer.framework.expression.spel.CodeFlow;
import com.rocket.summer.framework.expression.spel.ExpressionState;
import com.rocket.summer.framework.expression.spel.SpelEvaluationException;
import com.rocket.summer.framework.expression.spel.SpelMessage;
import com.rocket.summer.framework.expression.spel.support.BooleanTypedValue;

/**
 * Represents the boolean AND operation.
 *
 * @author Andy Clement
 * @author Mark Fisher
 * @author Oliver Becker
 * @since 3.0
 */
public class OpAnd extends Operator {

	public OpAnd(int pos, SpelNodeImpl... operands) {
		super("and", pos, operands);
		this.exitTypeDescriptor = "Z";
	}


	@Override
	public TypedValue getValueInternal(ExpressionState state) throws EvaluationException {
		if (!getBooleanValue(state, getLeftOperand())) {
			// no need to evaluate right operand
			return BooleanTypedValue.FALSE;
		}
		return BooleanTypedValue.forValue(getBooleanValue(state, getRightOperand()));
	}

	private boolean getBooleanValue(ExpressionState state, SpelNodeImpl operand) {
		try {
			Boolean value = operand.getValue(state, Boolean.class);
			assertValueNotNull(value);
			return value;
		}
		catch (SpelEvaluationException ex) {
			ex.setPosition(operand.getStartPosition());
			throw ex;
		}
	}

	private void assertValueNotNull(Boolean value) {
		if (value == null) {
			throw new SpelEvaluationException(SpelMessage.TYPE_CONVERSION_ERROR, "null", "boolean");
		}
	}

	@Override
	public boolean isCompilable() {
		SpelNodeImpl left = getLeftOperand();
		SpelNodeImpl right = getRightOperand();
		return (left.isCompilable() && right.isCompilable() &&
				CodeFlow.isBooleanCompatible(left.exitTypeDescriptor) &&
				CodeFlow.isBooleanCompatible(right.exitTypeDescriptor));
	}
	
	@Override
	public void generateCode(MethodVisitor mv, CodeFlow cf) {
		// Pseudo: if (!leftOperandValue) { result=false; } else { result=rightOperandValue; }
		Label elseTarget = new Label();
		Label endOfIf = new Label();
		cf.enterCompilationScope();
		getLeftOperand().generateCode(mv, cf);
		cf.unboxBooleanIfNecessary(mv);
		cf.exitCompilationScope();
		mv.visitJumpInsn(IFNE, elseTarget);
		mv.visitLdcInsn(0); // FALSE
		mv.visitJumpInsn(GOTO,endOfIf);
		mv.visitLabel(elseTarget);
		cf.enterCompilationScope();
		getRightOperand().generateCode(mv, cf);
		cf.unboxBooleanIfNecessary(mv);
		cf.exitCompilationScope();
		mv.visitLabel(endOfIf);
		cf.pushDescriptor(this.exitTypeDescriptor);
	}

}
