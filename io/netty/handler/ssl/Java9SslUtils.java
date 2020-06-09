/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.ssl;

import io.netty.handler.ssl.Java9SslUtils;
import io.netty.util.internal.EmptyArrays;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.SuppressJava6Requirement;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedExceptionAction;
import java.security.SecureRandom;
import java.util.List;
import java.util.function.BiFunction;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.TrustManager;

@SuppressJava6Requirement(reason="Usage guarded by java version check")
final class Java9SslUtils {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(Java9SslUtils.class);
    private static final Method SET_APPLICATION_PROTOCOLS;
    private static final Method GET_APPLICATION_PROTOCOL;
    private static final Method GET_HANDSHAKE_APPLICATION_PROTOCOL;
    private static final Method SET_HANDSHAKE_APPLICATION_PROTOCOL_SELECTOR;
    private static final Method GET_HANDSHAKE_APPLICATION_PROTOCOL_SELECTOR;

    private Java9SslUtils() {
    }

    static boolean supportsAlpn() {
        if (GET_APPLICATION_PROTOCOL == null) return false;
        return true;
    }

    static String getApplicationProtocol(SSLEngine sslEngine) {
        try {
            return (String)GET_APPLICATION_PROTOCOL.invoke((Object)sslEngine, (Object[])new Object[0]);
        }
        catch (UnsupportedOperationException ex) {
            throw ex;
        }
        catch (Exception ex) {
            throw new IllegalStateException((Throwable)ex);
        }
    }

    static String getHandshakeApplicationProtocol(SSLEngine sslEngine) {
        try {
            return (String)GET_HANDSHAKE_APPLICATION_PROTOCOL.invoke((Object)sslEngine, (Object[])new Object[0]);
        }
        catch (UnsupportedOperationException ex) {
            throw ex;
        }
        catch (Exception ex) {
            throw new IllegalStateException((Throwable)ex);
        }
    }

    static void setApplicationProtocols(SSLEngine engine, List<String> supportedProtocols) {
        SSLParameters parameters = engine.getSSLParameters();
        String[] protocolArray = supportedProtocols.toArray(EmptyArrays.EMPTY_STRINGS);
        try {
            SET_APPLICATION_PROTOCOLS.invoke((Object)parameters, (Object[])new Object[]{protocolArray});
        }
        catch (UnsupportedOperationException ex) {
            throw ex;
        }
        catch (Exception ex) {
            throw new IllegalStateException((Throwable)ex);
        }
        engine.setSSLParameters((SSLParameters)parameters);
    }

    static void setHandshakeApplicationProtocolSelector(SSLEngine engine, BiFunction<SSLEngine, List<String>, String> selector) {
        try {
            SET_HANDSHAKE_APPLICATION_PROTOCOL_SELECTOR.invoke((Object)engine, (Object[])new Object[]{selector});
            return;
        }
        catch (UnsupportedOperationException ex) {
            throw ex;
        }
        catch (Exception ex) {
            throw new IllegalStateException((Throwable)ex);
        }
    }

    static BiFunction<SSLEngine, List<String>, String> getHandshakeApplicationProtocolSelector(SSLEngine engine) {
        try {
            return (BiFunction)GET_HANDSHAKE_APPLICATION_PROTOCOL_SELECTOR.invoke((Object)engine, (Object[])new Object[0]);
        }
        catch (UnsupportedOperationException ex) {
            throw ex;
        }
        catch (Exception ex) {
            throw new IllegalStateException((Throwable)ex);
        }
    }

    static {
        Method getHandshakeApplicationProtocol = null;
        Method getApplicationProtocol = null;
        Method setApplicationProtocols = null;
        Method setHandshakeApplicationProtocolSelector = null;
        Method getHandshakeApplicationProtocolSelector = null;
        try {
            SSLContext context = SSLContext.getInstance((String)"TLS");
            context.init(null, null, null);
            SSLEngine engine = context.createSSLEngine();
            getHandshakeApplicationProtocol = AccessController.doPrivileged(new PrivilegedExceptionAction<Method>(){

                public Method run() throws Exception {
                    return SSLEngine.class.getMethod((String)"getHandshakeApplicationProtocol", new java.lang.Class[0]);
                }
            });
            getHandshakeApplicationProtocol.invoke((Object)engine, (Object[])new Object[0]);
            getApplicationProtocol = AccessController.doPrivileged(new PrivilegedExceptionAction<Method>(){

                public Method run() throws Exception {
                    return SSLEngine.class.getMethod((String)"getApplicationProtocol", new java.lang.Class[0]);
                }
            });
            getApplicationProtocol.invoke((Object)engine, (Object[])new Object[0]);
            setApplicationProtocols = AccessController.doPrivileged(new PrivilegedExceptionAction<Method>(){

                public Method run() throws Exception {
                    return SSLParameters.class.getMethod((String)"setApplicationProtocols", String[].class);
                }
            });
            setApplicationProtocols.invoke((Object)engine.getSSLParameters(), (Object[])new Object[]{EmptyArrays.EMPTY_STRINGS});
            setHandshakeApplicationProtocolSelector = AccessController.doPrivileged(new PrivilegedExceptionAction<Method>(){

                public Method run() throws Exception {
                    return SSLEngine.class.getMethod((String)"setHandshakeApplicationProtocolSelector", BiFunction.class);
                }
            });
            setHandshakeApplicationProtocolSelector.invoke((Object)engine, (Object[])new Object[]{new BiFunction<SSLEngine, List<String>, String>(){

                public String apply(SSLEngine sslEngine, List<String> strings) {
                    return null;
                }
            }});
            getHandshakeApplicationProtocolSelector = AccessController.doPrivileged(new PrivilegedExceptionAction<Method>(){

                public Method run() throws Exception {
                    return SSLEngine.class.getMethod((String)"getHandshakeApplicationProtocolSelector", new java.lang.Class[0]);
                }
            });
            getHandshakeApplicationProtocolSelector.invoke((Object)engine, (Object[])new Object[0]);
        }
        catch (Throwable t) {
            logger.error((String)"Unable to initialize Java9SslUtils, but the detected javaVersion was: {}", (Object)Integer.valueOf((int)PlatformDependent.javaVersion()), (Object)t);
            getHandshakeApplicationProtocol = null;
            getApplicationProtocol = null;
            setApplicationProtocols = null;
            setHandshakeApplicationProtocolSelector = null;
            getHandshakeApplicationProtocolSelector = null;
        }
        GET_HANDSHAKE_APPLICATION_PROTOCOL = getHandshakeApplicationProtocol;
        GET_APPLICATION_PROTOCOL = getApplicationProtocol;
        SET_APPLICATION_PROTOCOLS = setApplicationProtocols;
        SET_HANDSHAKE_APPLICATION_PROTOCOL_SELECTOR = setHandshakeApplicationProtocolSelector;
        GET_HANDSHAKE_APPLICATION_PROTOCOL_SELECTOR = getHandshakeApplicationProtocolSelector;
    }
}

