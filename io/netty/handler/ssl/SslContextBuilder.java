/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.ssl;

import io.netty.handler.ssl.ApplicationProtocolConfig;
import io.netty.handler.ssl.CipherSuiteFilter;
import io.netty.handler.ssl.ClientAuth;
import io.netty.handler.ssl.IdentityCipherSuiteFilter;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslProvider;
import io.netty.util.internal.ObjectUtil;
import java.io.File;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.cert.X509Certificate;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLException;
import javax.net.ssl.TrustManagerFactory;

public final class SslContextBuilder {
    private final boolean forServer;
    private SslProvider provider;
    private Provider sslContextProvider;
    private X509Certificate[] trustCertCollection;
    private TrustManagerFactory trustManagerFactory;
    private X509Certificate[] keyCertChain;
    private PrivateKey key;
    private String keyPassword;
    private KeyManagerFactory keyManagerFactory;
    private Iterable<String> ciphers;
    private CipherSuiteFilter cipherFilter = IdentityCipherSuiteFilter.INSTANCE;
    private ApplicationProtocolConfig apn;
    private long sessionCacheSize;
    private long sessionTimeout;
    private ClientAuth clientAuth = ClientAuth.NONE;
    private String[] protocols;
    private boolean startTls;
    private boolean enableOcsp;
    private String keyStoreType = KeyStore.getDefaultType();

    public static SslContextBuilder forClient() {
        return new SslContextBuilder((boolean)false);
    }

    public static SslContextBuilder forServer(File keyCertChainFile, File keyFile) {
        return new SslContextBuilder((boolean)true).keyManager((File)keyCertChainFile, (File)keyFile);
    }

    public static SslContextBuilder forServer(InputStream keyCertChainInputStream, InputStream keyInputStream) {
        return new SslContextBuilder((boolean)true).keyManager((InputStream)keyCertChainInputStream, (InputStream)keyInputStream);
    }

    public static SslContextBuilder forServer(PrivateKey key, X509Certificate ... keyCertChain) {
        return new SslContextBuilder((boolean)true).keyManager((PrivateKey)key, (X509Certificate[])keyCertChain);
    }

    public static SslContextBuilder forServer(File keyCertChainFile, File keyFile, String keyPassword) {
        return new SslContextBuilder((boolean)true).keyManager((File)keyCertChainFile, (File)keyFile, (String)keyPassword);
    }

    public static SslContextBuilder forServer(InputStream keyCertChainInputStream, InputStream keyInputStream, String keyPassword) {
        return new SslContextBuilder((boolean)true).keyManager((InputStream)keyCertChainInputStream, (InputStream)keyInputStream, (String)keyPassword);
    }

    public static SslContextBuilder forServer(PrivateKey key, String keyPassword, X509Certificate ... keyCertChain) {
        return new SslContextBuilder((boolean)true).keyManager((PrivateKey)key, (String)keyPassword, (X509Certificate[])keyCertChain);
    }

    public static SslContextBuilder forServer(KeyManagerFactory keyManagerFactory) {
        return new SslContextBuilder((boolean)true).keyManager((KeyManagerFactory)keyManagerFactory);
    }

    private SslContextBuilder(boolean forServer) {
        this.forServer = forServer;
    }

    public SslContextBuilder sslProvider(SslProvider provider) {
        this.provider = provider;
        return this;
    }

    public SslContextBuilder keyStoreType(String keyStoreType) {
        this.keyStoreType = keyStoreType;
        return this;
    }

    public SslContextBuilder sslContextProvider(Provider sslContextProvider) {
        this.sslContextProvider = sslContextProvider;
        return this;
    }

    public SslContextBuilder trustManager(File trustCertCollectionFile) {
        try {
            return this.trustManager((X509Certificate[])SslContext.toX509Certificates((File)trustCertCollectionFile));
        }
        catch (Exception e) {
            throw new IllegalArgumentException((String)("File does not contain valid certificates: " + trustCertCollectionFile), (Throwable)e);
        }
    }

    public SslContextBuilder trustManager(InputStream trustCertCollectionInputStream) {
        try {
            return this.trustManager((X509Certificate[])SslContext.toX509Certificates((InputStream)trustCertCollectionInputStream));
        }
        catch (Exception e) {
            throw new IllegalArgumentException((String)"Input stream does not contain valid certificates.", (Throwable)e);
        }
    }

    public SslContextBuilder trustManager(X509Certificate ... trustCertCollection) {
        this.trustCertCollection = trustCertCollection != null ? (X509Certificate[])trustCertCollection.clone() : null;
        this.trustManagerFactory = null;
        return this;
    }

    public SslContextBuilder trustManager(TrustManagerFactory trustManagerFactory) {
        this.trustCertCollection = null;
        this.trustManagerFactory = trustManagerFactory;
        return this;
    }

    public SslContextBuilder keyManager(File keyCertChainFile, File keyFile) {
        return this.keyManager((File)keyCertChainFile, (File)keyFile, null);
    }

    public SslContextBuilder keyManager(InputStream keyCertChainInputStream, InputStream keyInputStream) {
        return this.keyManager((InputStream)keyCertChainInputStream, (InputStream)keyInputStream, null);
    }

    public SslContextBuilder keyManager(PrivateKey key, X509Certificate ... keyCertChain) {
        return this.keyManager((PrivateKey)key, null, (X509Certificate[])keyCertChain);
    }

