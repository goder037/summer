package com.rocket.summer.framework.cglib.core;

import com.rocket.summer.framework.asm.Label;

public interface ObjectSwitchCallback {
    void processCase(Object key, Label end) throws Exception;
    void processDefault() throws Exception;
}
