/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.ssl;

import io.netty.handler.ssl.ApplicationProtocolConfig;
import io.netty.handler.ssl.CipherSuiteFilter;
import io.netty.handler.ssl.ClientAuth;
import io.netty.handler.ssl.IdentityCipherSuiteFilter;
import io.netty.handler.ssl.JdkApplicationProtocolNegotiator;
import io.netty.handler.ssl.JdkDefaultApplicationProtocolNegotiator;
import io.netty.handler.ssl.JdkSslContext;
import java.io.File;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSessionContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

@Deprecated
public final class JdkSslClientContext
extends JdkSslContext {
    @Deprecated
    public JdkSslClientContext() throws SSLException {
        this(null, null);
    }

    @Deprecated
    public JdkSslClientContext(File certChainFile) throws SSLException {
        this((File)certChainFile, null);
    }

    @Deprecated
    public JdkSslClientContext(TrustManagerFactory trustManagerFactory) throws SSLException {
        this(null, (TrustManagerFactory)trustManagerFactory);
    }

    @Deprecated
    public JdkSslClientContext(File certChainFile, TrustManagerFactory trustManagerFactory) throws SSLException {
        this((File)certChainFile, (TrustManagerFactory)trustManagerFactory, null, (CipherSuiteFilter)IdentityCipherSuiteFilter.INSTANCE, (JdkApplicationProtocolNegotiator)JdkDefaultApplicationProtocolNegotiator.INSTANCE, (long)0L, (long)0L);
    }

    @Deprecated
    public JdkSslClientContext(File certChainFile, TrustManagerFactory trustManagerFactory, Iterable<String> ciphers, Iterable<String> nextProtocols, long sessionCacheSize, long sessionTimeout) throws SSLException {
        this((File)certChainFile, (TrustManagerFactory)trustManagerFactory, ciphers, (CipherSuiteFilter)IdentityCipherSuiteFilter.INSTANCE, (JdkApplicationProtocolNegotiator)JdkSslClientContext.toNegotiator((ApplicationProtocolConfig)JdkSslClientContext.toApplicationProtocolConfig(nextProtocols), (boolean)false), (long)sessionCacheSize, (long)sessionTimeout);
    }

    @Deprecated
    public JdkSslClientContext(File certChainFile, TrustManagerFactory trustManagerFactory, Iterable<String> ciphers, CipherSuiteFilter cipherFilter, ApplicationProtocolConfig apn, long sessionCacheSize, long sessionTimeout) throws SSLException {
        this((File)certChainFile, (TrustManagerFactory)trustManagerFactory, ciphers, (CipherSuiteFilter)cipherFilter, (JdkApplicationProtocolNegotiator)JdkSslClientContext.toNegotiator((ApplicationProtocolConfig)apn, (boolean)false), (long)sessionCacheSize, (long)sessionTimeout);
    }

    @Deprecated
    public JdkSslClientContext(File certChainFile, TrustManagerFactory trustManagerFactory, Iterable<String> ciphers, CipherSuiteFilter cipherFilter, JdkApplicationProtocolNegotiator apn, long sessionCacheSize, long sessionTimeout) throws SSLException {
        this(null, (File)certChainFile, (TrustManagerFactory)trustManagerFactory, ciphers, (CipherSuiteFilter)cipherFilter, (JdkApplicationProtocolNegotiator)apn, (long)sessionCacheSize, (long)sessionTimeout);
    }

    JdkSslClientContext(Provider provider, File trustCertCollectionFile, TrustManagerFactory trustManagerFactory, Iterable<String> ciphers, CipherSuiteFilter cipherFilter, JdkApplicationProtocolNegotiator apn, long sessionCacheSize, long sessionTimeout) throws SSLException {
        super((SSLContext)JdkSslClientContext.newSSLContext((Provider)provider, (X509Certificate[])JdkSslClientContext.toX509CertificatesInternal((File)trustCertCollectionFile), (TrustManagerFactory)trustManagerFactory, null, null, null, null, (long)sessionCacheSize, (long)sessionTimeout, (String)KeyStore.getDefaultType()), (boolean)true, ciphers, (CipherSuiteFilter)cipherFilter, (JdkApplicationProtocolNegotiator)apn, (ClientAuth)ClientAuth.NONE, null, (boolean)false);
    }

    @Deprecated
    public JdkSslClientContext(File trustCertCollectionFile, TrustManagerFactory trustManagerFactory, File keyCertChainFile, File keyFile, String keyPassword, KeyManagerFactory keyManagerFactory, Iterable<String> ciphers, CipherSuiteFilter cipherFilter, ApplicationProtocolConfig apn, long sessionCacheSize, long sessionTimeout) throws SSLException {
        this((File)trustCertCollectionFile, (TrustManagerFactory)trustManagerFactory, (File)keyCertChainFile, (File)keyFile, (String)keyPassword, (KeyManagerFactory)keyManagerFactory, ciphers, (CipherSuiteFilter)cipherFilter, (JdkApplicationProtocolNegotiator)JdkSslClientContext.toNegotiator((ApplicationProtocolConfig)apn, (boolean)false), (long)sessionCacheSize, (long)sessionTimeout);
    }

    @Deprecated
    public JdkSslClientContext(File trustCertCollectionFile, TrustManagerFactory trustManagerFactory, File keyCertChainFile, File keyFile, String keyPassword, KeyManagerFactory keyManagerFactory, Iterable<String> ciphers, CipherSuiteFilter cipherFilter, JdkApplicationProtocolNegotiator apn, long sessionCacheSize, long sessionTimeout) throws SSLException {
        super((SSLContext)JdkSslClientContext.newSSLContext(null, (X509Certificate[])JdkSslClientContext.toX509CertificatesInternal((File)trustCertCollectionFile), (TrustManagerFactory)trustManagerFactory, (X509Certificate[])JdkSslClientContext.toX509CertificatesInternal((File)keyCertChainFile), (PrivateKey)JdkSslClientContext.toPrivateKeyInternal((File)keyFile, (String)keyPassword), (String)keyPassword, (KeyManagerFactory)keyManagerFactory, (long)sessionCacheSize, (long)sessionTimeout, (String)KeyStore.getDefaultType()), (boolean)true, ciphers, (CipherSuiteFilter)cipherFilter, (JdkApplicationProtocolNegotiator)apn, (ClientAuth)ClientAuth.NONE, null, (boolean)false);
    }

    JdkSslClientContext(Provider sslContextProvider, X509Certificate[] trustCertCollection, TrustManagerFactory trustManagerFactory, X509Certificate[] keyCertChain, PrivateKey key, String keyPassword, KeyManagerFactory keyManagerFactory, Iterable<String> ciphers, CipherSuiteFilter cipherFilter, ApplicationProtocolConfig apn, String[] protocols, long sessionCacheSize, long sessionTimeout, String keyStoreType) throws SSLException {
        super((SSLContext)JdkSslClientContext.newSSLContext((Provider)sslContextProvider, (X509Certificate[])trustCertCollection, (TrustManagerFactory)trustManagerFactory, (X509Certificate[])keyCertChain, (PrivateKey)key, (String)keyPassword, (KeyManagerFactory)keyManagerFactory, (long)sessionCacheSize, (long)sessionTimeout, (String)keyStoreType), (boolean)true, ciphers, (CipherSuiteFilter)cipherFilter, (JdkApplicationProtocolNegotiator)JdkSslClientContext.toNegotiator((ApplicationProtocolConfig)apn, (boolean)false), (ClientAuth)ClientAuth.NONE, (String[])protocols, (boolean)false);
    }

    private static SSLContext newSSLContext(Provider sslContextProvider, X509Certificate[] trustCertCollection, TrustManagerFactory trustManagerFactory, X509Certificate[] keyCertChain, PrivateKey key, String keyPassword, KeyManagerFactory keyManagerFactory, long sessionCacheSize, long sessionTimeout, String keyStore) throws SSLException {
        try {
            if (trustCertCollection != null) {
                trustManagerFactory = JdkSslClientContext.buildTrustManagerFactory((X509Certificate[])trustCertCollection, (TrustManagerFactory)trustManagerFactory, (String)keyStore);
            }
            if (keyCertChain != null) {
                keyManagerFactory = JdkSslClientContext.buildKeyManagerFactory((X509Certificate[])keyCertChain, (PrivateKey)key, (String)keyPassword, (KeyManagerFactory)keyManagerFactory, null);
            }
            SSLContext ctx = sslContextProvider == null ? SSLContext.getInstance((String)"TLS") : SSLContext.getInstance((String)"TLS", (Provider)sslContextProvider);
            ctx.init((KeyManager[])(keyManagerFactory == null ? null : keyManagerFactory.getKeyManagers()), (TrustManager[])(trustManagerFactory == null ? null : trustManagerFactory.getTrustManagers()), null);
            SSLSessionContext sessCtx = ctx.getClientSessionContext();
            if (sessionCacheSize > 0L) {
                sessCtx.setSessionCacheSize((int)((int)Math.min((long)sessionCacheSize, (long)Integer.MAX_VALUE)));
            }
            if (sessionTimeout <= 0L) return ctx;
            sessCtx.setSessionTimeout((int)((int)Math.min((long)sessionTimeout, (long)Integer.MAX_VALUE)));
            return ctx;
        }
        catch (Exception e) {
            if (!(e instanceof SSLException)) throw new SSLException((String)"failed to initialize the client-side SSL context", (Throwable)e);
            throw (SSLException)e;
        }
    }
}

