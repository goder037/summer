package com.rocket.summer.framework.expression.spel.ast;

import java.math.BigDecimal;
import java.math.BigInteger;

import com.rocket.summer.framework.expression.EvaluationException;
import com.rocket.summer.framework.expression.Operation;
import com.rocket.summer.framework.expression.TypedValue;
import com.rocket.summer.framework.expression.spel.CodeFlow;
import com.rocket.summer.framework.expression.spel.ExpressionState;
import com.rocket.summer.framework.util.NumberUtils;
import org.objectweb.asm.MethodVisitor;

/**
 * Implements the {@code multiply} operator.
 *
 * <p>Conversions and promotions are handled as defined in
 * <a href="https://java.sun.com/docs/books/jls/third_edition/html/conversions.html">Section 5.6.2 of the
 * Java Language Specification</a>, with the addiction of {@code BigDecimal}/{@code BigInteger} management:
 *
 * <p>If any of the operands is of a reference type, unboxing conversion (Section 5.1.8)
 * is performed. Then:<br>
 * If either operand is of type {@code BigDecimal}, the other is converted to {@code BigDecimal}.<br>
 * If either operand is of type double, the other is converted to double.<br>
 * Otherwise, if either operand is of type float, the other is converted to float.<br>
 * If either operand is of type {@code BigInteger}, the other is converted to {@code BigInteger}.<br>
 * Otherwise, if either operand is of type long, the other is converted to long.<br>
 * Otherwise, both operands are converted to type int.
 *
 * @author Andy Clement
 * @author Juergen Hoeller
 * @author Sam Brannen
 * @author Giovanni Dall'Oglio Risso
 * @since 3.0
 */
public class OpMultiply extends Operator {

	public OpMultiply(int pos, SpelNodeImpl... operands) {
		super("*", pos, operands);
	}


	/**
	 * Implements the {@code multiply} operator directly here for certain types
	 * of supported operands and otherwise delegates to any registered overloader
	 * for types not supported here.
	 * <p>Supported operand types:
	 * <ul>
	 * <li>numbers
	 * <li>String and int ('abc' * 2 == 'abcabc')
	 * </ul>
	 */
	@Override
	public TypedValue getValueInternal(ExpressionState state) throws EvaluationException {
		Object leftOperand = getLeftOperand().getValueInternal(state).getValue();
		Object rightOperand = getRightOperand().getValueInternal(state).getValue();

		if (leftOperand instanceof Number && rightOperand instanceof Number) {
			Number leftNumber = (Number) leftOperand;
			Number rightNumber = (Number) rightOperand;

			if (leftNumber instanceof BigDecimal || rightNumber instanceof BigDecimal) {
				BigDecimal leftBigDecimal = NumberUtils.convertNumberToTargetClass(leftNumber, BigDecimal.class);
				BigDecimal rightBigDecimal = NumberUtils.convertNumberToTargetClass(rightNumber, BigDecimal.class);
				return new TypedValue(leftBigDecimal.multiply(rightBigDecimal));
			}
			else if (leftNumber instanceof Double || rightNumber instanceof Double) {
				this.exitTypeDescriptor = "D";
				return new TypedValue(leftNumber.doubleValue() * rightNumber.doubleValue());
			}
			else if (leftNumber instanceof Float || rightNumber instanceof Float) {
				this.exitTypeDescriptor = "F";
				return new TypedValue(leftNumber.floatValue() * rightNumber.floatValue());
			}
			else if (leftNumber instanceof BigInteger || rightNumber instanceof BigInteger) {
				BigInteger leftBigInteger = NumberUtils.convertNumberToTargetClass(leftNumber, BigInteger.class);
				BigInteger rightBigInteger = NumberUtils.convertNumberToTargetClass(rightNumber, BigInteger.class);
				return new TypedValue(leftBigInteger.multiply(rightBigInteger));
			}
			else if (leftNumber instanceof Long || rightNumber instanceof Long) {
				this.exitTypeDescriptor = "J";
				return new TypedValue(leftNumber.longValue() * rightNumber.longValue());
			}
			else if (CodeFlow.isIntegerForNumericOp(leftNumber) || CodeFlow.isIntegerForNumericOp(rightNumber)) {
				this.exitTypeDescriptor = "I";
				return new TypedValue(leftNumber.intValue() * rightNumber.intValue());
			}
			else {
				// Unknown Number subtypes -> best guess is double multiplication
				return new TypedValue(leftNumber.doubleValue() * rightNumber.doubleValue());
			}
		}

		if (leftOperand instanceof String && rightOperand instanceof Integer) {
			int repeats = (Integer) rightOperand;
			StringBuilder result = new StringBuilder();
			for (int i = 0; i < repeats; i++) {
				result.append(leftOperand);
			}
			return new TypedValue(result.toString());
		}

		return state.operate(Operation.MULTIPLY, leftOperand, rightOperand);
	}

	@Override
	public boolean isCompilable() {
		if (!getLeftOperand().isCompilable()) {
			return false;
		}
		if (this.children.length > 1) {
			 if (!getRightOperand().isCompilable()) {
				 return false;
			 }
		}
		return (this.exitTypeDescriptor != null);
	}
	
	@Override
	public void generateCode(MethodVisitor mv, CodeFlow cf) {
		getLeftOperand().generateCode(mv, cf);
		String leftDesc = getLeftOperand().exitTypeDescriptor;
		CodeFlow.insertNumericUnboxOrPrimitiveTypeCoercion(mv, leftDesc, this.exitTypeDescriptor.charAt(0));
		if (this.children.length > 1) {
			cf.enterCompilationScope();
			getRightOperand().generateCode(mv, cf);
			String rightDesc = getRightOperand().exitTypeDescriptor;
			cf.exitCompilationScope();
			CodeFlow.insertNumericUnboxOrPrimitiveTypeCoercion(mv, rightDesc, this.exitTypeDescriptor.charAt(0));
			switch (this.exitTypeDescriptor.charAt(0)) {
				case 'I':
					mv.visitInsn(IMUL);
					break;
				case 'J':
					mv.visitInsn(LMUL);
					break;
				case 'F': 
					mv.visitInsn(FMUL);
					break;
				case 'D':
					mv.visitInsn(DMUL);
					break;				
				default:
					throw new IllegalStateException(
							"Unrecognized exit type descriptor: '" + this.exitTypeDescriptor + "'");
			}
		}
		cf.pushDescriptor(this.exitTypeDescriptor);
	}

}
