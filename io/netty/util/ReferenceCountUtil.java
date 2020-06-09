/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.util;

import io.netty.util.ReferenceCountUtil;
import io.netty.util.ReferenceCounted;
import io.netty.util.ResourceLeakDetector;
import io.netty.util.ThreadDeathWatcher;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

public final class ReferenceCountUtil {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(ReferenceCountUtil.class);

    public static <T> T retain(T msg) {
        if (!(msg instanceof ReferenceCounted)) return (T)msg;
        return (T)((ReferenceCounted)msg).retain();
    }

    public static <T> T retain(T msg, int increment) {
        if (!(msg instanceof ReferenceCounted)) return (T)msg;
        return (T)((ReferenceCounted)msg).retain((int)increment);
    }

    public static <T> T touch(T msg) {
        if (!(msg instanceof ReferenceCounted)) return (T)msg;
        return (T)((ReferenceCounted)msg).touch();
    }

    public static <T> T touch(T msg, Object hint) {
        if (!(msg instanceof ReferenceCounted)) return (T)msg;
        return (T)((ReferenceCounted)msg).touch((Object)hint);
    }

    public static boolean release(Object msg) {
        if (!(msg instanceof ReferenceCounted)) return false;
        return ((ReferenceCounted)msg).release();
    }

    public static boolean release(Object msg, int decrement) {
        if (!(msg instanceof ReferenceCounted)) return false;
        return ((ReferenceCounted)msg).release((int)decrement);
    }

    public static void safeRelease(Object msg) {
        try {
            ReferenceCountUtil.release((Object)msg);
            return;
        }
        catch (Throwable t) {
            logger.warn((String)"Failed to release a message: {}", (Object)msg, (Object)t);
        }
    }

    public static void safeRelease(Object msg, int decrement) {
        try {
            ReferenceCountUtil.release((Object)msg, (int)decrement);
            return;
        }
        catch (Throwable t) {
            if (!logger.isWarnEnabled()) return;
            logger.warn((String)"Failed to release a message: {} (decrement: {})", (Object[])new Object[]{msg, Integer.valueOf((int)decrement), t});
        }
    }

    @Deprecated
    public static <T> T releaseLater(T msg) {
        return (T)ReferenceCountUtil.releaseLater(msg, (int)1);
    }

    @Deprecated
    public static <T> T releaseLater(T msg, int decrement) {
        if (!(msg instanceof ReferenceCounted)) return (T)msg;
        ThreadDeathWatcher.watch((Thread)Thread.currentThread(), (Runnable)new ReleasingTask((ReferenceCounted)((ReferenceCounted)msg), (int)decrement));
        return (T)msg;
    }

    public static int refCnt(Object msg) {
        if (!(msg instanceof ReferenceCounted)) return -1;
        int n = ((ReferenceCounted)msg).refCnt();
        return n;
    }

    private ReferenceCountUtil() {
    }

    static /* synthetic */ InternalLogger access$000() {
        return logger;
    }

    static {
        ResourceLeakDetector.addExclusions(ReferenceCountUtil.class, (String[])new String[]{"touch"});
    }
}

