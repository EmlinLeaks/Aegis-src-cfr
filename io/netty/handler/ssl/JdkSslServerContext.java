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
public final class JdkSslServerContext
extends JdkSslContext {
    @Deprecated
    public JdkSslServerContext(File certChainFile, File keyFile) throws SSLException {
        this(null, (File)certChainFile, (File)keyFile, null, null, (CipherSuiteFilter)IdentityCipherSuiteFilter.INSTANCE, (JdkApplicationProtocolNegotiator)JdkDefaultApplicationProtocolNegotiator.INSTANCE, (long)0L, (long)0L, null);
    }

    @Deprecated
    public JdkSslServerContext(File certChainFile, File keyFile, String keyPassword) throws SSLException {
        this((File)certChainFile, (File)keyFile, (String)keyPassword, null, (CipherSuiteFilter)IdentityCipherSuiteFilter.INSTANCE, (JdkApplicationProtocolNegotiator)JdkDefaultApplicationProtocolNegotiator.INSTANCE, (long)0L, (long)0L);
    }

    @Deprecated
    public JdkSslServerContext(File certChainFile, File keyFile, String keyPassword, Iterable<String> ciphers, Iterable<String> nextProtocols, long sessionCacheSize, long sessionTimeout) throws SSLException {
        this(null, (File)certChainFile, (File)keyFile, (String)keyPassword, ciphers, (CipherSuiteFilter)IdentityCipherSuiteFilter.INSTANCE, (JdkApplicationProtocolNegotiator)JdkSslServerContext.toNegotiator((ApplicationProtocolConfig)JdkSslServerContext.toApplicationProtocolConfig(nextProtocols), (boolean)true), (long)sessionCacheSize, (long)sessionTimeout, (String)KeyStore.getDefaultType());
    }

    @Deprecated
    public JdkSslServerContext(File certChainFile, File keyFile, String keyPassword, Iterable<String> ciphers, CipherSuiteFilter cipherFilter, ApplicationProtocolConfig apn, long sessionCacheSize, long sessionTimeout) throws SSLException {
        this(null, (File)certChainFile, (File)keyFile, (String)keyPassword, ciphers, (CipherSuiteFilter)cipherFilter, (JdkApplicationProtocolNegotiator)JdkSslServerContext.toNegotiator((ApplicationProtocolConfig)apn, (boolean)true), (long)sessionCacheSize, (long)sessionTimeout, (String)KeyStore.getDefaultType());
    }

    @Deprecated
    public JdkSslServerContext(File certChainFile, File keyFile, String keyPassword, Iterable<String> ciphers, CipherSuiteFilter cipherFilter, JdkApplicationProtocolNegotiator apn, long sessionCacheSize, long sessionTimeout) throws SSLException {
        this(null, (File)certChainFile, (File)keyFile, (String)keyPassword, ciphers, (CipherSuiteFilter)cipherFilter, (JdkApplicationProtocolNegotiator)apn, (long)sessionCacheSize, (long)sessionTimeout, (String)KeyStore.getDefaultType());
    }

    JdkSslServerContext(Provider provider, File certChainFile, File keyFile, String keyPassword, Iterable<String> ciphers, CipherSuiteFilter cipherFilter, JdkApplicationProtocolNegotiator apn, long sessionCacheSize, long sessionTimeout, String keyStore) throws SSLException {
        super((SSLContext)JdkSslServerContext.newSSLContext((Provider)provider, null, null, (X509Certificate[])JdkSslServerContext.toX509CertificatesInternal((File)certChainFile), (PrivateKey)JdkSslServerContext.toPrivateKeyInternal((File)keyFile, (String)keyPassword), (String)keyPassword, null, (long)sessionCacheSize, (long)sessionTimeout, (String)keyStore), (boolean)false, ciphers, (CipherSuiteFilter)cipherFilter, (JdkApplicationProtocolNegotiator)apn, (ClientAuth)ClientAuth.NONE, null, (boolean)false);
    }

    @Deprecated
    public JdkSslServerContext(File trustCertCollectionFile, TrustManagerFactory trustManagerFactory, File keyCertChainFile, File keyFile, String keyPassword, KeyManagerFactory keyManagerFactory, Iterable<String> ciphers, CipherSuiteFilter cipherFilter, ApplicationProtocolConfig apn, long sessionCacheSize, long sessionTimeout) throws SSLException {
        super((SSLContext)JdkSslServerContext.newSSLContext(null, (X509Certificate[])JdkSslServerContext.toX509CertificatesInternal((File)trustCertCollectionFile), (TrustManagerFactory)trustManagerFactory, (X509Certificate[])JdkSslServerContext.toX509CertificatesInternal((File)keyCertChainFile), (PrivateKey)JdkSslServerContext.toPrivateKeyInternal((File)keyFile, (String)keyPassword), (String)keyPassword, (KeyManagerFactory)keyManagerFactory, (long)sessionCacheSize, (long)sessionTimeout, null), (boolean)false, ciphers, (CipherSuiteFilter)cipherFilter, (ApplicationProtocolConfig)apn, (ClientAuth)ClientAuth.NONE, null, (boolean)false);
    }

    @Deprecated
    public JdkSslServerContext(File trustCertCollectionFile, TrustManagerFactory trustManagerFactory, File keyCertChainFile, File keyFile, String keyPassword, KeyManagerFactory keyManagerFactory, Iterable<String> ciphers, CipherSuiteFilter cipherFilter, JdkApplicationProtocolNegotiator apn, long sessionCacheSize, long sessionTimeout) throws SSLException {
        super((SSLContext)JdkSslServerContext.newSSLContext(null, (X509Certificate[])JdkSslServerContext.toX509CertificatesInternal((File)trustCertCollectionFile), (TrustManagerFactory)trustManagerFactory, (X509Certificate[])JdkSslServerContext.toX509CertificatesInternal((File)keyCertChainFile), (PrivateKey)JdkSslServerContext.toPrivateKeyInternal((File)keyFile, (String)keyPassword), (String)keyPassword, (KeyManagerFactory)keyManagerFactory, (long)sessionCacheSize, (long)sessionTimeout, (String)KeyStore.getDefaultType()), (boolean)false, ciphers, (CipherSuiteFilter)cipherFilter, (JdkApplicationProtocolNegotiator)apn, (ClientAuth)ClientAuth.NONE, null, (boolean)false);
    }

    JdkSslServerContext(Provider provider, X509Certificate[] trustCertCollection, TrustManagerFactory trustManagerFactory, X509Certificate[] keyCertChain, PrivateKey key, String keyPassword, KeyManagerFactory keyManagerFactory, Iterable<String> ciphers, CipherSuiteFilter cipherFilter, ApplicationProtocolConfig apn, long sessionCacheSize, long sessionTimeout, ClientAuth clientAuth, String[] protocols, boolean startTls, String keyStore) throws SSLException {
        super((SSLContext)JdkSslServerContext.newSSLContext((Provider)provider, (X509Certificate[])trustCertCollection, (TrustManagerFactory)trustManagerFactory, (X509Certificate[])keyCertChain, (PrivateKey)key, (String)keyPassword, (KeyManagerFactory)keyManagerFactory, (long)sessionCacheSize, (long)sessionTimeout, (String)keyStore), (boolean)false, ciphers, (CipherSuiteFilter)cipherFilter, (JdkApplicationProtocolNegotiator)JdkSslServerContext.toNegotiator((ApplicationProtocolConfig)apn, (boolean)true), (ClientAuth)clientAuth, (String[])protocols, (boolean)startTls);
    }

    private static SSLContext newSSLContext(Provider sslContextProvider, X509Certificate[] trustCertCollection, TrustManagerFactory trustManagerFactory, X509Certificate[] keyCertChain, PrivateKey key, String keyPassword, KeyManagerFactory keyManagerFactory, long sessionCacheSize, long sessionTimeout, String keyStore) throws SSLException {
        if (key == null && keyManagerFactory == null) {
            throw new NullPointerException((String)"key, keyManagerFactory");
        }
        try {
            if (trustCertCollection != null) {
                trustManagerFactory = JdkSslServerContext.buildTrustManagerFactory((X509Certificate[])trustCertCollection, (TrustManagerFactory)trustManagerFactory, (String)keyStore);
            }
            if (key != null) {
                keyManagerFactory = JdkSslServerContext.buildKeyManagerFactory((X509Certificate[])keyCertChain, (PrivateKey)key, (String)keyPassword, (KeyManagerFactory)keyManagerFactory, null);
            }
            SSLContext ctx = sslContextProvider == null ? SSLContext.getInstance((String)"TLS") : SSLContext.getInstance((String)"TLS", (Provider)sslContextProvider);
            ctx.init((KeyManager[])keyManagerFactory.getKeyManagers(), (TrustManager[])(trustManagerFactory == null ? null : trustManagerFactory.getTrustManagers()), null);
            SSLSessionContext sessCtx = ctx.getServerSessionContext();
            if (sessionCacheSize > 0L) {
                sessCtx.setSessionCacheSize((int)((int)Math.min((long)sessionCacheSize, (long)Integer.MAX_VALUE)));
            }
            if (sessionTimeout <= 0L) return ctx;
            sessCtx.setSessionTimeout((int)((int)Math.min((long)sessionTimeout, (long)Integer.MAX_VALUE)));
            return ctx;
        }
        catch (Exception e) {
            if (!(e instanceof SSLException)) throw new SSLException((String)"failed to initialize the server-side SSL context", (Throwable)e);
            throw (SSLException)e;
        }
    }
}

