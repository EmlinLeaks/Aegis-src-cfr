/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.ssl;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufInputStream;
import io.netty.handler.ssl.ApplicationProtocolConfig;
import io.netty.handler.ssl.ApplicationProtocolNegotiator;
import io.netty.handler.ssl.CipherSuiteFilter;
import io.netty.handler.ssl.ClientAuth;
import io.netty.handler.ssl.IdentityCipherSuiteFilter;
import io.netty.handler.ssl.JdkSslClientContext;
import io.netty.handler.ssl.JdkSslServerContext;
import io.netty.handler.ssl.OpenSsl;
import io.netty.handler.ssl.OpenSslClientContext;
import io.netty.handler.ssl.OpenSslServerContext;
import io.netty.handler.ssl.PemReader;
import io.netty.handler.ssl.ReferenceCountedOpenSslClientContext;
import io.netty.handler.ssl.ReferenceCountedOpenSslServerContext;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.ssl.SslProvider;
import io.netty.util.AttributeMap;
import io.netty.util.DefaultAttributeMap;
import io.netty.util.internal.EmptyArrays;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyException;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.List;
import java.util.concurrent.Executor;
import javax.crypto.Cipher;
import javax.crypto.EncryptedPrivateKeyInfo;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSessionContext;
import javax.net.ssl.TrustManagerFactory;

public abstract class SslContext {
    static final String ALIAS = "key";
    static final CertificateFactory X509_CERT_FACTORY;
    private final boolean startTls;
    private final AttributeMap attributes = new DefaultAttributeMap();

    public static SslProvider defaultServerProvider() {
        return SslContext.defaultProvider();
    }

    public static SslProvider defaultClientProvider() {
        return SslContext.defaultProvider();
    }

    private static SslProvider defaultProvider() {
        if (!OpenSsl.isAvailable()) return SslProvider.JDK;
        return SslProvider.OPENSSL;
    }

    @Deprecated
    public static SslContext newServerContext(File certChainFile, File keyFile) throws SSLException {
        return SslContext.newServerContext((File)certChainFile, (File)keyFile, null);
    }

    @Deprecated
    public static SslContext newServerContext(File certChainFile, File keyFile, String keyPassword) throws SSLException {
        return SslContext.newServerContext(null, (File)certChainFile, (File)keyFile, (String)keyPassword);
    }

    @Deprecated
    public static SslContext newServerContext(File certChainFile, File keyFile, String keyPassword, Iterable<String> ciphers, Iterable<String> nextProtocols, long sessionCacheSize, long sessionTimeout) throws SSLException {
        return SslContext.newServerContext(null, (File)certChainFile, (File)keyFile, (String)keyPassword, ciphers, nextProtocols, (long)sessionCacheSize, (long)sessionTimeout);
    }

    @Deprecated
    public static SslContext newServerContext(File certChainFile, File keyFile, String keyPassword, Iterable<String> ciphers, CipherSuiteFilter cipherFilter, ApplicationProtocolConfig apn, long sessionCacheSize, long sessionTimeout) throws SSLException {
        return SslContext.newServerContext(null, (File)certChainFile, (File)keyFile, (String)keyPassword, ciphers, (CipherSuiteFilter)cipherFilter, (ApplicationProtocolConfig)apn, (long)sessionCacheSize, (long)sessionTimeout);
    }

    @Deprecated
    public static SslContext newServerContext(SslProvider provider, File certChainFile, File keyFile) throws SSLException {
        return SslContext.newServerContext((SslProvider)provider, (File)certChainFile, (File)keyFile, null);
    }

    @Deprecated
    public static SslContext newServerContext(SslProvider provider, File certChainFile, File keyFile, String keyPassword) throws SSLException {
        return SslContext.newServerContext((SslProvider)provider, (File)certChainFile, (File)keyFile, (String)keyPassword, null, (CipherSuiteFilter)IdentityCipherSuiteFilter.INSTANCE, null, (long)0L, (long)0L);
    }

