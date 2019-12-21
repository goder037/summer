package com.rocket.summer.framework.cglib.transform.impl;

import com.rocket.summer.framework.asm.Type;

public interface InterceptFieldFilter {
    boolean acceptRead(Type owner, String name);
    boolean acceptWrite(Type owner, String name);
}