    public SslContextBuilder keyManager(File keyCertChainFile, File keyFile, String keyPassword) {
        X509Certificate[] keyCertChain;
        try {
            keyCertChain = SslContext.toX509Certificates((File)keyCertChainFile);
        }
        catch (Exception e) {
            throw new IllegalArgumentException((String)("File does not contain valid certificates: " + keyCertChainFile), (Throwable)e);
        }
        try {
            PrivateKey key = SslContext.toPrivateKey((File)keyFile, (String)keyPassword);
            return this.keyManager((PrivateKey)key, (String)keyPassword, (X509Certificate[])keyCertChain);
        }
        catch (Exception e) {
            throw new IllegalArgumentException((String)("File does not contain valid private key: " + keyFile), (Throwable)e);
        }
    }

    public SslContextBuilder keyManager(InputStream keyCertChainInputStream, InputStream keyInputStream, String keyPassword) {
        X509Certificate[] keyCertChain;
        try {
            keyCertChain = SslContext.toX509Certificates((InputStream)keyCertChainInputStream);
        }
        catch (Exception e) {
            throw new IllegalArgumentException((String)"Input stream not contain valid certificates.", (Throwable)e);
        }
        try {
            PrivateKey key = SslContext.toPrivateKey((InputStream)keyInputStream, (String)keyPassword);
            return this.keyManager((PrivateKey)key, (String)keyPassword, (X509Certificate[])keyCertChain);
        }
        catch (Exception e) {
            throw new IllegalArgumentException((String)"Input stream does not contain valid private key.", (Throwable)e);
        }
    }

    public SslContextBuilder keyManager(PrivateKey key, String keyPassword, X509Certificate ... keyCertChain) {
        if (this.forServer) {
            ObjectUtil.checkNotNull(keyCertChain, (String)"keyCertChain required for servers");
            if (keyCertChain.length == 0) {
                throw new IllegalArgumentException((String)"keyCertChain must be non-empty");
            }
            ObjectUtil.checkNotNull(key, (String)"key required for servers");
        }
        if (keyCertChain == null || keyCertChain.length == 0) {
            this.keyCertChain = null;
        } else {
            for (X509Certificate cert : keyCertChain) {
                if (cert != null) continue;
                throw new IllegalArgumentException((String)"keyCertChain contains null entry");
            }
            this.keyCertChain = (X509Certificate[])keyCertChain.clone();
        }
        this.key = key;
        this.keyPassword = keyPassword;
        this.keyManagerFactory = null;
        return this;
    }

    public SslContextBuilder keyManager(KeyManagerFactory keyManagerFactory) {
        if (this.forServer) {
            ObjectUtil.checkNotNull(keyManagerFactory, (String)"keyManagerFactory required for servers");
        }
        this.keyCertChain = null;
        this.key = null;
        this.keyPassword = null;
        this.keyManagerFactory = keyManagerFactory;
        return this;
    }

    public SslContextBuilder ciphers(Iterable<String> ciphers) {
        return this.ciphers(ciphers, (CipherSuiteFilter)IdentityCipherSuiteFilter.INSTANCE);
    }

    public SslContextBuilder ciphers(Iterable<String> ciphers, CipherSuiteFilter cipherFilter) {
        ObjectUtil.checkNotNull(cipherFilter, (String)"cipherFilter");
        this.ciphers = ciphers;
        this.cipherFilter = cipherFilter;
        return this;
    }

    public SslContextBuilder applicationProtocolConfig(ApplicationProtocolConfig apn) {
        this.apn = apn;
        return this;
    }

    public SslContextBuilder sessionCacheSize(long sessionCacheSize) {
        this.sessionCacheSize = sessionCacheSize;
        return this;
    }

    public SslContextBuilder sessionTimeout(long sessionTimeout) {
        this.sessionTimeout = sessionTimeout;
        return this;
    }

    public SslContextBuilder clientAuth(ClientAuth clientAuth) {
        this.clientAuth = ObjectUtil.checkNotNull(clientAuth, (String)"clientAuth");
        return this;
    }

    public SslContextBuilder protocols(String ... protocols) {
        this.protocols = protocols == null ? null : (String[])protocols.clone();
        return this;
    }

    public SslContextBuilder startTls(boolean startTls) {
        this.startTls = startTls;
        return this;
    }

    public SslContextBuilder enableOcsp(boolean enableOcsp) {
        this.enableOcsp = enableOcsp;
        return this;
    }

    public SslContext build() throws SSLException {
        if (!this.forServer) return SslContext.newClientContextInternal((SslProvider)this.provider, (Provider)this.sslContextProvider, (X509Certificate[])this.trustCertCollection, (TrustManagerFactory)this.trustManagerFactory, (X509Certificate[])this.keyCertChain, (PrivateKey)this.key, (String)this.keyPassword, (KeyManagerFactory)this.keyManagerFactory, this.ciphers, (CipherSuiteFilter)this.cipherFilter, (ApplicationProtocolConfig)this.apn, (String[])this.protocols, (long)this.sessionCacheSize, (long)this.sessionTimeout, (boolean)this.enableOcsp, (String)this.keyStoreType);
        return SslContext.newServerContextInternal((SslProvider)this.provider, (Provider)this.sslContextProvider, (X509Certificate[])this.trustCertCollection, (TrustManagerFactory)this.trustManagerFactory, (X509Certificate[])this.keyCertChain, (PrivateKey)this.key, (String)this.keyPassword, (KeyManagerFactory)this.keyManagerFactory, this.ciphers, (CipherSuiteFilter)this.cipherFilter, (ApplicationProtocolConfig)this.apn, (long)this.sessionCacheSize, (long)this.sessionTimeout, (ClientAuth)this.clientAuth, (String[])this.protocols, (boolean)this.startTls, (boolean)this.enableOcsp, (String)this.keyStoreType);
    }
}

