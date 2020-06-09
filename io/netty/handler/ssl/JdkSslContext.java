/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.ssl;

import io.netty.buffer.ByteBufAllocator;
import io.netty.handler.ssl.ApplicationProtocolConfig;
import io.netty.handler.ssl.ApplicationProtocolNegotiator;
import io.netty.handler.ssl.CipherSuiteFilter;
import io.netty.handler.ssl.ClientAuth;
import io.netty.handler.ssl.IdentityCipherSuiteFilter;
import io.netty.handler.ssl.JdkAlpnApplicationProtocolNegotiator;
import io.netty.handler.ssl.JdkApplicationProtocolNegotiator;
import io.netty.handler.ssl.JdkDefaultApplicationProtocolNegotiator;
import io.netty.handler.ssl.JdkNpnApplicationProtocolNegotiator;
import io.netty.handler.ssl.JdkSslContext;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslUtils;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.File;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.SecureRandom;
import java.security.Security;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import javax.crypto.NoSuchPaddingException;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLSessionContext;
import javax.net.ssl.TrustManager;

public class JdkSslContext
extends SslContext {
    private static final InternalLogger logger;
    static final String PROTOCOL = "TLS";
    private static final String[] DEFAULT_PROTOCOLS;
    private static final List<String> DEFAULT_CIPHERS;
    private static final List<String> DEFAULT_CIPHERS_NON_TLSV13;
    private static final Set<String> SUPPORTED_CIPHERS;
    private static final Set<String> SUPPORTED_CIPHERS_NON_TLSV13;
    private static final Provider DEFAULT_PROVIDER;
    private final String[] protocols;
    private final String[] cipherSuites;
    private final List<String> unmodifiableCipherSuites;
    private final JdkApplicationProtocolNegotiator apn;
    private final ClientAuth clientAuth;
    private final SSLContext sslContext;
    private final boolean isClient;

    private static String[] defaultProtocols(SSLEngine engine) {
        String[] supportedProtocols = engine.getSupportedProtocols();
        HashSet<String> supportedProtocolsSet = new HashSet<String>((int)supportedProtocols.length);
        Collections.addAll(supportedProtocolsSet, supportedProtocols);
        ArrayList<String> protocols = new ArrayList<String>();
        SslUtils.addIfSupported(supportedProtocolsSet, protocols, (String[])new String[]{"TLSv1.2", "TLSv1.1", "TLSv1"});
        if (protocols.isEmpty()) return engine.getEnabledProtocols();
        return protocols.toArray(new String[0]);
    }

    private static Set<String> supportedCiphers(SSLEngine engine) {
        String[] supportedCiphers = engine.getSupportedCipherSuites();
        LinkedHashSet<String> supportedCiphersSet = new LinkedHashSet<String>((int)supportedCiphers.length);
        int i = 0;
        while (i < supportedCiphers.length) {
            String supportedCipher = supportedCiphers[i];
            supportedCiphersSet.add((String)supportedCipher);
            if (supportedCipher.startsWith((String)"SSL_")) {
                String tlsPrefixedCipherName = "TLS_" + supportedCipher.substring((int)"SSL_".length());
                try {
                    engine.setEnabledCipherSuites((String[])new String[]{tlsPrefixedCipherName});
                    supportedCiphersSet.add((String)tlsPrefixedCipherName);
                }
                catch (IllegalArgumentException illegalArgumentException) {
                    // empty catch block
                }
            }
            ++i;
        }
        return supportedCiphersSet;
    }

    private static List<String> defaultCiphers(SSLEngine engine, Set<String> supportedCiphers) {
        ArrayList<String> ciphers = new ArrayList<String>();
        SslUtils.addIfSupported(supportedCiphers, ciphers, (String[])SslUtils.DEFAULT_CIPHER_SUITES);
        SslUtils.useFallbackCiphersIfDefaultIsEmpty(ciphers, (String[])engine.getEnabledCipherSuites());
        return ciphers;
    }

    private static boolean isTlsV13Supported(String[] protocols) {
        String[] arrstring = protocols;
        int n = arrstring.length;
        int n2 = 0;
        while (n2 < n) {
            String protocol = arrstring[n2];
            if ("TLSv1.3".equals((Object)protocol)) {
                return true;
            }
            ++n2;
        }
        return false;
    }

    @Deprecated
    public JdkSslContext(SSLContext sslContext, boolean isClient, ClientAuth clientAuth) {
        this((SSLContext)sslContext, (boolean)isClient, null, (CipherSuiteFilter)IdentityCipherSuiteFilter.INSTANCE, (JdkApplicationProtocolNegotiator)JdkDefaultApplicationProtocolNegotiator.INSTANCE, (ClientAuth)clientAuth, null, (boolean)false);
    }

    @Deprecated
    public JdkSslContext(SSLContext sslContext, boolean isClient, Iterable<String> ciphers, CipherSuiteFilter cipherFilter, ApplicationProtocolConfig apn, ClientAuth clientAuth) {
        this((SSLContext)sslContext, (boolean)isClient, ciphers, (CipherSuiteFilter)cipherFilter, (ApplicationProtocolConfig)apn, (ClientAuth)clientAuth, null, (boolean)false);
    }

    public JdkSslContext(SSLContext sslContext, boolean isClient, Iterable<String> ciphers, CipherSuiteFilter cipherFilter, ApplicationProtocolConfig apn, ClientAuth clientAuth, String[] protocols, boolean startTls) {
        this((SSLContext)sslContext, (boolean)isClient, ciphers, (CipherSuiteFilter)cipherFilter, (JdkApplicationProtocolNegotiator)JdkSslContext.toNegotiator((ApplicationProtocolConfig)apn, (boolean)(!isClient)), (ClientAuth)clientAuth, (String[])(protocols == null ? null : (String[])protocols.clone()), (boolean)startTls);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    JdkSslContext(SSLContext sslContext, boolean isClient, Iterable<String> ciphers, CipherSuiteFilter cipherFilter, JdkApplicationProtocolNegotiator apn, ClientAuth clientAuth, String[] protocols, boolean startTls) {
        super((boolean)startTls);
        Set<String> supportedCiphers;
        List<String> defaultCiphers;
        this.apn = ObjectUtil.checkNotNull(apn, (String)"apn");
        this.clientAuth = ObjectUtil.checkNotNull(clientAuth, (String)"clientAuth");
        this.sslContext = ObjectUtil.checkNotNull(sslContext, (String)"sslContext");
        if (DEFAULT_PROVIDER.equals((Object)sslContext.getProvider())) {
            String[] arrstring = this.protocols = protocols == null ? DEFAULT_PROTOCOLS : protocols;
            if (JdkSslContext.isTlsV13Supported((String[])this.protocols)) {
                supportedCiphers = SUPPORTED_CIPHERS;
                defaultCiphers = DEFAULT_CIPHERS;
            } else {
                supportedCiphers = SUPPORTED_CIPHERS_NON_TLSV13;
                defaultCiphers = DEFAULT_CIPHERS_NON_TLSV13;
            }
        } else {
            SSLEngine engine = sslContext.createSSLEngine();
            try {
                this.protocols = protocols == null ? JdkSslContext.defaultProtocols((SSLEngine)engine) : protocols;
                supportedCiphers = JdkSslContext.supportedCiphers((SSLEngine)engine);
                defaultCiphers = JdkSslContext.defaultCiphers((SSLEngine)engine, supportedCiphers);
                if (!JdkSslContext.isTlsV13Supported((String[])this.protocols)) {
                    for (String cipher : SslUtils.DEFAULT_TLSV13_CIPHER_SUITES) {
                        supportedCiphers.remove((Object)cipher);
                        defaultCiphers.remove((Object)cipher);
                    }
                }
            }
            finally {
                ReferenceCountUtil.release((Object)engine);
            }
        }
        this.cipherSuites = ObjectUtil.checkNotNull(cipherFilter, (String)"cipherFilter").filterCipherSuites(ciphers, defaultCiphers, supportedCiphers);
        this.unmodifiableCipherSuites = Collections.unmodifiableList(Arrays.asList(this.cipherSuites));
        this.isClient = isClient;
    }

    public final SSLContext context() {
        return this.sslContext;
    }

    @Override
    public final boolean isClient() {
        return this.isClient;
    }

    @Override
    public final SSLSessionContext sessionContext() {
        if (!this.isServer()) return this.context().getClientSessionContext();
        return this.context().getServerSessionContext();
    }

    @Override
    public final List<String> cipherSuites() {
        return this.unmodifiableCipherSuites;
    }

    @Override
    public final long sessionCacheSize() {
        return (long)this.sessionContext().getSessionCacheSize();
    }

    @Override
    public final long sessionTimeout() {
        return (long)this.sessionContext().getSessionTimeout();
    }

    @Override
    public final SSLEngine newEngine(ByteBufAllocator alloc) {
        return this.configureAndWrapEngine((SSLEngine)this.context().createSSLEngine(), (ByteBufAllocator)alloc);
    }

    @Override
    public final SSLEngine newEngine(ByteBufAllocator alloc, String peerHost, int peerPort) {
        return this.configureAndWrapEngine((SSLEngine)this.context().createSSLEngine((String)peerHost, (int)peerPort), (ByteBufAllocator)alloc);
    }

    /*
     * Unable to fully structure code
     */
    private SSLEngine configureAndWrapEngine(SSLEngine engine, ByteBufAllocator alloc) {
        engine.setEnabledCipherSuites((String[])this.cipherSuites);
        engine.setEnabledProtocols((String[])this.protocols);
        engine.setUseClientMode((boolean)this.isClient());
        if (this.isServer()) {
            switch (1.$SwitchMap$io$netty$handler$ssl$ClientAuth[this.clientAuth.ordinal()]) {
                case 1: {
                    engine.setWantClientAuth((boolean)true);
                    ** break;
                }
                case 2: {
                    engine.setNeedClientAuth((boolean)true);
                    ** break;
                }
                case 3: {
                    ** break;
                }
            }
            throw new Error((String)("Unknown auth " + (Object)this.clientAuth));
        }
lbl15: // 5 sources:
        if ((factory = this.apn.wrapperFactory()) instanceof JdkApplicationProtocolNegotiator.AllocatorAwareSslEngineWrapperFactory == false) return factory.wrapSslEngine((SSLEngine)engine, (JdkApplicationProtocolNegotiator)this.apn, (boolean)this.isServer());
        return ((JdkApplicationProtocolNegotiator.AllocatorAwareSslEngineWrapperFactory)factory).wrapSslEngine((SSLEngine)engine, (ByteBufAllocator)alloc, (JdkApplicationProtocolNegotiator)this.apn, (boolean)this.isServer());
    }

    @Override
    public final JdkApplicationProtocolNegotiator applicationProtocolNegotiator() {
        return this.apn;
    }

    static JdkApplicationProtocolNegotiator toNegotiator(ApplicationProtocolConfig config, boolean isServer) {
        if (config == null) {
            return JdkDefaultApplicationProtocolNegotiator.INSTANCE;
        }
        switch (config.protocol()) {
            case NONE: {
                return JdkDefaultApplicationProtocolNegotiator.INSTANCE;
            }
            case ALPN: {
                if (isServer) {
                    switch (config.selectorFailureBehavior()) {
                        case FATAL_ALERT: {
                            return new JdkAlpnApplicationProtocolNegotiator((boolean)true, config.supportedProtocols());
                        }
                        case NO_ADVERTISE: {
                            return new JdkAlpnApplicationProtocolNegotiator((boolean)false, config.supportedProtocols());
                        }
                    }
                    throw new UnsupportedOperationException((String)("JDK provider does not support " + (Object)((Object)config.selectorFailureBehavior()) + " failure behavior"));
                }
                switch (config.selectedListenerFailureBehavior()) {
                    case ACCEPT: {
                        return new JdkAlpnApplicationProtocolNegotiator((boolean)false, config.supportedProtocols());
                    }
                    case FATAL_ALERT: {
                        return new JdkAlpnApplicationProtocolNegotiator((boolean)true, config.supportedProtocols());
                    }
                }
                throw new UnsupportedOperationException((String)("JDK provider does not support " + (Object)((Object)config.selectedListenerFailureBehavior()) + " failure behavior"));
            }
            case NPN: {
                if (isServer) {
                    switch (config.selectedListenerFailureBehavior()) {
                        case ACCEPT: {
                            return new JdkNpnApplicationProtocolNegotiator((boolean)false, config.supportedProtocols());
                        }
                        case FATAL_ALERT: {
                            return new JdkNpnApplicationProtocolNegotiator((boolean)true, config.supportedProtocols());
                        }
                    }
                    throw new UnsupportedOperationException((String)("JDK provider does not support " + (Object)((Object)config.selectedListenerFailureBehavior()) + " failure behavior"));
                }
                switch (config.selectorFailureBehavior()) {
                    case FATAL_ALERT: {
                        return new JdkNpnApplicationProtocolNegotiator((boolean)true, config.supportedProtocols());
                    }
                    case NO_ADVERTISE: {
                        return new JdkNpnApplicationProtocolNegotiator((boolean)false, config.supportedProtocols());
                    }
                }
                throw new UnsupportedOperationException((String)("JDK provider does not support " + (Object)((Object)config.selectorFailureBehavior()) + " failure behavior"));
            }
        }
        throw new UnsupportedOperationException((String)("JDK provider does not support " + (Object)((Object)config.protocol()) + " protocol"));
    }

    static KeyManagerFactory buildKeyManagerFactory(File certChainFile, File keyFile, String keyPassword, KeyManagerFactory kmf, String keyStore) throws UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeySpecException, InvalidAlgorithmParameterException, CertificateException, KeyException, IOException {
        String algorithm = Security.getProperty((String)"ssl.KeyManagerFactory.algorithm");
        if (algorithm != null) return JdkSslContext.buildKeyManagerFactory((File)certChainFile, (String)algorithm, (File)keyFile, (String)keyPassword, (KeyManagerFactory)kmf, (String)keyStore);
        algorithm = "SunX509";
        return JdkSslContext.buildKeyManagerFactory((File)certChainFile, (String)algorithm, (File)keyFile, (String)keyPassword, (KeyManagerFactory)kmf, (String)keyStore);
    }

    @Deprecated
    protected static KeyManagerFactory buildKeyManagerFactory(File certChainFile, File keyFile, String keyPassword, KeyManagerFactory kmf) throws UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeySpecException, InvalidAlgorithmParameterException, CertificateException, KeyException, IOException {
        return JdkSslContext.buildKeyManagerFactory((File)certChainFile, (File)keyFile, (String)keyPassword, (KeyManagerFactory)kmf, (String)KeyStore.getDefaultType());
    }

    static KeyManagerFactory buildKeyManagerFactory(File certChainFile, String keyAlgorithm, File keyFile, String keyPassword, KeyManagerFactory kmf, String keyStore) throws KeyStoreException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeySpecException, InvalidAlgorithmParameterException, IOException, CertificateException, KeyException, UnrecoverableKeyException {
        return JdkSslContext.buildKeyManagerFactory((X509Certificate[])JdkSslContext.toX509Certificates((File)certChainFile), (String)keyAlgorithm, (PrivateKey)JdkSslContext.toPrivateKey((File)keyFile, (String)keyPassword), (String)keyPassword, (KeyManagerFactory)kmf, (String)keyStore);
    }

    @Deprecated
    protected static KeyManagerFactory buildKeyManagerFactory(File certChainFile, String keyAlgorithm, File keyFile, String keyPassword, KeyManagerFactory kmf) throws KeyStoreException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeySpecException, InvalidAlgorithmParameterException, IOException, CertificateException, KeyException, UnrecoverableKeyException {
        return JdkSslContext.buildKeyManagerFactory((X509Certificate[])JdkSslContext.toX509Certificates((File)certChainFile), (String)keyAlgorithm, (PrivateKey)JdkSslContext.toPrivateKey((File)keyFile, (String)keyPassword), (String)keyPassword, (KeyManagerFactory)kmf, (String)KeyStore.getDefaultType());
    }

    static {
        SSLContext context;
        logger = InternalLoggerFactory.getInstance(JdkSslContext.class);
        try {
            context = SSLContext.getInstance((String)PROTOCOL);
            context.init(null, null, null);
        }
        catch (Exception e) {
            throw new Error((String)"failed to initialize the default SSL context", (Throwable)e);
        }
        DEFAULT_PROVIDER = context.getProvider();
        SSLEngine engine = context.createSSLEngine();
        DEFAULT_PROTOCOLS = JdkSslContext.defaultProtocols((SSLEngine)engine);
        SUPPORTED_CIPHERS = Collections.unmodifiableSet(JdkSslContext.supportedCiphers((SSLEngine)engine));
        DEFAULT_CIPHERS = Collections.unmodifiableList(JdkSslContext.defaultCiphers((SSLEngine)engine, SUPPORTED_CIPHERS));
        ArrayList<String> ciphersNonTLSv13 = new ArrayList<String>(DEFAULT_CIPHERS);
        ciphersNonTLSv13.removeAll(Arrays.asList(SslUtils.DEFAULT_TLSV13_CIPHER_SUITES));
        DEFAULT_CIPHERS_NON_TLSV13 = Collections.unmodifiableList(ciphersNonTLSv13);
        LinkedHashSet<String> suppertedCiphersNonTLSv13 = new LinkedHashSet<String>(SUPPORTED_CIPHERS);
        suppertedCiphersNonTLSv13.removeAll(Arrays.asList(SslUtils.DEFAULT_TLSV13_CIPHER_SUITES));
        SUPPORTED_CIPHERS_NON_TLSV13 = Collections.unmodifiableSet(suppertedCiphersNonTLSv13);
        if (!logger.isDebugEnabled()) return;
        logger.debug((String)"Default protocols (JDK): {} ", Arrays.asList(DEFAULT_PROTOCOLS));
        logger.debug((String)"Default cipher suites (JDK): {}", DEFAULT_CIPHERS);
    }
}

