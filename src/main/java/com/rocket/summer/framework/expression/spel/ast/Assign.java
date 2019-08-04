package com.rocket.summer.framework.expression.spel.ast;

import com.rocket.summer.framework.expression.EvaluationException;
import com.rocket.summer.framework.expression.TypedValue;
import com.rocket.summer.framework.expression.spel.ExpressionState;

/**
 * Represents assignment. An alternative to calling {@code setValue}
 * for an expression which indicates an assign statement.
 *
 * <p>Example: 'someNumberProperty=42'
 *
 * @author Andy Clement
 * @since 3.0
 */
public class Assign extends SpelNodeImpl {

	public Assign(int pos, SpelNodeImpl... operands) {
		super(pos, operands);
	}


	@Override
	public TypedValue getValueInternal(ExpressionState state) throws EvaluationException {
		TypedValue newValue = this.children[1].getValueInternal(state);
		getChild(0).setValue(state, newValue.getValue());
		return newValue;
	}

	@Override
	public String toStringAST() {
		return getChild(0).toStringAST() + "=" + getChild(1).toStringAST();
	}

}
