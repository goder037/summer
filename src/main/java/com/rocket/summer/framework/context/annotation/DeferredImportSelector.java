package com.rocket.summer.framework.context.annotation;

/**
 * A variation of {@link ImportSelector} that runs after all {@code @Configuration} beans
 * have been processed. This type of selector can be particularly useful when the selected
 * imports are {@code @Conditional}.
 *
 * <p>Implementations can also extend the {@link com.rocket.summer.framework.core.Ordered}
 * interface or use the {@link com.rocket.summer.framework.core.annotation.Order} annotation to
 * indicate a precedence against other {@link DeferredImportSelector}s.
 *
 * @author Phillip Webb
 * @since 4.0
 */
public interface DeferredImportSelector extends ImportSelector {

}
