/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.util.internal;

import io.netty.util.internal.Cleaner;
import io.netty.util.internal.CleanerJava9;
import io.netty.util.internal.PlatformDependent0;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.security.AccessController;
import java.security.PrivilegedAction;
import sun.misc.Unsafe;

final class CleanerJava9
implements Cleaner {
    private static final InternalLogger logger;
    private static final Method INVOKE_CLEANER;

    CleanerJava9() {
    }

    static boolean isSupported() {
        if (INVOKE_CLEANER == null) return false;
        return true;
    }

    @Override
    public void freeDirectBuffer(ByteBuffer buffer) {
        if (System.getSecurityManager() != null) {
            CleanerJava9.freeDirectBufferPrivileged((ByteBuffer)buffer);
            return;
        }
        try {
            INVOKE_CLEANER.invoke((Object)PlatformDependent0.UNSAFE, (Object[])new Object[]{buffer});
            return;
        }
        catch (Throwable cause) {
            PlatformDependent0.throwException((Throwable)cause);
            return;
        }
    }

    private static void freeDirectBufferPrivileged(ByteBuffer buffer) {
        Exception error = AccessController.doPrivileged(new PrivilegedAction<Exception>((ByteBuffer)buffer){
            final /* synthetic */ ByteBuffer val$buffer;
            {
                this.val$buffer = byteBuffer;
            }

            public Exception run() {
                try {
                    CleanerJava9.access$000().invoke((Object)PlatformDependent0.UNSAFE, (Object[])new Object[]{this.val$buffer});
                    return null;
                }
                catch (java.lang.reflect.InvocationTargetException e) {
                    return e;
                }
                catch (java.lang.IllegalAccessException e) {
                    return e;
                }
            }
        });
        if (error == null) return;
        PlatformDependent0.throwException((Throwable)error);
    }

    static /* synthetic */ Method access$000() {
        return INVOKE_CLEANER;
    }

    static {
        Method method;
        Throwable error;
        logger = InternalLoggerFactory.getInstance(CleanerJava9.class);
        if (PlatformDependent0.hasUnsafe()) {
            ByteBuffer buffer = ByteBuffer.allocateDirect((int)1);
            Object maybeInvokeMethod = AccessController.doPrivileged(new PrivilegedAction<Object>((ByteBuffer)buffer){
                final /* synthetic */ ByteBuffer val$buffer;
                {
                    this.val$buffer = byteBuffer;
                }

                public Object run() {
                    try {
                        Method m = PlatformDependent0.UNSAFE.getClass().getDeclaredMethod((String)"invokeCleaner", ByteBuffer.class);
                        m.invoke((Object)PlatformDependent0.UNSAFE, (Object[])new Object[]{this.val$buffer});
                        return m;
                    }
                    catch (java.lang.NoSuchMethodException e) {
                        return e;
                    }
                    catch (java.lang.reflect.InvocationTargetException e) {
                        return e;
                    }
                    catch (java.lang.IllegalAccessException e) {
                        return e;
                    }
                }
            });
            if (maybeInvokeMethod instanceof Throwable) {
                method = null;
                error = (Throwable)maybeInvokeMethod;
            } else {
                method = (Method)maybeInvokeMethod;
                error = null;
            }
        } else {
            method = null;
            error = new UnsupportedOperationException((String)"sun.misc.Unsafe unavailable");
        }
        if (error == null) {
            logger.debug((String)"java.nio.ByteBuffer.cleaner(): available");
        } else {
            logger.debug((String)"java.nio.ByteBuffer.cleaner(): unavailable", (Throwable)error);
        }
        INVOKE_CLEANER = method;
    }
}

