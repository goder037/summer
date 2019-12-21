package com.rocket.summer.framework.expression.spel.ast;

import com.rocket.summer.framework.asm.MethodVisitor;
import com.rocket.summer.framework.asm.Type;
import com.rocket.summer.framework.expression.EvaluationException;
import com.rocket.summer.framework.expression.TypedValue;
import com.rocket.summer.framework.expression.spel.CodeFlow;
import com.rocket.summer.framework.expression.spel.ExpressionState;
import com.rocket.summer.framework.expression.spel.SpelEvaluationException;
import com.rocket.summer.framework.expression.spel.SpelMessage;
import com.rocket.summer.framework.expression.spel.support.BooleanTypedValue;

/**
 * The operator 'instanceof' checks if an object is of the class specified in the right
 * hand operand, in the same way that {@code instanceof} does in Java.
 *
 * @author Andy Clement
 * @since 3.0
 */
public class OperatorInstanceof extends Operator {

	private Class<?> type;
	

	public OperatorInstanceof(int pos, SpelNodeImpl... operands) {
		super("instanceof", pos, operands);
	}


	/**
	 * Compare the left operand to see it is an instance of the type specified as the
	 * right operand. The right operand must be a class.
	 * @param state the expression state
	 * @return {@code true} if the left operand is an instanceof of the right operand,
	 * otherwise {@code false}
	 * @throws EvaluationException if there is a problem evaluating the expression
	 */
	@Override
	public BooleanTypedValue getValueInternal(ExpressionState state) throws EvaluationException {
		SpelNodeImpl rightOperand = getRightOperand();
		TypedValue left = getLeftOperand().getValueInternal(state);
		TypedValue right = rightOperand.getValueInternal(state);
		Object leftValue = left.getValue();
		Object rightValue = right.getValue();
		BooleanTypedValue result;
		if (rightValue == null || !(rightValue instanceof Class)) {
			throw new SpelEvaluationException(getRightOperand().getStartPosition(),
					SpelMessage.INSTANCEOF_OPERATOR_NEEDS_CLASS_OPERAND,
					(rightValue == null ? "null" : rightValue.getClass().getName()));
		}
		Class<?> rightClass = (Class<?>) rightValue;
		if (leftValue == null) {
			result = BooleanTypedValue.FALSE;  // null is not an instanceof anything
		}
		else {
			result = BooleanTypedValue.forValue(rightClass.isAssignableFrom(leftValue.getClass()));
		}
		this.type = rightClass;
		if (rightOperand instanceof TypeReference) {
			// Can only generate bytecode where the right operand is a direct type reference, 
			// not if it is indirect (for example when right operand is a variable reference)
			this.exitTypeDescriptor = "Z";
		}
		return result;
	}

	@Override
	public boolean isCompilable() {
		return (this.exitTypeDescriptor != null && getLeftOperand().isCompilable());
	}
	
	@Override
	public void generateCode(MethodVisitor mv, CodeFlow cf) {
		getLeftOperand().generateCode(mv, cf);
		CodeFlow.insertBoxIfNecessary(mv, cf.lastDescriptor());
		if (this.type.isPrimitive()) {
			// always false - but left operand code always driven
			// in case it had side effects
			mv.visitInsn(POP);
			mv.visitInsn(ICONST_0); // value of false
		} 
		else {
			mv.visitTypeInsn(INSTANCEOF, Type.getInternalName(this.type));
		}
		cf.pushDescriptor(this.exitTypeDescriptor);
	}

}
