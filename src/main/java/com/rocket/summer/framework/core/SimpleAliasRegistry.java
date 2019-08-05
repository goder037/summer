package com.rocket.summer.framework.core;

import com.rocket.summer.framework.util.Assert;
import com.rocket.summer.framework.util.StringUtils;
import com.rocket.summer.framework.util.StringValueResolver;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Simple implementation of the {@link AliasRegistry} interface.
 * Serves as base class for
 * {@link com.rocket.summer.framework.beans.factory.support.BeanDefinitionRegistry}
 * implementations.
 *
 * @author Juergen Hoeller
 * @since 2.5.2
 */
public class SimpleAliasRegistry implements AliasRegistry {

    /** Map from alias to canonical name */
    private final Map<String, String> aliasMap = new ConcurrentHashMap<String, String>(16);


    @Override
    public void registerAlias(String name, String alias) {
        Assert.hasText(name, "'name' must not be empty");
        Assert.hasText(alias, "'alias' must not be empty");
        synchronized (this.aliasMap) {
            if (alias.equals(name)) {
                this.aliasMap.remove(alias);
            }
            else {
                String registeredName = this.aliasMap.get(alias);
                if (registeredName != null) {
                    if (registeredName.equals(name)) {
                        // An existing alias - no need to re-register
                        return;
                    }
                    if (!allowAliasOverriding()) {
                        throw new IllegalStateException("Cannot register alias '" + alias + "' for name '" +
                                name + "': It is already registered for name '" + registeredName + "'.");
                    }
                }
                checkForAliasCircle(name, alias);
                this.aliasMap.put(alias, name);
            }
        }
    }

    /**
     * Return whether alias overriding is allowed.
     * Default is {@code true}.
     */
    protected boolean allowAliasOverriding() {
        return true;
    }

    /**
     * Determine whether the given name has the given alias registered.
     * @param name the name to check
     * @param alias the alias to look for
     * @since 4.2.1
     */
    public boolean hasAlias(String name, String alias) {
        for (Map.Entry<String, String> entry : this.aliasMap.entrySet()) {
            String registeredName = entry.getValue();
            if (registeredName.equals(name)) {
                String registeredAlias = entry.getKey();
                if (registeredAlias.equals(alias) || hasAlias(registeredAlias, alias)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void removeAlias(String alias) {
        synchronized (this.aliasMap) {
            String name = this.aliasMap.remove(alias);
            if (name == null) {
                throw new IllegalStateException("No alias '" + alias + "' registered");
            }
        }
    }

    @Override
    public boolean isAlias(String name) {
        return this.aliasMap.containsKey(name);
    }

    @Override
    public String[] getAliases(String name) {
        List<String> result = new ArrayList<String>();
        synchronized (this.aliasMap) {
            retrieveAliases(name, result);
        }
        return StringUtils.toStringArray(result);
    }

    /**
     * Transitively retrieve all aliases for the given name.
     * @param name the target name to find aliases for
     * @param result the resulting aliases list
     */
    private void retrieveAliases(String name, List<String> result) {
        for (Map.Entry<String, String> entry : this.aliasMap.entrySet()) {
            String registeredName = entry.getValue();
            if (registeredName.equals(name)) {
                String alias = entry.getKey();
                result.add(alias);
                retrieveAliases(alias, result);
            }
        }
    }

    /**
     * Resolve all alias target names and aliases registered in this
     * factory, applying the given StringValueResolver to them.
     * <p>The value resolver may for example resolve placeholders
     * in target bean names and even in alias names.
     * @param valueResolver the StringValueResolver to apply
     */
    public void resolveAliases(StringValueResolver valueResolver) {
        Assert.notNull(valueResolver, "StringValueResolver must not be null");
        synchronized (this.aliasMap) {
            Map<String, String> aliasCopy = new HashMap<String, String>(this.aliasMap);
            for (String alias : aliasCopy.keySet()) {
                String registeredName = aliasCopy.get(alias);
                String resolvedAlias = valueResolver.resolveStringValue(alias);
                String resolvedName = valueResolver.resolveStringValue(registeredName);
                if (resolvedAlias == null || resolvedName == null || resolvedAlias.equals(resolvedName)) {
                    this.aliasMap.remove(alias);
                }
                else if (!resolvedAlias.equals(alias)) {
                    String existingName = this.aliasMap.get(resolvedAlias);
                    if (existingName != null) {
                        if (existingName.equals(resolvedName)) {
                            // Pointing to existing alias - just remove placeholder
                            this.aliasMap.remove(alias);
                            break;
                        }
                        throw new IllegalStateException(
                                "Cannot register resolved alias '" + resolvedAlias + "' (original: '" + alias +
                                        "') for name '" + resolvedName + "': It is already registered for name '" +
                                        registeredName + "'.");
                    }
                    checkForAliasCircle(resolvedName, resolvedAlias);
                    this.aliasMap.remove(alias);
                    this.aliasMap.put(resolvedAlias, resolvedName);
                }
                else if (!registeredName.equals(resolvedName)) {
                    this.aliasMap.put(alias, resolvedName);
                }
            }
        }
    }

    /**
     * Check whether the given name points back to the given alias as an alias
     * in the other direction already, catching a circular reference upfront
     * and throwing a corresponding IllegalStateException.
     * @param name the candidate name
     * @param alias the candidate alias
     * @see #registerAlias
     * @see #hasAlias
     */
    protected void checkForAliasCircle(String name, String alias) {
        if (hasAlias(alias, name)) {
            throw new IllegalStateException("Cannot register alias '" + alias +
                    "' for name '" + name + "': Circular reference - '" +
                    name + "' is a direct or indirect alias for '" + alias + "' already");
        }
    }

    /**
     * Determine the raw name, resolving aliases to canonical names.
     * @param name the user-specified name
     * @return the transformed name
     */
    public String canonicalName(String name) {
        String canonicalName = name;
        // Handle aliasing...
        String resolvedName;
        do {
            resolvedName = this.aliasMap.get(canonicalName);
            if (resolvedName != null) {
                canonicalName = resolvedName;
            }
        }
        while (resolvedName != null);
        return canonicalName;
    }

}