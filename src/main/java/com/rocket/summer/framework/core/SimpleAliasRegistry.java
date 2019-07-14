package com.rocket.summer.framework.core;

import com.rocket.summer.framework.util.Assert;
import com.rocket.summer.framework.util.StringUtils;
import com.rocket.summer.framework.util.StringValueResolver;

import java.util.*;

/**
 * Simple implementation of the {@link AliasRegistry} interface.
 * Serves as base class for
 * {@link org.springframework.beans.factory.support.BeanDefinitionRegistry}
 * implementations.
 *
 * @author Juergen Hoeller
 * @since 2.5.2
 */
public class SimpleAliasRegistry implements AliasRegistry {

    /** Map from alias to canonical name */
    private final Map aliasMap = CollectionFactory.createConcurrentMapIfPossible(16);


    public void registerAlias(String name, String alias) {
        Assert.hasText(name, "'name' must not be empty");
        Assert.hasText(alias, "'alias' must not be empty");
        if (alias.equals(name)) {
            this.aliasMap.remove(alias);
        }
        else {
            if (!allowAliasOverriding()) {
                String registeredName = (String) this.aliasMap.get(alias);
                if (registeredName != null && !registeredName.equals(name)) {
                    throw new IllegalStateException("Cannot register alias '" + alias + "' for name '" +
                            name + "': It is already registered for name '" + registeredName + "'.");
                }
            }
            this.aliasMap.put(alias, name);
        }
    }

    /**
     * Return whether alias overriding is allowed.
     * Default is <code>true</code>.
     */
    protected boolean allowAliasOverriding() {
        return true;
    }

    public void removeAlias(String alias) {
        String name = (String) this.aliasMap.remove(alias);
        if (name == null) {
            throw new IllegalStateException("No alias '" + alias + "' registered");
        }
    }

    public boolean isAlias(String name) {
        return this.aliasMap.containsKey(name);
    }

    public String[] getAliases(String name) {
        List aliases = new ArrayList();
        synchronized (this.aliasMap) {
            for (Iterator it = this.aliasMap.entrySet().iterator(); it.hasNext();) {
                Map.Entry entry = (Map.Entry) it.next();
                String registeredName = (String) entry.getValue();
                if (registeredName.equals(name)) {
                    aliases.add(entry.getKey());
                }
            }
        }
        return StringUtils.toStringArray(aliases);
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
            Map aliasCopy = new HashMap(this.aliasMap);
            for (Iterator it = aliasCopy.keySet().iterator(); it.hasNext();) {
                String alias = (String) it.next();
                String registeredName = (String) aliasCopy.get(alias);
                String resolvedAlias = valueResolver.resolveStringValue(alias);
                String resolvedName = valueResolver.resolveStringValue(registeredName);
                if (!resolvedAlias.equals(alias)) {
                    String existingName = (String) this.aliasMap.get(resolvedAlias);
                    if (existingName != null && !existingName.equals(resolvedName)) {
                        throw new IllegalStateException("Cannot register resolved alias '" +
                                resolvedAlias + "' (original: '" + alias + "') for name '" + resolvedName +
                                "': It is already registered for name '" + registeredName + "'.");
                    }
                    this.aliasMap.put(resolvedAlias, resolvedName);
                    this.aliasMap.remove(alias);
                }
                else if (!registeredName.equals(resolvedName)) {
                    this.aliasMap.put(alias, resolvedName);
                }
            }
        }
    }

    /**
     * Determine the raw name, resolving aliases to canonical names.
     * @param name the user-specified name
     * @return the transformed name
     */
    public String canonicalName(String name) {
        String canonicalName = name;
        // Handle aliasing.
        String resolvedName = null;
        do {
            resolvedName = (String) this.aliasMap.get(canonicalName);
            if (resolvedName != null) {
                canonicalName = resolvedName;
            }
        }
        while (resolvedName != null);
        return canonicalName;
    }

}
