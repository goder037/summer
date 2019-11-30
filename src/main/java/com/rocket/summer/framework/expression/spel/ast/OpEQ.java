package com.rocket.summer.framework.expression.spel.ast;

import com.rocket.summer.framework.asm.MethodVisitor;
import com.rocket.summer.framework.expression.EvaluationContext;
import com.rocket.summer.framework.expression.EvaluationException;
import com.rocket.summer.framework.expression.spel.CodeFlow;
import com.rocket.summer.framework.expression.spel.ExpressionState;
import com.rocket.summer.framework.expression.spel.support.BooleanTypedValue;

/**
 * Implements the equality operator.
 *
 * @author Andy Clement
 * @since 3.0
 */
public class OpEQ extends Operator {

	public OpEQ(int pos, SpelNodeImpl... operands) {
		super("==", pos, operands);
		this.exitTypeDescriptor = "Z";
	}


	@Override
	public BooleanTypedValue getValueInternal(ExpressionState state) throws EvaluationException {
		Object left = getLeftOperand().getValueInternal(state).getValue();
		Object right = getRightOperand().getValueInternal(state).getValue();
		this.leftActualDescriptor = CodeFlow.toDescriptorFromObject(left);
		this.rightActualDescriptor = CodeFlow.toDescriptorFromObject(right);
		return BooleanTypedValue.forValue(equalityCheck(state.getEvaluationContext(), left, right));
	}

	// This check is different to the one in the other numeric operators (OpLt/etc)
	// because it allows for simple object comparison
	@Override
	public boolean isCompilable() {
		SpelNodeImpl left = getLeftOperand();
		SpelNodeImpl right = getRightOperand();
		if (!left.isCompilable() || !right.isCompilable()) {
			return false;
		}

		String leftDesc = left.exitTypeDescriptor;
		String rightDesc = right.exitTypeDescriptor;
		DescriptorComparison dc = DescriptorComparison.checkNumericCompatibility(leftDesc,
				rightDesc, this.leftActualDescriptor, this.rightActualDescriptor);
		return (!dc.areNumbers || dc.areCompatible);
	}

	@Override
	public void generateCode(MethodVisitor mv, CodeFlow cf) {
		cf.loadEvaluationContext(mv);
		String leftDesc = getLeftOperand().exitTypeDescriptor;
		String rightDesc = getRightOperand().exitTypeDescriptor;
		boolean leftPrim = CodeFlow.isPrimitive(leftDesc);
		boolean rightPrim = CodeFlow.isPrimitive(rightDesc);

		cf.enterCompilationScope();
		getLeftOperand().generateCode(mv, cf);
		cf.exitCompilationScope();
		if (leftPrim) {
			CodeFlow.insertBoxIfNecessary(mv, leftDesc.charAt(0));
		}
		cf.enterCompilationScope();
		getRightOperand().generateCode(mv, cf);
		cf.exitCompilationScope();
		if (rightPrim) {
			CodeFlow.insertBoxIfNecessary(mv, rightDesc.charAt(0));
		}

		String operatorClassName = Operator.class.getName().replace('.', '/');
		String evaluationContextClassName = EvaluationContext.class.getName().replace('.', '/');
		mv.visitMethodInsn(INVOKESTATIC, operatorClassName, "equalityCheck",
				"(L" + evaluationContextClassName + ";Ljava/lang/Object;Ljava/lang/Object;)Z", false);
		cf.pushDescriptor("Z");
	}

}
