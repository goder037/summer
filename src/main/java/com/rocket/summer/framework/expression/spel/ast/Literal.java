package com.rocket.summer.framework.expression.spel.ast;

import com.rocket.summer.framework.expression.TypedValue;
import com.rocket.summer.framework.expression.spel.ExpressionState;
import com.rocket.summer.framework.expression.spel.InternalParseException;
import com.rocket.summer.framework.expression.spel.SpelEvaluationException;
import com.rocket.summer.framework.expression.spel.SpelMessage;
import com.rocket.summer.framework.expression.spel.SpelParseException;

/**
 * Common superclass for nodes representing literals (boolean, string, number, etc).
 *
 * @author Andy Clement
 * @author Juergen Hoeller
 */
public abstract class Literal extends SpelNodeImpl {

	private final String originalValue;


	public Literal(String originalValue, int pos) {
		super(pos);
		this.originalValue = originalValue;
	}


	public final String getOriginalValue() {
		return this.originalValue;
	}

	@Override
	public final TypedValue getValueInternal(ExpressionState state) throws SpelEvaluationException {
		return getLiteralValue();
	}

	@Override
	public String toString() {
		return getLiteralValue().getValue().toString();
	}

	@Override
	public String toStringAST() {
		return toString();
	}


	public abstract TypedValue getLiteralValue();


	/**
	 * Process the string form of a number, using the specified base if supplied
	 * and return an appropriate literal to hold it. Any suffix to indicate a
	 * long will be taken into account (either 'l' or 'L' is supported).
	 * @param numberToken the token holding the number as its payload (eg. 1234 or 0xCAFE)
	 * @param radix the base of number
	 * @return a subtype of Literal that can represent it
	 */
	public static Literal getIntLiteral(String numberToken, int pos, int radix) {
		try {
			int value = Integer.parseInt(numberToken, radix);
			return new IntLiteral(numberToken, pos, value);
		}
		catch (NumberFormatException ex) {
			throw new InternalParseException(new SpelParseException(pos>>16, ex, SpelMessage.NOT_AN_INTEGER, numberToken));
		}
	}

	public static Literal getLongLiteral(String numberToken, int pos, int radix) {
		try {
			long value = Long.parseLong(numberToken, radix);
			return new LongLiteral(numberToken, pos, value);
		}
		catch (NumberFormatException ex) {
			throw new InternalParseException(new SpelParseException(pos>>16, ex, SpelMessage.NOT_A_LONG, numberToken));
		}
	}

	public static Literal getRealLiteral(String numberToken, int pos, boolean isFloat) {
		try {
			if (isFloat) {
				float value = Float.parseFloat(numberToken);
				return new FloatLiteral(numberToken, pos, value);
			}
			else {
				double value = Double.parseDouble(numberToken);
				return new RealLiteral(numberToken, pos, value);
			}
		}
		catch (NumberFormatException ex) {
			throw new InternalParseException(new SpelParseException(pos>>16, ex, SpelMessage.NOT_A_REAL, numberToken));
		}
	}

}
