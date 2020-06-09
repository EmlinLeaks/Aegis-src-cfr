/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  io.netty.internal.tcnative.SSL
 *  io.netty.internal.tcnative.SSLContext
 *  io.netty.internal.tcnative.SSLPrivateKeyMethod
 */
package io.netty.handler.ssl;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.handler.ssl.ApplicationProtocolConfig;
import io.netty.handler.ssl.ApplicationProtocolNegotiator;
import io.netty.handler.ssl.CipherSuiteConverter;
import io.netty.handler.ssl.CipherSuiteFilter;
import io.netty.handler.ssl.ClientAuth;
import io.netty.handler.ssl.OpenSsl;
import io.netty.handler.ssl.OpenSslApplicationProtocolNegotiator;
import io.netty.handler.ssl.OpenSslCachingKeyMaterialProvider;
import io.netty.handler.ssl.OpenSslCachingX509KeyManagerFactory;
import io.netty.handler.ssl.OpenSslDefaultApplicationProtocolNegotiator;
import io.netty.handler.ssl.OpenSslEngineMap;
import io.netty.handler.ssl.OpenSslKeyMaterialProvider;
import io.netty.handler.ssl.OpenSslPrivateKeyMethod;
import io.netty.handler.ssl.OpenSslSessionContext;
import io.netty.handler.ssl.OpenSslSessionStats;
import io.netty.handler.ssl.OpenSslX509Certificate;
import io.netty.handler.ssl.OpenSslX509KeyManagerFactory;
import io.netty.handler.ssl.OpenSslX509TrustManagerWrapper;
import io.netty.handler.ssl.PemEncoded;
import io.netty.handler.ssl.PemPrivateKey;
import io.netty.handler.ssl.PemX509Certificate;
import io.netty.handler.ssl.ReferenceCountedOpenSslContext;
import io.netty.handler.ssl.ReferenceCountedOpenSslEngine;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslHandler;
import io.netty.internal.tcnative.SSL;
import io.netty.internal.tcnative.SSLContext;
import io.netty.internal.tcnative.SSLPrivateKeyMethod;
import io.netty.util.AbstractReferenceCounted;
import io.netty.util.ReferenceCounted;
import io.netty.util.ResourceLeakDetector;
import io.netty.util.ResourceLeakDetectorFactory;
import io.netty.util.ResourceLeakTracker;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.SuppressJava6Requirement;
import io.netty.util.internal.SystemPropertyUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSessionContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509ExtendedTrustManager;
import javax.net.ssl.X509KeyManager;
import javax.net.ssl.X509TrustManager;

