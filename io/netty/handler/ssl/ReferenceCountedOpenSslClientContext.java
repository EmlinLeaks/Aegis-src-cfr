/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  io.netty.internal.tcnative.CertificateCallback
 *  io.netty.internal.tcnative.CertificateVerifier
 *  io.netty.internal.tcnative.SSLContext
 */
package io.netty.handler.ssl;

import io.netty.handler.ssl.ApplicationProtocolConfig;
import io.netty.handler.ssl.CipherSuiteFilter;
import io.netty.handler.ssl.ClientAuth;
import io.netty.handler.ssl.OpenSsl;
import io.netty.handler.ssl.OpenSslCachingX509KeyManagerFactory;
import io.netty.handler.ssl.OpenSslEngineMap;
import io.netty.handler.ssl.OpenSslKeyMaterialManager;
import io.netty.handler.ssl.OpenSslKeyMaterialProvider;
import io.netty.handler.ssl.OpenSslSessionContext;
import io.netty.handler.ssl.OpenSslX509KeyManagerFactory;
import io.netty.handler.ssl.ReferenceCountedOpenSslClientContext;
import io.netty.handler.ssl.ReferenceCountedOpenSslContext;
import io.netty.internal.tcnative.CertificateCallback;
import io.netty.internal.tcnative.CertificateVerifier;
import io.netty.internal.tcnative.SSLContext;
import io.netty.util.internal.SuppressJava6Requirement;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSessionContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509ExtendedTrustManager;
import javax.net.ssl.X509TrustManager;

