package com.rocket.summer.framework.beans.factory.parsing;

import com.rocket.summer.framework.core.io.Resource;

/**
 * Context that gets passed along a bean definition reading process,
 * encapsulating all relevant configuration as well as state.
 *
 * @author Rob Harrop
 * @author Juergen Hoeller
 * @since 2.0
 */
public class ReaderContext {

    private final Resource resource;

    private final ProblemReporter problemReporter;

    private final ReaderEventListener eventListener;

    private final SourceExtractor sourceExtractor;


    public ReaderContext(Resource resource, ProblemReporter problemReporter,
                         ReaderEventListener eventListener, SourceExtractor sourceExtractor) {

        this.resource = resource;
        this.problemReporter = problemReporter;
        this.eventListener = eventListener;
        this.sourceExtractor = sourceExtractor;
    }

    public final Resource getResource() {
        return this.resource;
    }

    public SourceExtractor getSourceExtractor() {
        return this.sourceExtractor;
    }

    public Object extractSource(Object sourceCandidate) {
        return this.sourceExtractor.extractSource(sourceCandidate, this.resource);
    }

    public void fireDefaultsRegistered(DefaultsDefinition defaultsDefinition) {
        this.eventListener.defaultsRegistered(defaultsDefinition);
    }

    public void fireComponentRegistered(ComponentDefinition componentDefinition) {
        this.eventListener.componentRegistered(componentDefinition);
    }

    public void fireAliasRegistered(String beanName, String alias, Object source) {
        this.eventListener.aliasRegistered(new AliasDefinition(beanName, alias, source));
    }

    public void fireImportProcessed(String importedResource, Object source) {
        this.eventListener.importProcessed(new ImportDefinition(importedResource, source));
    }

    public void fireImportProcessed(String importedResource, Resource[] actualResources, Object source) {
        this.eventListener.importProcessed(new ImportDefinition(importedResource, actualResources, source));
    }

    public void fatal(String message, Object source) {
        fatal(message, source, null, null);
    }

    public void fatal(String message, Object source, Throwable ex) {
        fatal(message, source, null, ex);
    }

    public void fatal(String message, Object source, ParseState parseState) {
        fatal(message, source, parseState, null);
    }

    public void fatal(String message, Object source, ParseState parseState, Throwable cause) {
        Location location = new Location(getResource(), source);
        this.problemReporter.fatal(new Problem(message, location, parseState, cause));
    }

    public void error(String message, Object source) {
        error(message, source, null, null);
    }

    public void error(String message, Object source, Throwable ex) {
        error(message, source, null, ex);
    }

    public void error(String message, Object source, ParseState parseState) {
        error(message, source, parseState, null);
    }

    public void error(String message, Object source, ParseState parseState, Throwable cause) {
        Location location = new Location(getResource(), source);
        this.problemReporter.error(new Problem(message, location, parseState, cause));
    }

    public void warning(String message, Object source) {
        warning(message, source, null, null);
    }

    public void warning(String message, Object source, Throwable ex) {
        warning(message, source, null, ex);
    }

    public void warning(String message, Object source, ParseState parseState) {
        warning(message, source, parseState, null);
    }

    public void warning(String message, Object source, ParseState parseState, Throwable cause) {
        Location location = new Location(getResource(), source);
        this.problemReporter.warning(new Problem(message, location, parseState, cause));
    }
}