    @Deprecated
    public static SslContext newServerContext(SslProvider provider, File certChainFile, File keyFile, String keyPassword, Iterable<String> ciphers, Iterable<String> nextProtocols, long sessionCacheSize, long sessionTimeout) throws SSLException {
        return SslContext.newServerContext((SslProvider)provider, (File)certChainFile, (File)keyFile, (String)keyPassword, ciphers, (CipherSuiteFilter)IdentityCipherSuiteFilter.INSTANCE, (ApplicationProtocolConfig)SslContext.toApplicationProtocolConfig(nextProtocols), (long)sessionCacheSize, (long)sessionTimeout);
    }

    @Deprecated
    public static SslContext newServerContext(SslProvider provider, File certChainFile, File keyFile, String keyPassword, TrustManagerFactory trustManagerFactory, Iterable<String> ciphers, Iterable<String> nextProtocols, long sessionCacheSize, long sessionTimeout) throws SSLException {
        return SslContext.newServerContext((SslProvider)provider, null, (TrustManagerFactory)trustManagerFactory, (File)certChainFile, (File)keyFile, (String)keyPassword, null, ciphers, (CipherSuiteFilter)IdentityCipherSuiteFilter.INSTANCE, (ApplicationProtocolConfig)SslContext.toApplicationProtocolConfig(nextProtocols), (long)sessionCacheSize, (long)sessionTimeout);
    }

    @Deprecated
    public static SslContext newServerContext(SslProvider provider, File certChainFile, File keyFile, String keyPassword, Iterable<String> ciphers, CipherSuiteFilter cipherFilter, ApplicationProtocolConfig apn, long sessionCacheSize, long sessionTimeout) throws SSLException {
        return SslContext.newServerContext((SslProvider)provider, null, null, (File)certChainFile, (File)keyFile, (String)keyPassword, null, ciphers, (CipherSuiteFilter)cipherFilter, (ApplicationProtocolConfig)apn, (long)sessionCacheSize, (long)sessionTimeout, (String)KeyStore.getDefaultType());
    }

    @Deprecated
    public static SslContext newServerContext(SslProvider provider, File trustCertCollectionFile, TrustManagerFactory trustManagerFactory, File keyCertChainFile, File keyFile, String keyPassword, KeyManagerFactory keyManagerFactory, Iterable<String> ciphers, CipherSuiteFilter cipherFilter, ApplicationProtocolConfig apn, long sessionCacheSize, long sessionTimeout) throws SSLException {
        return SslContext.newServerContext((SslProvider)provider, (File)trustCertCollectionFile, (TrustManagerFactory)trustManagerFactory, (File)keyCertChainFile, (File)keyFile, (String)keyPassword, (KeyManagerFactory)keyManagerFactory, ciphers, (CipherSuiteFilter)cipherFilter, (ApplicationProtocolConfig)apn, (long)sessionCacheSize, (long)sessionTimeout, (String)KeyStore.getDefaultType());
    }

    static SslContext newServerContext(SslProvider provider, File trustCertCollectionFile, TrustManagerFactory trustManagerFactory, File keyCertChainFile, File keyFile, String keyPassword, KeyManagerFactory keyManagerFactory, Iterable<String> ciphers, CipherSuiteFilter cipherFilter, ApplicationProtocolConfig apn, long sessionCacheSize, long sessionTimeout, String keyStore) throws SSLException {
        try {
            return SslContext.newServerContextInternal((SslProvider)provider, null, (X509Certificate[])SslContext.toX509Certificates((File)trustCertCollectionFile), (TrustManagerFactory)trustManagerFactory, (X509Certificate[])SslContext.toX509Certificates((File)keyCertChainFile), (PrivateKey)SslContext.toPrivateKey((File)keyFile, (String)keyPassword), (String)keyPassword, (KeyManagerFactory)keyManagerFactory, ciphers, (CipherSuiteFilter)cipherFilter, (ApplicationProtocolConfig)apn, (long)sessionCacheSize, (long)sessionTimeout, (ClientAuth)ClientAuth.NONE, null, (boolean)false, (boolean)false, (String)keyStore);
        }
        catch (Exception e) {
            if (!(e instanceof SSLException)) throw new SSLException((String)"failed to initialize the server-side SSL context", (Throwable)e);
            throw (SSLException)e;
        }
    }

