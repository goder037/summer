package com.rocket.summer.framework.beans.factory;

import com.rocket.summer.framework.context.BeansException;
import com.rocket.summer.framework.core.ResolvableType;
import com.rocket.summer.framework.util.Assert;
import com.rocket.summer.framework.util.StringUtils;

import java.util.*;

/**
 * Convenience methods operating on bean factories, in particular
 * on the {@link ListableBeanFactory} interface.
 *
 * <p>Returns bean counts, bean names or bean instances,
 * taking into account the nesting hierarchy of a bean factory
 * (which the methods defined on the ListableBeanFactory interface don't,
 * in contrast to the methods defined on the BeanFactory interface).
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @since 04.07.2003
 */
public abstract class BeanFactoryUtils {

    /**
     * Separator for generated bean names. If a class name or parent name is not
     * unique, "#1", "#2" etc will be appended, until the name becomes unique.
     */
    public static final String GENERATED_BEAN_NAME_SEPARATOR = "#";


    /**
     * Return whether the given name is a factory dereference
     * (beginning with the factory dereference prefix).
     * @param name the name of the bean
     * @return whether the given name is a factory dereference
     * @see BeanFactory#FACTORY_BEAN_PREFIX
     */
    public static boolean isFactoryDereference(String name) {
        return (name != null && name.startsWith(BeanFactory.FACTORY_BEAN_PREFIX));
    }

    /**
     * Return the actual bean name, stripping out the factory dereference
     * prefix (if any, also stripping repeated factory prefixes if found).
     * @param name the name of the bean
     * @return the transformed name
     * @see BeanFactory#FACTORY_BEAN_PREFIX
     */
    public static String transformedBeanName(String name) {
        Assert.notNull(name, "'name' must not be null");
        String beanName = name;
        while (beanName.startsWith(BeanFactory.FACTORY_BEAN_PREFIX)) {
            beanName = beanName.substring(BeanFactory.FACTORY_BEAN_PREFIX.length());
        }
        return beanName;
    }

    /**
     * Return whether the given name is a bean name which has been generated
     * by the default naming strategy (containing a "#..." part).
     * @param name the name of the bean
     * @return whether the given name is a generated bean name
     * @see #GENERATED_BEAN_NAME_SEPARATOR
     * @see com.rocket.summer.framework.beans.factory.support.BeanDefinitionReaderUtils#generateBeanName
     * @see com.rocket.summer.framework.beans.factory.support.DefaultBeanNameGenerator
     */
    public static boolean isGeneratedBeanName(String name) {
        return (name != null && name.indexOf(GENERATED_BEAN_NAME_SEPARATOR) != -1);
    }

    /**
     * Get all bean names for the given type, including those defined in ancestor
     * factories. Will return unique names in case of overridden bean definitions.
     * <p>Does consider objects created by FactoryBeans, which means that FactoryBeans
     * will get initialized. If the object created by the FactoryBean doesn't match,
     * the raw FactoryBean itself will be matched against the type.
     * <p>This version of {@code beanNamesForTypeIncludingAncestors} automatically
     * includes prototypes and FactoryBeans.
     * @param lbf the bean factory
     * @param type the type that beans must match (as a {@code ResolvableType})
     * @return the array of matching bean names, or an empty array if none
     * @since 4.2
     */
    public static String[] beanNamesForTypeIncludingAncestors(ListableBeanFactory lbf, ResolvableType type) {
        Assert.notNull(lbf, "ListableBeanFactory must not be null");
        String[] result = lbf.getBeanNamesForType(type);
        if (lbf instanceof HierarchicalBeanFactory) {
            HierarchicalBeanFactory hbf = (HierarchicalBeanFactory) lbf;
            if (hbf.getParentBeanFactory() instanceof ListableBeanFactory) {
                String[] parentResult = beanNamesForTypeIncludingAncestors(
                        (ListableBeanFactory) hbf.getParentBeanFactory(), type);
                result = mergeNamesWithParent(result, parentResult, hbf);
            }
        }
        return result;
    }

