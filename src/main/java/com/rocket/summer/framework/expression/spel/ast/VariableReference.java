package com.rocket.summer.framework.expression.spel.ast;

import java.lang.reflect.Modifier;

import org.objectweb.asm.MethodVisitor;
import com.rocket.summer.framework.expression.EvaluationContext;
import com.rocket.summer.framework.expression.TypedValue;
import com.rocket.summer.framework.expression.spel.CodeFlow;
import com.rocket.summer.framework.expression.spel.ExpressionState;
import com.rocket.summer.framework.expression.spel.SpelEvaluationException;

/**
 * Represents a variable reference, eg. #someVar. Note this is different to a *local*
 * variable like $someVar
 *
 * @author Andy Clement
 * @since 3.0
 */
public class VariableReference extends SpelNodeImpl {

	// Well known variables:
	private static final String THIS = "this";  // currently active context object

	private static final String ROOT = "root";  // root context object


	private final String name;


	public VariableReference(String variableName, int pos) {
		super(pos);
		this.name = variableName;
	}


	@Override
	public ValueRef getValueRef(ExpressionState state) throws SpelEvaluationException {
		if (this.name.equals(THIS)) {
			return new ValueRef.TypedValueHolderValueRef(state.getActiveContextObject(),this);
		}
		if (this.name.equals(ROOT)) {
			return new ValueRef.TypedValueHolderValueRef(state.getRootContextObject(),this);
		}
		TypedValue result = state.lookupVariable(this.name);
		// a null value will mean either the value was null or the variable was not found
		return new VariableRef(this.name,result,state.getEvaluationContext());
	}

	@Override
	public TypedValue getValueInternal(ExpressionState state) throws SpelEvaluationException {
		if (this.name.equals(THIS)) {
			return state.getActiveContextObject();
		}
		if (this.name.equals(ROOT)) {
			TypedValue result = state.getRootContextObject();
			this.exitTypeDescriptor = CodeFlow.toDescriptorFromObject(result.getValue());
			return result;
		}
		TypedValue result = state.lookupVariable(this.name);
		Object value = result.getValue();
		if (value == null || !Modifier.isPublic(value.getClass().getModifiers())) {
			// If the type is not public then when generateCode produces a checkcast to it
			// then an IllegalAccessError will occur.
			// If resorting to Object isn't sufficient, the hierarchy could be traversed for 
			// the first public type.
			this.exitTypeDescriptor = "Ljava/lang/Object";
		}
		else {
			this.exitTypeDescriptor = CodeFlow.toDescriptorFromObject(value);
		}
		// a null value will mean either the value was null or the variable was not found
		return result;
	}

	@Override
	public void setValue(ExpressionState state, Object value) throws SpelEvaluationException {
		state.setVariable(this.name, value);
	}

	@Override
	public String toStringAST() {
		return "#" + this.name;
	}

	@Override
	public boolean isWritable(ExpressionState expressionState) throws SpelEvaluationException {
		return !(this.name.equals(THIS) || this.name.equals(ROOT));
	}


	class VariableRef implements ValueRef {

		private final String name;

		private final TypedValue value;

		private final EvaluationContext evaluationContext;


		public VariableRef(String name, TypedValue value,
				EvaluationContext evaluationContext) {
			this.name = name;
			this.value = value;
			this.evaluationContext = evaluationContext;
		}


		@Override
		public TypedValue getValue() {
			return this.value;
		}

		@Override
		public void setValue(Object newValue) {
			this.evaluationContext.setVariable(this.name, newValue);
		}

		@Override
		public boolean isWritable() {
			return true;
		}
	}

	@Override
	public boolean isCompilable() {
		return this.exitTypeDescriptor!=null;
	}
	
	@Override
	public void generateCode(MethodVisitor mv, CodeFlow cf) {
		if (this.name.equals(ROOT)) {
			mv.visitVarInsn(ALOAD,1);
		}
		else {
			mv.visitVarInsn(ALOAD, 2);
			mv.visitLdcInsn(name);
			mv.visitMethodInsn(INVOKEINTERFACE, "org/springframework/expression/EvaluationContext", "lookupVariable", "(Ljava/lang/String;)Ljava/lang/Object;",true);
		}
		CodeFlow.insertCheckCast(mv,this.exitTypeDescriptor);
		cf.pushDescriptor(this.exitTypeDescriptor);
	}


}
