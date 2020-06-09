/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.util.internal;

import io.netty.util.internal.Cleaner;
import io.netty.util.internal.CleanerJava6;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.PlatformDependent0;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.security.AccessController;
import java.security.PrivilegedAction;

final class CleanerJava6
implements Cleaner {
    private static final long CLEANER_FIELD_OFFSET;
    private static final Method CLEAN_METHOD;
    private static final Field CLEANER_FIELD;
    private static final InternalLogger logger;

    CleanerJava6() {
    }

    static boolean isSupported() {
        if (CLEANER_FIELD_OFFSET != -1L) return true;
        if (CLEANER_FIELD != null) return true;
        return false;
    }

    @Override
    public void freeDirectBuffer(ByteBuffer buffer) {
        if (!buffer.isDirect()) {
            return;
        }
        if (System.getSecurityManager() != null) {
            CleanerJava6.freeDirectBufferPrivileged((ByteBuffer)buffer);
            return;
        }
        try {
            CleanerJava6.freeDirectBuffer0((ByteBuffer)buffer);
            return;
        }
        catch (Throwable cause) {
            PlatformDependent0.throwException((Throwable)cause);
            return;
        }
    }

    private static void freeDirectBufferPrivileged(ByteBuffer buffer) {
        Throwable cause = AccessController.doPrivileged(new PrivilegedAction<Throwable>((ByteBuffer)buffer){
            final /* synthetic */ ByteBuffer val$buffer;
            {
                this.val$buffer = byteBuffer;
            }

            public Throwable run() {
                try {
                    CleanerJava6.access$000((ByteBuffer)this.val$buffer);
                    return null;
                }
                catch (Throwable cause) {
                    return cause;
                }
            }
        });
        if (cause == null) return;
        PlatformDependent0.throwException((Throwable)cause);
    }

    private static void freeDirectBuffer0(ByteBuffer buffer) throws Exception {
        Object cleaner = CLEANER_FIELD_OFFSET == -1L ? CLEANER_FIELD.get((Object)buffer) : PlatformDependent0.getObject((Object)buffer, (long)CLEANER_FIELD_OFFSET);
        if (cleaner == null) return;
        CLEAN_METHOD.invoke((Object)cleaner, (Object[])new Object[0]);
    }

    static /* synthetic */ void access$000(ByteBuffer x0) throws Exception {
        CleanerJava6.freeDirectBuffer0((ByteBuffer)x0);
    }

    static {
        Method clean;
        long fieldOffset;
        Field cleanerField;
        logger = InternalLoggerFactory.getInstance(CleanerJava6.class);
        Throwable error = null;
        ByteBuffer direct = ByteBuffer.allocateDirect((int)1);
        try {
            Object cleaner;
            Object mayBeCleanerField = AccessController.doPrivileged(new PrivilegedAction<Object>((ByteBuffer)direct){
                final /* synthetic */ ByteBuffer val$direct;
                {
                    this.val$direct = byteBuffer;
                }

                public Object run() {
                    try {
                        Field cleanerField = this.val$direct.getClass().getDeclaredField((String)"cleaner");
                        if (PlatformDependent.hasUnsafe()) return cleanerField;
                        cleanerField.setAccessible((boolean)true);
                        return cleanerField;
                    }
                    catch (Throwable cause) {
                        return cause;
                    }
                }
            });
            if (mayBeCleanerField instanceof Throwable) {
                throw (Throwable)mayBeCleanerField;
            }
            cleanerField = (Field)mayBeCleanerField;
            if (PlatformDependent.hasUnsafe()) {
                fieldOffset = PlatformDependent0.objectFieldOffset((Field)cleanerField);
                cleaner = PlatformDependent0.getObject((Object)direct, (long)fieldOffset);
            } else {
                fieldOffset = -1L;
                cleaner = cleanerField.get((Object)direct);
            }
            clean = cleaner.getClass().getDeclaredMethod((String)"clean", new Class[0]);
            clean.invoke((Object)cleaner, (Object[])new Object[0]);
        }
        catch (Throwable t) {
            fieldOffset = -1L;
            clean = null;
            error = t;
            cleanerField = null;
        }
        if (error == null) {
            logger.debug((String)"java.nio.ByteBuffer.cleaner(): available");
        } else {
            logger.debug((String)"java.nio.ByteBuffer.cleaner(): unavailable", (Throwable)error);
        }
        CLEANER_FIELD = cleanerField;
        CLEANER_FIELD_OFFSET = fieldOffset;
        CLEAN_METHOD = clean;
    }
}

