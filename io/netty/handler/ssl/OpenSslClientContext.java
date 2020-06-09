/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.ssl;

import io.netty.handler.ssl.ApplicationProtocolConfig;
import io.netty.handler.ssl.CipherSuiteFilter;
import io.netty.handler.ssl.ClientAuth;
import io.netty.handler.ssl.IdentityCipherSuiteFilter;
import io.netty.handler.ssl.OpenSslContext;
import io.netty.handler.ssl.OpenSslEngineMap;
import io.netty.handler.ssl.OpenSslKeyMaterialProvider;
import io.netty.handler.ssl.OpenSslSessionContext;
import io.netty.handler.ssl.ReferenceCountedOpenSslClientContext;
import io.netty.handler.ssl.ReferenceCountedOpenSslContext;
import java.io.File;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSessionContext;
import javax.net.ssl.TrustManagerFactory;

public final class OpenSslClientContext
extends OpenSslContext {
    private final OpenSslSessionContext sessionContext;

    @Deprecated
    public OpenSslClientContext() throws SSLException {
        this(null, null, null, null, null, null, null, (CipherSuiteFilter)IdentityCipherSuiteFilter.INSTANCE, null, (long)0L, (long)0L);
    }

    @Deprecated
    public OpenSslClientContext(File certChainFile) throws SSLException {
        this((File)certChainFile, null);
    }

    @Deprecated
    public OpenSslClientContext(TrustManagerFactory trustManagerFactory) throws SSLException {
        this(null, (TrustManagerFactory)trustManagerFactory);
    }

    @Deprecated
    public OpenSslClientContext(File certChainFile, TrustManagerFactory trustManagerFactory) throws SSLException {
        this((File)certChainFile, (TrustManagerFactory)trustManagerFactory, null, null, null, null, null, (CipherSuiteFilter)IdentityCipherSuiteFilter.INSTANCE, null, (long)0L, (long)0L);
    }

    @Deprecated
    public OpenSslClientContext(File certChainFile, TrustManagerFactory trustManagerFactory, Iterable<String> ciphers, ApplicationProtocolConfig apn, long sessionCacheSize, long sessionTimeout) throws SSLException {
        this((File)certChainFile, (TrustManagerFactory)trustManagerFactory, null, null, null, null, ciphers, (CipherSuiteFilter)IdentityCipherSuiteFilter.INSTANCE, (ApplicationProtocolConfig)apn, (long)sessionCacheSize, (long)sessionTimeout);
    }

    @Deprecated
    public OpenSslClientContext(File certChainFile, TrustManagerFactory trustManagerFactory, Iterable<String> ciphers, CipherSuiteFilter cipherFilter, ApplicationProtocolConfig apn, long sessionCacheSize, long sessionTimeout) throws SSLException {
        this((File)certChainFile, (TrustManagerFactory)trustManagerFactory, null, null, null, null, ciphers, (CipherSuiteFilter)cipherFilter, (ApplicationProtocolConfig)apn, (long)sessionCacheSize, (long)sessionTimeout);
    }

    @Deprecated
    public OpenSslClientContext(File trustCertCollectionFile, TrustManagerFactory trustManagerFactory, File keyCertChainFile, File keyFile, String keyPassword, KeyManagerFactory keyManagerFactory, Iterable<String> ciphers, CipherSuiteFilter cipherFilter, ApplicationProtocolConfig apn, long sessionCacheSize, long sessionTimeout) throws SSLException {
        this((X509Certificate[])OpenSslClientContext.toX509CertificatesInternal((File)trustCertCollectionFile), (TrustManagerFactory)trustManagerFactory, (X509Certificate[])OpenSslClientContext.toX509CertificatesInternal((File)keyCertChainFile), (PrivateKey)OpenSslClientContext.toPrivateKeyInternal((File)keyFile, (String)keyPassword), (String)keyPassword, (KeyManagerFactory)keyManagerFactory, ciphers, (CipherSuiteFilter)cipherFilter, (ApplicationProtocolConfig)apn, null, (long)sessionCacheSize, (long)sessionTimeout, (boolean)false, (String)KeyStore.getDefaultType());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    OpenSslClientContext(X509Certificate[] trustCertCollection, TrustManagerFactory trustManagerFactory, X509Certificate[] keyCertChain, PrivateKey key, String keyPassword, KeyManagerFactory keyManagerFactory, Iterable<String> ciphers, CipherSuiteFilter cipherFilter, ApplicationProtocolConfig apn, String[] protocols, long sessionCacheSize, long sessionTimeout, boolean enableOcsp, String keyStore) throws SSLException {
        super(ciphers, (CipherSuiteFilter)cipherFilter, (ApplicationProtocolConfig)apn, (long)sessionCacheSize, (long)sessionTimeout, (int)0, (Certificate[])keyCertChain, (ClientAuth)ClientAuth.NONE, (String[])protocols, (boolean)false, (boolean)enableOcsp);
        boolean success = false;
        try {
            OpenSslKeyMaterialProvider.validateKeyMaterialSupported((X509Certificate[])keyCertChain, (PrivateKey)key, (String)keyPassword);
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
}