    /**
     * Merge the given bean names result with the given parent result.
     * @param result the local bean name result
     * @param parentResult the parent bean name result (possibly empty)
     * @param hbf the local bean factory
     * @return the merged result (possibly the local result as-is)
     * @since 4.3.15
     */
    private static String[] mergeNamesWithParent(String[] result, String[] parentResult, HierarchicalBeanFactory hbf) {
        if (parentResult.length == 0) {
            return result;
        }
        List<String> merged = new ArrayList<String>(result.length + parentResult.length);
        merged.addAll(Arrays.asList(result));
        for (String beanName : parentResult) {
            if (!merged.contains(beanName) && !hbf.containsLocalBean(beanName)) {
                merged.add(beanName);
            }
        }
        return StringUtils.toStringArray(merged);
    }

    /**
     * Extract the "raw" bean name from the given (potentially generated) bean name,
     * excluding any "#..." suffixes which might have been added for uniqueness.
     * @param name the potentially generated bean name
     * @return the raw bean name
     * @see #GENERATED_BEAN_NAME_SEPARATOR
     */
    public static String originalBeanName(String name) {
        Assert.notNull(name, "'name' must not be null");
        int separatorIndex = name.indexOf(GENERATED_BEAN_NAME_SEPARATOR);
        return (separatorIndex != -1 ? name.substring(0, separatorIndex) : name);
    }


    /**
     * Count all beans in any hierarchy in which this factory participates.
     * Includes counts of ancestor bean factories.
     * <p>Beans that are "overridden" (specified in a descendant factory
     * with the same name) are only counted once.
     * @param lbf the bean factory
     * @return count of beans including those defined in ancestor factories
     */
    public static int countBeansIncludingAncestors(ListableBeanFactory lbf) {
        return beanNamesIncludingAncestors(lbf).length;
    }

    /**
     * Return all bean names in the factory, including ancestor factories.
     * @param lbf the bean factory
     * @return the array of matching bean names, or an empty array if none
     * @see #beanNamesForTypeIncludingAncestors
     */
    public static String[] beanNamesIncludingAncestors(ListableBeanFactory lbf) {
        return beanNamesForTypeIncludingAncestors(lbf, Object.class);
    }


    /**
     * Get all bean names for the given type, including those defined in ancestor
     * factories. Will return unique names in case of overridden bean definitions.
     * <p>Does consider objects created by FactoryBeans, which means that FactoryBeans
     * will get initialized. If the object created by the FactoryBean doesn't match,
     * the raw FactoryBean itself will be matched against the type.
     * <p>This version of <code>beanNamesForTypeIncludingAncestors</code> automatically
     * includes prototypes and FactoryBeans.
     * @param lbf the bean factory
     * @param type the type that beans must match
     * @return the array of matching bean names, or an empty array if none
     */
    public static String[] beanNamesForTypeIncludingAncestors(ListableBeanFactory lbf, Class type) {
        Assert.notNull(lbf, "ListableBeanFactory must not be null");
        String[] result = lbf.getBeanNamesForType(type);
        if (lbf instanceof HierarchicalBeanFactory) {
            HierarchicalBeanFactory hbf = (HierarchicalBeanFactory) lbf;
            if (hbf.getParentBeanFactory() instanceof ListableBeanFactory) {
                String[] parentResult = beanNamesForTypeIncludingAncestors(
                        (ListableBeanFactory) hbf.getParentBeanFactory(), type);
                List resultList = new ArrayList();
                resultList.addAll(Arrays.asList(result));
                for (int i = 0; i < parentResult.length; i++) {
                    String beanName = parentResult[i];
                    if (!resultList.contains(beanName) && !hbf.containsLocalBean(beanName)) {
                        resultList.add(beanName);
                    }
                }
                result = StringUtils.toStringArray(resultList);
            }
        }
        return result;
    }