    static SslContext newServerContextInternal(SslProvider provider, Provider sslContextProvider, X509Certificate[] trustCertCollection, TrustManagerFactory trustManagerFactory, X509Certificate[] keyCertChain, PrivateKey key, String keyPassword, KeyManagerFactory keyManagerFactory, Iterable<String> ciphers, CipherSuiteFilter cipherFilter, ApplicationProtocolConfig apn, long sessionCacheSize, long sessionTimeout, ClientAuth clientAuth, String[] protocols, boolean startTls, boolean enableOcsp, String keyStoreType) throws SSLException {
        if (provider == null) {
            provider = SslContext.defaultServerProvider();
        }
        switch (1.$SwitchMap$io$netty$handler$ssl$SslProvider[provider.ordinal()]) {
            case 1: {
                if (!enableOcsp) return new JdkSslServerContext((Provider)sslContextProvider, (X509Certificate[])trustCertCollection, (TrustManagerFactory)trustManagerFactory, (X509Certificate[])keyCertChain, (PrivateKey)key, (String)keyPassword, (KeyManagerFactory)keyManagerFactory, ciphers, (CipherSuiteFilter)cipherFilter, (ApplicationProtocolConfig)apn, (long)sessionCacheSize, (long)sessionTimeout, (ClientAuth)clientAuth, (String[])protocols, (boolean)startTls, (String)keyStoreType);
                throw new IllegalArgumentException((String)("OCSP is not supported with this SslProvider: " + (Object)((Object)provider)));
            }
            case 2: {
                SslContext.verifyNullSslContextProvider((SslProvider)provider, (Provider)sslContextProvider);
                return new OpenSslServerContext((X509Certificate[])trustCertCollection, (TrustManagerFactory)trustManagerFactory, (X509Certificate[])keyCertChain, (PrivateKey)key, (String)keyPassword, (KeyManagerFactory)keyManagerFactory, ciphers, (CipherSuiteFilter)cipherFilter, (ApplicationProtocolConfig)apn, (long)sessionCacheSize, (long)sessionTimeout, (ClientAuth)clientAuth, (String[])protocols, (boolean)startTls, (boolean)enableOcsp, (String)keyStoreType);
            }
            case 3: {
                SslContext.verifyNullSslContextProvider((SslProvider)provider, (Provider)sslContextProvider);
                return new ReferenceCountedOpenSslServerContext((X509Certificate[])trustCertCollection, (TrustManagerFactory)trustManagerFactory, (X509Certificate[])keyCertChain, (PrivateKey)key, (String)keyPassword, (KeyManagerFactory)keyManagerFactory, ciphers, (CipherSuiteFilter)cipherFilter, (ApplicationProtocolConfig)apn, (long)sessionCacheSize, (long)sessionTimeout, (ClientAuth)clientAuth, (String[])protocols, (boolean)startTls, (boolean)enableOcsp, (String)keyStoreType);
            }
        }
        throw new Error((String)provider.toString());
    }

    private static void verifyNullSslContextProvider(SslProvider provider, Provider sslContextProvider) {
        if (sslContextProvider == null) return;
        throw new IllegalArgumentException((String)("Java Security Provider unsupported for SslProvider: " + (Object)((Object)provider)));
    }

    @Deprecated
    public static SslContext newClientContext() throws SSLException {
        return SslContext.newClientContext(null, null, null);
    }

    @Deprecated
    public static SslContext newClientContext(File certChainFile) throws SSLException {
        return SslContext.newClientContext(null, (File)certChainFile);
    }

    @Deprecated
    public static SslContext newClientContext(TrustManagerFactory trustManagerFactory) throws SSLException {
        return SslContext.newClientContext(null, null, (TrustManagerFactory)trustManagerFactory);
    }

    @Deprecated
    public static SslContext newClientContext(File certChainFile, TrustManagerFactory trustManagerFactory) throws SSLException {
        return SslContext.newClientContext(null, (File)certChainFile, (TrustManagerFactory)trustManagerFactory);
    }

    @Deprecated
    public static SslContext newClientContext(File certChainFile, TrustManagerFactory trustManagerFactory, Iterable<String> ciphers, Iterable<String> nextProtocols, long sessionCacheSize, long sessionTimeout) throws SSLException {
        return SslContext.newClientContext(null, (File)certChainFile, (TrustManagerFactory)trustManagerFactory, ciphers, nextProtocols, (long)sessionCacheSize, (long)sessionTimeout);
    }

