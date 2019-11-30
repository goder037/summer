package com.rocket.summer.framework.cglib.core;

import com.rocket.summer.framework.asm.Opcodes;

final class AsmApi {

    /**
     * Returns the latest stable ASM API value in {@link Opcodes}.
     */
    static int value() {
        return Opcodes.ASM6;
    }

    private AsmApi() {
    }
}