public final class ReferenceCountedOpenSslClientContext
extends ReferenceCountedOpenSslContext {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(ReferenceCountedOpenSslClientContext.class);
    private static final Set<String> SUPPORTED_KEY_TYPES = Collections.unmodifiableSet(new LinkedHashSet<String>(Arrays.asList("RSA", "DH_RSA", "EC", "EC_RSA", "EC_EC")));
    private final OpenSslSessionContext sessionContext;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    ReferenceCountedOpenSslClientContext(X509Certificate[] trustCertCollection, TrustManagerFactory trustManagerFactory, X509Certificate[] keyCertChain, PrivateKey key, String keyPassword, KeyManagerFactory keyManagerFactory, Iterable<String> ciphers, CipherSuiteFilter cipherFilter, ApplicationProtocolConfig apn, String[] protocols, long sessionCacheSize, long sessionTimeout, boolean enableOcsp, String keyStore) throws SSLException {
        super(ciphers, (CipherSuiteFilter)cipherFilter, (ApplicationProtocolConfig)apn, (long)sessionCacheSize, (long)sessionTimeout, (int)0, (Certificate[])keyCertChain, (ClientAuth)ClientAuth.NONE, (String[])protocols, (boolean)false, (boolean)enableOcsp, (boolean)true);
        boolean success = false;
        try {
            this.sessionContext = ReferenceCountedOpenSslClientContext.newSessionContext((ReferenceCountedOpenSslContext)this, (long)this.ctx, (OpenSslEngineMap)this.engineMap, (X509Certificate[])trustCertCollection, (TrustManagerFactory)trustManagerFactory, (X509Certificate[])keyCertChain, (PrivateKey)key, (String)keyPassword, (KeyManagerFactory)keyManagerFactory, (String)keyStore);
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
    public OpenSslSessionContext sessionContext() {
        return this.sessionContext;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    static OpenSslSessionContext newSessionContext(ReferenceCountedOpenSslContext thiz, long ctx, OpenSslEngineMap engineMap, X509Certificate[] trustCertCollection, TrustManagerFactory trustManagerFactory, X509Certificate[] keyCertChain, PrivateKey key, String keyPassword, KeyManagerFactory keyManagerFactory, String keyStore) throws SSLException {
        if (key == null) {
            if (keyCertChain != null) throw new IllegalArgumentException((String)"Either both keyCertChain and key needs to be null or none of them");
        }
        if (key != null && keyCertChain == null) {
            throw new IllegalArgumentException((String)"Either both keyCertChain and key needs to be null or none of them");
        }
        OpenSslKeyMaterialProvider keyMaterialProvider = null;
        try {
            Object ks;
            try {
                if (!OpenSsl.useKeyManagerFactory()) {
                    if (keyManagerFactory != null) {
                        throw new IllegalArgumentException((String)"KeyManagerFactory not supported");
                    }
                    if (keyCertChain != null) {
                        ReferenceCountedOpenSslClientContext.setKeyMaterial((long)ctx, (X509Certificate[])keyCertChain, (PrivateKey)key, (String)keyPassword);
                    }
                } else {
                    if (keyManagerFactory == null && keyCertChain != null) {
                        char[] keyPasswordChars = ReferenceCountedOpenSslClientContext.keyStorePassword((String)keyPassword);
                        ks = ReferenceCountedOpenSslClientContext.buildKeyStore((X509Certificate[])keyCertChain, (PrivateKey)key, (char[])keyPasswordChars, (String)keyStore);
                        keyManagerFactory = ((KeyStore)ks).aliases().hasMoreElements() ? new OpenSslX509KeyManagerFactory() : new OpenSslCachingX509KeyManagerFactory((KeyManagerFactory)KeyManagerFactory.getInstance((String)KeyManagerFactory.getDefaultAlgorithm()));
                        keyManagerFactory.init((KeyStore)ks, (char[])keyPasswordChars);
                        keyMaterialProvider = ReferenceCountedOpenSslClientContext.providerFor((KeyManagerFactory)keyManagerFactory, (String)keyPassword);
                    } else if (keyManagerFactory != null) {
                        keyMaterialProvider = ReferenceCountedOpenSslClientContext.providerFor((KeyManagerFactory)keyManagerFactory, (String)keyPassword);
                    }
                    if (keyMaterialProvider != null) {
                        OpenSslKeyMaterialManager materialManager = new OpenSslKeyMaterialManager((OpenSslKeyMaterialProvider)keyMaterialProvider);
                        SSLContext.setCertificateCallback((long)ctx, (CertificateCallback)new OpenSslClientCertificateCallback((OpenSslEngineMap)engineMap, (OpenSslKeyMaterialManager)materialManager));
                    }
                }
            }
            catch (Exception e) {
                throw new SSLException((String)"failed to set certificate and key", (Throwable)e);
            }
            SSLContext.setVerify((long)ctx, (int)1, (int)10);
            try {
                if (trustCertCollection != null) {
                    trustManagerFactory = ReferenceCountedOpenSslClientContext.buildTrustManagerFactory((X509Certificate[])trustCertCollection, (TrustManagerFactory)trustManagerFactory, (String)keyStore);
                } else if (trustManagerFactory == null) {
                    trustManagerFactory = TrustManagerFactory.getInstance((String)TrustManagerFactory.getDefaultAlgorithm());
                    trustManagerFactory.init((KeyStore)((KeyStore)null));
                }
                X509TrustManager manager = ReferenceCountedOpenSslClientContext.chooseTrustManager((TrustManager[])trustManagerFactory.getTrustManagers());
                ReferenceCountedOpenSslClientContext.setVerifyCallback((long)ctx, (OpenSslEngineMap)engineMap, (X509TrustManager)manager);
            }
            catch (Exception e) {
                if (keyMaterialProvider == null) throw new SSLException((String)"unable to setup trustmanager", (Throwable)e);
                keyMaterialProvider.destroy();
                throw new SSLException((String)"unable to setup trustmanager", (Throwable)e);
            }
            OpenSslClientSessionContext context = new OpenSslClientSessionContext((ReferenceCountedOpenSslContext)thiz, (OpenSslKeyMaterialProvider)keyMaterialProvider);
            keyMaterialProvider = null;
            ks = context;
            return ks;
        }
        finally {
            if (keyMaterialProvider != null) {
                keyMaterialProvider.destroy();
            }
        }
    }

    @SuppressJava6Requirement(reason="Guarded by java version check")
    private static void setVerifyCallback(long ctx, OpenSslEngineMap engineMap, X509TrustManager manager) {
        if (ReferenceCountedOpenSslClientContext.useExtendedTrustManager((X509TrustManager)manager)) {
            SSLContext.setCertVerifyCallback((long)ctx, (CertificateVerifier)new ExtendedTrustManagerVerifyCallback((OpenSslEngineMap)engineMap, (X509ExtendedTrustManager)((X509ExtendedTrustManager)manager)));
            return;
        }
        SSLContext.setCertVerifyCallback((long)ctx, (CertificateVerifier)new TrustManagerVerifyCallback((OpenSslEngineMap)engineMap, (X509TrustManager)manager));
    }

    static /* synthetic */ InternalLogger access$000() {
        return logger;
    }

    static /* synthetic */ Set access$100() {
        return SUPPORTED_KEY_TYPES;
    }
}