    @Deprecated
    public static SslContext newClientContext(File certChainFile, TrustManagerFactory trustManagerFactory, Iterable<String> ciphers, CipherSuiteFilter cipherFilter, ApplicationProtocolConfig apn, long sessionCacheSize, long sessionTimeout) throws SSLException {
        return SslContext.newClientContext(null, (File)certChainFile, (TrustManagerFactory)trustManagerFactory, ciphers, (CipherSuiteFilter)cipherFilter, (ApplicationProtocolConfig)apn, (long)sessionCacheSize, (long)sessionTimeout);
    }

    @Deprecated
    public static SslContext newClientContext(SslProvider provider) throws SSLException {
        return SslContext.newClientContext((SslProvider)provider, null, null);
    }

    @Deprecated
    public static SslContext newClientContext(SslProvider provider, File certChainFile) throws SSLException {
        return SslContext.newClientContext((SslProvider)provider, (File)certChainFile, null);
    }

    @Deprecated
    public static SslContext newClientContext(SslProvider provider, TrustManagerFactory trustManagerFactory) throws SSLException {
        return SslContext.newClientContext((SslProvider)provider, null, (TrustManagerFactory)trustManagerFactory);
    }

    @Deprecated
    public static SslContext newClientContext(SslProvider provider, File certChainFile, TrustManagerFactory trustManagerFactory) throws SSLException {
        return SslContext.newClientContext((SslProvider)provider, (File)certChainFile, (TrustManagerFactory)trustManagerFactory, null, (CipherSuiteFilter)IdentityCipherSuiteFilter.INSTANCE, null, (long)0L, (long)0L);
    }

    @Deprecated
    public static SslContext newClientContext(SslProvider provider, File certChainFile, TrustManagerFactory trustManagerFactory, Iterable<String> ciphers, Iterable<String> nextProtocols, long sessionCacheSize, long sessionTimeout) throws SSLException {
        return SslContext.newClientContext((SslProvider)provider, (File)certChainFile, (TrustManagerFactory)trustManagerFactory, null, null, null, null, ciphers, (CipherSuiteFilter)IdentityCipherSuiteFilter.INSTANCE, (ApplicationProtocolConfig)SslContext.toApplicationProtocolConfig(nextProtocols), (long)sessionCacheSize, (long)sessionTimeout);
    }

    @Deprecated
    public static SslContext newClientContext(SslProvider provider, File certChainFile, TrustManagerFactory trustManagerFactory, Iterable<String> ciphers, CipherSuiteFilter cipherFilter, ApplicationProtocolConfig apn, long sessionCacheSize, long sessionTimeout) throws SSLException {
        return SslContext.newClientContext((SslProvider)provider, (File)certChainFile, (TrustManagerFactory)trustManagerFactory, null, null, null, null, ciphers, (CipherSuiteFilter)cipherFilter, (ApplicationProtocolConfig)apn, (long)sessionCacheSize, (long)sessionTimeout);
    }

    @Deprecated
    public static SslContext newClientContext(SslProvider provider, File trustCertCollectionFile, TrustManagerFactory trustManagerFactory, File keyCertChainFile, File keyFile, String keyPassword, KeyManagerFactory keyManagerFactory, Iterable<String> ciphers, CipherSuiteFilter cipherFilter, ApplicationProtocolConfig apn, long sessionCacheSize, long sessionTimeout) throws SSLException {
        try {
            return SslContext.newClientContextInternal((SslProvider)provider, null, (X509Certificate[])SslContext.toX509Certificates((File)trustCertCollectionFile), (TrustManagerFactory)trustManagerFactory, (X509Certificate[])SslContext.toX509Certificates((File)keyCertChainFile), (PrivateKey)SslContext.toPrivateKey((File)keyFile, (String)keyPassword), (String)keyPassword, (KeyManagerFactory)keyManagerFactory, ciphers, (CipherSuiteFilter)cipherFilter, (ApplicationProtocolConfig)apn, null, (long)sessionCacheSize, (long)sessionTimeout, (boolean)false, (String)KeyStore.getDefaultType());
        }
        catch (Exception e) {
            if (!(e instanceof SSLException)) throw new SSLException((String)"failed to initialize the client-side SSL context", (Throwable)e);
            throw (SSLException)e;
        }
    }

