package com.rocket.summer.framework.beans.factory.parsing;

import com.rocket.summer.framework.util.Assert;

import java.util.LinkedList;
import java.util.List;

public class CompositeComponentDefinition extends AbstractComponentDefinition {

    private final String name;

    private final Object source;

    private final List nestedComponents = new LinkedList();


    /**
     * Create a new CompositeComponentDefinition.
     * @param name the name of the composite component
     * @param source the source element that defines the root of the composite component
     */
    public CompositeComponentDefinition(String name, Object source) {
        Assert.notNull(name, "Name must not be null");
        this.name = name;
        this.source = source;
    }


    public String getName() {
        return this.name;
    }

    public Object getSource() {
        return this.source;
    }

    /**
     * Add the given component as nested element of this composite component.
     * @param component the nested component to add
     */
    public void addNestedComponent(ComponentDefinition component) {
        Assert.notNull(component, "ComponentDefinition must not be null");
        this.nestedComponents.add(component);
    }
}
