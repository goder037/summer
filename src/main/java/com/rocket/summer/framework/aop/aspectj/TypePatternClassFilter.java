package com.rocket.summer.framework.aop.aspectj;

import org.aspectj.weaver.tools.PointcutParser;
import org.aspectj.weaver.tools.TypePatternMatcher;

import com.rocket.summer.framework.aop.ClassFilter;
import com.rocket.summer.framework.util.Assert;
import com.rocket.summer.framework.util.StringUtils;

/**
 * Spring AOP {@link ClassFilter} implementation using AspectJ type matching.
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @since 2.0
 */
public class TypePatternClassFilter implements ClassFilter {

    private String typePattern;

    private TypePatternMatcher aspectJTypePatternMatcher;


    /**
     * Creates a new instance of the {@link TypePatternClassFilter} class.
     * <p>This is the JavaBean constructor; be sure to set the
     * {@link #setTypePattern(String) typePattern} property, else a
     * no doubt fatal {@link IllegalStateException} will be thrown
     * when the {@link #matches(Class)} method is first invoked.
     */
    public TypePatternClassFilter() {
    }

    /**
     * Create a fully configured {@link TypePatternClassFilter} using the
     * given type pattern.
     * @param typePattern the type pattern that AspectJ weaver should parse
     * @throws IllegalArgumentException if the supplied {@code typePattern} is {@code null}
     * or is recognized as invalid
     */
    public TypePatternClassFilter(String typePattern) {
        setTypePattern(typePattern);
    }


    /**
     * Set the AspectJ type pattern to match.
     * <p>Examples include:
     * <code class="code">
     * com.rocket.summer.framework.beans.*
     * </code>
     * This will match any class or interface in the given package.
     * <code class="code">
     * com.rocket.summer.framework.beans.ITestBean+
     * </code>
     * This will match the {@code ITestBean} interface and any class
     * that implements it.
     * <p>These conventions are established by AspectJ, not Spring AOP.
     * @param typePattern the type pattern that AspectJ weaver should parse
     * @throws IllegalArgumentException if the supplied {@code typePattern} is {@code null}
     * or is recognized as invalid
     */
    public void setTypePattern(String typePattern) {
        Assert.notNull(typePattern, "Type pattern must not be null");
        this.typePattern = typePattern;
        this.aspectJTypePatternMatcher =
                PointcutParser.getPointcutParserSupportingAllPrimitivesAndUsingContextClassloaderForResolution().
                        parseTypePattern(replaceBooleanOperators(typePattern));
    }

    /**
     * Return the AspectJ type pattern to match.
     */
    public String getTypePattern() {
        return this.typePattern;
    }


    /**
     * Should the pointcut apply to the given interface or target class?
     * @param clazz candidate target class
     * @return whether the advice should apply to this candidate target class
     * @throws IllegalStateException if no {@link #setTypePattern(String)} has been set
     */
    @Override
    public boolean matches(Class<?> clazz) {
        Assert.state(this.aspectJTypePatternMatcher != null, "No type pattern has been set");
        return this.aspectJTypePatternMatcher.matches(clazz);
    }

    /**
     * If a type pattern has been specified in XML, the user cannot
     * write {@code and} as "&&" (though &amp;&amp; will work).
     * We also allow {@code and} between two sub-expressions.
     * <p>This method converts back to {@code &&} for the AspectJ pointcut parser.
     */
    private String replaceBooleanOperators(String pcExpr) {
        String result = StringUtils.replace(pcExpr," and "," && ");
        result = StringUtils.replace(result, " or ", " || ");
        return StringUtils.replace(result, " not ", " ! ");
    }
}