    /**
     * Get all bean names for the given type, including those defined in ancestor
     * factories. Will return unique names in case of overridden bean definitions.
     * <p>Does consider objects created by FactoryBeans if the "allowEagerInit"
     * flag is set, which means that FactoryBeans will get initialized. If the
     * object created by the FactoryBean doesn't match, the raw FactoryBean itself
     * will be matched against the type. If "allowEagerInit" is not set,
     * only raw FactoryBeans will be checked (which doesn't require initialization
     * of each FactoryBean).
     * @param lbf the bean factory
     * @param includeNonSingletons whether to include prototype or scoped beans too
     * or just singletons (also applies to FactoryBeans)
     * @param allowEagerInit whether to initialize <i>lazy-init singletons</i> and
     * <i>objects created by FactoryBeans</i> (or by factory methods with a
     * "factory-bean" reference) for the type check. Note that FactoryBeans need to be
     * eagerly initialized to determine their type: So be aware that passing in "true"
     * for this flag will initialize FactoryBeans and "factory-bean" references.
     * @param type the type that beans must match
     * @return the array of matching bean names, or an empty array if none
     */
    public static String[] beanNamesForTypeIncludingAncestors(
            ListableBeanFactory lbf, Class type, boolean includeNonSingletons, boolean allowEagerInit) {

        Assert.notNull(lbf, "ListableBeanFactory must not be null");
        String[] result = lbf.getBeanNamesForType(type, includeNonSingletons, allowEagerInit);
        if (lbf instanceof HierarchicalBeanFactory) {
            HierarchicalBeanFactory hbf = (HierarchicalBeanFactory) lbf;
            if (hbf.getParentBeanFactory() instanceof ListableBeanFactory) {
                String[] parentResult = beanNamesForTypeIncludingAncestors(
                        (ListableBeanFactory) hbf.getParentBeanFactory(), type, includeNonSingletons, allowEagerInit);
                List resultList = new ArrayList();
                resultList.addAll(Arrays.asList(result));
                for (int i = 0; i < parentResult.length; i++) {
                    String beanName = parentResult[i];
                    if (!resultList.contains(beanName) && !hbf.containsLocalBean(beanName)) {
                        resultList.add(beanName);
                    }
                }
                result = StringUtils.toStringArray(resultList);
            }
        }
        return result;
    }

    /**
     * Return all beans of the given type or subtypes, also picking up beans defined in
     * ancestor bean factories if the current bean factory is a HierarchicalBeanFactory.
     * The returned Map will only contain beans of this type.
     * <p>Does consider objects created by FactoryBeans, which means that FactoryBeans
     * will get initialized. If the object created by the FactoryBean doesn't match,
     * the raw FactoryBean itself will be matched against the type.
     * @param lbf the bean factory
     * @param type type of bean to match
     * @return the Map of matching bean instances, or an empty Map if none
     * @throws BeansException if a bean could not be created
     */
    public static Map beansOfTypeIncludingAncestors(ListableBeanFactory lbf, Class type)
            throws BeansException {

        Assert.notNull(lbf, "ListableBeanFactory must not be null");
        Map result = new LinkedHashMap(4);
        result.putAll(lbf.getBeansOfType(type));
        if (lbf instanceof HierarchicalBeanFactory) {
            HierarchicalBeanFactory hbf = (HierarchicalBeanFactory) lbf;
            if (hbf.getParentBeanFactory() instanceof ListableBeanFactory) {
                Map parentResult = beansOfTypeIncludingAncestors(
                        (ListableBeanFactory) hbf.getParentBeanFactory(), type);
                for (Iterator it = parentResult.entrySet().iterator(); it.hasNext();) {
                    Map.Entry entry = (Map.Entry) it.next();
                    String beanName = (String) entry.getKey();
                    if (!result.containsKey(beanName) && !hbf.containsLocalBean(beanName)) {
                        result.put(beanName, entry.getValue());
                    }
                }
            }
        }
        return result;
    }

