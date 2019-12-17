package com.rocket.summer.framework.objenesis.strategy;

import com.rocket.summer.framework.objenesis.instantiator.ObjectInstantiator;

public interface InstantiatorStrategy {
    <T> ObjectInstantiator<T> newInstantiatorOf(Class<T> var1);
}

