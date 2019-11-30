package com.rocket.summer.framework.cglib.core;

import com.rocket.summer.framework.asm.ClassReader;
import com.rocket.summer.framework.asm.ClassVisitor;
import com.rocket.summer.framework.asm.ClassWriter;
import com.rocket.summer.framework.cglib.core.CodeGenerationException;
import com.rocket.summer.framework.cglib.core.Constants;


import java.io.*;
import java.lang.reflect.Constructor;

public class DebuggingClassWriter extends ClassVisitor {

    public static final String DEBUG_LOCATION_PROPERTY = "cglib.debugLocation";

    private static String debugLocation;
    private static Constructor traceCtor;

    private String className;
    private String superName;

    static {
        debugLocation = System.getProperty(DEBUG_LOCATION_PROPERTY);
        if (debugLocation != null) {
            System.err.println("CGLIB debugging enabled, writing to '" + debugLocation + "'");
            try {
                Class clazz = Class.forName("com.rocket.summer.framework.asm.util.TraceClassVisitor");
                traceCtor = clazz.getConstructor(new Class[]{ClassVisitor.class, PrintWriter.class});
            } catch (Throwable ignore) {
            }
        }
    }

    public DebuggingClassWriter(int flags) {
        super(Constants.ASM_API, new ClassWriter(flags));
    }

    public void visit(int version,
                      int access,
                      String name,
                      String signature,
                      String superName,
                      String[] interfaces) {
        className = name.replace('/', '.');
        this.superName = superName.replace('/', '.');
        super.visit(version, access, name, signature, superName, interfaces);
    }

    public String getClassName() {
        return className;
    }

    public String getSuperName() {
        return superName;
    }

    public byte[] toByteArray() {

        return (byte[]) java.security.AccessController.doPrivileged(
                new java.security.PrivilegedAction() {
                    public Object run() {


                        byte[] b = ((ClassWriter) com.rocket.summer.framework.cglib.core.DebuggingClassWriter.super.cv).toByteArray();
                        if (debugLocation != null) {
                            String dirs = className.replace('.', File.separatorChar);
                            try {
                                new File(debugLocation + File.separatorChar + dirs).getParentFile().mkdirs();

                                File file = new File(new File(debugLocation), dirs + ".class");
                                OutputStream out = new BufferedOutputStream(new FileOutputStream(file));
                                try {
                                    out.write(b);
                                } finally {
                                    out.close();
                                }

                                if (traceCtor != null) {
                                    file = new File(new File(debugLocation), dirs + ".asm");
                                    out = new BufferedOutputStream(new FileOutputStream(file));
                                    try {
                                        ClassReader cr = new ClassReader(b);
                                        PrintWriter pw = new PrintWriter(new OutputStreamWriter(out));
                                        ClassVisitor tcv = (ClassVisitor)traceCtor.newInstance(new Object[]{null, pw});
                                        cr.accept(tcv, 0);
                                        pw.flush();
                                    } finally {
                                        out.close();
                                    }
                                }
                            } catch (Exception e) {
                                throw new CodeGenerationException(e);
                            }
                        }
                        return b;
                    }
                });

    }
}

