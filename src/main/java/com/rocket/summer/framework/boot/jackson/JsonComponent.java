package com.rocket.summer.framework.boot.jackson;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;

import com.rocket.summer.framework.stereotype.Component;

/**
 * {@link Component} that provides {@link JsonSerializer} and/or {@link JsonDeserializer}
 * implementations to be registered with Jackson when {@link JsonComponentModule} is in
 * use. Can be used to annotate {@link JsonSerializer} or {@link JsonDeserializer}
 * implementations directly or a class that contains them as inner-classes. For example:
 * <pre class="code">
 * &#064;JsonComponent
 * public class CustomerJsonComponent {
 *
 *     public static class Serializer extends JsonSerializer&lt;Customer&gt; {
 *
 *         // ...
 *
 *     }
 *
 *     public static class Deserializer extends JsonDeserializer&lt;Customer&gt; {
 *
 *         // ...
 *
 *     }
 *
 * }
 *
 * </pre>
 *
 * @see JsonComponentModule
 * @since 1.4.0
 * @author Phillip Webb
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface JsonComponent {

    /**
     * The value may indicate a suggestion for a logical component name, to be turned into
     * a Spring bean in case of an autodetected component.
     * @return the component name
     */
    String value() default "";

}