    static SslContext newClientContextInternal(SslProvider provider, Provider sslContextProvider, X509Certificate[] trustCert, TrustManagerFactory trustManagerFactory, X509Certificate[] keyCertChain, PrivateKey key, String keyPassword, KeyManagerFactory keyManagerFactory, Iterable<String> ciphers, CipherSuiteFilter cipherFilter, ApplicationProtocolConfig apn, String[] protocols, long sessionCacheSize, long sessionTimeout, boolean enableOcsp, String keyStoreType) throws SSLException {
        if (provider == null) {
            provider = SslContext.defaultClientProvider();
        }
        switch (1.$SwitchMap$io$netty$handler$ssl$SslProvider[provider.ordinal()]) {
            case 1: {
                if (!enableOcsp) return new JdkSslClientContext((Provider)sslContextProvider, (X509Certificate[])trustCert, (TrustManagerFactory)trustManagerFactory, (X509Certificate[])keyCertChain, (PrivateKey)key, (String)keyPassword, (KeyManagerFactory)keyManagerFactory, ciphers, (CipherSuiteFilter)cipherFilter, (ApplicationProtocolConfig)apn, (String[])protocols, (long)sessionCacheSize, (long)sessionTimeout, (String)keyStoreType);
                throw new IllegalArgumentException((String)("OCSP is not supported with this SslProvider: " + (Object)((Object)provider)));
            }
            case 2: {
                SslContext.verifyNullSslContextProvider((SslProvider)provider, (Provider)sslContextProvider);
                return new OpenSslClientContext((X509Certificate[])trustCert, (TrustManagerFactory)trustManagerFactory, (X509Certificate[])keyCertChain, (PrivateKey)key, (String)keyPassword, (KeyManagerFactory)keyManagerFactory, ciphers, (CipherSuiteFilter)cipherFilter, (ApplicationProtocolConfig)apn, (String[])protocols, (long)sessionCacheSize, (long)sessionTimeout, (boolean)enableOcsp, (String)keyStoreType);
            }
            case 3: {
                SslContext.verifyNullSslContextProvider((SslProvider)provider, (Provider)sslContextProvider);
                return new ReferenceCountedOpenSslClientContext((X509Certificate[])trustCert, (TrustManagerFactory)trustManagerFactory, (X509Certificate[])keyCertChain, (PrivateKey)key, (String)keyPassword, (KeyManagerFactory)keyManagerFactory, ciphers, (CipherSuiteFilter)cipherFilter, (ApplicationProtocolConfig)apn, (String[])protocols, (long)sessionCacheSize, (long)sessionTimeout, (boolean)enableOcsp, (String)keyStoreType);
            }
        }
        throw new Error((String)provider.toString());
    }

    static ApplicationProtocolConfig toApplicationProtocolConfig(Iterable<String> nextProtocols) {
        if (nextProtocols != null) return new ApplicationProtocolConfig((ApplicationProtocolConfig.Protocol)ApplicationProtocolConfig.Protocol.NPN_AND_ALPN, (ApplicationProtocolConfig.SelectorFailureBehavior)ApplicationProtocolConfig.SelectorFailureBehavior.CHOOSE_MY_LAST_PROTOCOL, (ApplicationProtocolConfig.SelectedListenerFailureBehavior)ApplicationProtocolConfig.SelectedListenerFailureBehavior.ACCEPT, nextProtocols);
        return ApplicationProtocolConfig.DISABLED;
    }

    protected SslContext() {
        this((boolean)false);
    }

    protected SslContext(boolean startTls) {
        this.startTls = startTls;
    }

    public final AttributeMap attributes() {
        return this.attributes;
    }

    public final boolean isServer() {
        if (this.isClient()) return false;
        return true;
    }

    public abstract boolean isClient();

    public abstract List<String> cipherSuites();

    public abstract long sessionCacheSize();

    public abstract long sessionTimeout();

    @Deprecated
    public final List<String> nextProtocols() {
        return this.applicationProtocolNegotiator().protocols();
    }

    public abstract ApplicationProtocolNegotiator applicationProtocolNegotiator();