public abstract class ReferenceCountedOpenSslContext
extends SslContext
implements ReferenceCounted {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(ReferenceCountedOpenSslContext.class);
    private static final int DEFAULT_BIO_NON_APPLICATION_BUFFER_SIZE = Math.max((int)1, (int)SystemPropertyUtil.getInt((String)"io.netty.handler.ssl.openssl.bioNonApplicationBufferSize", (int)2048));
    static final boolean USE_TASKS = SystemPropertyUtil.getBoolean((String)"io.netty.handler.ssl.openssl.useTasks", (boolean)false);
    private static final Integer DH_KEY_LENGTH;
    private static final ResourceLeakDetector<ReferenceCountedOpenSslContext> leakDetector;
    protected static final int VERIFY_DEPTH = 10;
    protected long ctx;
    private final List<String> unmodifiableCiphers;
    private final long sessionCacheSize;
    private final long sessionTimeout;
    private final OpenSslApplicationProtocolNegotiator apn;
    private final int mode;
    private final ResourceLeakTracker<ReferenceCountedOpenSslContext> leak;
    private final AbstractReferenceCounted refCnt = new AbstractReferenceCounted((ReferenceCountedOpenSslContext)this){
        static final /* synthetic */ boolean $assertionsDisabled;
        final /* synthetic */ ReferenceCountedOpenSslContext this$0;
        {
            this.this$0 = this$0;
        }

        public ReferenceCounted touch(Object hint) {
            if (ReferenceCountedOpenSslContext.access$000((ReferenceCountedOpenSslContext)this.this$0) == null) return this.this$0;
            ReferenceCountedOpenSslContext.access$000((ReferenceCountedOpenSslContext)this.this$0).record((Object)hint);
            return this.this$0;
        }

        protected void deallocate() {
            ReferenceCountedOpenSslContext.access$100((ReferenceCountedOpenSslContext)this.this$0);
            if (ReferenceCountedOpenSslContext.access$000((ReferenceCountedOpenSslContext)this.this$0) == null) return;
            boolean closed = ReferenceCountedOpenSslContext.access$000((ReferenceCountedOpenSslContext)this.this$0).close(this.this$0);
            if ($assertionsDisabled) return;
            if (closed) return;
            throw new java.lang.AssertionError();
        }

        static {
            $assertionsDisabled = !ReferenceCountedOpenSslContext.class.desiredAssertionStatus();
        }
    };
    final Certificate[] keyCertChain;
    final ClientAuth clientAuth;
    final String[] protocols;
    final boolean enableOcsp;
    final OpenSslEngineMap engineMap = new DefaultOpenSslEngineMap(null);
    final ReadWriteLock ctxLock = new ReentrantReadWriteLock();
    private volatile int bioNonApplicationBufferSize = ReferenceCountedOpenSslContext.DEFAULT_BIO_NON_APPLICATION_BUFFER_SIZE;
    static final OpenSslApplicationProtocolNegotiator NONE_PROTOCOL_NEGOTIATOR;

    ReferenceCountedOpenSslContext(Iterable<String> ciphers, CipherSuiteFilter cipherFilter, ApplicationProtocolConfig apnCfg, long sessionCacheSize, long sessionTimeout, int mode, Certificate[] keyCertChain, ClientAuth clientAuth, String[] protocols, boolean startTls, boolean enableOcsp, boolean leakDetection) throws SSLException {
        this(ciphers, (CipherSuiteFilter)cipherFilter, (OpenSslApplicationProtocolNegotiator)ReferenceCountedOpenSslContext.toNegotiator((ApplicationProtocolConfig)apnCfg), (long)sessionCacheSize, (long)sessionTimeout, (int)mode, (Certificate[])keyCertChain, (ClientAuth)clientAuth, (String[])protocols, (boolean)startTls, (boolean)enableOcsp, (boolean)leakDetection);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Unable to fully structure code
     * Enabled unnecessary exception pruning
     */
    ReferenceCountedOpenSslContext(Iterable<String> ciphers, CipherSuiteFilter cipherFilter, OpenSslApplicationProtocolNegotiator apn, long sessionCacheSize, long sessionTimeout, int mode, Certificate[] keyCertChain, ClientAuth clientAuth, String[] protocols, boolean startTls, boolean enableOcsp, boolean leakDetection) throws SSLException {
        super((boolean)startTls);
        OpenSsl.ensureAvailability();
        if (enableOcsp && !OpenSsl.isOcspSupported()) {
            throw new IllegalStateException((String)"OCSP is not supported.");
        }
        if (mode != 1 && mode != 0) {
            throw new IllegalArgumentException((String)"mode most be either SSL.SSL_MODE_SERVER or SSL.SSL_MODE_CLIENT");
        }
        this.leak = leakDetection != false ? ReferenceCountedOpenSslContext.leakDetector.track((ReferenceCountedOpenSslContext)this) : null;
        this.mode = mode;
        this.clientAuth = this.isServer() != false ? ObjectUtil.checkNotNull(clientAuth, (String)"clientAuth") : ClientAuth.NONE;
        this.protocols = protocols;
        this.enableOcsp = enableOcsp;
        this.keyCertChain = keyCertChain == null ? null : (Certificate[])keyCertChain.clone();
        this.unmodifiableCiphers = Arrays.asList(ObjectUtil.checkNotNull(cipherFilter, (String)"cipherFilter").filterCipherSuites(ciphers, OpenSsl.DEFAULT_CIPHERS, OpenSsl.availableJavaCipherSuites()));
        this.apn = ObjectUtil.checkNotNull(apn, (String)"apn");
        success = false;
        try {
            try {
                protocolOpts = 30;
                if (OpenSsl.isTlsv13Supported()) {
                    protocolOpts |= 32;
                }
                this.ctx = SSLContext.make((int)protocolOpts, (int)mode);
            }
            catch (Exception e) {
                throw new SSLException((String)"failed to create an SSL_CTX", (Throwable)e);
            }
            tlsv13Supported = OpenSsl.isTlsv13Supported();
            cipherBuilder = new StringBuilder();
            cipherTLSv13Builder = new StringBuilder();
            try {
                if (this.unmodifiableCiphers.isEmpty()) {
                    SSLContext.setCipherSuite((long)this.ctx, (String)"", (boolean)false);
                    if (tlsv13Supported) {
                        SSLContext.setCipherSuite((long)this.ctx, (String)"", (boolean)true);
                    }
                } else {
                    CipherSuiteConverter.convertToCipherStrings(this.unmodifiableCiphers, (StringBuilder)cipherBuilder, (StringBuilder)cipherTLSv13Builder, (boolean)OpenSsl.isBoringSSL());
                    SSLContext.setCipherSuite((long)this.ctx, (String)cipherBuilder.toString(), (boolean)false);
                    if (tlsv13Supported) {
                        SSLContext.setCipherSuite((long)this.ctx, (String)cipherTLSv13Builder.toString(), (boolean)true);
                    }
                }
            }
            catch (SSLException e) {
                throw e;
            }
            catch (Exception e) {
                throw new SSLException((String)("failed to set cipher suite: " + this.unmodifiableCiphers), (Throwable)e);
            }
            options = SSLContext.getOptions((long)this.ctx) | SSL.SSL_OP_NO_SSLv2 | SSL.SSL_OP_NO_SSLv3 | SSL.SSL_OP_NO_TLSv1_3 | SSL.SSL_OP_CIPHER_SERVER_PREFERENCE | SSL.SSL_OP_NO_COMPRESSION | SSL.SSL_OP_NO_TICKET;
            if (cipherBuilder.length() == 0) {
                options |= SSL.SSL_OP_NO_SSLv2 | SSL.SSL_OP_NO_SSLv3 | SSL.SSL_OP_NO_TLSv1 | SSL.SSL_OP_NO_TLSv1_1 | SSL.SSL_OP_NO_TLSv1_2;
            }
            SSLContext.setOptions((long)this.ctx, (int)options);
            SSLContext.setMode((long)this.ctx, (int)(SSLContext.getMode((long)this.ctx) | SSL.SSL_MODE_ACCEPT_MOVING_WRITE_BUFFER));
            if (ReferenceCountedOpenSslContext.DH_KEY_LENGTH != null) {
                SSLContext.setTmpDHLength((long)this.ctx, (int)ReferenceCountedOpenSslContext.DH_KEY_LENGTH.intValue());
            }
            if (!(nextProtoList = apn.protocols()).isEmpty()) {
                appProtocols = nextProtoList.toArray(new String[0]);
                selectorBehavior = ReferenceCountedOpenSslContext.opensslSelectorFailureBehavior((ApplicationProtocolConfig.SelectorFailureBehavior)apn.selectorFailureBehavior());
                switch (3.$SwitchMap$io$netty$handler$ssl$ApplicationProtocolConfig$Protocol[apn.protocol().ordinal()]) {
                    case 1: {
                        SSLContext.setNpnProtos((long)this.ctx, (String[])appProtocols, (int)selectorBehavior);
                        ** break;
                    }
                    case 2: {
                        SSLContext.setAlpnProtos((long)this.ctx, (String[])appProtocols, (int)selectorBehavior);
                        ** break;
                    }
                    case 3: {
                        SSLContext.setNpnProtos((long)this.ctx, (String[])appProtocols, (int)selectorBehavior);
                        SSLContext.setAlpnProtos((long)this.ctx, (String[])appProtocols, (int)selectorBehavior);
                        ** break;
                    }
                }
                throw new Error();
            }
lbl74: // 5 sources:
            if (sessionCacheSize <= 0L) {
                sessionCacheSize = SSLContext.setSessionCacheSize((long)this.ctx, (long)20480L);
            }
            this.sessionCacheSize = sessionCacheSize;
            SSLContext.setSessionCacheSize((long)this.ctx, (long)sessionCacheSize);
            if (sessionTimeout <= 0L) {
                sessionTimeout = SSLContext.setSessionCacheTimeout((long)this.ctx, (long)300L);
            }
            this.sessionTimeout = sessionTimeout;
            SSLContext.setSessionCacheTimeout((long)this.ctx, (long)sessionTimeout);
            if (enableOcsp) {
                SSLContext.enableOcsp((long)this.ctx, (boolean)this.isClient());
            }
            SSLContext.setUseTasks((long)this.ctx, (boolean)ReferenceCountedOpenSslContext.USE_TASKS);
            success = true;
            return;
        }
        finally {
            if (!success) {
                this.release();
            }
        }
    }

    private static int opensslSelectorFailureBehavior(ApplicationProtocolConfig.SelectorFailureBehavior behavior) {
        switch (behavior) {
            case NO_ADVERTISE: {
                return 0;
            }
            case CHOOSE_MY_LAST_PROTOCOL: {
                return 1;
            }
        }
        throw new Error();
    }

    @Override
    public final List<String> cipherSuites() {
        return this.unmodifiableCiphers;
    }

    @Override
    public final long sessionCacheSize() {
        return this.sessionCacheSize;
    }

    @Override
    public final long sessionTimeout() {
        return this.sessionTimeout;
    }

    @Override
    public ApplicationProtocolNegotiator applicationProtocolNegotiator() {
        return this.apn;
    }

    @Override
    public final boolean isClient() {
        if (this.mode != 0) return false;
        return true;
    }

    @Override
    public final SSLEngine newEngine(ByteBufAllocator alloc, String peerHost, int peerPort) {
        return this.newEngine0((ByteBufAllocator)alloc, (String)peerHost, (int)peerPort, (boolean)true);
    }

    @Override
    protected final SslHandler newHandler(ByteBufAllocator alloc, boolean startTls) {
        return new SslHandler((SSLEngine)this.newEngine0((ByteBufAllocator)alloc, null, (int)-1, (boolean)false), (boolean)startTls);
    }

    @Override
    protected final SslHandler newHandler(ByteBufAllocator alloc, String peerHost, int peerPort, boolean startTls) {
        return new SslHandler((SSLEngine)this.newEngine0((ByteBufAllocator)alloc, (String)peerHost, (int)peerPort, (boolean)false), (boolean)startTls);
    }

    @Override
    protected SslHandler newHandler(ByteBufAllocator alloc, boolean startTls, Executor executor) {
        return new SslHandler((SSLEngine)this.newEngine0((ByteBufAllocator)alloc, null, (int)-1, (boolean)false), (boolean)startTls, (Executor)executor);
    }

    @Override
    protected SslHandler newHandler(ByteBufAllocator alloc, String peerHost, int peerPort, boolean startTls, Executor executor) {
        return new SslHandler((SSLEngine)this.newEngine0((ByteBufAllocator)alloc, (String)peerHost, (int)peerPort, (boolean)false), (Executor)executor);
    }

    SSLEngine newEngine0(ByteBufAllocator alloc, String peerHost, int peerPort, boolean jdkCompatibilityMode) {
        return new ReferenceCountedOpenSslEngine((ReferenceCountedOpenSslContext)this, (ByteBufAllocator)alloc, (String)peerHost, (int)peerPort, (boolean)jdkCompatibilityMode, (boolean)true);
    }

    @Override
    public final SSLEngine newEngine(ByteBufAllocator alloc) {
        return this.newEngine((ByteBufAllocator)alloc, null, (int)-1);
    }

    @Deprecated
    public final long context() {
        return this.sslCtxPointer();
    }

    @Deprecated
    public final OpenSslSessionStats stats() {
        return this.sessionContext().stats();
    }

    @Deprecated
    public void setRejectRemoteInitiatedRenegotiation(boolean rejectRemoteInitiatedRenegotiation) {
        if (rejectRemoteInitiatedRenegotiation) return;
        throw new UnsupportedOperationException((String)"Renegotiation is not supported");
    }

    @Deprecated
    public boolean getRejectRemoteInitiatedRenegotiation() {
        return true;
    }

    public void setBioNonApplicationBufferSize(int bioNonApplicationBufferSize) {
        this.bioNonApplicationBufferSize = ObjectUtil.checkPositiveOrZero((int)bioNonApplicationBufferSize, (String)"bioNonApplicationBufferSize");
    }

    public int getBioNonApplicationBufferSize() {
        return this.bioNonApplicationBufferSize;
    }

    @Deprecated
    public final void setTicketKeys(byte[] keys) {
        this.sessionContext().setTicketKeys((byte[])keys);
    }

    @Override
    public abstract OpenSslSessionContext sessionContext();

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Deprecated
    public final long sslCtxPointer() {
        Lock readerLock = this.ctxLock.readLock();
        readerLock.lock();
        try {
            long l = SSLContext.getSslCtx((long)this.ctx);
            return l;
        }
        finally {
            readerLock.unlock();
        }
    }

    public final void setPrivateKeyMethod(OpenSslPrivateKeyMethod method) {
        ObjectUtil.checkNotNull(method, (String)"method");
        Lock writerLock = this.ctxLock.writeLock();
        writerLock.lock();
        try {
            SSLContext.setPrivateKeyMethod((long)this.ctx, (SSLPrivateKeyMethod)new PrivateKeyMethod((OpenSslEngineMap)this.engineMap, (OpenSslPrivateKeyMethod)method));
            return;
        }
        finally {
            writerLock.unlock();
        }
    }

    final void setUseTasks(boolean useTasks) {
        Lock writerLock = this.ctxLock.writeLock();
        writerLock.lock();
        try {
            SSLContext.setUseTasks((long)this.ctx, (boolean)useTasks);
            return;
        }
        finally {
            writerLock.unlock();
        }
    }

    private void destroy() {
        Lock writerLock = this.ctxLock.writeLock();
        writerLock.lock();
        try {
            if (this.ctx == 0L) return;
            if (this.enableOcsp) {
                SSLContext.disableOcsp((long)this.ctx);
            }
            SSLContext.free((long)this.ctx);
            this.ctx = 0L;
            OpenSslSessionContext context = this.sessionContext();
            if (context == null) return;
            context.destroy();
            return;
        }
        finally {
            writerLock.unlock();
        }
    }

    protected static X509Certificate[] certificates(byte[][] chain) {
        X509Certificate[] peerCerts = new X509Certificate[chain.length];
        int i = 0;
        while (i < peerCerts.length) {
            peerCerts[i] = new OpenSslX509Certificate((byte[])chain[i]);
            ++i;
        }
        return peerCerts;
    }

    protected static X509TrustManager chooseTrustManager(TrustManager[] managers) {
        TrustManager[] arrtrustManager = managers;
        int n = arrtrustManager.length;
        int n2 = 0;
        while (n2 < n) {
            TrustManager m = arrtrustManager[n2];
            if (m instanceof X509TrustManager) {
                if (PlatformDependent.javaVersion() < 7) return (X509TrustManager)m;
                return OpenSslX509TrustManagerWrapper.wrapIfNeeded((X509TrustManager)((X509TrustManager)m));
            }
            ++n2;
        }
        throw new IllegalStateException((String)"no X509TrustManager found");
    }

    protected static X509KeyManager chooseX509KeyManager(KeyManager[] kms) {
        KeyManager[] arrkeyManager = kms;
        int n = arrkeyManager.length;
        int n2 = 0;
        while (n2 < n) {
            KeyManager km = arrkeyManager[n2];
            if (km instanceof X509KeyManager) {
                return (X509KeyManager)km;
            }
            ++n2;
        }
        throw new IllegalStateException((String)"no X509KeyManager found");
    }

    static OpenSslApplicationProtocolNegotiator toNegotiator(ApplicationProtocolConfig config) {
        if (config == null) {
            return NONE_PROTOCOL_NEGOTIATOR;
        }
        switch (3.$SwitchMap$io$netty$handler$ssl$ApplicationProtocolConfig$Protocol[config.protocol().ordinal()]) {
            case 4: {
                return NONE_PROTOCOL_NEGOTIATOR;
            }
            case 1: 
            case 2: 
            case 3: {
                switch (config.selectedListenerFailureBehavior()) {
                    case CHOOSE_MY_LAST_PROTOCOL: 
                    case ACCEPT: {
                        switch (config.selectorFailureBehavior()) {
                            case NO_ADVERTISE: 
                            case CHOOSE_MY_LAST_PROTOCOL: {
                                return new OpenSslDefaultApplicationProtocolNegotiator((ApplicationProtocolConfig)config);
                            }
                        }
                        throw new UnsupportedOperationException((String)("OpenSSL provider does not support " + (Object)((Object)config.selectorFailureBehavior()) + " behavior"));
                    }
                }
                throw new UnsupportedOperationException((String)("OpenSSL provider does not support " + (Object)((Object)config.selectedListenerFailureBehavior()) + " behavior"));
            }
        }
        throw new Error();
    }

    @SuppressJava6Requirement(reason="Guarded by java version check")
    static boolean useExtendedTrustManager(X509TrustManager trustManager) {
        if (PlatformDependent.javaVersion() < 7) return false;
        if (!(trustManager instanceof X509ExtendedTrustManager)) return false;
        return true;
    }

    @Override
    public final int refCnt() {
        return this.refCnt.refCnt();
    }

    @Override
    public final ReferenceCounted retain() {
        this.refCnt.retain();
        return this;
    }

    @Override
    public final ReferenceCounted retain(int increment) {
        this.refCnt.retain((int)increment);
        return this;
    }

    @Override
    public final ReferenceCounted touch() {
        this.refCnt.touch();
        return this;
    }

    @Override
    public final ReferenceCounted touch(Object hint) {
        this.refCnt.touch((Object)hint);
        return this;
    }

    @Override
    public final boolean release() {
        return this.refCnt.release();
    }

    @Override
    public final boolean release(int decrement) {
        return this.refCnt.release((int)decrement);
    }

    static void setKeyMaterial(long ctx, X509Certificate[] keyCertChain, PrivateKey key, String keyPassword) throws SSLException {
        long keyBio = 0L;
        long keyCertChainBio = 0L;
        long keyCertChainBio2 = 0L;
        PemEncoded encoded = null;
        try {
            encoded = PemX509Certificate.toPEM((ByteBufAllocator)ByteBufAllocator.DEFAULT, (boolean)true, (X509Certificate[])keyCertChain);
            keyCertChainBio = ReferenceCountedOpenSslContext.toBIO((ByteBufAllocator)ByteBufAllocator.DEFAULT, (PemEncoded)encoded.retain());
            keyCertChainBio2 = ReferenceCountedOpenSslContext.toBIO((ByteBufAllocator)ByteBufAllocator.DEFAULT, (PemEncoded)encoded.retain());
            if (key != null) {
                keyBio = ReferenceCountedOpenSslContext.toBIO((ByteBufAllocator)ByteBufAllocator.DEFAULT, (PrivateKey)key);
            }
            SSLContext.setCertificateBio((long)ctx, (long)keyCertChainBio, (long)keyBio, (String)(keyPassword == null ? "" : keyPassword));
            SSLContext.setCertificateChainBio((long)ctx, (long)keyCertChainBio2, (boolean)true);
            return;
        }
        catch (SSLException e) {
            throw e;
        }
        catch (Exception e) {
            throw new SSLException((String)"failed to set certificate and key", (Throwable)e);
        }
        finally {
            ReferenceCountedOpenSslContext.freeBio((long)keyBio);
            ReferenceCountedOpenSslContext.freeBio((long)keyCertChainBio);
            ReferenceCountedOpenSslContext.freeBio((long)keyCertChainBio2);
            if (encoded != null) {
                encoded.release();
            }
        }
    }

    static void freeBio(long bio) {
        if (bio == 0L) return;
        SSL.freeBIO((long)bio);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    static long toBIO(ByteBufAllocator allocator, PrivateKey key) throws Exception {
        if (key == null) {
            return 0L;
        }
        PemEncoded pem = PemPrivateKey.toPEM((ByteBufAllocator)allocator, (boolean)true, (PrivateKey)key);
        try {
            long l = ReferenceCountedOpenSslContext.toBIO((ByteBufAllocator)allocator, (PemEncoded)pem.retain());
            return l;
        }
        finally {
            pem.release();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    static long toBIO(ByteBufAllocator allocator, X509Certificate ... certChain) throws Exception {
        if (certChain == null) {
            return 0L;
        }
        if (certChain.length == 0) {
            throw new IllegalArgumentException((String)"certChain can't be empty");
        }
        PemEncoded pem = PemX509Certificate.toPEM((ByteBufAllocator)allocator, (boolean)true, (X509Certificate[])certChain);
        try {
            long l = ReferenceCountedOpenSslContext.toBIO((ByteBufAllocator)allocator, (PemEncoded)pem.retain());
            return l;
        }
        finally {
            pem.release();
        }
    }

    /*
     * Exception decompiling
     */
    static long toBIO(ByteBufAllocator allocator, PemEncoded pem) throws Exception {
        // This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
        // org.benf.cfr.reader.util.ConfusedCFRException: Tried to end blocks [0[TRYBLOCK]], but top level block is 7[CATCHBLOCK]
        // org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.processEndingBlocks(Op04StructuredStatement.java:427)
        // org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.buildNestedBlocks(Op04StructuredStatement.java:479)
        // org.benf.cfr.reader.bytecode.analysis.opgraph.Op03SimpleStatement.createInitialStructuredBlock(Op03SimpleStatement.java:607)
        // org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:696)
        // org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:184)
        // org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:129)
        // org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:96)
        // org.benf.cfr.reader.entities.Method.analyse(Method.java:397)
        // org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:906)
        // org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:797)
        // org.benf.cfr.reader.Driver.doJarVersionTypes(Driver.java:225)
        // org.benf.cfr.reader.Driver.doJar(Driver.java:109)
        // org.benf.cfr.reader.CfrDriverImpl.analyse(CfrDriverImpl.java:65)
        // org.benf.cfr.reader.Main.main(Main.java:48)
        // the.bytecode.club.bytecodeviewer.decompilers.CFRDecompiler.decompileToZip(CFRDecompiler.java:311)
        // the.bytecode.club.bytecodeviewer.gui.MainViewerGUI$14$1$7.run(MainViewerGUI.java:1287)
        throw new IllegalStateException("Decompilation failed");
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static long newBIO(ByteBuf buffer) throws Exception {
        try {
            long bio = SSL.newMemBIO();
            int readable = buffer.readableBytes();
            if (SSL.bioWrite((long)bio, (long)(OpenSsl.memoryAddress((ByteBuf)buffer) + (long)buffer.readerIndex()), (int)readable) != readable) {
                SSL.freeBIO((long)bio);
                throw new IllegalStateException((String)"Could not write data to memory BIO");
            }
            long l = bio;
            return l;
        }
        finally {
            buffer.release();
        }
    }

    static OpenSslKeyMaterialProvider providerFor(KeyManagerFactory factory, String password) {
        if (factory instanceof OpenSslX509KeyManagerFactory) {
            return ((OpenSslX509KeyManagerFactory)factory).newProvider();
        }
        X509KeyManager keyManager = ReferenceCountedOpenSslContext.chooseX509KeyManager((KeyManager[])factory.getKeyManagers());
        if (!(factory instanceof OpenSslCachingX509KeyManagerFactory)) return new OpenSslKeyMaterialProvider((X509KeyManager)keyManager, (String)password);
        return new OpenSslCachingKeyMaterialProvider((X509KeyManager)keyManager, (String)password);
    }

    static /* synthetic */ ResourceLeakTracker access$000(ReferenceCountedOpenSslContext x0) {
        return x0.leak;
    }

    static /* synthetic */ void access$100(ReferenceCountedOpenSslContext x0) {
        x0.destroy();
    }

    static /* synthetic */ InternalLogger access$300() {
        return logger;
    }

    static {
        leakDetector = ResourceLeakDetectorFactory.instance().newResourceLeakDetector(ReferenceCountedOpenSslContext.class);
        NONE_PROTOCOL_NEGOTIATOR = new OpenSslApplicationProtocolNegotiator(){

            public ApplicationProtocolConfig.Protocol protocol() {
                return ApplicationProtocolConfig.Protocol.NONE;
            }

            public List<String> protocols() {
                return java.util.Collections.emptyList();
            }

            public ApplicationProtocolConfig.SelectorFailureBehavior selectorFailureBehavior() {
                return ApplicationProtocolConfig.SelectorFailureBehavior.CHOOSE_MY_LAST_PROTOCOL;
            }

            public ApplicationProtocolConfig.SelectedListenerFailureBehavior selectedListenerFailureBehavior() {
                return ApplicationProtocolConfig.SelectedListenerFailureBehavior.ACCEPT;
            }
        };
        Integer dhLen = null;
        try {
            String dhKeySize = SystemPropertyUtil.get((String)"jdk.tls.ephemeralDHKeySize");
            if (dhKeySize != null) {
                try {
                    dhLen = Integer.valueOf((String)dhKeySize);
                }
                catch (NumberFormatException e) {
                    logger.debug((String)("ReferenceCountedOpenSslContext supports -Djdk.tls.ephemeralDHKeySize={int}, but got: " + dhKeySize));
                }
            }
        }
        catch (Throwable dhKeySize) {
            // empty catch block
        }
        DH_KEY_LENGTH = dhLen;
    }
}

