/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.ssl;

import io.netty.handler.ssl.ApplicationProtocolConfig;
import io.netty.handler.ssl.CipherSuiteFilter;
import io.netty.handler.ssl.ClientAuth;
import io.netty.handler.ssl.IdentityCipherSuiteFilter;
import io.netty.handler.ssl.OpenSslApplicationProtocolNegotiator;
import io.netty.handler.ssl.OpenSslContext;
import io.netty.handler.ssl.OpenSslEngineMap;
import io.netty.handler.ssl.OpenSslKeyMaterialProvider;
import io.netty.handler.ssl.OpenSslServerSessionContext;
import io.netty.handler.ssl.OpenSslSessionContext;
import io.netty.handler.ssl.ReferenceCountedOpenSslContext;
import io.netty.handler.ssl.ReferenceCountedOpenSslServerContext;
import java.io.File;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSessionContext;
import javax.net.ssl.TrustManagerFactory;

public final class OpenSslServerContext
extends OpenSslContext {
    private final OpenSslServerSessionContext sessionContext;

    @Deprecated
    public OpenSslServerContext(File certChainFile, File keyFile) throws SSLException {
        this((File)certChainFile, (File)keyFile, null);
    }

    @Deprecated
    public OpenSslServerContext(File certChainFile, File keyFile, String keyPassword) throws SSLException {
        this((File)certChainFile, (File)keyFile, (String)keyPassword, null, (CipherSuiteFilter)IdentityCipherSuiteFilter.INSTANCE, (ApplicationProtocolConfig)ApplicationProtocolConfig.DISABLED, (long)0L, (long)0L);
    }

    @Deprecated
    public OpenSslServerContext(File certChainFile, File keyFile, String keyPassword, Iterable<String> ciphers, ApplicationProtocolConfig apn, long sessionCacheSize, long sessionTimeout) throws SSLException {
        this((File)certChainFile, (File)keyFile, (String)keyPassword, ciphers, (CipherSuiteFilter)IdentityCipherSuiteFilter.INSTANCE, (ApplicationProtocolConfig)apn, (long)sessionCacheSize, (long)sessionTimeout);
    }

    @Deprecated
    public OpenSslServerContext(File certChainFile, File keyFile, String keyPassword, Iterable<String> ciphers, Iterable<String> nextProtocols, long sessionCacheSize, long sessionTimeout) throws SSLException {
        this((File)certChainFile, (File)keyFile, (String)keyPassword, ciphers, (ApplicationProtocolConfig)OpenSslServerContext.toApplicationProtocolConfig(nextProtocols), (long)sessionCacheSize, (long)sessionTimeout);
    }

    @Deprecated
    public OpenSslServerContext(File certChainFile, File keyFile, String keyPassword, TrustManagerFactory trustManagerFactory, Iterable<String> ciphers, ApplicationProtocolConfig config, long sessionCacheSize, long sessionTimeout) throws SSLException {
        this((File)certChainFile, (File)keyFile, (String)keyPassword, (TrustManagerFactory)trustManagerFactory, ciphers, (OpenSslApplicationProtocolNegotiator)OpenSslServerContext.toNegotiator((ApplicationProtocolConfig)config), (long)sessionCacheSize, (long)sessionTimeout);
    }

    @Deprecated
    public OpenSslServerContext(File certChainFile, File keyFile, String keyPassword, TrustManagerFactory trustManagerFactory, Iterable<String> ciphers, OpenSslApplicationProtocolNegotiator apn, long sessionCacheSize, long sessionTimeout) throws SSLException {
        this(null, (TrustManagerFactory)trustManagerFactory, (File)certChainFile, (File)keyFile, (String)keyPassword, null, ciphers, null, (OpenSslApplicationProtocolNegotiator)apn, (long)sessionCacheSize, (long)sessionTimeout);
    }

    @Deprecated
    public OpenSslServerContext(File certChainFile, File keyFile, String keyPassword, Iterable<String> ciphers, CipherSuiteFilter cipherFilter, ApplicationProtocolConfig apn, long sessionCacheSize, long sessionTimeout) throws SSLException {
        this(null, null, (File)certChainFile, (File)keyFile, (String)keyPassword, null, ciphers, (CipherSuiteFilter)cipherFilter, (ApplicationProtocolConfig)apn, (long)sessionCacheSize, (long)sessionTimeout);
    }

    @Deprecated
    public OpenSslServerContext(File trustCertCollectionFile, TrustManagerFactory trustManagerFactory, File keyCertChainFile, File keyFile, String keyPassword, KeyManagerFactory keyManagerFactory, Iterable<String> ciphers, CipherSuiteFilter cipherFilter, ApplicationProtocolConfig config, long sessionCacheSize, long sessionTimeout) throws SSLException {
        this((File)trustCertCollectionFile, (TrustManagerFactory)trustManagerFactory, (File)keyCertChainFile, (File)keyFile, (String)keyPassword, (KeyManagerFactory)keyManagerFactory, ciphers, (CipherSuiteFilter)cipherFilter, (OpenSslApplicationProtocolNegotiator)OpenSslServerContext.toNegotiator((ApplicationProtocolConfig)config), (long)sessionCacheSize, (long)sessionTimeout);
    }

    @Deprecated
    public OpenSslServerContext(File certChainFile, File keyFile, String keyPassword, TrustManagerFactory trustManagerFactory, Iterable<String> ciphers, CipherSuiteFilter cipherFilter, ApplicationProtocolConfig config, long sessionCacheSize, long sessionTimeout) throws SSLException {
        this(null, (TrustManagerFactory)trustManagerFactory, (File)certChainFile, (File)keyFile, (String)keyPassword, null, ciphers, (CipherSuiteFilter)cipherFilter, (OpenSslApplicationProtocolNegotiator)OpenSslServerContext.toNegotiator((ApplicationProtocolConfig)config), (long)sessionCacheSize, (long)sessionTimeout);
    }

    @Deprecated
    public OpenSslServerContext(File certChainFile, File keyFile, String keyPassword, TrustManagerFactory trustManagerFactory, Iterable<String> ciphers, CipherSuiteFilter cipherFilter, OpenSslApplicationProtocolNegotiator apn, long sessionCacheSize, long sessionTimeout) throws SSLException {
        this(null, (TrustManagerFactory)trustManagerFactory, (File)certChainFile, (File)keyFile, (String)keyPassword, null, ciphers, (CipherSuiteFilter)cipherFilter, (OpenSslApplicationProtocolNegotiator)apn, (long)sessionCacheSize, (long)sessionTimeout);
    }

    @Deprecated
    public OpenSslServerContext(File trustCertCollectionFile, TrustManagerFactory trustManagerFactory, File keyCertChainFile, File keyFile, String keyPassword, KeyManagerFactory keyManagerFactory, Iterable<String> ciphers, CipherSuiteFilter cipherFilter, OpenSslApplicationProtocolNegotiator apn, long sessionCacheSize, long sessionTimeout) throws SSLException {
        this((X509Certificate[])OpenSslServerContext.toX509CertificatesInternal((File)trustCertCollectionFile), (TrustManagerFactory)trustManagerFactory, (X509Certificate[])OpenSslServerContext.toX509CertificatesInternal((File)keyCertChainFile), (PrivateKey)OpenSslServerContext.toPrivateKeyInternal((File)keyFile, (String)keyPassword), (String)keyPassword, (KeyManagerFactory)keyManagerFactory, ciphers, (CipherSuiteFilter)cipherFilter, (OpenSslApplicationProtocolNegotiator)apn, (long)sessionCacheSize, (long)sessionTimeout, (ClientAuth)ClientAuth.NONE, null, (boolean)false, (boolean)false, (String)KeyStore.getDefaultType());
    }

    OpenSslServerContext(X509Certificate[] trustCertCollection, TrustManagerFactory trustManagerFactory, X509Certificate[] keyCertChain, PrivateKey key, String keyPassword, KeyManagerFactory keyManagerFactory, Iterable<String> ciphers, CipherSuiteFilter cipherFilter, ApplicationProtocolConfig apn, long sessionCacheSize, long sessionTimeout, ClientAuth clientAuth, String[] protocols, boolean startTls, boolean enableOcsp, String keyStore) throws SSLException {
        this((X509Certificate[])trustCertCollection, (TrustManagerFactory)trustManagerFactory, (X509Certificate[])keyCertChain, (PrivateKey)key, (String)keyPassword, (KeyManagerFactory)keyManagerFactory, ciphers, (CipherSuiteFilter)cipherFilter, (OpenSslApplicationProtocolNegotiator)OpenSslServerContext.toNegotiator((ApplicationProtocolConfig)apn), (long)sessionCacheSize, (long)sessionTimeout, (ClientAuth)clientAuth, (String[])protocols, (boolean)startTls, (boolean)enableOcsp, (String)keyStore);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private OpenSslServerContext(X509Certificate[] trustCertCollection, TrustManagerFactory trustManagerFactory, X509Certificate[] keyCertChain, PrivateKey key, String keyPassword, KeyManagerFactory keyManagerFactory, Iterable<String> ciphers, CipherSuiteFilter cipherFilter, OpenSslApplicationProtocolNegotiator apn, long sessionCacheSize, long sessionTimeout, ClientAuth clientAuth, String[] protocols, boolean startTls, boolean enableOcsp, String keyStore) throws SSLException {
        super(ciphers, (CipherSuiteFilter)cipherFilter, (OpenSslApplicationProtocolNegotiator)apn, (long)sessionCacheSize, (long)sessionTimeout, (int)1, (Certificate[])keyCertChain, (ClientAuth)clientAuth, (String[])protocols, (boolean)startTls, (boolean)enableOcsp);
        boolean success = false;
        try {
            OpenSslKeyMaterialProvider.validateKeyMaterialSupported((X509Certificate[])keyCertChain, (PrivateKey)key, (String)keyPassword);
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
}