    public abstract SSLEngine newEngine(ByteBufAllocator var1);

    public abstract SSLEngine newEngine(ByteBufAllocator var1, String var2, int var3);

    public abstract SSLSessionContext sessionContext();

    public final SslHandler newHandler(ByteBufAllocator alloc) {
        return this.newHandler((ByteBufAllocator)alloc, (boolean)this.startTls);
    }

    protected SslHandler newHandler(ByteBufAllocator alloc, boolean startTls) {
        return new SslHandler((SSLEngine)this.newEngine((ByteBufAllocator)alloc), (boolean)startTls);
    }

    public SslHandler newHandler(ByteBufAllocator alloc, Executor delegatedTaskExecutor) {
        return this.newHandler((ByteBufAllocator)alloc, (boolean)this.startTls, (Executor)delegatedTaskExecutor);
    }

    protected SslHandler newHandler(ByteBufAllocator alloc, boolean startTls, Executor executor) {
        return new SslHandler((SSLEngine)this.newEngine((ByteBufAllocator)alloc), (boolean)startTls, (Executor)executor);
    }

    public final SslHandler newHandler(ByteBufAllocator alloc, String peerHost, int peerPort) {
        return this.newHandler((ByteBufAllocator)alloc, (String)peerHost, (int)peerPort, (boolean)this.startTls);
    }

    protected SslHandler newHandler(ByteBufAllocator alloc, String peerHost, int peerPort, boolean startTls) {
        return new SslHandler((SSLEngine)this.newEngine((ByteBufAllocator)alloc, (String)peerHost, (int)peerPort), (boolean)startTls);
    }

    public SslHandler newHandler(ByteBufAllocator alloc, String peerHost, int peerPort, Executor delegatedTaskExecutor) {
        return this.newHandler((ByteBufAllocator)alloc, (String)peerHost, (int)peerPort, (boolean)this.startTls, (Executor)delegatedTaskExecutor);
    }

    protected SslHandler newHandler(ByteBufAllocator alloc, String peerHost, int peerPort, boolean startTls, Executor delegatedTaskExecutor) {
        return new SslHandler((SSLEngine)this.newEngine((ByteBufAllocator)alloc, (String)peerHost, (int)peerPort), (boolean)startTls, (Executor)delegatedTaskExecutor);
    }

    protected static PKCS8EncodedKeySpec generateKeySpec(char[] password, byte[] key) throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeySpecException, InvalidKeyException, InvalidAlgorithmParameterException {
        if (password == null) {
            return new PKCS8EncodedKeySpec((byte[])key);
        }
        EncryptedPrivateKeyInfo encryptedPrivateKeyInfo = new EncryptedPrivateKeyInfo((byte[])key);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance((String)encryptedPrivateKeyInfo.getAlgName());
        PBEKeySpec pbeKeySpec = new PBEKeySpec((char[])password);
        SecretKey pbeKey = keyFactory.generateSecret((KeySpec)pbeKeySpec);
        Cipher cipher = Cipher.getInstance((String)encryptedPrivateKeyInfo.getAlgName());
        cipher.init((int)2, (Key)pbeKey, (AlgorithmParameters)encryptedPrivateKeyInfo.getAlgParameters());
        return encryptedPrivateKeyInfo.getKeySpec((Cipher)cipher);
    }

