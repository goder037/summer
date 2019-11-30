package com.rocket.summer.framework.cglib.core;

import com.rocket.summer.framework.asm.ClassReader;
import com.rocket.summer.framework.asm.ClassVisitor;

import java.util.ArrayList;
import java.util.List;

public class ClassNameReader {
    private ClassNameReader() {
    }

    private static final EarlyExitException EARLY_EXIT = new EarlyExitException();
    private static class EarlyExitException extends RuntimeException { }

    public static String getClassName(ClassReader r) {

        return getClassInfo(r)[0];

    }

    public static String[] getClassInfo(ClassReader r) {
        final List array = new ArrayList();
        try {
            r.accept(new ClassVisitor(Constants.ASM_API, null) {
                public void visit(int version,
                                  int access,
                                  String name,
                                  String signature,
                                  String superName,
                                  String[] interfaces) {
                    array.add( name.replace('/', '.') );
                    if(superName != null){
                        array.add( superName.replace('/', '.') );
                    }
                    for(int i = 0; i < interfaces.length; i++  ){
                        array.add( interfaces[i].replace('/', '.') );
                    }

                    throw EARLY_EXIT;
                }
            }, ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
        } catch (EarlyExitException e) { }

        return (String[])array.toArray( new String[]{} );
    }
}

