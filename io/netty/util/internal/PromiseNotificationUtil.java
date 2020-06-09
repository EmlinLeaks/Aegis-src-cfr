/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.util.internal;

import io.netty.util.concurrent.Promise;
import io.netty.util.internal.ThrowableUtil;
import io.netty.util.internal.logging.InternalLogger;

public final class PromiseNotificationUtil {
    private PromiseNotificationUtil() {
    }

    public static void tryCancel(Promise<?> p, InternalLogger logger) {
        if (p.cancel((boolean)false)) return;
        if (logger == null) return;
        Throwable err = p.cause();
        if (err == null) {
            logger.warn((String)"Failed to cancel promise because it has succeeded already: {}", p);
            return;
        }
        logger.warn((String)"Failed to cancel promise because it has failed already: {}, unnotified cause:", p, (Object)err);
    }

    public static <V> void trySuccess(Promise<? super V> p, V result, InternalLogger logger) {
        if (p.trySuccess(result)) return;
        if (logger == null) return;
        Throwable err = p.cause();
        if (err == null) {
            logger.warn((String)"Failed to mark a promise as success because it has succeeded already: {}", p);
            return;
        }
        logger.warn((String)"Failed to mark a promise as success because it has failed already: {}, unnotified cause:", p, (Object)err);
    }

    public static void tryFailure(Promise<?> p, Throwable cause, InternalLogger logger) {
        if (p.tryFailure((Throwable)cause)) return;
        if (logger == null) return;
        Throwable err = p.cause();
        if (err == null) {
            logger.warn((String)"Failed to mark a promise as failure because it has succeeded already: {}", p, (Object)cause);
            return;
        }
        logger.warn((String)"Failed to mark a promise as failure because it has failed already: {}, unnotified cause: {}", (Object[])new Object[]{p, ThrowableUtil.stackTraceToString((Throwable)err), cause});
    }
}