    /**
     * Return all beans of the given type or subtypes, also picking up beans defined in
     * ancestor bean factories if the current bean factory is a HierarchicalBeanFactory.
     * The returned Map will only contain beans of this type.
     * <p>Does consider objects created by FactoryBeans if the "allowEagerInit"
     * flag is set, which means that FactoryBeans will get initialized. If the
     * object created by the FactoryBean doesn't match, the raw FactoryBean itself
     * will be matched against the type. If "allowEagerInit" is not set,
     * only raw FactoryBeans will be checked (which doesn't require initialization
     * of each FactoryBean).
     * @param lbf the bean factory
     * @param type type of bean to match
     * @param includeNonSingletons whether to include prototype or scoped beans too
     * or just singletons (also applies to FactoryBeans)
     * @param allowEagerInit whether to initialize <i>lazy-init singletons</i> and
     * <i>objects created by FactoryBeans</i> (or by factory methods with a
     * "factory-bean" reference) for the type check. Note that FactoryBeans need to be
     * eagerly initialized to determine their type: So be aware that passing in "true"
     * for this flag will initialize FactoryBeans and "factory-bean" references.
     * @return the Map of matching bean instances, or an empty Map if none
     * @throws BeansException if a bean could not be created
     */
    public static Map beansOfTypeIncludingAncestors(
            ListableBeanFactory lbf, Class type, boolean includeNonSingletons, boolean allowEagerInit)
            throws BeansException {

        Assert.notNull(lbf, "ListableBeanFactory must not be null");
        Map result = new LinkedHashMap(4);
        result.putAll(lbf.getBeansOfType(type, includeNonSingletons, allowEagerInit));
        if (lbf instanceof HierarchicalBeanFactory) {
            HierarchicalBeanFactory hbf = (HierarchicalBeanFactory) lbf;
            if (hbf.getParentBeanFactory() instanceof ListableBeanFactory) {
                Map parentResult = beansOfTypeIncludingAncestors(
                        (ListableBeanFactory) hbf.getParentBeanFactory(), type, includeNonSingletons, allowEagerInit);
                for (Iterator it = parentResult.entrySet().iterator(); it.hasNext();) {
                    Map.Entry entry = (Map.Entry) it.next();
                    String beanName = (String) entry.getKey();
                    if (!result.containsKey(beanName) && !hbf.containsLocalBean(beanName)) {
                        result.put(beanName, entry.getValue());
                    }
                }
            }
        }
        return result;
    }


    /**
     * Return a single bean of the given type or subtypes, also picking up beans
     * defined in ancestor bean factories if the current bean factory is a
     * HierarchicalBeanFactory. Useful convenience method when we expect a
     * single bean and don't care about the bean name.
     * <p>Does consider objects created by FactoryBeans, which means that FactoryBeans
     * will get initialized. If the object created by the FactoryBean doesn't match,
     * the raw FactoryBean itself will be matched against the type.
     * <p>This version of <code>beanOfTypeIncludingAncestors</code> automatically includes
     * prototypes and FactoryBeans.
     * @param lbf the bean factory
     * @param type type of bean to match
     * @return the matching bean instance
     * @throws com.rocket.summer.framework.beans.factory.NoSuchBeanDefinitionException
     * if 0 or more than 1 beans of the given type were found
     * @throws BeansException if the bean could not be created
     */
    public static Object beanOfTypeIncludingAncestors(ListableBeanFactory lbf, Class type)
            throws BeansException {

        Map beansOfType = beansOfTypeIncludingAncestors(lbf, type);
        if (beansOfType.size() == 1) {
            return beansOfType.values().iterator().next();
        }
        else {
            throw new NoSuchBeanDefinitionException(type, "expected single bean but found " + beansOfType.size());
        }
    }