    static KeyStore buildKeyStore(X509Certificate[] certChain, PrivateKey key, char[] keyPasswordChars, String keyStoreType) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
        if (keyStoreType == null) {
            keyStoreType = KeyStore.getDefaultType();
        }
        KeyStore ks = KeyStore.getInstance((String)keyStoreType);
        ks.load(null, null);
        ks.setKeyEntry((String)"key", (Key)key, (char[])keyPasswordChars, (Certificate[])certChain);
        return ks;
    }

    static PrivateKey toPrivateKey(File keyFile, String keyPassword) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeySpecException, InvalidAlgorithmParameterException, KeyException, IOException {
        if (keyFile != null) return SslContext.getPrivateKeyFromByteBuffer((ByteBuf)PemReader.readPrivateKey((File)keyFile), (String)keyPassword);
        return null;
    }

    static PrivateKey toPrivateKey(InputStream keyInputStream, String keyPassword) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeySpecException, InvalidAlgorithmParameterException, KeyException, IOException {
        if (keyInputStream != null) return SslContext.getPrivateKeyFromByteBuffer((ByteBuf)PemReader.readPrivateKey((InputStream)keyInputStream), (String)keyPassword);
        return null;
    }

    private static PrivateKey getPrivateKeyFromByteBuffer(ByteBuf encodedKeyBuf, String keyPassword) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeySpecException, InvalidAlgorithmParameterException, KeyException, IOException {
        byte[] encodedKey = new byte[encodedKeyBuf.readableBytes()];
        encodedKeyBuf.readBytes((byte[])encodedKey).release();
        PKCS8EncodedKeySpec encodedKeySpec = SslContext.generateKeySpec((char[])(keyPassword == null ? null : keyPassword.toCharArray()), (byte[])encodedKey);
        try {
            return KeyFactory.getInstance((String)"RSA").generatePrivate((KeySpec)encodedKeySpec);
        }
        catch (InvalidKeySpecException ignore) {
            try {
                return KeyFactory.getInstance((String)"DSA").generatePrivate((KeySpec)encodedKeySpec);
            }
            catch (InvalidKeySpecException ignore2) {
                try {
                    return KeyFactory.getInstance((String)"EC").generatePrivate((KeySpec)encodedKeySpec);
                }
                catch (InvalidKeySpecException e) {
                    throw new InvalidKeySpecException((String)"Neither RSA, DSA nor EC worked", (Throwable)e);
                }
            }
        }
    }

    @Deprecated
    protected static TrustManagerFactory buildTrustManagerFactory(File certChainFile, TrustManagerFactory trustManagerFactory) throws NoSuchAlgorithmException, CertificateException, KeyStoreException, IOException {
        return SslContext.buildTrustManagerFactory((File)certChainFile, (TrustManagerFactory)trustManagerFactory, (String)KeyStore.getDefaultType());
    }

    static TrustManagerFactory buildTrustManagerFactory(File certChainFile, TrustManagerFactory trustManagerFactory, String keyType) throws NoSuchAlgorithmException, CertificateException, KeyStoreException, IOException {
        X509Certificate[] x509Certs = SslContext.toX509Certificates((File)certChainFile);
        return SslContext.buildTrustManagerFactory((X509Certificate[])x509Certs, (TrustManagerFactory)trustManagerFactory, (String)keyType);
    }

    static X509Certificate[] toX509Certificates(File file) throws CertificateException {
        if (file != null) return SslContext.getCertificatesFromBuffers((ByteBuf[])PemReader.readCertificates((File)file));
        return null;
    }

    static X509Certificate[] toX509Certificates(InputStream in) throws CertificateException {
        if (in != null) return SslContext.getCertificatesFromBuffers((ByteBuf[])PemReader.readCertificates((InputStream)in));
        return null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static X509Certificate[] getCertificatesFromBuffers(ByteBuf[] certs) throws CertificateException {
        CertificateFactory cf = CertificateFactory.getInstance((String)"X.509");
        X509Certificate[] x509Certs = new X509Certificate[certs.length];
        try {
            int i = 0;
            while (i < certs.length) {
                ByteBufInputStream is = new ByteBufInputStream((ByteBuf)certs[i], (boolean)false);
                try {
                    x509Certs[i] = (X509Certificate)cf.generateCertificate((InputStream)is);
                }
                finally {
                    try {
                        ((InputStream)is).close();
                    }
                    catch (IOException e) {
                        throw new RuntimeException((Throwable)e);
                    }
                }
                ++i;
            }
            return x509Certs;
        }
        finally {
            ByteBuf[] i = certs;
            int is = i.length;
            int e = 0;
            do {
                if (e >= is) {
                }
                ByteBuf buf = i[e];
                buf.release();
                ++e;
            } while (true);
        }
    }

    static TrustManagerFactory buildTrustManagerFactory(X509Certificate[] certCollection, TrustManagerFactory trustManagerFactory, String keyStoreType) throws NoSuchAlgorithmException, CertificateException, KeyStoreException, IOException {
        if (keyStoreType == null) {
            keyStoreType = KeyStore.getDefaultType();
        }
        KeyStore ks = KeyStore.getInstance((String)keyStoreType);
        ks.load(null, null);
        int i = 1;
        X509Certificate[] arrx509Certificate = certCollection;
        int n = arrx509Certificate.length;
        for (int j = 0; j < n; ++i, ++j) {
            X509Certificate cert = arrx509Certificate[j];
            String alias = Integer.toString((int)i);
            ks.setCertificateEntry((String)alias, (Certificate)cert);
        }
        if (trustManagerFactory == null) {
            trustManagerFactory = TrustManagerFactory.getInstance((String)TrustManagerFactory.getDefaultAlgorithm());
        }
        trustManagerFactory.init((KeyStore)ks);
        return trustManagerFactory;
    }

    static PrivateKey toPrivateKeyInternal(File keyFile, String keyPassword) throws SSLException {
        try {
            return SslContext.toPrivateKey((File)keyFile, (String)keyPassword);
        }
        catch (Exception e) {
            throw new SSLException((Throwable)e);
        }
    }

    static X509Certificate[] toX509CertificatesInternal(File file) throws SSLException {
        try {
            return SslContext.toX509Certificates((File)file);
        }
        catch (CertificateException e) {
            throw new SSLException((Throwable)e);
        }
    }

    static KeyManagerFactory buildKeyManagerFactory(X509Certificate[] certChain, PrivateKey key, String keyPassword, KeyManagerFactory kmf, String keyStoreType) throws UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
        return SslContext.buildKeyManagerFactory((X509Certificate[])certChain, (String)KeyManagerFactory.getDefaultAlgorithm(), (PrivateKey)key, (String)keyPassword, (KeyManagerFactory)kmf, (String)keyStoreType);
    }

    static KeyManagerFactory buildKeyManagerFactory(X509Certificate[] certChainFile, String keyAlgorithm, PrivateKey key, String keyPassword, KeyManagerFactory kmf, String keyStore) throws KeyStoreException, NoSuchAlgorithmException, IOException, CertificateException, UnrecoverableKeyException {
        char[] keyPasswordChars = SslContext.keyStorePassword((String)keyPassword);
        KeyStore ks = SslContext.buildKeyStore((X509Certificate[])certChainFile, (PrivateKey)key, (char[])keyPasswordChars, (String)keyStore);
        return SslContext.buildKeyManagerFactory((KeyStore)ks, (String)keyAlgorithm, (char[])keyPasswordChars, (KeyManagerFactory)kmf);
    }

    static KeyManagerFactory buildKeyManagerFactory(X509Certificate[] certChainFile, String keyAlgorithm, PrivateKey key, String keyPassword, KeyManagerFactory kmf) throws KeyStoreException, NoSuchAlgorithmException, IOException, CertificateException, UnrecoverableKeyException {
        char[] keyPasswordChars = SslContext.keyStorePassword((String)keyPassword);
        KeyStore ks = SslContext.buildKeyStore((X509Certificate[])certChainFile, (PrivateKey)key, (char[])keyPasswordChars, (String)KeyStore.getDefaultType());
        return SslContext.buildKeyManagerFactory((KeyStore)ks, (String)keyAlgorithm, (char[])keyPasswordChars, (KeyManagerFactory)kmf);
    }

    static KeyManagerFactory buildKeyManagerFactory(KeyStore ks, String keyAlgorithm, char[] keyPasswordChars, KeyManagerFactory kmf) throws KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException {
        if (kmf == null) {
            kmf = KeyManagerFactory.getInstance((String)keyAlgorithm);
        }
        kmf.init((KeyStore)ks, (char[])keyPasswordChars);
        return kmf;
    }

    static char[] keyStorePassword(String keyPassword) {
        char[] arrc;
        if (keyPassword == null) {
            arrc = EmptyArrays.EMPTY_CHARS;
            return arrc;
        }
        arrc = keyPassword.toCharArray();
        return arrc;
    }

    static {
        try {
            X509_CERT_FACTORY = CertificateFactory.getInstance((String)"X.509");
            return;
        }
        catch (CertificateException e) {
            throw new IllegalStateException((String)"unable to instance X.509 CertificateFactory", (Throwable)e);
        }
    }
}

