package com.rocket.summer.framework.boot.autoconfigure.condition;

import com.rocket.summer.framework.context.annotation.Conditional;

import java.lang.annotation.*;

/**
 * {@link Conditional} that checks if the specified properties have a specific value. By
 * default the properties must be present in the {@link Environment} and
 * <strong>not</strong> equal to {@code false}. The {@link #havingValue()} and
 * {@link #matchIfMissing()} attributes allow further customizations.
 *
 * <p>
 * The {@link #havingValue} attribute can be used to specify the value that the property
 * should have. The table below shows when a condition matches according to the property
 * value and the {@link #havingValue()} attribute:
 *
 * <table summary="having values" border="1">
 * <tr>
 * <th>Property Value</th>
 * <th>{@code havingValue=""}</th>
 * <th>{@code havingValue="true"}</th>
 * <th>{@code havingValue="false"}</th>
 * <th>{@code havingValue="foo"}</th>
 * </tr>
 * <tr>
 * <td>{@code "true"}</td>
 * <td>yes</td>
 * <td>yes</td>
 * <td>no</td>
 * <td>no</td>
 * </tr>
 * <tr>
 * <td>{@code "false"}</td>
 * <td>no</td>
 * <td>no</td>
 * <td>yes</td>
 * <td>no</td>
 * </tr>
 * <tr>
 * <td>{@code "foo"}</td>
 * <td>yes</td>
 * <td>no</td>
 * <td>no</td>
 * <td>yes</td>
 * </tr>
 * </table>
 *
 * <p>
 * If the property is not contained in the {@link Environment} at all, the
 * {@link #matchIfMissing()} attribute is consulted. By default missing attributes do not
 * match.
 *
 * @author Maciej Walkowiak
 * @author Stephane Nicoll
 * @author Phillip Webb
 * @since 1.1.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.METHOD })
@Documented
@Conditional(OnPropertyCondition.class)
public @interface ConditionalOnProperty {

    /**
     * Alias for {@link #name()}.
     * @return the names
     */
    String[] value() default {};

    /**
     * A prefix that should be applied to each property. The prefix automatically ends
     * with a dot if not specified.
     * @return the prefix
     */
    String prefix() default "";

    /**
     * The name of the properties to test. If a prefix has been defined, it is applied to
     * compute the full key of each property. For instance if the prefix is
     * {@code app.config} and one value is {@code my-value}, the fully key would be
     * {@code app.config.my-value}
     * <p>
     * Use the dashed notation to specify each property, that is all lower case with a "-"
     * to separate words (e.g. {@code my-long-property}).
     * @return the names
     */
    String[] name() default {};

    /**
     * The string representation of the expected value for the properties. If not
     * specified, the property must <strong>not</strong> be equals to {@code false}.
     * @return the expected value
     */
    String havingValue() default "";

    /**
     * Specify if the condition should match if the property is not set. Defaults to
     * {@code false}.
     * @return if should match if the property is missing
     */
    boolean matchIfMissing() default false;

    /**
     * If relaxed names should be checked. Defaults to {@code true}.
     * @return if relaxed names are used
     */
    boolean relaxedNames() default true;

}
