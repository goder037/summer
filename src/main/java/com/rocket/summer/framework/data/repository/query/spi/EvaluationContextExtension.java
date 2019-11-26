package com.rocket.summer.framework.data.repository.query.spi;

import java.util.Map;

import com.rocket.summer.framework.data.repository.query.ExtensionAwareEvaluationContextProvider;
import com.rocket.summer.framework.expression.EvaluationContext;

/**
 * SPI to allow adding a set of properties and function definitions accessible via the root of an
 * {@link EvaluationContext} provided by a {@link ExtensionAwareEvaluationContextProvider}.
 *
 * @author Thomas Darimont
 * @author Oliver Gierke
 * @since 1.9
 */
public interface EvaluationContextExtension {

    /**
     * Returns the identifier of the extension. The id can be leveraged by users to fully qualify property lookups and
     * thus overcome ambiguities in case multiple extensions expose properties with the same name.
     *
     * @return the extension id, must not be {@literal null}.
     */
    String getExtensionId();

    /**
     * Returns the properties exposed by the extension.
     *
     * @return the properties
     */
    Map<String, Object> getProperties();

    /**
     * Returns the functions exposed by the extension.
     *
     * @return the functions
     */
    Map<String, Function> getFunctions();

    /**
     * Returns the root object to be exposed by the extension. It's strongly recommended to declare the most concrete type
     * possible as return type of the implementation method. This will allow us to obtain the necessary metadata once and
     * not for every evaluation.
     *
     * @return
     */
    Object getRootObject();
}

