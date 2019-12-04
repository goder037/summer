package com.rocket.summer.framework.cglib.transform.impl;

public interface InterceptFieldEnabled {
    void setInterceptFieldCallback(InterceptFieldCallback callback);
    InterceptFieldCallback getInterceptFieldCallback();
}
