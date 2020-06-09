/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.util.internal;

import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.SuppressJava6Requirement;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Iterator;
import java.util.List;

public final class ThrowableUtil {
    private ThrowableUtil() {
    }

    public static <T extends Throwable> T unknownStackTrace(T cause, Class<?> clazz, String method) {
        ((Throwable)cause).setStackTrace((StackTraceElement[])new StackTraceElement[]{new StackTraceElement((String)clazz.getName(), (String)method, null, (int)-1)});
        return (T)cause;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static String stackTraceToString(Throwable cause) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream pout = new PrintStream((OutputStream)out);
        cause.printStackTrace((PrintStream)pout);
        pout.flush();
        try {
            String string = new String((byte[])out.toByteArray());
            return string;
        }
        finally {
            try {
                out.close();
            }
            catch (IOException iOException) {}
        }
    }

    public static boolean haveSuppressed() {
        if (PlatformDependent.javaVersion() < 7) return false;
        return true;
    }

    @SuppressJava6Requirement(reason="Throwable addSuppressed is only available for >= 7. Has check for < 7.")
    public static void addSuppressed(Throwable target, Throwable suppressed) {
        if (!ThrowableUtil.haveSuppressed()) {
            return;
        }
        target.addSuppressed((Throwable)suppressed);
    }

    public static void addSuppressedAndClear(Throwable target, List<Throwable> suppressed) {
        ThrowableUtil.addSuppressed((Throwable)target, suppressed);
        suppressed.clear();
    }

    public static void addSuppressed(Throwable target, List<Throwable> suppressed) {
        Iterator<Throwable> iterator = suppressed.iterator();
        while (iterator.hasNext()) {
            Throwable t = iterator.next();
            ThrowableUtil.addSuppressed((Throwable)target, (Throwable)t);
        }
    }
}

