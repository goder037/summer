package com.rocket.summer.framework.validation.support;

import com.rocket.summer.framework.validation.BindingResult;
import com.rocket.summer.framework.web.ui.ExtendedModelMap;

import java.util.Map;

/**
 * Subclass of {@link org.springframework.ui.ExtendedModelMap} that
 * automatically removes a {@link org.springframework.validation.BindingResult}
 * object if the corresponding target attribute gets replaced.
 *
 * <p>Used by {@link org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter}
 *
 * @author Juergen Hoeller
 * @since 2.5.6
 * @see org.springframework.validation.BindingResult
 */
public class BindingAwareModelMap extends ExtendedModelMap {

    @Override
    public Object put(String key, Object value) {
        removeBindingResultIfNecessary(key, value);
        return super.put(key, value);
    }

    @Override
    public void putAll(Map<? extends String, ?> map) {
        for (Map.Entry entry : map.entrySet()) {
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

