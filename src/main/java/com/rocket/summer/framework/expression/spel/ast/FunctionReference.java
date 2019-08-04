package com.rocket.summer.framework.expression.spel.ast;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.objectweb.asm.MethodVisitor;
import com.rocket.summer.framework.core.MethodParameter;
import com.rocket.summer.framework.core.convert.TypeDescriptor;
import com.rocket.summer.framework.expression.EvaluationException;
import com.rocket.summer.framework.expression.TypeConverter;
import com.rocket.summer.framework.expression.TypedValue;
import com.rocket.summer.framework.expression.spel.CodeFlow;
import com.rocket.summer.framework.expression.spel.ExpressionState;
import com.rocket.summer.framework.expression.spel.SpelEvaluationException;
import com.rocket.summer.framework.expression.spel.SpelMessage;
import com.rocket.summer.framework.expression.spel.support.ReflectionHelper;
import com.rocket.summer.framework.util.Assert;
import com.rocket.summer.framework.util.ClassUtils;
import com.rocket.summer.framework.util.ReflectionUtils;

/**
 * A function reference is of the form "#someFunction(a,b,c)". Functions may be defined
 * in the context prior to the expression being evaluated. Functions may also be static
 * Java methods, registered in the context prior to invocation of the expression.
 *
 * <p>Functions are very simplistic. The arguments are not part of the definition
 * (right now), so the names must be unique.
 *
 * @author Andy Clement
 * @author Juergen Hoeller
 * @since 3.0
 */
public class FunctionReference extends SpelNodeImpl {

	private final String name;

	// Captures the most recently used method for the function invocation *if* the method
	// can safely be used for compilation (i.e. no argument conversion is going on)
	private volatile Method method;


	public FunctionReference(String functionName, int pos, SpelNodeImpl... arguments) {
		super(pos, arguments);
		this.name = functionName;
	}


	@Override
	public TypedValue getValueInternal(ExpressionState state) throws EvaluationException {
		TypedValue value = state.lookupVariable(this.name);
		if (value == TypedValue.NULL) {
			throw new SpelEvaluationException(getStartPosition(), SpelMessage.FUNCTION_NOT_DEFINED, this.name);
		}
		if (!(value.getValue() instanceof Method)) {
			// Possibly a static Java method registered as a function
			throw new SpelEvaluationException(
					SpelMessage.FUNCTION_REFERENCE_CANNOT_BE_INVOKED, this.name, value.getClass());
		}

		try {
			return executeFunctionJLRMethod(state, (Method) value.getValue());
		}
		catch (SpelEvaluationException ex) {
			ex.setPosition(getStartPosition());
			throw ex;
		}
	}

	/**
	 * Execute a function represented as a {@code java.lang.reflect.Method}.
	 * @param state the expression evaluation state
	 * @param method the method to invoke
	 * @return the return value of the invoked Java method
	 * @throws EvaluationException if there is any problem invoking the method
	 */
	private TypedValue executeFunctionJLRMethod(ExpressionState state, Method method) throws EvaluationException {
		Object[] functionArgs = getArguments(state);

		if (!method.isVarArgs()) {
			int declaredParamCount = method.getParameterTypes().length;
			if (declaredParamCount != functionArgs.length) {
				throw new SpelEvaluationException(SpelMessage.INCORRECT_NUMBER_OF_ARGUMENTS_TO_FUNCTION,
						functionArgs.length, declaredParamCount);
			}
		}
		if (!Modifier.isStatic(method.getModifiers())) {
			throw new SpelEvaluationException(getStartPosition(),
					SpelMessage.FUNCTION_MUST_BE_STATIC, ClassUtils.getQualifiedMethodName(method), this.name);
		}

		// Convert arguments if necessary and remap them for varargs if required
		TypeConverter converter = state.getEvaluationContext().getTypeConverter();
		boolean argumentConversionOccurred = ReflectionHelper.convertAllArguments(converter, functionArgs, method);
		if (method.isVarArgs()) {
			functionArgs = ReflectionHelper.setupArgumentsForVarargsInvocation(
					method.getParameterTypes(), functionArgs);
		}
		boolean compilable = false;

		try {
			ReflectionUtils.makeAccessible(method);
			Object result = method.invoke(method.getClass(), functionArgs);
			compilable = !argumentConversionOccurred;
			return new TypedValue(result, new TypeDescriptor(new MethodParameter(method, -1)).narrow(result));
		}
		catch (Exception ex) {
			throw new SpelEvaluationException(getStartPosition(), ex, SpelMessage.EXCEPTION_DURING_FUNCTION_CALL,
					this.name, ex.getMessage());
		}
		finally {
			if (compilable) {
				this.exitTypeDescriptor = CodeFlow.toDescriptor(method.getReturnType());
				this.method = method;
			}
			else {
				this.exitTypeDescriptor = null;
				this.method = null;
			}
		}
	}

	@Override
	public String toStringAST() {
		StringBuilder sb = new StringBuilder("#").append(this.name);
		sb.append("(");
		for (int i = 0; i < getChildCount(); i++) {
			if (i > 0) {
				sb.append(",");
			}
			sb.append(getChild(i).toStringAST());
		}
		sb.append(")");
		return sb.toString();
	}

	/**
	 * Compute the arguments to the function, they are the children of this expression node.
	 * @return an array of argument values for the function call
	 */
	private Object[] getArguments(ExpressionState state) throws EvaluationException {
		// Compute arguments to the function
		Object[] arguments = new Object[getChildCount()];
		for (int i = 0; i < arguments.length; i++) {
			arguments[i] = this.children[i].getValueInternal(state).getValue();
		}
		return arguments;
	}
	
	@Override
	public boolean isCompilable() {
		Method method = this.method;
		if (method == null) {
			return false;
		}
		int methodModifiers = method.getModifiers();
		if (!Modifier.isStatic(methodModifiers) || !Modifier.isPublic(methodModifiers) ||
				!Modifier.isPublic(method.getDeclaringClass().getModifiers())) {
			return false;
		}
		for (SpelNodeImpl child : this.children) {
			if (!child.isCompilable()) {
				return false;
			}
		}
		return true;
	}
	
	@Override 
	public void generateCode(MethodVisitor mv, CodeFlow cf) {
		Method method = this.method;
		Assert.state(method != null, "No method handle");
		String classDesc = method.getDeclaringClass().getName().replace('.', '/');
		generateCodeForArguments(mv, cf, method, this.children);
		mv.visitMethodInsn(INVOKESTATIC, classDesc, method.getName(),
				CodeFlow.createSignatureDescriptor(method), false);
		cf.pushDescriptor(this.exitTypeDescriptor);
	}

}
