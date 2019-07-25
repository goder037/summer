package com.rocket.summer.framework.web.bind.annotation;

import java.lang.annotation.*;

/**
 * Annotation that indicates the session attributes that a specific handler
 * uses. This will typically list the names of model attributes which should be
 * transparently stored in the session or some conversational storage,
 * serving as form-backing beans. <b>Declared at the type level,</b> applying
 * to the model attributes that the annotated handler class operates on.
 *
 * <p><b>NOTE:</b> Session attributes as indicated using this annotation
 * correspond to a specific handler's model attributes, getting transparently
 * stored in a conversational session. Those attributes will be removed once
 * the handler indicates completion of its conversational session. Therefore,
 * use this facility for such conversational attributes which are supposed
 * to be stored in the session <i>temporarily</i> during the course of a
 * specific handler's conversation.
 *
 * <p>For permanent session attributes, e.g. a user authentication object,
 * use the traditional <code>session.setAttribute</code> method instead.
 * Alternatively, consider using the attribute management capabilities of the
 * generic {@link org.springframework.web.context.request.WebRequest} interface.
 *
 * <p><b>NOTE:</b> When using controller interfaces (e.g. for AOP proxying),
 * make sure to consistently put <i>all</i> your mapping annotations - such as
 * <code>@RequestMapping</code> and <code>@SessionAttributes</code> - on
 * the controller <i>interface</i> rather than on the implementation class.
 *
 * @author Juergen Hoeller
 * @since 2.5
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface SessionAttributes {

    /**
     * The names of session attributes in the model, to be stored in the
     * session or some conversational storage.
     * <p>Note: This indicates the model attribute names. The session attribute
     * names may or may not match the model attribute names; applications should
     * not rely on the session attribute names but rather operate on the model only.
     */
    String[] value() default {};

    /**
     * The types of session attributes in the model, to be stored in the
     * session or some conversational storage. All model attributes of this
     * type will be stored in the session, regardless of attribute name.
     */
    Class[] types() default {};

}
