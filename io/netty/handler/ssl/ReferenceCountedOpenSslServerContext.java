/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  io.netty.internal.tcnative.CertificateCallback
 *  io.netty.internal.tcnative.CertificateVerifier
 *  io.netty.internal.tcnative.SSLContext
 *  io.netty.internal.tcnative.SniHostNameMatcher
 */
package io.netty.handler.ssl;

import io.netty.buffer.ByteBufAllocator;
import io.netty.handler.ssl.ApplicationProtocolConfig;
import io.netty.handler.ssl.CipherSuiteFilter;
import io.netty.handler.ssl.ClientAuth;
import io.netty.handler.ssl.OpenSsl;
import io.netty.handler.ssl.OpenSslApplicationProtocolNegotiator;
import io.netty.handler.ssl.OpenSslCachingX509KeyManagerFactory;
import io.netty.handler.ssl.OpenSslEngineMap;
import io.netty.handler.ssl.OpenSslKeyMaterialManager;
import io.netty.handler.ssl.OpenSslKeyMaterialProvider;
import io.netty.handler.ssl.OpenSslServerSessionContext;
import io.netty.handler.ssl.OpenSslSessionContext;
import io.netty.handler.ssl.OpenSslX509KeyManagerFactory;
import io.netty.handler.ssl.ReferenceCountedOpenSslContext;
import io.netty.handler.ssl.ReferenceCountedOpenSslServerContext;
import io.netty.internal.tcnative.CertificateCallback;
import io.netty.internal.tcnative.CertificateVerifier;
import io.netty.internal.tcnative.SSLContext;
import io.netty.internal.tcnative.SniHostNameMatcher;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.SuppressJava6Requirement;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Enumeration;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSessionContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509ExtendedTrustManager;
import javax.net.ssl.X509TrustManager;

