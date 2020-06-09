/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  io.netty.internal.tcnative.Buffer
 *  io.netty.internal.tcnative.Library
 *  io.netty.internal.tcnative.SSL
 *  io.netty.internal.tcnative.SSLContext
 */
package io.netty.handler.ssl;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.UnpooledByteBufAllocator;
import io.netty.handler.ssl.CipherSuiteConverter;
import io.netty.handler.ssl.OpenSslEngine;
import io.netty.handler.ssl.PemEncoded;
import io.netty.handler.ssl.PemPrivateKey;
import io.netty.handler.ssl.ReferenceCountedOpenSslContext;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslUtils;
import io.netty.internal.tcnative.Buffer;
import io.netty.internal.tcnative.Library;
import io.netty.internal.tcnative.SSL;
import io.netty.internal.tcnative.SSLContext;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.ReferenceCounted;
import io.netty.util.internal.NativeLibraryLoader;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.SystemPropertyUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public final class OpenSsl {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(OpenSsl.class);
    private static final Throwable UNAVAILABILITY_CAUSE;
    static final List<String> DEFAULT_CIPHERS;
    static final Set<String> AVAILABLE_CIPHER_SUITES;
    private static final Set<String> AVAILABLE_OPENSSL_CIPHER_SUITES;
    private static final Set<String> AVAILABLE_JAVA_CIPHER_SUITES;
    private static final boolean SUPPORTS_KEYMANAGER_FACTORY;
    private static final boolean USE_KEYMANAGER_FACTORY;
    private static final boolean SUPPORTS_OCSP;
    private static final boolean TLSV13_SUPPORTED;
    private static final boolean IS_BORINGSSL;
    static final Set<String> SUPPORTED_PROTOCOLS_SET;
    private static final String CERT = "longStr2[-----BEGIN]";
    private static final String KEY = "longStr3[-----BEGIN]";

    static X509Certificate selfSignedCertificate() throws CertificateException {
        return (X509Certificate)SslContext.X509_CERT_FACTORY.generateCertificate((InputStream)new ByteArrayInputStream((byte[])CERT.getBytes((Charset)CharsetUtil.US_ASCII)));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static boolean doesSupportOcsp() {
        boolean supportsOcsp = false;
        if ((long)OpenSsl.version() < 268443648L) return supportsOcsp;
        long sslCtx = -1L;
        try {
            sslCtx = SSLContext.make((int)16, (int)1);
            SSLContext.enableOcsp((long)sslCtx, (boolean)false);
            supportsOcsp = true;
            return supportsOcsp;
        }
        catch (Exception exception) {
            return supportsOcsp;
        }
        finally {
            if (sslCtx != -1L) {
                SSLContext.free((long)sslCtx);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static boolean doesSupportProtocol(int protocol, int opt) {
        if (opt == 0) {
            return false;
        }
        long sslCtx = -1L;
        try {
            sslCtx = SSLContext.make((int)protocol, (int)2);
            boolean bl = true;
            return bl;
        }
        catch (Exception ignore) {
            boolean bl = false;
            return bl;
        }
        finally {
            if (sslCtx != -1L) {
                SSLContext.free((long)sslCtx);
            }
        }
    }

    public static boolean isAvailable() {
        if (UNAVAILABILITY_CAUSE != null) return false;
        return true;
    }

    @Deprecated
    public static boolean isAlpnSupported() {
        if ((long)OpenSsl.version() < 268443648L) return false;
        return true;
    }

    public static boolean isOcspSupported() {
        return SUPPORTS_OCSP;
    }

    public static int version() {
        if (!OpenSsl.isAvailable()) return -1;
        int n = SSL.version();
        return n;
    }

    public static String versionString() {
        if (!OpenSsl.isAvailable()) return null;
        String string = SSL.versionString();
        return string;
    }

    public static void ensureAvailability() {
        if (UNAVAILABILITY_CAUSE == null) return;
        throw (Error)new UnsatisfiedLinkError((String)"failed to load the required native library").initCause((Throwable)UNAVAILABILITY_CAUSE);
    }

    public static Throwable unavailabilityCause() {
        return UNAVAILABILITY_CAUSE;
    }

    @Deprecated
    public static Set<String> availableCipherSuites() {
        return OpenSsl.availableOpenSslCipherSuites();
    }

    public static Set<String> availableOpenSslCipherSuites() {
        return AVAILABLE_OPENSSL_CIPHER_SUITES;
    }

    public static Set<String> availableJavaCipherSuites() {
        return AVAILABLE_JAVA_CIPHER_SUITES;
    }

    public static boolean isCipherSuiteAvailable(String cipherSuite) {
        String converted = CipherSuiteConverter.toOpenSsl((String)cipherSuite, (boolean)IS_BORINGSSL);
        if (converted == null) return AVAILABLE_OPENSSL_CIPHER_SUITES.contains((Object)cipherSuite);
        cipherSuite = converted;
        return AVAILABLE_OPENSSL_CIPHER_SUITES.contains((Object)cipherSuite);
    }

    public static boolean supportsKeyManagerFactory() {
        return SUPPORTS_KEYMANAGER_FACTORY;
    }

    @Deprecated
    public static boolean supportsHostnameValidation() {
        return OpenSsl.isAvailable();
    }

    static boolean useKeyManagerFactory() {
        return USE_KEYMANAGER_FACTORY;
    }

    static long memoryAddress(ByteBuf buf) {
        long l;
        assert (buf.isDirect());
        if (buf.hasMemoryAddress()) {
            l = buf.memoryAddress();
            return l;
        }
        l = Buffer.address((ByteBuffer)buf.nioBuffer());
        return l;
    }

    private OpenSsl() {
    }

    private static void loadTcNative() throws Exception {
        String os = PlatformDependent.normalizedOs();
        String arch = PlatformDependent.normalizedArch();
        LinkedHashSet<String> libNames = new LinkedHashSet<String>((int)5);
        String staticLibName = "netty_tcnative";
        if ("linux".equalsIgnoreCase((String)os)) {
            Set<String> classifiers = PlatformDependent.normalizedLinuxClassifiers();
            for (String classifier : classifiers) {
                libNames.add(staticLibName + "_" + os + '_' + arch + "_" + classifier);
            }
            libNames.add(staticLibName + "_" + os + '_' + arch);
            libNames.add(staticLibName + "_" + os + '_' + arch + "_fedora");
        } else {
            libNames.add(staticLibName + "_" + os + '_' + arch);
        }
        libNames.add(staticLibName + "_" + arch);
        libNames.add(staticLibName);
        NativeLibraryLoader.loadFirstAvailable((ClassLoader)SSL.class.getClassLoader(), (String[])libNames.toArray(new String[0]));
    }

    private static boolean initializeTcNative(String engine) throws Exception {
        return Library.initialize((String)"provided", (String)engine);
    }

    static void releaseIfNeeded(ReferenceCounted counted) {
        if (counted.refCnt() <= 0) return;
        ReferenceCountUtil.safeRelease((Object)counted);
    }

    static boolean isTlsv13Supported() {
        return TLSV13_SUPPORTED;
    }

    static boolean isBoringSSL() {
        return IS_BORINGSSL;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    static {
        Throwable cause = null;
        if (SystemPropertyUtil.getBoolean((String)"io.netty.handler.ssl.noOpenSsl", (boolean)false)) {
            cause = new UnsupportedOperationException((String)"OpenSSL was explicit disabled with -Dio.netty.handler.ssl.noOpenSsl=true");
            logger.debug((String)("netty-tcnative explicit disabled; " + OpenSslEngine.class.getSimpleName() + " will be unavailable."), (Throwable)cause);
        } else {
            try {
                Class.forName((String)"io.netty.internal.tcnative.SSL", (boolean)false, (ClassLoader)OpenSsl.class.getClassLoader());
            }
            catch (ClassNotFoundException t) {
                cause = t;
                logger.debug((String)("netty-tcnative not in the classpath; " + OpenSslEngine.class.getSimpleName() + " will be unavailable."));
            }
            if (cause == null) {
                try {
                    OpenSsl.loadTcNative();
                }
                catch (Throwable t) {
                    cause = t;
                    logger.debug((String)("Failed to load netty-tcnative; " + OpenSslEngine.class.getSimpleName() + " will be unavailable, unless the application has already loaded the symbols by some other means. See https://netty.io/wiki/forked-tomcat-native.html for more information."), (Throwable)t);
                }
                try {
                    String engine = SystemPropertyUtil.get((String)"io.netty.handler.ssl.openssl.engine", null);
                    if (engine == null) {
                        logger.debug((String)"Initialize netty-tcnative using engine: 'default'");
                    } else {
                        logger.debug((String)"Initialize netty-tcnative using engine: '{}'", (Object)engine);
                    }
                    OpenSsl.initializeTcNative((String)engine);
                    cause = null;
                }
                catch (Throwable t) {
                    if (cause == null) {
                        cause = t;
                    }
                    logger.debug((String)("Failed to initialize netty-tcnative; " + OpenSslEngine.class.getSimpleName() + " will be unavailable. See https://netty.io/wiki/forked-tomcat-native.html for more information."), (Throwable)t);
                }
            }
        }
        UNAVAILABILITY_CAUSE = cause;
        if (cause != null) {
            DEFAULT_CIPHERS = Collections.emptyList();
            AVAILABLE_OPENSSL_CIPHER_SUITES = Collections.emptySet();
            AVAILABLE_JAVA_CIPHER_SUITES = Collections.emptySet();
            AVAILABLE_CIPHER_SUITES = Collections.emptySet();
            SUPPORTS_KEYMANAGER_FACTORY = false;
            USE_KEYMANAGER_FACTORY = false;
            SUPPORTED_PROTOCOLS_SET = Collections.emptySet();
            SUPPORTS_OCSP = false;
            TLSV13_SUPPORTED = false;
            IS_BORINGSSL = false;
            return;
        }
        logger.debug((String)"netty-tcnative using native library: {}", (Object)SSL.versionString());
        ArrayList<String> defaultCiphers = new ArrayList<String>();
        LinkedHashSet<String> availableOpenSslCipherSuites = new LinkedHashSet<String>((int)128);
        boolean supportsKeyManagerFactory = false;
        boolean useKeyManagerFactory = false;
        boolean tlsv13Supported = false;
        IS_BORINGSSL = "BoringSSL".equals((Object)OpenSsl.versionString());
        try {
            long sslCtx = SSLContext.make((int)63, (int)1);
            long certBio = 0L;
            long keyBio = 0L;
            long cert = 0L;
            long key = 0L;
            try {
                try {
                    StringBuilder tlsv13Ciphers = new StringBuilder();
                    for (String cipher : SslUtils.TLSV13_CIPHERS) {
                        String converted = CipherSuiteConverter.toOpenSsl((String)cipher, (boolean)IS_BORINGSSL);
                        if (converted == null) continue;
                        tlsv13Ciphers.append((String)converted).append((char)':');
                    }
                    if (tlsv13Ciphers.length() == 0) {
                        tlsv13Supported = false;
                    } else {
                        tlsv13Ciphers.setLength((int)(tlsv13Ciphers.length() - 1));
                        SSLContext.setCipherSuite((long)sslCtx, (String)tlsv13Ciphers.toString(), (boolean)true);
                        tlsv13Supported = true;
                    }
                }
                catch (Exception ignore) {
                    tlsv13Supported = false;
                }
                SSLContext.setCipherSuite((long)sslCtx, (String)"ALL", (boolean)false);
                long ssl = SSL.newSSL((long)sslCtx, (boolean)true);
                try {
                    for (String c : SSL.getCiphers((long)ssl)) {
                        if (c == null || c.isEmpty() || availableOpenSslCipherSuites.contains((Object)c) || !tlsv13Supported && SslUtils.isTLSv13Cipher((String)c)) continue;
                        availableOpenSslCipherSuites.add(c);
                    }
                    if (IS_BORINGSSL) {
                        Collections.addAll(availableOpenSslCipherSuites, "TLS_AES_128_GCM_SHA256", "TLS_AES_256_GCM_SHA384", "TLS_CHACHA20_POLY1305_SHA256", "AEAD-AES128-GCM-SHA256", "AEAD-AES256-GCM-SHA384", "AEAD-CHACHA20-POLY1305-SHA256");
                    }
                    PemPrivateKey privateKey = PemPrivateKey.valueOf((byte[])KEY.getBytes((Charset)CharsetUtil.US_ASCII));
                    try {
                        SSLContext.setCertificateCallback((long)sslCtx, null);
                        X509Certificate certificate = OpenSsl.selfSignedCertificate();
                        certBio = ReferenceCountedOpenSslContext.toBIO((ByteBufAllocator)ByteBufAllocator.DEFAULT, (X509Certificate[])new X509Certificate[]{certificate});
                        cert = SSL.parseX509Chain((long)certBio);
                        keyBio = ReferenceCountedOpenSslContext.toBIO((ByteBufAllocator)UnpooledByteBufAllocator.DEFAULT, (PemEncoded)privateKey.retain());
                        key = SSL.parsePrivateKey((long)keyBio, null);
                        SSL.setKeyMaterial((long)ssl, (long)cert, (long)key);
                        supportsKeyManagerFactory = true;
                        try {
                            boolean propertySet = SystemPropertyUtil.contains((String)"io.netty.handler.ssl.openssl.useKeyManagerFactory");
                            if (!IS_BORINGSSL) {
                                useKeyManagerFactory = SystemPropertyUtil.getBoolean((String)"io.netty.handler.ssl.openssl.useKeyManagerFactory", (boolean)true);
                                if (propertySet) {
                                    logger.info((String)"System property 'io.netty.handler.ssl.openssl.useKeyManagerFactory' is deprecated and so will be ignored in the future");
                                }
                            } else {
                                useKeyManagerFactory = true;
                                if (propertySet) {
                                    logger.info((String)"System property 'io.netty.handler.ssl.openssl.useKeyManagerFactory' is deprecated and will be ignored when using BoringSSL");
                                }
                            }
                        }
                        catch (Throwable ignore) {
                            logger.debug((String)"Failed to get useKeyManagerFactory system property.");
                        }
                    }
                    catch (Error ignore) {
                        logger.debug((String)"KeyManagerFactory not supported.");
                    }
                    finally {
                        privateKey.release();
                    }
                }
                finally {
                    SSL.freeSSL((long)ssl);
                    if (certBio != 0L) {
                        SSL.freeBIO((long)certBio);
                    }
                    if (keyBio != 0L) {
                        SSL.freeBIO((long)keyBio);
                    }
                    if (cert != 0L) {
                        SSL.freeX509Chain((long)cert);
                    }
                    if (key != 0L) {
                        SSL.freePrivateKey((long)key);
                    }
                }
            }
            finally {
                SSLContext.free((long)sslCtx);
            }
        }
        catch (Exception e) {
            logger.warn((String)"Failed to get the list of available OpenSSL cipher suites.", (Throwable)e);
        }
        AVAILABLE_OPENSSL_CIPHER_SUITES = Collections.unmodifiableSet(availableOpenSslCipherSuites);
        LinkedHashSet<String> availableJavaCipherSuites = new LinkedHashSet<String>((int)(AVAILABLE_OPENSSL_CIPHER_SUITES.size() * 2));
        for (String cipher : AVAILABLE_OPENSSL_CIPHER_SUITES) {
            if (!SslUtils.isTLSv13Cipher((String)cipher)) {
                availableJavaCipherSuites.add((String)CipherSuiteConverter.toJava((String)cipher, (String)"TLS"));
                availableJavaCipherSuites.add((String)CipherSuiteConverter.toJava((String)cipher, (String)"SSL"));
                continue;
            }
            availableJavaCipherSuites.add((String)cipher);
        }
        SslUtils.addIfSupported(availableJavaCipherSuites, defaultCiphers, (String[])SslUtils.DEFAULT_CIPHER_SUITES);
        SslUtils.addIfSupported(availableJavaCipherSuites, defaultCiphers, (String[])SslUtils.TLSV13_CIPHER_SUITES);
        SslUtils.useFallbackCiphersIfDefaultIsEmpty(defaultCiphers, availableJavaCipherSuites);
        DEFAULT_CIPHERS = Collections.unmodifiableList(defaultCiphers);
        AVAILABLE_JAVA_CIPHER_SUITES = Collections.unmodifiableSet(availableJavaCipherSuites);
        LinkedHashSet<String> availableCipherSuites = new LinkedHashSet<String>((int)(AVAILABLE_OPENSSL_CIPHER_SUITES.size() + AVAILABLE_JAVA_CIPHER_SUITES.size()));
        availableCipherSuites.addAll(AVAILABLE_OPENSSL_CIPHER_SUITES);
        availableCipherSuites.addAll(AVAILABLE_JAVA_CIPHER_SUITES);
        AVAILABLE_CIPHER_SUITES = availableCipherSuites;
        SUPPORTS_KEYMANAGER_FACTORY = supportsKeyManagerFactory;
        USE_KEYMANAGER_FACTORY = useKeyManagerFactory;
        LinkedHashSet<String> protocols = new LinkedHashSet<String>((int)6);
        protocols.add("SSLv2Hello");
        if (OpenSsl.doesSupportProtocol((int)1, (int)SSL.SSL_OP_NO_SSLv2)) {
            protocols.add("SSLv2");
        }
        if (OpenSsl.doesSupportProtocol((int)2, (int)SSL.SSL_OP_NO_SSLv3)) {
            protocols.add("SSLv3");
        }
        if (OpenSsl.doesSupportProtocol((int)4, (int)SSL.SSL_OP_NO_TLSv1)) {
            protocols.add("TLSv1");
        }
        if (OpenSsl.doesSupportProtocol((int)8, (int)SSL.SSL_OP_NO_TLSv1_1)) {
            protocols.add("TLSv1.1");
        }
        if (OpenSsl.doesSupportProtocol((int)16, (int)SSL.SSL_OP_NO_TLSv1_2)) {
            protocols.add("TLSv1.2");
        }
        if (tlsv13Supported && OpenSsl.doesSupportProtocol((int)32, (int)SSL.SSL_OP_NO_TLSv1_3)) {
            protocols.add("TLSv1.3");
            TLSV13_SUPPORTED = true;
        } else {
            TLSV13_SUPPORTED = false;
        }
        SUPPORTED_PROTOCOLS_SET = Collections.unmodifiableSet(protocols);
        SUPPORTS_OCSP = OpenSsl.doesSupportOcsp();
        if (!logger.isDebugEnabled()) return;
        logger.debug((String)"Supported protocols (OpenSSL): {} ", SUPPORTED_PROTOCOLS_SET);
        logger.debug((String)"Default cipher suites (OpenSSL): {}", DEFAULT_CIPHERS);
    }
}

