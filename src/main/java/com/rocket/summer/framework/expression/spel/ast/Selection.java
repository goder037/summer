package com.rocket.summer.framework.expression.spel.ast;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.rocket.summer.framework.expression.EvaluationException;
import com.rocket.summer.framework.expression.TypedValue;
import com.rocket.summer.framework.expression.spel.ExpressionState;
import com.rocket.summer.framework.expression.spel.SpelEvaluationException;
import com.rocket.summer.framework.expression.spel.SpelMessage;
import com.rocket.summer.framework.util.Assert;
import com.rocket.summer.framework.util.ClassUtils;
import com.rocket.summer.framework.util.ObjectUtils;

/**
 * Represents selection over a map or collection.
 * For example: {1,2,3,4,5,6,7,8,9,10}.?{#isEven(#this) == 'y'} returns [2, 4, 6, 8, 10]
 *
 * <p>Basically a subset of the input data is returned based on the
 * evaluation of the expression supplied as selection criteria.
 *
 * @author Andy Clement
 * @author Mark Fisher
 * @author Sam Brannen
 * @author Juergen Hoeller
 * @since 3.0
 */
public class Selection extends SpelNodeImpl {

	public static final int ALL = 0; // ?[]

	public static final int FIRST = 1; // ^[]

	public static final int LAST = 2; // $[]

	private final int variant;

	private final boolean nullSafe;


	public Selection(boolean nullSafe, int variant, int pos, SpelNodeImpl expression) {
		super(pos, expression);
		Assert.notNull(expression, "Expression must not be null");
		this.nullSafe = nullSafe;
		this.variant = variant;
	}


	@Override
	public TypedValue getValueInternal(ExpressionState state) throws EvaluationException {
		return getValueRef(state).getValue();
	}

	@Override
	protected ValueRef getValueRef(ExpressionState state) throws EvaluationException {
		TypedValue op = state.getActiveContextObject();
		Object operand = op.getValue();
		SpelNodeImpl selectionCriteria = this.children[0];

		if (operand instanceof Map) {
			Map<?, ?> mapdata = (Map<?, ?>) operand;
			// TODO don't lose generic info for the new map
			Map<Object, Object> result = new HashMap<Object, Object>();
			Object lastKey = null;

			for (Map.Entry<?, ?> entry : mapdata.entrySet()) {
				try {
					TypedValue kvPair = new TypedValue(entry);
					state.pushActiveContextObject(kvPair);
					state.enterScope();
					Object val = selectionCriteria.getValueInternal(state).getValue();
					if (val instanceof Boolean) {
						if ((Boolean) val) {
							if (this.variant == FIRST) {
								result.put(entry.getKey(), entry.getValue());
								return new ValueRef.TypedValueHolderValueRef(new TypedValue(result), this);
							}
							result.put(entry.getKey(), entry.getValue());
							lastKey = entry.getKey();
						}
					}
					else {
						throw new SpelEvaluationException(selectionCriteria.getStartPosition(),
								SpelMessage.RESULT_OF_SELECTION_CRITERIA_IS_NOT_BOOLEAN);
					}
				}
				finally {
					state.popActiveContextObject();
					state.exitScope();
				}
			}

			if ((this.variant == FIRST || this.variant == LAST) && result.isEmpty()) {
				return new ValueRef.TypedValueHolderValueRef(new TypedValue(null), this);
			}

			if (this.variant == LAST) {
				Map<Object, Object> resultMap = new HashMap<Object, Object>();
				Object lastValue = result.get(lastKey);
				resultMap.put(lastKey,lastValue);
				return new ValueRef.TypedValueHolderValueRef(new TypedValue(resultMap),this);
			}

			return new ValueRef.TypedValueHolderValueRef(new TypedValue(result),this);
		}

		if (operand instanceof Iterable || ObjectUtils.isArray(operand)) {
			Iterable<?> data = (operand instanceof Iterable ?
					(Iterable<?>) operand : Arrays.asList(ObjectUtils.toObjectArray(operand)));

			List<Object> result = new ArrayList<Object>();
			int index = 0;
			for (Object element : data) {
				try {
					state.pushActiveContextObject(new TypedValue(element));
					state.enterScope("index", index);
					Object val = selectionCriteria.getValueInternal(state).getValue();
					if (val instanceof Boolean) {
						if ((Boolean) val) {
							if (this.variant == FIRST) {
								return new ValueRef.TypedValueHolderValueRef(new TypedValue(element), this);
							}
							result.add(element);
						}
					}
					else {
						throw new SpelEvaluationException(selectionCriteria.getStartPosition(),
								SpelMessage.RESULT_OF_SELECTION_CRITERIA_IS_NOT_BOOLEAN);
					}
					index++;
				}
				finally {
					state.exitScope();
					state.popActiveContextObject();
				}
			}

			if ((this.variant == FIRST || this.variant == LAST) && result.isEmpty()) {
				return ValueRef.NullValueRef.INSTANCE;
			}

			if (this.variant == LAST) {
				return new ValueRef.TypedValueHolderValueRef(new TypedValue(result.get(result.size() - 1)), this);
			}

			if (operand instanceof Iterable) {
				return new ValueRef.TypedValueHolderValueRef(new TypedValue(result), this);
			}

			Class<?> elementType = ClassUtils.resolvePrimitiveIfNecessary(
					op.getTypeDescriptor().getElementTypeDescriptor().getType());
			Object resultArray = Array.newInstance(elementType, result.size());
			System.arraycopy(result.toArray(), 0, resultArray, 0, result.size());
			return new ValueRef.TypedValueHolderValueRef(new TypedValue(resultArray), this);
		}
		if (operand == null) {
			if (this.nullSafe) {
				return ValueRef.NullValueRef.INSTANCE;
			}
			throw new SpelEvaluationException(getStartPosition(), SpelMessage.INVALID_TYPE_FOR_SELECTION, "null");
		}
		throw new SpelEvaluationException(getStartPosition(), SpelMessage.INVALID_TYPE_FOR_SELECTION,
				operand.getClass().getName());
	}

	@Override
	public String toStringAST() {
		StringBuilder sb = new StringBuilder();
		switch (this.variant) {
			case ALL:
				sb.append("?[");
				break;
			case FIRST:
				sb.append("^[");
				break;
			case LAST:
				sb.append("$[");
				break;
		}
		return sb.append(getChild(0).toStringAST()).append("]").toString();
	}

}
