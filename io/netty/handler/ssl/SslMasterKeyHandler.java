/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.ssl;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.ssl.OpenSsl;
import io.netty.handler.ssl.ReferenceCountedOpenSslEngine;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.ssl.SslHandshakeCompletionEvent;
import io.netty.handler.ssl.SslMasterKeyHandler;
import io.netty.util.internal.ReflectionUtil;
import io.netty.util.internal.SystemPropertyUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLSession;

public abstract class SslMasterKeyHandler
extends ChannelInboundHandlerAdapter {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(SslMasterKeyHandler.class);
    private static final Class<?> SSL_SESSIONIMPL_CLASS;
    private static final Field SSL_SESSIONIMPL_MASTER_SECRET_FIELD;
    public static final String SYSTEM_PROP_KEY = "io.netty.ssl.masterKeyHandler";
    private static final Throwable UNAVAILABILITY_CAUSE;

    protected SslMasterKeyHandler() {
    }

    public static void ensureSunSslEngineAvailability() {
        if (UNAVAILABILITY_CAUSE == null) return;
        throw new IllegalStateException((String)"Failed to find SSLSessionImpl on classpath", (Throwable)UNAVAILABILITY_CAUSE);
    }

    public static Throwable sunSslEngineUnavailabilityCause() {
        return UNAVAILABILITY_CAUSE;
    }

    public static boolean isSunSslEngineAvailable() {
        if (UNAVAILABILITY_CAUSE != null) return false;
        return true;
    }

    protected abstract void accept(SecretKey var1, SSLSession var2);

    @Override
    public final void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
        boolean shouldHandle;
        if (evt == SslHandshakeCompletionEvent.SUCCESS && (shouldHandle = SystemPropertyUtil.getBoolean((String)"io.netty.ssl.masterKeyHandler", (boolean)false))) {
            SslHandler handler = ctx.pipeline().get(SslHandler.class);
            SSLEngine engine = handler.engine();
            SSLSession sslSession = engine.getSession();
            if (SslMasterKeyHandler.isSunSslEngineAvailable() && sslSession.getClass().equals(SSL_SESSIONIMPL_CLASS)) {
                SecretKey secretKey;
                try {
                    secretKey = (SecretKey)SSL_SESSIONIMPL_MASTER_SECRET_FIELD.get((Object)sslSession);
                }
                catch (IllegalAccessException e) {
                    throw new IllegalArgumentException((String)"Failed to access the field 'masterSecret' via reflection.", (Throwable)e);
                }
                this.accept((SecretKey)secretKey, (SSLSession)sslSession);
            } else if (OpenSsl.isAvailable() && engine instanceof ReferenceCountedOpenSslEngine) {
                SecretKeySpec secretKey = ((ReferenceCountedOpenSslEngine)engine).masterKey();
                this.accept((SecretKey)secretKey, (SSLSession)sslSession);
            }
        }
        ctx.fireUserEventTriggered((Object)evt);
    }

    public static SslMasterKeyHandler newWireSharkSslMasterKeyHandler() {
        return new WiresharkSslMasterKeyHandler(null);
    }

    static {
        Throwable cause = null;
        Class<?> clazz = null;
        Field field = null;
        try {
            clazz = Class.forName((String)"sun.security.ssl.SSLSessionImpl");
            field = clazz.getDeclaredField((String)"masterSecret");
            cause = ReflectionUtil.trySetAccessible((AccessibleObject)field, (boolean)true);
        }
        catch (Throwable e) {
            cause = e;
            logger.debug((String)"sun.security.ssl.SSLSessionImpl is unavailable.", (Throwable)e);
        }
        UNAVAILABILITY_CAUSE = cause;
        SSL_SESSIONIMPL_CLASS = clazz;
        SSL_SESSIONIMPL_MASTER_SECRET_FIELD = field;
    }
}

