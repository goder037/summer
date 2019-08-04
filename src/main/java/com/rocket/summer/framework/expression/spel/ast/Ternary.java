package com.rocket.summer.framework.expression.spel.ast;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import com.rocket.summer.framework.expression.EvaluationException;
import com.rocket.summer.framework.expression.TypedValue;
import com.rocket.summer.framework.expression.spel.CodeFlow;
import com.rocket.summer.framework.expression.spel.ExpressionState;
import com.rocket.summer.framework.expression.spel.SpelEvaluationException;
import com.rocket.summer.framework.expression.spel.SpelMessage;

/**
 * Represents a ternary expression, for example: "someCheck()?true:false".
 *
 * @author Andy Clement
 * @author Juergen Hoeller
 * @since 3.0
 */
public class Ternary extends SpelNodeImpl {

	public Ternary(int pos, SpelNodeImpl... args) {
		super(pos, args);
	}


	/**
	 * Evaluate the condition and if true evaluate the first alternative, otherwise
	 * evaluate the second alternative.
	 * @param state the expression state
	 * @throws EvaluationException if the condition does not evaluate correctly to
	 * a boolean or there is a problem executing the chosen alternative
	 */
	@Override
	public TypedValue getValueInternal(ExpressionState state) throws EvaluationException {
		Boolean value = this.children[0].getValue(state, Boolean.class);
		if (value == null) {
			throw new SpelEvaluationException(getChild(0).getStartPosition(),
					SpelMessage.TYPE_CONVERSION_ERROR, "null", "boolean");
		}
		TypedValue result = this.children[value ? 1 : 2].getValueInternal(state);
		computeExitTypeDescriptor();
		return result;
	}
	
	@Override
	public String toStringAST() {
		return getChild(0).toStringAST() + " ? " + getChild(1).toStringAST() + " : " + getChild(2).toStringAST();
	}

	private void computeExitTypeDescriptor() {
		if (this.exitTypeDescriptor == null && this.children[1].exitTypeDescriptor != null &&
				this.children[2].exitTypeDescriptor != null) {
			String leftDescriptor = this.children[1].exitTypeDescriptor;
			String rightDescriptor = this.children[2].exitTypeDescriptor;
			if (leftDescriptor.equals(rightDescriptor)) {
				this.exitTypeDescriptor = leftDescriptor;
			}
			else {
				// Use the easiest to compute common super type
				this.exitTypeDescriptor = "Ljava/lang/Object";
			}
		}
	}

	@Override
	public boolean isCompilable() {
		SpelNodeImpl condition = this.children[0];
		SpelNodeImpl left = this.children[1];
		SpelNodeImpl right = this.children[2];
		return (condition.isCompilable() && left.isCompilable() && right.isCompilable() &&
				CodeFlow.isBooleanCompatible(condition.exitTypeDescriptor) &&
				left.exitTypeDescriptor != null && right.exitTypeDescriptor != null);
	}
	
	@Override
	public void generateCode(MethodVisitor mv, CodeFlow cf) {
		// May reach here without it computed if all elements are literals
		computeExitTypeDescriptor();
		cf.enterCompilationScope();
		this.children[0].generateCode(mv, cf);
		if (!CodeFlow.isPrimitive(cf.lastDescriptor())) {
			CodeFlow.insertUnboxInsns(mv, 'Z', cf.lastDescriptor());
		}
		cf.exitCompilationScope();
		Label elseTarget = new Label();
		Label endOfIf = new Label();
		mv.visitJumpInsn(IFEQ, elseTarget);
		cf.enterCompilationScope();
		this.children[1].generateCode(mv, cf);
		if (!CodeFlow.isPrimitive(this.exitTypeDescriptor)) {
			CodeFlow.insertBoxIfNecessary(mv, cf.lastDescriptor().charAt(0));
		}
		cf.exitCompilationScope();
		mv.visitJumpInsn(GOTO, endOfIf);
		mv.visitLabel(elseTarget);
		cf.enterCompilationScope();
		this.children[2].generateCode(mv, cf);
		if (!CodeFlow.isPrimitive(this.exitTypeDescriptor)) {
			CodeFlow.insertBoxIfNecessary(mv, cf.lastDescriptor().charAt(0));
		}
		cf.exitCompilationScope();
		mv.visitLabel(endOfIf);
		cf.pushDescriptor(this.exitTypeDescriptor);
	}

}