    /**
     * Return a single bean of the given type or subtypes, also picking up beans
     * defined in ancestor bean factories if the current bean factory is a
     * HierarchicalBeanFactory. Useful convenience method when we expect a
     * single bean and don't care about the bean name.
     * <p>Does consider objects created by FactoryBeans if the "allowEagerInit"
     * flag is set, which means that FactoryBeans will get initialized. If the
     * object created by the FactoryBean doesn't match, the raw FactoryBean itself
     * will be matched against the type. If "allowEagerInit" is not set,
     * only raw FactoryBeans will be checked (which doesn't require initialization
     * of each FactoryBean).
     * @param lbf the bean factory
     * @param type type of bean to match
     * @param includeNonSingletons whether to include prototype or scoped beans too
     * or just singletons (also applies to FactoryBeans)
     * @param allowEagerInit whether to initialize <i>lazy-init singletons</i> and
     * <i>objects created by FactoryBeans</i> (or by factory methods with a
     * "factory-bean" reference) for the type check. Note that FactoryBeans need to be
     * eagerly initialized to determine their type: So be aware that passing in "true"
     * for this flag will initialize FactoryBeans and "factory-bean" references.
     * @return the matching bean instance
     * @throws com.rocket.summer.framework.beans.factory.NoSuchBeanDefinitionException
     * if 0 or more than 1 beans of the given type were found
     * @throws BeansException if the bean could not be created
     */
    public static Object beanOfTypeIncludingAncestors(
            ListableBeanFactory lbf, Class type, boolean includeNonSingletons, boolean allowEagerInit)
            throws BeansException {

        Map beansOfType = beansOfTypeIncludingAncestors(lbf, type, includeNonSingletons, allowEagerInit);
        if (beansOfType.size() == 1) {
            return beansOfType.values().iterator().next();
        }
        else {
            throw new NoSuchBeanDefinitionException(type, "expected single bean but found " + beansOfType.size());
        }
    }

    /**
     * Return a single bean of the given type or subtypes, not looking in ancestor
     * factories. Useful convenience method when we expect a single bean and
     * don't care about the bean name.
     * <p>Does consider objects created by FactoryBeans, which means that FactoryBeans
     * will get initialized. If the object created by the FactoryBean doesn't match,
     * the raw FactoryBean itself will be matched against the type.
     * <p>This version of <code>beanOfType</code> automatically includes
     * prototypes and FactoryBeans.
     * @param lbf the bean factory
     * @param type type of bean to match
     * @return the matching bean instance
     * @throws com.rocket.summer.framework.beans.factory.NoSuchBeanDefinitionException
     * if 0 or more than 1 beans of the given type were found
     * @throws BeansException if the bean could not be created
     */
    public static Object beanOfType(ListableBeanFactory lbf, Class type) throws BeansException {
        Assert.notNull(lbf, "ListableBeanFactory must not be null");
        Map beansOfType = lbf.getBeansOfType(type);
        if (beansOfType.size() == 1) {
            return beansOfType.values().iterator().next();
        }
        else {
            throw new NoSuchBeanDefinitionException(type, "expected single bean but found " + beansOfType.size());
        }
    }

    /**
     * Return a single bean of the given type or subtypes, not looking in ancestor
     * factories. Useful convenience method when we expect a single bean and
     * don't care about the bean name.
     * <p>Does consider objects created by FactoryBeans if the "allowEagerInit"
     * flag is set, which means that FactoryBeans will get initialized. If the
     * object created by the FactoryBean doesn't match, the raw FactoryBean itself
     * will be matched against the type. If "allowEagerInit" is not set,
     * only raw FactoryBeans will be checked (which doesn't require initialization
     * of each FactoryBean).
     * @param lbf the bean factory
     * @param type type of bean to match
     * @param includeNonSingletons whether to include prototype or scoped beans too
     * or just singletons (also applies to FactoryBeans)
     * @param allowEagerInit whether to initialize <i>lazy-init singletons</i> and
     * <i>objects created by FactoryBeans</i> (or by factory methods with a
     * "factory-bean" reference) for the type check. Note that FactoryBeans need to be
     * eagerly initialized to determine their type: So be aware that passing in "true"
     * for this flag will initialize FactoryBeans and "factory-bean" references.
     * @return the matching bean instance
     * @throws com.rocket.summer.framework.beans.factory.NoSuchBeanDefinitionException
     * if 0 or more than 1 beans of the given type were found
     * @throws BeansException if the bean could not be created
     */
    public static Object beanOfType(
            ListableBeanFactory lbf, Class type, boolean includeNonSingletons, boolean allowEagerInit)
            throws BeansException {

        Assert.notNull(lbf, "ListableBeanFactory must not be null");
        Map beansOfType = lbf.getBeansOfType(type, includeNonSingletons, allowEagerInit);
        if (beansOfType.size() == 1) {
            return beansOfType.values().iterator().next();
        }
        else {
            throw new NoSuchBeanDefinitionException(type, "expected single bean but found " + beansOfType.size());
        }
    }

}

