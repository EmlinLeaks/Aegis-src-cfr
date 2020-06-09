/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.channel;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelHandlerMask;
import io.netty.channel.ChannelInboundHandler;
import io.netty.channel.ChannelOutboundHandler;
import io.netty.channel.ChannelPromise;
import io.netty.util.concurrent.FastThreadLocal;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.net.SocketAddress;
import java.security.AccessController;
import java.security.PrivilegedExceptionAction;
import java.util.Map;

final class ChannelHandlerMask {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(ChannelHandlerMask.class);
    static final int MASK_EXCEPTION_CAUGHT = 1;
    static final int MASK_CHANNEL_REGISTERED = 2;
    static final int MASK_CHANNEL_UNREGISTERED = 4;
    static final int MASK_CHANNEL_ACTIVE = 8;
    static final int MASK_CHANNEL_INACTIVE = 16;
    static final int MASK_CHANNEL_READ = 32;
    static final int MASK_CHANNEL_READ_COMPLETE = 64;
    static final int MASK_USER_EVENT_TRIGGERED = 128;
    static final int MASK_CHANNEL_WRITABILITY_CHANGED = 256;
    static final int MASK_BIND = 512;
    static final int MASK_CONNECT = 1024;
    static final int MASK_DISCONNECT = 2048;
    static final int MASK_CLOSE = 4096;
    static final int MASK_DEREGISTER = 8192;
    static final int MASK_READ = 16384;
    static final int MASK_WRITE = 32768;
    static final int MASK_FLUSH = 65536;
    private static final int MASK_ALL_INBOUND = 511;
    private static final int MASK_ALL_OUTBOUND = 130561;
    private static final FastThreadLocal<Map<Class<? extends ChannelHandler>, Integer>> MASKS = new FastThreadLocal<Map<Class<? extends ChannelHandler>, Integer>>(){

        protected Map<Class<? extends ChannelHandler>, Integer> initialValue() {
            return new java.util.WeakHashMap<Class<? extends ChannelHandler>, Integer>((int)32);
        }
    };

    static int mask(Class<? extends ChannelHandler> clazz) {
        Map<Class<? extends ChannelHandler>, Integer> cache = MASKS.get();
        Integer mask = cache.get(clazz);
        if (mask != null) return mask.intValue();
        mask = Integer.valueOf((int)ChannelHandlerMask.mask0(clazz));
        cache.put(clazz, (Integer)mask);
        return mask.intValue();
    }

    private static int mask0(Class<? extends ChannelHandler> handlerType) {
        int mask = 1;
        try {
            if (ChannelInboundHandler.class.isAssignableFrom(handlerType)) {
                mask |= 511;
                if (ChannelHandlerMask.isSkippable(handlerType, (String)"channelRegistered", ChannelHandlerContext.class)) {
                    mask &= -3;
                }
                if (ChannelHandlerMask.isSkippable(handlerType, (String)"channelUnregistered", ChannelHandlerContext.class)) {
                    mask &= -5;
                }
                if (ChannelHandlerMask.isSkippable(handlerType, (String)"channelActive", ChannelHandlerContext.class)) {
                    mask &= -9;
                }
                if (ChannelHandlerMask.isSkippable(handlerType, (String)"channelInactive", ChannelHandlerContext.class)) {
                    mask &= -17;
                }
                if (ChannelHandlerMask.isSkippable(handlerType, (String)"channelRead", ChannelHandlerContext.class, Object.class)) {
                    mask &= -33;
                }
                if (ChannelHandlerMask.isSkippable(handlerType, (String)"channelReadComplete", ChannelHandlerContext.class)) {
                    mask &= -65;
                }
                if (ChannelHandlerMask.isSkippable(handlerType, (String)"channelWritabilityChanged", ChannelHandlerContext.class)) {
                    mask &= -257;
                }
                if (ChannelHandlerMask.isSkippable(handlerType, (String)"userEventTriggered", ChannelHandlerContext.class, Object.class)) {
                    mask &= -129;
                }
            }
            if (ChannelOutboundHandler.class.isAssignableFrom(handlerType)) {
                mask |= 130561;
                if (ChannelHandlerMask.isSkippable(handlerType, (String)"bind", ChannelHandlerContext.class, SocketAddress.class, ChannelPromise.class)) {
                    mask &= -513;
                }
                if (ChannelHandlerMask.isSkippable(handlerType, (String)"connect", ChannelHandlerContext.class, SocketAddress.class, SocketAddress.class, ChannelPromise.class)) {
                    mask &= -1025;
                }
                if (ChannelHandlerMask.isSkippable(handlerType, (String)"disconnect", ChannelHandlerContext.class, ChannelPromise.class)) {
                    mask &= -2049;
                }
                if (ChannelHandlerMask.isSkippable(handlerType, (String)"close", ChannelHandlerContext.class, ChannelPromise.class)) {
                    mask &= -4097;
                }
                if (ChannelHandlerMask.isSkippable(handlerType, (String)"deregister", ChannelHandlerContext.class, ChannelPromise.class)) {
                    mask &= -8193;
                }
                if (ChannelHandlerMask.isSkippable(handlerType, (String)"read", ChannelHandlerContext.class)) {
                    mask &= -16385;
                }
                if (ChannelHandlerMask.isSkippable(handlerType, (String)"write", ChannelHandlerContext.class, Object.class, ChannelPromise.class)) {
                    mask &= -32769;
                }
                if (ChannelHandlerMask.isSkippable(handlerType, (String)"flush", ChannelHandlerContext.class)) {
                    mask &= -65537;
                }
            }
            if (!ChannelHandlerMask.isSkippable(handlerType, (String)"exceptionCaught", ChannelHandlerContext.class, Throwable.class)) return mask;
            mask &= -2;
            return mask;
        }
        catch (Exception e) {
            PlatformDependent.throwException((Throwable)e);
        }
        return mask;
    }

    private static boolean isSkippable(Class<?> handlerType, String methodName, Class<?> ... paramTypes) throws Exception {
        return AccessController.doPrivileged(new PrivilegedExceptionAction<Boolean>(handlerType, (String)methodName, (Class[])paramTypes){
            final /* synthetic */ Class val$handlerType;
            final /* synthetic */ String val$methodName;
            final /* synthetic */ Class[] val$paramTypes;
            {
                this.val$handlerType = class_;
                this.val$methodName = string;
                this.val$paramTypes = arrclass;
            }

            public Boolean run() throws Exception {
                boolean bl;
                java.lang.reflect.Method m;
                try {
                    m = this.val$handlerType.getMethod((String)this.val$methodName, this.val$paramTypes);
                }
                catch (java.lang.NoSuchMethodException e) {
                    ChannelHandlerMask.access$000().debug((String)"Class {} missing method {}, assume we can not skip execution", (Object[])new Object[]{this.val$handlerType, this.val$methodName, e});
                    return Boolean.valueOf((boolean)false);
                }
                if (m != null && m.isAnnotationPresent(io.netty.channel.ChannelHandlerMask$Skip.class)) {
                    bl = true;
                    return Boolean.valueOf((boolean)bl);
                }
                bl = false;
                return Boolean.valueOf((boolean)bl);
            }
        }).booleanValue();
    }

    private ChannelHandlerMask() {
    }

    static /* synthetic */ InternalLogger access$000() {
        return logger;
    }
}