public final class ReferenceCountedOpenSslServerContext
extends ReferenceCountedOpenSslContext {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(ReferenceCountedOpenSslServerContext.class);
    private static final byte[] ID = new byte[]{110, 101, 116, 116, 121};
    private final OpenSslServerSessionContext sessionContext;

    ReferenceCountedOpenSslServerContext(X509Certificate[] trustCertCollection, TrustManagerFactory trustManagerFactory, X509Certificate[] keyCertChain, PrivateKey key, String keyPassword, KeyManagerFactory keyManagerFactory, Iterable<String> ciphers, CipherSuiteFilter cipherFilter, ApplicationProtocolConfig apn, long sessionCacheSize, long sessionTimeout, ClientAuth clientAuth, String[] protocols, boolean startTls, boolean enableOcsp, String keyStore) throws SSLException {
        this((X509Certificate[])trustCertCollection, (TrustManagerFactory)trustManagerFactory, (X509Certificate[])keyCertChain, (PrivateKey)key, (String)keyPassword, (KeyManagerFactory)keyManagerFactory, ciphers, (CipherSuiteFilter)cipherFilter, (OpenSslApplicationProtocolNegotiator)ReferenceCountedOpenSslServerContext.toNegotiator((ApplicationProtocolConfig)apn), (long)sessionCacheSize, (long)sessionTimeout, (ClientAuth)clientAuth, (String[])protocols, (boolean)startTls, (boolean)enableOcsp, (String)keyStore);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    ReferenceCountedOpenSslServerContext(X509Certificate[] trustCertCollection, TrustManagerFactory trustManagerFactory, X509Certificate[] keyCertChain, PrivateKey key, String keyPassword, KeyManagerFactory keyManagerFactory, Iterable<String> ciphers, CipherSuiteFilter cipherFilter, OpenSslApplicationProtocolNegotiator apn, long sessionCacheSize, long sessionTimeout, ClientAuth clientAuth, String[] protocols, boolean startTls, boolean enableOcsp, String keyStore) throws SSLException {
        super(ciphers, (CipherSuiteFilter)cipherFilter, (OpenSslApplicationProtocolNegotiator)apn, (long)sessionCacheSize, (long)sessionTimeout, (int)1, (Certificate[])keyCertChain, (ClientAuth)clientAuth, (String[])protocols, (boolean)startTls, (boolean)enableOcsp, (boolean)true);
        boolean success = false;
        try {
            this.sessionContext = ReferenceCountedOpenSslServerContext.newSessionContext((ReferenceCountedOpenSslContext)this, (long)this.ctx, (OpenSslEngineMap)this.engineMap, (X509Certificate[])trustCertCollection, (TrustManagerFactory)trustManagerFactory, (X509Certificate[])keyCertChain, (PrivateKey)key, (String)keyPassword, (KeyManagerFactory)keyManagerFactory, (String)keyStore);
            success = true;
            return;
        }
        finally {
            if (!success) {
                this.release();
            }
        }
    }

    @Override
    public OpenSslServerSessionContext sessionContext() {
        return this.sessionContext;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    static OpenSslServerSessionContext newSessionContext(ReferenceCountedOpenSslContext thiz, long ctx, OpenSslEngineMap engineMap, X509Certificate[] trustCertCollection, TrustManagerFactory trustManagerFactory, X509Certificate[] keyCertChain, PrivateKey key, String keyPassword, KeyManagerFactory keyManagerFactory, String keyStore) throws SSLException {
        OpenSslKeyMaterialProvider keyMaterialProvider = null;
        try {
            Object issuers;
            try {
                SSLContext.setVerify((long)ctx, (int)0, (int)10);
                if (!OpenSsl.useKeyManagerFactory()) {
                    if (keyManagerFactory != null) {
                        throw new IllegalArgumentException((String)"KeyManagerFactory not supported");
                    }
                    ObjectUtil.checkNotNull(keyCertChain, (String)"keyCertChain");
                    ReferenceCountedOpenSslServerContext.setKeyMaterial((long)ctx, (X509Certificate[])keyCertChain, (PrivateKey)key, (String)keyPassword);
                } else {
                    if (keyManagerFactory == null) {
                        char[] keyPasswordChars = ReferenceCountedOpenSslServerContext.keyStorePassword((String)keyPassword);
                        KeyStore ks = ReferenceCountedOpenSslServerContext.buildKeyStore((X509Certificate[])keyCertChain, (PrivateKey)key, (char[])keyPasswordChars, (String)keyStore);
                        keyManagerFactory = ks.aliases().hasMoreElements() ? new OpenSslX509KeyManagerFactory() : new OpenSslCachingX509KeyManagerFactory((KeyManagerFactory)KeyManagerFactory.getInstance((String)KeyManagerFactory.getDefaultAlgorithm()));
                        keyManagerFactory.init((KeyStore)ks, (char[])keyPasswordChars);
                    }
                    keyMaterialProvider = ReferenceCountedOpenSslServerContext.providerFor((KeyManagerFactory)keyManagerFactory, (String)keyPassword);
                    SSLContext.setCertificateCallback((long)ctx, (CertificateCallback)new OpenSslServerCertificateCallback((OpenSslEngineMap)engineMap, (OpenSslKeyMaterialManager)new OpenSslKeyMaterialManager((OpenSslKeyMaterialProvider)keyMaterialProvider)));
                }
            }
            catch (Exception e) {
                throw new SSLException((String)"failed to set certificate and key", (Throwable)e);
            }
            try {
                if (trustCertCollection != null) {
                    trustManagerFactory = ReferenceCountedOpenSslServerContext.buildTrustManagerFactory((X509Certificate[])trustCertCollection, (TrustManagerFactory)trustManagerFactory, (String)keyStore);
                } else if (trustManagerFactory == null) {
                    trustManagerFactory = TrustManagerFactory.getInstance((String)TrustManagerFactory.getDefaultAlgorithm());
                    trustManagerFactory.init((KeyStore)((KeyStore)null));
                }
                X509TrustManager manager = ReferenceCountedOpenSslServerContext.chooseTrustManager((TrustManager[])trustManagerFactory.getTrustManagers());
                ReferenceCountedOpenSslServerContext.setVerifyCallback((long)ctx, (OpenSslEngineMap)engineMap, (X509TrustManager)manager);
                issuers = manager.getAcceptedIssuers();
                if (issuers != null && ((X509Certificate[])issuers).length > 0) {
                    long bio = 0L;
                    try {
                        bio = ReferenceCountedOpenSslServerContext.toBIO((ByteBufAllocator)ByteBufAllocator.DEFAULT, (X509Certificate[])issuers);
                        if (!SSLContext.setCACertificateBio((long)ctx, (long)bio)) {
                            throw new SSLException((String)("unable to setup accepted issuers for trustmanager " + manager));
                        }
                    }
                    finally {
                        ReferenceCountedOpenSslServerContext.freeBio((long)bio);
                    }
                }
                if (PlatformDependent.javaVersion() >= 8) {
                    SSLContext.setSniHostnameMatcher((long)ctx, (SniHostNameMatcher)new OpenSslSniHostnameMatcher((OpenSslEngineMap)engineMap));
                }
            }
            catch (SSLException e) {
                throw e;
            }
            catch (Exception e) {
                throw new SSLException((String)"unable to setup trustmanager", (Throwable)e);
            }
            OpenSslServerSessionContext sessionContext = new OpenSslServerSessionContext((ReferenceCountedOpenSslContext)thiz, (OpenSslKeyMaterialProvider)keyMaterialProvider);
            sessionContext.setSessionIdContext((byte[])ID);
            keyMaterialProvider = null;
            issuers = sessionContext;
            return issuers;
        }
        finally {
            if (keyMaterialProvider != null) {
                keyMaterialProvider.destroy();
            }
        }
    }

    @SuppressJava6Requirement(reason="Guarded by java version check")
    private static void setVerifyCallback(long ctx, OpenSslEngineMap engineMap, X509TrustManager manager) {
        if (ReferenceCountedOpenSslServerContext.useExtendedTrustManager((X509TrustManager)manager)) {
            SSLContext.setCertVerifyCallback((long)ctx, (CertificateVerifier)new ExtendedTrustManagerVerifyCallback((OpenSslEngineMap)engineMap, (X509ExtendedTrustManager)((X509ExtendedTrustManager)manager)));
            return;
        }
        SSLContext.setCertVerifyCallback((long)ctx, (CertificateVerifier)new TrustManagerVerifyCallback((OpenSslEngineMap)engineMap, (X509TrustManager)manager));
    }

    static /* synthetic */ InternalLogger access$000() {
        return logger;
    }
}

