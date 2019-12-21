package com.rocket.summer.framework.expression.spel.support;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import com.rocket.summer.framework.core.MethodParameter;
import com.rocket.summer.framework.core.convert.TypeDescriptor;
import com.rocket.summer.framework.expression.AccessException;
import com.rocket.summer.framework.expression.ConstructorExecutor;
import com.rocket.summer.framework.expression.ConstructorResolver;
import com.rocket.summer.framework.expression.EvaluationContext;
import com.rocket.summer.framework.expression.EvaluationException;
import com.rocket.summer.framework.expression.TypeConverter;

/**
 * A constructor resolver that uses reflection to locate the constructor that should be invoked.
 *
 * @author Andy Clement
 * @author Juergen Hoeller
 * @since 3.0
 */
public class ReflectiveConstructorResolver implements ConstructorResolver {

    /**
     * Locate a constructor on the type. There are three kinds of match that might occur:
     * <ol>
     * <li>An exact match where the types of the arguments match the types of the constructor
     * <li>An in-exact match where the types we are looking for are subtypes of those defined on the constructor
     * <li>A match where we are able to convert the arguments into those expected by the constructor, according to the
     * registered type converter.
     * </ol>
     */
    @Override
    public ConstructorExecutor resolve(EvaluationContext context, String typeName, List<TypeDescriptor> argumentTypes)
            throws AccessException {

        try {
            TypeConverter typeConverter = context.getTypeConverter();
            Class<?> type = context.getTypeLocator().findType(typeName);
            Constructor<?>[] ctors = type.getConstructors();

            Arrays.sort(ctors, new Comparator<Constructor<?>>() {
                @Override
                public int compare(Constructor<?> c1, Constructor<?> c2) {
                    int c1pl = c1.getParameterTypes().length;
                    int c2pl = c2.getParameterTypes().length;
                    return (c1pl < c2pl ? -1 : (c1pl > c2pl ? 1 : 0));
                }
            });

            Constructor<?> closeMatch = null;
            Constructor<?> matchRequiringConversion = null;

            for (Constructor<?> ctor : ctors) {
                Class<?>[] paramTypes = ctor.getParameterTypes();
                List<TypeDescriptor> paramDescriptors = new ArrayList<TypeDescriptor>(paramTypes.length);
                for (int i = 0; i < paramTypes.length; i++) {
                    paramDescriptors.add(new TypeDescriptor(new MethodParameter(ctor, i)));
                }
                ReflectionHelper.ArgumentsMatchInfo matchInfo = null;
                if (ctor.isVarArgs() && argumentTypes.size() >= paramTypes.length - 1) {
                    // *sigh* complicated
                    // Basically.. we have to have all parameters match up until the varargs one, then the rest of what is
                    // being provided should be
                    // the same type whilst the final argument to the method must be an array of that (oh, how easy...not) -
                    // or the final parameter
                    // we are supplied does match exactly (it is an array already).
                    matchInfo = ReflectionHelper.compareArgumentsVarargs(paramDescriptors, argumentTypes, typeConverter);
                }
                else if (paramTypes.length == argumentTypes.size()) {
                    // worth a closer look
                    matchInfo = ReflectionHelper.compareArguments(paramDescriptors, argumentTypes, typeConverter);
                }
                if (matchInfo != null) {
                    if (matchInfo.isExactMatch()) {
                        return new ReflectiveConstructorExecutor(ctor);
                    }
                    else if (matchInfo.isCloseMatch()) {
                        closeMatch = ctor;
                    }
                    else if (matchInfo.isMatchRequiringConversion()) {
                        matchRequiringConversion = ctor;
                    }
                }
            }

            if (closeMatch != null) {
                return new ReflectiveConstructorExecutor(closeMatch);
            }
            else if (matchRequiringConversion != null) {
                return new ReflectiveConstructorExecutor(matchRequiringConversion);
            }
            else {
                return null;
            }
        }
        catch (EvaluationException ex) {
            throw new AccessException("Failed to resolve constructor", ex);
        }
    }

}
