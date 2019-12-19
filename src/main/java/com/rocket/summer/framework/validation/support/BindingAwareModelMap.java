package com.rocket.summer.framework.validation.support;


import java.util.Map;

import com.rocket.summer.framework.ui.ExtendedModelMap;
import com.rocket.summer.framework.validation.BindingResult;

/**
 * Subclass of {@link com.rocket.summer.framework.ui.ExtendedModelMap} that automatically removes
 * a {@link com.rocket.summer.framework.validation.BindingResult} object if the corresponding
 * target attribute gets replaced through regular {@link Map} operations.
 *
 * <p>This is the class exposed to handler methods by Spring MVC, typically consumed through
 * a declaration of the {@link com.rocket.summer.framework.ui.Model} interface. There is no need to
 * build it within user code; a plain {@link com.rocket.summer.framework.ui.ModelMap} or even a just
 * a regular {@link Map} with String keys will be good enough to return a user model.
 *
 * @author Juergen Hoeller
 * @since 2.5.6
 * @see com.rocket.summer.framework.validation.BindingResult
 */
@SuppressWarnings("serial")
public class BindingAwareModelMap extends ExtendedModelMap {

    @Override
    public Object put(String key, Object value) {
        removeBindingResultIfNecessary(key, value);
        return super.put(key, value);
    }

    @Override
    public void putAll(Map<? extends String, ?> map) {
        for (Map.Entry<? extends String, ?> entry : map.entrySet()) {
            removeBindingResultIfNecessary(entry.getKey(), entry.getValue());
        }
        super.putAll(map);
    }

    private void removeBindingResultIfNecessary(Object key, Object value) {
        if (key instanceof String) {
            String attributeName = (String) key;
            if (!attributeName.startsWith(BindingResult.MODEL_KEY_PREFIX)) {
                String bindingResultKey = BindingResult.MODEL_KEY_PREFIX + attributeName;
                BindingResult bindingResult = (BindingResult) get(bindingResultKey);
                if (bindingResult != null && bindingResult.getTarget() != value) {
                    remove(bindingResultKey);
                }
            }
        }
    }

}

