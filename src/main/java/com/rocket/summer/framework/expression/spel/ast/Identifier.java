package com.rocket.summer.framework.expression.spel.ast;

import com.rocket.summer.framework.expression.TypedValue;
import com.rocket.summer.framework.expression.spel.ExpressionState;

/**
 * @author Andy Clement
 * @since 3.0
 */
public class Identifier extends SpelNodeImpl {

	private final TypedValue id;


	public Identifier(String payload, int pos) {
		super(pos);
		this.id = new TypedValue(payload);
	}


	@Override
	public String toStringAST() {
		return (String) this.id.getValue();
	}

	@Override
	public TypedValue getValueInternal(ExpressionState state) {
		return this.id;
	}

}
