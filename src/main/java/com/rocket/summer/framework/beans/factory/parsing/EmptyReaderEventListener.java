package com.rocket.summer.framework.beans.factory.parsing;

/**
 * Empty implementation of the ReaderEventListener interface,
 * providing no-op implementations of all callback methods.
 *
 * @author Juergen Hoeller
 * @since 2.0
 */
public class EmptyReaderEventListener implements ReaderEventListener {

    public void defaultsRegistered(DefaultsDefinition defaultsDefinition) {
        // no-op
    }

    public void componentRegistered(ComponentDefinition componentDefinition) {
        // no-op
    }

    public void aliasRegistered(AliasDefinition aliasDefinition) {
        // no-op
    }

    public void importProcessed(ImportDefinition importDefinition) {
        // no-op
    }

}

