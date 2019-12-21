package com.rocket.summer.framework.expression.spel.ast;

import java.math.BigDecimal;
import java.math.BigInteger;

import com.rocket.summer.framework.expression.EvaluationException;
import com.rocket.summer.framework.expression.Operation;
import com.rocket.summer.framework.expression.TypedValue;
import com.rocket.summer.framework.expression.spel.ExpressionState;
import com.rocket.summer.framework.util.NumberUtils;

/**
 * The power operator.
 *
 * @author Andy Clement
 * @author Giovanni Dall'Oglio Risso
 * @since 3.0
 */
public class OperatorPower extends Operator {

	public OperatorPower(int pos, SpelNodeImpl... operands) {
		super("^", pos, operands);
	}


	@Override
	public TypedValue getValueInternal(ExpressionState state) throws EvaluationException {
		SpelNodeImpl leftOp = getLeftOperand();
		SpelNodeImpl rightOp = getRightOperand();

		Object leftOperand = leftOp.getValueInternal(state).getValue();
		Object rightOperand = rightOp.getValueInternal(state).getValue();

		if (leftOperand instanceof Number && rightOperand instanceof Number) {
			Number leftNumber = (Number) leftOperand;
			Number rightNumber = (Number) rightOperand;

			if (leftNumber instanceof BigDecimal) {
				BigDecimal leftBigDecimal = NumberUtils.convertNumberToTargetClass(leftNumber, BigDecimal.class);
				return new TypedValue(leftBigDecimal.pow(rightNumber.intValue()));
			}
			else if (leftNumber instanceof BigInteger) {
				BigInteger leftBigInteger = NumberUtils.convertNumberToTargetClass(leftNumber, BigInteger.class);
				return new TypedValue(leftBigInteger.pow(rightNumber.intValue()));
			}
			else if (leftNumber instanceof Double || rightNumber instanceof Double) {
				return new TypedValue(Math.pow(leftNumber.doubleValue(), rightNumber.doubleValue()));
			}
			else if (leftNumber instanceof Float || rightNumber instanceof Float) {
				return new TypedValue(Math.pow(leftNumber.floatValue(), rightNumber.floatValue()));
			}

			double d = Math.pow(leftNumber.doubleValue(), rightNumber.doubleValue());
			if (d > Integer.MAX_VALUE || leftNumber instanceof Long || rightNumber instanceof Long) {
				return new TypedValue((long) d);
			}
			else {
				return new TypedValue((int) d);
			}
		}

		return state.operate(Operation.POWER, leftOperand, rightOperand);
	}

}
