/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  io.netty.internal.tcnative.Buffer
 *  io.netty.internal.tcnative.SSL
 */
package io.netty.handler.ssl;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.handler.ssl.ApplicationProtocolAccessor;
import io.netty.handler.ssl.ApplicationProtocolNegotiator;
import io.netty.handler.ssl.CipherSuiteConverter;
import io.netty.handler.ssl.ClientAuth;
import io.netty.handler.ssl.ExtendedOpenSslSession;
import io.netty.handler.ssl.Java7SslParametersUtils;
import io.netty.handler.ssl.Java8SslUtils;
import io.netty.handler.ssl.OpenSsl;
import io.netty.handler.ssl.OpenSslApplicationProtocolNegotiator;
import io.netty.handler.ssl.OpenSslEngineMap;
import io.netty.handler.ssl.OpenSslKeyMaterial;
import io.netty.handler.ssl.OpenSslSession;
import io.netty.handler.ssl.OpenSslSessionContext;
import io.netty.handler.ssl.ReferenceCountedOpenSslContext;
import io.netty.handler.ssl.ReferenceCountedOpenSslEngine;
import io.netty.handler.ssl.SslUtils;
import io.netty.internal.tcnative.Buffer;
import io.netty.internal.tcnative.SSL;
import io.netty.util.AbstractReferenceCounted;
import io.netty.util.ReferenceCounted;
import io.netty.util.ResourceLeakDetector;
import io.netty.util.ResourceLeakDetectorFactory;
import io.netty.util.ResourceLeakTracker;
import io.netty.util.internal.EmptyArrays;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.SuppressJava6Requirement;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.nio.ByteBuffer;
import java.nio.ReadOnlyBufferException;
import java.security.AlgorithmConstraints;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.SNIMatcher;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLEngineResult;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLSession;

public class ReferenceCountedOpenSslEngine
extends SSLEngine
implements ReferenceCounted,
ApplicationProtocolAccessor {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(ReferenceCountedOpenSslEngine.class);
    private static final ResourceLeakDetector<ReferenceCountedOpenSslEngine> leakDetector = ResourceLeakDetectorFactory.instance().newResourceLeakDetector(ReferenceCountedOpenSslEngine.class);
    private static final int OPENSSL_OP_NO_PROTOCOL_INDEX_SSLV2 = 0;
    private static final int OPENSSL_OP_NO_PROTOCOL_INDEX_SSLV3 = 1;
    private static final int OPENSSL_OP_NO_PROTOCOL_INDEX_TLSv1 = 2;
    private static final int OPENSSL_OP_NO_PROTOCOL_INDEX_TLSv1_1 = 3;
    private static final int OPENSSL_OP_NO_PROTOCOL_INDEX_TLSv1_2 = 4;
    private static final int OPENSSL_OP_NO_PROTOCOL_INDEX_TLSv1_3 = 5;
    private static final int[] OPENSSL_OP_NO_PROTOCOLS = new int[]{SSL.SSL_OP_NO_SSLv2, SSL.SSL_OP_NO_SSLv3, SSL.SSL_OP_NO_TLSv1, SSL.SSL_OP_NO_TLSv1_1, SSL.SSL_OP_NO_TLSv1_2, SSL.SSL_OP_NO_TLSv1_3};
    static final int MAX_PLAINTEXT_LENGTH = SSL.SSL_MAX_PLAINTEXT_LENGTH;
    private static final int MAX_RECORD_SIZE = SSL.SSL_MAX_RECORD_LENGTH;
    private static final SSLEngineResult NEED_UNWRAP_OK = new SSLEngineResult((SSLEngineResult.Status)SSLEngineResult.Status.OK, (SSLEngineResult.HandshakeStatus)SSLEngineResult.HandshakeStatus.NEED_UNWRAP, (int)0, (int)0);
    private static final SSLEngineResult NEED_UNWRAP_CLOSED = new SSLEngineResult((SSLEngineResult.Status)SSLEngineResult.Status.CLOSED, (SSLEngineResult.HandshakeStatus)SSLEngineResult.HandshakeStatus.NEED_UNWRAP, (int)0, (int)0);
    private static final SSLEngineResult NEED_WRAP_OK = new SSLEngineResult((SSLEngineResult.Status)SSLEngineResult.Status.OK, (SSLEngineResult.HandshakeStatus)SSLEngineResult.HandshakeStatus.NEED_WRAP, (int)0, (int)0);
    private static final SSLEngineResult NEED_WRAP_CLOSED = new SSLEngineResult((SSLEngineResult.Status)SSLEngineResult.Status.CLOSED, (SSLEngineResult.HandshakeStatus)SSLEngineResult.HandshakeStatus.NEED_WRAP, (int)0, (int)0);
    private static final SSLEngineResult CLOSED_NOT_HANDSHAKING = new SSLEngineResult((SSLEngineResult.Status)SSLEngineResult.Status.CLOSED, (SSLEngineResult.HandshakeStatus)SSLEngineResult.HandshakeStatus.NOT_HANDSHAKING, (int)0, (int)0);
    private long ssl;
    private long networkBIO;
    private HandshakeState handshakeState = HandshakeState.NOT_STARTED;
    private boolean receivedShutdown;
    private volatile boolean destroyed;
    private volatile String applicationProtocol;
    private volatile boolean needTask;
    private final ResourceLeakTracker<ReferenceCountedOpenSslEngine> leak;
    private final AbstractReferenceCounted refCnt = new AbstractReferenceCounted((ReferenceCountedOpenSslEngine)this){
        static final /* synthetic */ boolean $assertionsDisabled;
        final /* synthetic */ ReferenceCountedOpenSslEngine this$0;
        {
            this.this$0 = this$0;
        }

        public ReferenceCounted touch(Object hint) {
            if (ReferenceCountedOpenSslEngine.access$000((ReferenceCountedOpenSslEngine)this.this$0) == null) return this.this$0;
            ReferenceCountedOpenSslEngine.access$000((ReferenceCountedOpenSslEngine)this.this$0).record((Object)hint);
            return this.this$0;
        }

        protected void deallocate() {
            this.this$0.shutdown();
            if (ReferenceCountedOpenSslEngine.access$000((ReferenceCountedOpenSslEngine)this.this$0) != null) {
                boolean closed = ReferenceCountedOpenSslEngine.access$000((ReferenceCountedOpenSslEngine)this.this$0).close(this.this$0);
                if (!$assertionsDisabled && !closed) {
                    throw new java.lang.AssertionError();
                }
            }
            ReferenceCountedOpenSslEngine.access$100((ReferenceCountedOpenSslEngine)this.this$0).release();
        }

        static {
            $assertionsDisabled = !ReferenceCountedOpenSslEngine.class.desiredAssertionStatus();
        }
    };
    private volatile ClientAuth clientAuth = ClientAuth.NONE;
    private volatile Certificate[] localCertificateChain;
    private volatile long lastAccessed = -1L;
    private String endPointIdentificationAlgorithm;
    private Object algorithmConstraints;
    private List<String> sniHostNames;
    private volatile Collection<?> matchers;
    private boolean isInboundDone;
    private boolean outboundClosed;
    final boolean jdkCompatibilityMode;
    private final boolean clientMode;
    final ByteBufAllocator alloc;
    private final OpenSslEngineMap engineMap;
    private final OpenSslApplicationProtocolNegotiator apn;
    private final ReferenceCountedOpenSslContext parentContext;
    private final OpenSslSession session;
    private final ByteBuffer[] singleSrcBuffer = new ByteBuffer[1];
    private final ByteBuffer[] singleDstBuffer = new ByteBuffer[1];
    private final boolean enableOcsp;
    private int maxWrapOverhead;
    private int maxWrapBufferSize;
    private Throwable handshakeException;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    ReferenceCountedOpenSslEngine(ReferenceCountedOpenSslContext context, ByteBufAllocator alloc, String peerHost, int peerPort, boolean jdkCompatibilityMode, boolean leakDetection) {
        super((String)peerHost, (int)peerPort);
        long finalSsl;
        OpenSsl.ensureAvailability();
        this.alloc = ObjectUtil.checkNotNull(alloc, (String)"alloc");
        this.apn = (OpenSslApplicationProtocolNegotiator)context.applicationProtocolNegotiator();
        this.clientMode = context.isClient();
        this.session = PlatformDependent.javaVersion() >= 7 ? new ExtendedOpenSslSession((ReferenceCountedOpenSslEngine)this, (OpenSslSession)new DefaultOpenSslSession((ReferenceCountedOpenSslEngine)this, (OpenSslSessionContext)context.sessionContext())){
            private String[] peerSupportedSignatureAlgorithms;
            private List requestedServerNames;
            final /* synthetic */ ReferenceCountedOpenSslEngine this$0;
            {
                this.this$0 = this$0;
                super((OpenSslSession)wrapped);
            }

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            public List getRequestedServerNames() {
                if (ReferenceCountedOpenSslEngine.access$200((ReferenceCountedOpenSslEngine)this.this$0)) {
                    return Java8SslUtils.getSniHostNames((List<String>)ReferenceCountedOpenSslEngine.access$300((ReferenceCountedOpenSslEngine)this.this$0));
                }
                ReferenceCountedOpenSslEngine referenceCountedOpenSslEngine = this.this$0;
                // MONITORENTER : referenceCountedOpenSslEngine
                if (this.requestedServerNames == null) {
                    if (ReferenceCountedOpenSslEngine.access$400((ReferenceCountedOpenSslEngine)this.this$0)) {
                        this.requestedServerNames = Collections.emptyList();
                        return this.requestedServerNames;
                    }
                    String name = SSL.getSniHostname((long)ReferenceCountedOpenSslEngine.access$500((ReferenceCountedOpenSslEngine)this.this$0));
                    if (name == null) {
                        this.requestedServerNames = Collections.emptyList();
                        return this.requestedServerNames;
                    }
                    this.requestedServerNames = Java8SslUtils.getSniHostName((byte[])SSL.getSniHostname((long)ReferenceCountedOpenSslEngine.access$500((ReferenceCountedOpenSslEngine)this.this$0)).getBytes((java.nio.charset.Charset)io.netty.util.CharsetUtil.UTF_8));
                }
                // MONITOREXIT : referenceCountedOpenSslEngine
                return this.requestedServerNames;
            }

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            public String[] getPeerSupportedSignatureAlgorithms() {
                ReferenceCountedOpenSslEngine referenceCountedOpenSslEngine = this.this$0;
                // MONITORENTER : referenceCountedOpenSslEngine
                if (this.peerSupportedSignatureAlgorithms == null) {
                    if (ReferenceCountedOpenSslEngine.access$400((ReferenceCountedOpenSslEngine)this.this$0)) {
                        this.peerSupportedSignatureAlgorithms = EmptyArrays.EMPTY_STRINGS;
                        return (String[])this.peerSupportedSignatureAlgorithms.clone();
                    }
                    String[] algs = SSL.getSigAlgs((long)ReferenceCountedOpenSslEngine.access$500((ReferenceCountedOpenSslEngine)this.this$0));
                    if (algs == null) {
                        this.peerSupportedSignatureAlgorithms = EmptyArrays.EMPTY_STRINGS;
                        return (String[])this.peerSupportedSignatureAlgorithms.clone();
                    }
                    java.util.LinkedHashSet<String> algorithmList = new java.util.LinkedHashSet<String>((int)algs.length);
                    for (String alg : algs) {
                        String converted = io.netty.handler.ssl.SignatureAlgorithmConverter.toJavaName((String)alg);
                        if (converted == null) continue;
                        algorithmList.add(converted);
                    }
                    this.peerSupportedSignatureAlgorithms = algorithmList.toArray(new String[0]);
                }
                // MONITOREXIT : referenceCountedOpenSslEngine
                return (String[])this.peerSupportedSignatureAlgorithms.clone();
            }

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            public List<byte[]> getStatusResponses() {
                List<byte[]> list;
                byte[] ocspResponse = null;
                if (ReferenceCountedOpenSslEngine.access$600((ReferenceCountedOpenSslEngine)this.this$0) && ReferenceCountedOpenSslEngine.access$200((ReferenceCountedOpenSslEngine)this.this$0)) {
                    ReferenceCountedOpenSslEngine referenceCountedOpenSslEngine = this.this$0;
                    // MONITORENTER : referenceCountedOpenSslEngine
                    if (!ReferenceCountedOpenSslEngine.access$400((ReferenceCountedOpenSslEngine)this.this$0)) {
                        ocspResponse = SSL.getOcspResponse((long)ReferenceCountedOpenSslEngine.access$500((ReferenceCountedOpenSslEngine)this.this$0));
                    }
                    // MONITOREXIT : referenceCountedOpenSslEngine
                }
                if (ocspResponse == null) {
                    list = Collections.emptyList();
                    return list;
                }
                list = Collections.singletonList(ocspResponse);
                return list;
            }
        } : new DefaultOpenSslSession((ReferenceCountedOpenSslEngine)this, (OpenSslSessionContext)context.sessionContext());
        this.engineMap = context.engineMap;
        this.enableOcsp = context.enableOcsp;
        this.localCertificateChain = context.keyCertChain;
        this.jdkCompatibilityMode = jdkCompatibilityMode;
        Lock readerLock = context.ctxLock.readLock();
        readerLock.lock();
        try {
            finalSsl = SSL.newSSL((long)context.ctx, (boolean)(!context.isClient()));
        }
        finally {
            readerLock.unlock();
        }
        ReferenceCountedOpenSslEngine referenceCountedOpenSslEngine = this;
        // MONITORENTER : referenceCountedOpenSslEngine
        this.ssl = finalSsl;
        try {
            this.networkBIO = SSL.bioNewByteBuffer((long)this.ssl, (int)context.getBioNonApplicationBufferSize());
            this.setClientAuth((ClientAuth)(this.clientMode ? ClientAuth.NONE : context.clientAuth));
            if (context.protocols != null) {
                this.setEnabledProtocols((String[])context.protocols);
            }
            if (this.clientMode && SslUtils.isValidHostNameForSNI((String)peerHost)) {
                SSL.setTlsExtHostName((long)this.ssl, (String)peerHost);
                this.sniHostNames = Collections.singletonList(peerHost);
            }
            if (this.enableOcsp) {
                SSL.enableOcsp((long)this.ssl);
            }
            if (!jdkCompatibilityMode) {
                SSL.setMode((long)this.ssl, (int)(SSL.getMode((long)this.ssl) | SSL.SSL_MODE_ENABLE_PARTIAL_WRITE));
            }
            this.calculateMaxWrapOverhead();
        }
        catch (Throwable cause) {
            this.shutdown();
            PlatformDependent.throwException((Throwable)cause);
        }
        this.parentContext = context;
        this.parentContext.retain();
        this.leak = leakDetection ? leakDetector.track((ReferenceCountedOpenSslEngine)this) : null;
    }

    final synchronized String[] authMethods() {
        if (!this.isDestroyed()) return SSL.authenticationMethods((long)this.ssl);
        return EmptyArrays.EMPTY_STRINGS;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    final boolean setKeyMaterial(OpenSslKeyMaterial keyMaterial) throws Exception {
        ReferenceCountedOpenSslEngine referenceCountedOpenSslEngine = this;
        // MONITORENTER : referenceCountedOpenSslEngine
        if (this.isDestroyed()) {
            // MONITOREXIT : referenceCountedOpenSslEngine
            return false;
        }
        SSL.setKeyMaterial((long)this.ssl, (long)keyMaterial.certificateChainAddress(), (long)keyMaterial.privateKeyAddress());
        // MONITOREXIT : referenceCountedOpenSslEngine
        this.localCertificateChain = keyMaterial.certificateChain();
        return true;
    }

    final synchronized SecretKeySpec masterKey() {
        if (!this.isDestroyed()) return new SecretKeySpec((byte[])SSL.getMasterKey((long)this.ssl), (String)"AES");
        return null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void setOcspResponse(byte[] response) {
        if (!this.enableOcsp) {
            throw new IllegalStateException((String)"OCSP stapling is not enabled");
        }
        if (this.clientMode) {
            throw new IllegalStateException((String)"Not a server SSLEngine");
        }
        ReferenceCountedOpenSslEngine referenceCountedOpenSslEngine = this;
        // MONITORENTER : referenceCountedOpenSslEngine
        if (!this.isDestroyed()) {
            SSL.setOcspResponse((long)this.ssl, (byte[])response);
        }
        // MONITOREXIT : referenceCountedOpenSslEngine
        return;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public byte[] getOcspResponse() {
        if (!this.enableOcsp) {
            throw new IllegalStateException((String)"OCSP stapling is not enabled");
        }
        if (!this.clientMode) {
            throw new IllegalStateException((String)"Not a client SSLEngine");
        }
        ReferenceCountedOpenSslEngine referenceCountedOpenSslEngine = this;
        // MONITORENTER : referenceCountedOpenSslEngine
        if (this.isDestroyed()) {
            // MONITOREXIT : referenceCountedOpenSslEngine
            return EmptyArrays.EMPTY_BYTES;
        }
        // MONITOREXIT : referenceCountedOpenSslEngine
        return SSL.getOcspResponse((long)this.ssl);
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

    @Override
    public final synchronized SSLSession getHandshakeSession() {
        switch (this.handshakeState) {
            case NOT_STARTED: 
            case FINISHED: {
                return null;
            }
        }
        return this.session;
    }

    public final synchronized long sslPointer() {
        return this.ssl;
    }

    public final synchronized void shutdown() {
        if (!this.destroyed) {
            this.destroyed = true;
            this.engineMap.remove((long)this.ssl);
            SSL.freeSSL((long)this.ssl);
            this.networkBIO = 0L;
            this.ssl = 0L;
            this.outboundClosed = true;
            this.isInboundDone = true;
        }
        SSL.clearError();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private int writePlaintextData(ByteBuffer src, int len) {
        int sslWrote;
        int pos = src.position();
        int limit = src.limit();
        if (src.isDirect()) {
            sslWrote = SSL.writeToSSL((long)this.ssl, (long)(ReferenceCountedOpenSslEngine.bufferAddress((ByteBuffer)src) + (long)pos), (int)len);
            if (sslWrote <= 0) return sslWrote;
            src.position((int)(pos + sslWrote));
            return sslWrote;
        }
        ByteBuf buf = this.alloc.directBuffer((int)len);
        try {
            src.limit((int)(pos + len));
            buf.setBytes((int)0, (ByteBuffer)src);
            src.limit((int)limit);
            sslWrote = SSL.writeToSSL((long)this.ssl, (long)OpenSsl.memoryAddress((ByteBuf)buf), (int)len);
            if (sslWrote > 0) {
                src.position((int)(pos + sslWrote));
                return sslWrote;
            }
            src.position((int)pos);
            return sslWrote;
        }
        finally {
            buf.release();
        }
    }

    private ByteBuf writeEncryptedData(ByteBuffer src, int len) {
        int pos = src.position();
        if (src.isDirect()) {
            SSL.bioSetByteBuffer((long)this.networkBIO, (long)(ReferenceCountedOpenSslEngine.bufferAddress((ByteBuffer)src) + (long)pos), (int)len, (boolean)false);
            return null;
        }
        ByteBuf buf = this.alloc.directBuffer((int)len);
        try {
            int limit = src.limit();
            src.limit((int)(pos + len));
            buf.writeBytes((ByteBuffer)src);
            src.position((int)pos);
            src.limit((int)limit);
            SSL.bioSetByteBuffer((long)this.networkBIO, (long)OpenSsl.memoryAddress((ByteBuf)buf), (int)len, (boolean)false);
            return buf;
        }
        catch (Throwable cause) {
            buf.release();
            PlatformDependent.throwException((Throwable)cause);
        }
        return null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private int readPlaintextData(ByteBuffer dst) {
        int sslRead;
        int pos = dst.position();
        if (dst.isDirect()) {
            sslRead = SSL.readFromSSL((long)this.ssl, (long)(ReferenceCountedOpenSslEngine.bufferAddress((ByteBuffer)dst) + (long)pos), (int)(dst.limit() - pos));
            if (sslRead <= 0) return sslRead;
            dst.position((int)(pos + sslRead));
            return sslRead;
        }
        int limit = dst.limit();
        int len = Math.min((int)this.maxEncryptedPacketLength0(), (int)(limit - pos));
        ByteBuf buf = this.alloc.directBuffer((int)len);
        try {
            sslRead = SSL.readFromSSL((long)this.ssl, (long)OpenSsl.memoryAddress((ByteBuf)buf), (int)len);
            if (sslRead <= 0) return sslRead;
            dst.limit((int)(pos + sslRead));
            buf.getBytes((int)buf.readerIndex(), (ByteBuffer)dst);
            dst.limit((int)limit);
            return sslRead;
        }
        finally {
            buf.release();
        }
    }

    final synchronized int maxWrapOverhead() {
        return this.maxWrapOverhead;
    }

    final synchronized int maxEncryptedPacketLength() {
        return this.maxEncryptedPacketLength0();
    }

    final int maxEncryptedPacketLength0() {
        return this.maxWrapOverhead + MAX_PLAINTEXT_LENGTH;
    }

    final int calculateMaxLengthForWrap(int plaintextLength, int numComponents) {
        return (int)Math.min((long)((long)this.maxWrapBufferSize), (long)((long)plaintextLength + (long)this.maxWrapOverhead * (long)numComponents));
    }

    final synchronized int sslPending() {
        return this.sslPending0();
    }

    private void calculateMaxWrapOverhead() {
        this.maxWrapOverhead = SSL.getMaxWrapOverhead((long)this.ssl);
        this.maxWrapBufferSize = this.jdkCompatibilityMode ? this.maxEncryptedPacketLength0() : this.maxEncryptedPacketLength0() << 4;
    }

    private int sslPending0() {
        if (this.handshakeState != HandshakeState.FINISHED) {
            return 0;
        }
        int n = SSL.sslPending((long)this.ssl);
        return n;
    }

    private boolean isBytesAvailableEnoughForWrap(int bytesAvailable, int plaintextLength, int numComponents) {
        if ((long)bytesAvailable - (long)this.maxWrapOverhead * (long)numComponents < (long)plaintextLength) return false;
        return true;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled unnecessary exception pruning
     */
    @Override
    public final SSLEngineResult wrap(ByteBuffer[] srcs, int offset, int length, ByteBuffer dst) throws SSLException {
        if (srcs == null) {
            throw new IllegalArgumentException((String)"srcs is null");
        }
        if (dst == null) {
            throw new IllegalArgumentException((String)"dst is null");
        }
        if (offset >= srcs.length) throw new IndexOutOfBoundsException((String)("offset: " + offset + ", length: " + length + " (expected: offset <= offset + length <= srcs.length (" + srcs.length + "))"));
        if (offset + length > srcs.length) {
            throw new IndexOutOfBoundsException((String)("offset: " + offset + ", length: " + length + " (expected: offset <= offset + length <= srcs.length (" + srcs.length + "))"));
        }
        if (dst.isReadOnly()) {
            throw new ReadOnlyBufferException();
        }
        ReferenceCountedOpenSslEngine referenceCountedOpenSslEngine = this;
        // MONITORENTER : referenceCountedOpenSslEngine
        if (this.isOutboundDone()) {
            SSLEngineResult sSLEngineResult;
            if (!this.isInboundDone() && !this.isDestroyed()) {
                sSLEngineResult = NEED_UNWRAP_CLOSED;
                // MONITOREXIT : referenceCountedOpenSslEngine
                return sSLEngineResult;
            }
            sSLEngineResult = CLOSED_NOT_HANDSHAKING;
            return sSLEngineResult;
        }
        int bytesProduced = 0;
        ByteBuf bioReadCopyBuf = null;
        try {
            Object src;
            if (dst.isDirect()) {
                SSL.bioSetByteBuffer((long)this.networkBIO, (long)(ReferenceCountedOpenSslEngine.bufferAddress((ByteBuffer)dst) + (long)dst.position()), (int)dst.remaining(), (boolean)true);
            } else {
                bioReadCopyBuf = this.alloc.directBuffer((int)dst.remaining());
                SSL.bioSetByteBuffer((long)this.networkBIO, (long)OpenSsl.memoryAddress((ByteBuf)bioReadCopyBuf), (int)bioReadCopyBuf.writableBytes(), (boolean)true);
            }
            int bioLengthBefore = SSL.bioLengthByteBuffer((long)this.networkBIO);
            if (this.outboundClosed) {
                if (!this.isBytesAvailableEnoughForWrap((int)dst.remaining(), (int)2, (int)1)) {
                    SSLEngineResult sSLEngineResult = new SSLEngineResult((SSLEngineResult.Status)SSLEngineResult.Status.BUFFER_OVERFLOW, (SSLEngineResult.HandshakeStatus)this.getHandshakeStatus(), (int)0, (int)0);
                    return sSLEngineResult;
                }
                bytesProduced = SSL.bioFlushByteBuffer((long)this.networkBIO);
                if (bytesProduced <= 0) {
                    SSLEngineResult sSLEngineResult = this.newResultMayFinishHandshake((SSLEngineResult.HandshakeStatus)SSLEngineResult.HandshakeStatus.NOT_HANDSHAKING, (int)0, (int)0);
                    return sSLEngineResult;
                }
                if (!this.doSSLShutdown()) {
                    SSLEngineResult sSLEngineResult = this.newResultMayFinishHandshake((SSLEngineResult.HandshakeStatus)SSLEngineResult.HandshakeStatus.NOT_HANDSHAKING, (int)0, (int)bytesProduced);
                    return sSLEngineResult;
                }
                bytesProduced = bioLengthBefore - SSL.bioLengthByteBuffer((long)this.networkBIO);
                SSLEngineResult sSLEngineResult = this.newResultMayFinishHandshake((SSLEngineResult.HandshakeStatus)SSLEngineResult.HandshakeStatus.NEED_WRAP, (int)0, (int)bytesProduced);
                return sSLEngineResult;
            }
            SSLEngineResult.HandshakeStatus status = SSLEngineResult.HandshakeStatus.NOT_HANDSHAKING;
            if (this.handshakeState != HandshakeState.FINISHED) {
                if (this.handshakeState != HandshakeState.STARTED_EXPLICITLY) {
                    this.handshakeState = HandshakeState.STARTED_IMPLICITLY;
                }
                bytesProduced = SSL.bioFlushByteBuffer((long)this.networkBIO);
                if (this.handshakeException != null) {
                    if (bytesProduced > 0) {
                        SSLEngineResult sSLEngineResult = this.newResult((SSLEngineResult.HandshakeStatus)SSLEngineResult.HandshakeStatus.NEED_WRAP, (int)0, (int)bytesProduced);
                        return sSLEngineResult;
                    }
                    SSLEngineResult sSLEngineResult = this.newResult((SSLEngineResult.HandshakeStatus)this.handshakeException(), (int)0, (int)0);
                    return sSLEngineResult;
                }
                status = this.handshake();
                bytesProduced = bioLengthBefore - SSL.bioLengthByteBuffer((long)this.networkBIO);
                if (status == SSLEngineResult.HandshakeStatus.NEED_TASK) {
                    SSLEngineResult sSLEngineResult = this.newResult((SSLEngineResult.HandshakeStatus)status, (int)0, (int)bytesProduced);
                    return sSLEngineResult;
                }
                if (bytesProduced > 0) {
                    SSLEngineResult sSLEngineResult = this.newResult((SSLEngineResult.HandshakeStatus)this.mayFinishHandshake((SSLEngineResult.HandshakeStatus)(status != SSLEngineResult.HandshakeStatus.FINISHED ? (bytesProduced == bioLengthBefore ? SSLEngineResult.HandshakeStatus.NEED_WRAP : this.getHandshakeStatus((int)SSL.bioLengthNonApplication((long)this.networkBIO))) : SSLEngineResult.HandshakeStatus.FINISHED)), (int)0, (int)bytesProduced);
                    return sSLEngineResult;
                }
                if (status == SSLEngineResult.HandshakeStatus.NEED_UNWRAP) {
                    SSLEngineResult sSLEngineResult = this.isOutboundDone() ? NEED_UNWRAP_CLOSED : NEED_UNWRAP_OK;
                    return sSLEngineResult;
                }
                if (this.outboundClosed) {
                    bytesProduced = SSL.bioFlushByteBuffer((long)this.networkBIO);
                    SSLEngineResult sSLEngineResult = this.newResultMayFinishHandshake((SSLEngineResult.HandshakeStatus)status, (int)0, (int)bytesProduced);
                    return sSLEngineResult;
                }
            }
            int endOffset = offset + length;
            if (this.jdkCompatibilityMode) {
                int srcsLen = 0;
                for (int i = offset; i < endOffset; ++i) {
                    ByteBuffer src2 = srcs[i];
                    if (src2 == null) {
                        throw new IllegalArgumentException((String)("srcs[" + i + "] is null"));
                    }
                    if (srcsLen == MAX_PLAINTEXT_LENGTH || (srcsLen += src2.remaining()) <= MAX_PLAINTEXT_LENGTH && srcsLen >= 0) continue;
                    srcsLen = MAX_PLAINTEXT_LENGTH;
                }
                if (!this.isBytesAvailableEnoughForWrap((int)dst.remaining(), (int)srcsLen, (int)1)) {
                    SSLEngineResult i = new SSLEngineResult((SSLEngineResult.Status)SSLEngineResult.Status.BUFFER_OVERFLOW, (SSLEngineResult.HandshakeStatus)this.getHandshakeStatus(), (int)0, (int)0);
                    return i;
                }
            }
            int bytesConsumed = 0;
            bytesProduced = SSL.bioFlushByteBuffer((long)this.networkBIO);
            while (offset < endOffset) {
                src = srcs[offset];
                int remaining = ((java.nio.Buffer)src).remaining();
                if (remaining != 0) {
                    int bytesWritten;
                    if (this.jdkCompatibilityMode) {
                        bytesWritten = this.writePlaintextData((ByteBuffer)src, (int)Math.min((int)remaining, (int)(MAX_PLAINTEXT_LENGTH - bytesConsumed)));
                    } else {
                        int availableCapacityForWrap = dst.remaining() - bytesProduced - this.maxWrapOverhead;
                        if (availableCapacityForWrap <= 0) {
                            SSLEngineResult sSLEngineResult = new SSLEngineResult((SSLEngineResult.Status)SSLEngineResult.Status.BUFFER_OVERFLOW, (SSLEngineResult.HandshakeStatus)this.getHandshakeStatus(), (int)bytesConsumed, (int)bytesProduced);
                            return sSLEngineResult;
                        }
                        bytesWritten = this.writePlaintextData((ByteBuffer)src, (int)Math.min((int)remaining, (int)availableCapacityForWrap));
                    }
                    if (bytesWritten > 0) {
                        bytesConsumed += bytesWritten;
                        int pendingNow = SSL.bioLengthByteBuffer((long)this.networkBIO);
                        bioLengthBefore = pendingNow;
                        if (this.jdkCompatibilityMode || (bytesProduced += bioLengthBefore - pendingNow) == dst.remaining()) {
                            SSLEngineResult sSLEngineResult = this.newResultMayFinishHandshake((SSLEngineResult.HandshakeStatus)status, (int)bytesConsumed, (int)bytesProduced);
                            return sSLEngineResult;
                        }
                    } else {
                        int sslError = SSL.getError((long)this.ssl, (int)bytesWritten);
                        if (sslError == SSL.SSL_ERROR_ZERO_RETURN) {
                            if (this.receivedShutdown) {
                                SSLEngineResult hs = this.newResult((SSLEngineResult.HandshakeStatus)SSLEngineResult.HandshakeStatus.NOT_HANDSHAKING, (int)bytesConsumed, (int)bytesProduced);
                                return hs;
                            }
                            this.closeAll();
                            SSLEngineResult.HandshakeStatus hs = this.mayFinishHandshake((SSLEngineResult.HandshakeStatus)(status != SSLEngineResult.HandshakeStatus.FINISHED ? ((bytesProduced += bioLengthBefore - SSL.bioLengthByteBuffer((long)this.networkBIO)) == dst.remaining() ? SSLEngineResult.HandshakeStatus.NEED_WRAP : this.getHandshakeStatus((int)SSL.bioLengthNonApplication((long)this.networkBIO))) : SSLEngineResult.HandshakeStatus.FINISHED));
                            SSLEngineResult sSLEngineResult = this.newResult((SSLEngineResult.HandshakeStatus)hs, (int)bytesConsumed, (int)bytesProduced);
                            return sSLEngineResult;
                        }
                        if (sslError == SSL.SSL_ERROR_WANT_READ) {
                            SSLEngineResult hs = this.newResult((SSLEngineResult.HandshakeStatus)SSLEngineResult.HandshakeStatus.NEED_UNWRAP, (int)bytesConsumed, (int)bytesProduced);
                            return hs;
                        }
                        if (sslError == SSL.SSL_ERROR_WANT_WRITE) {
                            SSLEngineResult hs = this.newResult((SSLEngineResult.Status)SSLEngineResult.Status.BUFFER_OVERFLOW, (SSLEngineResult.HandshakeStatus)status, (int)bytesConsumed, (int)bytesProduced);
                            return hs;
                        }
                        if (sslError != SSL.SSL_ERROR_WANT_X509_LOOKUP && sslError != SSL.SSL_ERROR_WANT_CERTIFICATE_VERIFY) {
                            if (sslError != SSL.SSL_ERROR_WANT_PRIVATE_KEY_OPERATION) throw this.shutdownWithError((String)"SSL_write", (int)sslError);
                        }
                        SSLEngineResult hs = this.newResult((SSLEngineResult.HandshakeStatus)SSLEngineResult.HandshakeStatus.NEED_TASK, (int)bytesConsumed, (int)bytesProduced);
                        return hs;
                    }
                }
                ++offset;
            }
            src = this.newResultMayFinishHandshake((SSLEngineResult.HandshakeStatus)status, (int)bytesConsumed, (int)bytesProduced);
            return src;
        }
        finally {
            SSL.bioClearByteBuffer((long)this.networkBIO);
            if (bioReadCopyBuf == null) {
                dst.position((int)(dst.position() + bytesProduced));
            } else {
                assert (bioReadCopyBuf.readableBytes() <= dst.remaining()) : "The destination buffer " + dst + " didn't have enough remaining space to hold the encrypted content in " + bioReadCopyBuf;
                dst.put((ByteBuffer)bioReadCopyBuf.internalNioBuffer((int)bioReadCopyBuf.readerIndex(), (int)bytesProduced));
                bioReadCopyBuf.release();
            }
        }
    }

    private SSLEngineResult newResult(SSLEngineResult.HandshakeStatus hs, int bytesConsumed, int bytesProduced) {
        return this.newResult((SSLEngineResult.Status)SSLEngineResult.Status.OK, (SSLEngineResult.HandshakeStatus)hs, (int)bytesConsumed, (int)bytesProduced);
    }

    private SSLEngineResult newResult(SSLEngineResult.Status status, SSLEngineResult.HandshakeStatus hs, int bytesConsumed, int bytesProduced) {
        if (this.isOutboundDone()) {
            if (!this.isInboundDone()) return new SSLEngineResult((SSLEngineResult.Status)SSLEngineResult.Status.CLOSED, (SSLEngineResult.HandshakeStatus)hs, (int)bytesConsumed, (int)bytesProduced);
            hs = SSLEngineResult.HandshakeStatus.NOT_HANDSHAKING;
            this.shutdown();
            return new SSLEngineResult((SSLEngineResult.Status)SSLEngineResult.Status.CLOSED, (SSLEngineResult.HandshakeStatus)hs, (int)bytesConsumed, (int)bytesProduced);
        }
        if (hs != SSLEngineResult.HandshakeStatus.NEED_TASK) return new SSLEngineResult((SSLEngineResult.Status)status, (SSLEngineResult.HandshakeStatus)hs, (int)bytesConsumed, (int)bytesProduced);
        this.needTask = true;
        return new SSLEngineResult((SSLEngineResult.Status)status, (SSLEngineResult.HandshakeStatus)hs, (int)bytesConsumed, (int)bytesProduced);
    }

    private SSLEngineResult newResultMayFinishHandshake(SSLEngineResult.HandshakeStatus hs, int bytesConsumed, int bytesProduced) throws SSLException {
        SSLEngineResult.HandshakeStatus handshakeStatus;
        if (hs != SSLEngineResult.HandshakeStatus.FINISHED) {
            handshakeStatus = this.getHandshakeStatus();
            return this.newResult((SSLEngineResult.HandshakeStatus)this.mayFinishHandshake((SSLEngineResult.HandshakeStatus)handshakeStatus), (int)bytesConsumed, (int)bytesProduced);
        }
        handshakeStatus = SSLEngineResult.HandshakeStatus.FINISHED;
        return this.newResult((SSLEngineResult.HandshakeStatus)this.mayFinishHandshake((SSLEngineResult.HandshakeStatus)handshakeStatus), (int)bytesConsumed, (int)bytesProduced);
    }

    private SSLEngineResult newResultMayFinishHandshake(SSLEngineResult.Status status, SSLEngineResult.HandshakeStatus hs, int bytesConsumed, int bytesProduced) throws SSLException {
        SSLEngineResult.HandshakeStatus handshakeStatus;
        if (hs != SSLEngineResult.HandshakeStatus.FINISHED) {
            handshakeStatus = this.getHandshakeStatus();
            return this.newResult((SSLEngineResult.Status)status, (SSLEngineResult.HandshakeStatus)this.mayFinishHandshake((SSLEngineResult.HandshakeStatus)handshakeStatus), (int)bytesConsumed, (int)bytesProduced);
        }
        handshakeStatus = SSLEngineResult.HandshakeStatus.FINISHED;
        return this.newResult((SSLEngineResult.Status)status, (SSLEngineResult.HandshakeStatus)this.mayFinishHandshake((SSLEngineResult.HandshakeStatus)handshakeStatus), (int)bytesConsumed, (int)bytesProduced);
    }

    private SSLException shutdownWithError(String operations, int sslError) {
        return this.shutdownWithError((String)operations, (int)sslError, (int)SSL.getLastErrorNumber());
    }

    private SSLException shutdownWithError(String operation, int sslError, int error) {
        String errorString = SSL.getErrorString((long)((long)error));
        if (logger.isDebugEnabled()) {
            logger.debug((String)"{} failed with {}: OpenSSL error: {} {}", (Object[])new Object[]{operation, Integer.valueOf((int)sslError), Integer.valueOf((int)error), errorString});
        }
        this.shutdown();
        if (this.handshakeState == HandshakeState.FINISHED) {
            return new SSLException((String)errorString);
        }
        SSLHandshakeException exception = new SSLHandshakeException((String)errorString);
        if (this.handshakeException == null) return exception;
        exception.initCause((Throwable)this.handshakeException);
        this.handshakeException = null;
        return exception;
    }

    /*
     * Exception decompiling
     */
    public final SSLEngineResult unwrap(ByteBuffer[] srcs, int srcsOffset, int srcsLength, ByteBuffer[] dsts, int dstsOffset, int dstsLength) throws SSLException {
        // This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
        // org.benf.cfr.reader.util.ConfusedCFRException: Invalid source, tried to remove [1] lbl251 : GotoStatement: goto lbl126;\u000a\u000afrom [] lbl125 : TryStatement: try { 1[TRYBLOCK]\u000a\u000abut was not a source.
        // org.benf.cfr.reader.bytecode.analysis.opgraph.Op03SimpleStatement.removeSource(Op03SimpleStatement.java:313)
        // org.benf.cfr.reader.bytecode.analysis.parse.utils.finalhelp.FinalAnalyzer$2.call(FinalAnalyzer.java:259)
        // org.benf.cfr.reader.bytecode.analysis.parse.utils.finalhelp.FinalAnalyzer$2.call(FinalAnalyzer.java:247)
        // org.benf.cfr.reader.util.graph.GraphVisitorDFS.process(GraphVisitorDFS.java:68)
        // org.benf.cfr.reader.bytecode.analysis.parse.utils.finalhelp.FinalAnalyzer.identifyFinally(FinalAnalyzer.java:267)
        // org.benf.cfr.reader.bytecode.analysis.opgraph.op3rewriters.FinallyRewriter.identifyFinally(FinallyRewriter.java:40)
        // org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:414)
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

    private SSLEngineResult sslReadErrorResult(int error, int stackError, int bytesConsumed, int bytesProduced) throws SSLException {
        if (SSL.bioLengthNonApplication((long)this.networkBIO) <= 0) throw this.shutdownWithError((String)"SSL_read", (int)error, (int)stackError);
        if (this.handshakeException == null && this.handshakeState != HandshakeState.FINISHED) {
            this.handshakeException = new SSLHandshakeException((String)SSL.getErrorString((long)((long)stackError)));
        }
        SSL.clearError();
        return new SSLEngineResult((SSLEngineResult.Status)SSLEngineResult.Status.OK, (SSLEngineResult.HandshakeStatus)SSLEngineResult.HandshakeStatus.NEED_WRAP, (int)bytesConsumed, (int)bytesProduced);
    }

    private void closeAll() throws SSLException {
        this.receivedShutdown = true;
        this.closeOutbound();
        this.closeInbound();
    }

    private void rejectRemoteInitiatedRenegotiation() throws SSLHandshakeException {
        if (this.isDestroyed()) return;
        if (SSL.getHandshakeCount((long)this.ssl) <= 1) return;
        if ("TLSv1.3".equals((Object)this.session.getProtocol())) return;
        if (this.handshakeState != HandshakeState.FINISHED) return;
        this.shutdown();
        throw new SSLHandshakeException((String)"remote-initiated renegotiation not allowed");
    }

    public final SSLEngineResult unwrap(ByteBuffer[] srcs, ByteBuffer[] dsts) throws SSLException {
        return this.unwrap((ByteBuffer[])srcs, (int)0, (int)srcs.length, (ByteBuffer[])dsts, (int)0, (int)dsts.length);
    }

    private ByteBuffer[] singleSrcBuffer(ByteBuffer src) {
        this.singleSrcBuffer[0] = src;
        return this.singleSrcBuffer;
    }

    private void resetSingleSrcBuffer() {
        this.singleSrcBuffer[0] = null;
    }

    private ByteBuffer[] singleDstBuffer(ByteBuffer src) {
        this.singleDstBuffer[0] = src;
        return this.singleDstBuffer;
    }

    private void resetSingleDstBuffer() {
        this.singleDstBuffer[0] = null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public final synchronized SSLEngineResult unwrap(ByteBuffer src, ByteBuffer[] dsts, int offset, int length) throws SSLException {
        try {
            SSLEngineResult sSLEngineResult = this.unwrap((ByteBuffer[])this.singleSrcBuffer((ByteBuffer)src), (int)0, (int)1, (ByteBuffer[])dsts, (int)offset, (int)length);
            return sSLEngineResult;
        }
        finally {
            this.resetSingleSrcBuffer();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public final synchronized SSLEngineResult wrap(ByteBuffer src, ByteBuffer dst) throws SSLException {
        try {
            SSLEngineResult sSLEngineResult = this.wrap((ByteBuffer[])this.singleSrcBuffer((ByteBuffer)src), (ByteBuffer)dst);
            return sSLEngineResult;
        }
        finally {
            this.resetSingleSrcBuffer();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public final synchronized SSLEngineResult unwrap(ByteBuffer src, ByteBuffer dst) throws SSLException {
        try {
            SSLEngineResult sSLEngineResult = this.unwrap((ByteBuffer[])this.singleSrcBuffer((ByteBuffer)src), (ByteBuffer[])this.singleDstBuffer((ByteBuffer)dst));
            return sSLEngineResult;
        }
        finally {
            this.resetSingleSrcBuffer();
            this.resetSingleDstBuffer();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public final synchronized SSLEngineResult unwrap(ByteBuffer src, ByteBuffer[] dsts) throws SSLException {
        try {
            SSLEngineResult sSLEngineResult = this.unwrap((ByteBuffer[])this.singleSrcBuffer((ByteBuffer)src), (ByteBuffer[])dsts);
            return sSLEngineResult;
        }
        finally {
            this.resetSingleSrcBuffer();
        }
    }

    @Override
    public final synchronized Runnable getDelegatedTask() {
        if (this.isDestroyed()) {
            return null;
        }
        Runnable task = SSL.getTask((long)this.ssl);
        if (task != null) return new Runnable((ReferenceCountedOpenSslEngine)this, (Runnable)task){
            final /* synthetic */ Runnable val$task;
            final /* synthetic */ ReferenceCountedOpenSslEngine this$0;
            {
                this.this$0 = this$0;
                this.val$task = runnable;
            }

            public void run() {
                if (ReferenceCountedOpenSslEngine.access$400((ReferenceCountedOpenSslEngine)this.this$0)) {
                    return;
                }
                try {
                    this.val$task.run();
                    return;
                }
                finally {
                    ReferenceCountedOpenSslEngine.access$702((ReferenceCountedOpenSslEngine)this.this$0, (boolean)false);
                }
            }
        };
        return null;
    }

    @Override
    public final synchronized void closeInbound() throws SSLException {
        if (this.isInboundDone) {
            return;
        }
        this.isInboundDone = true;
        if (this.isOutboundDone()) {
            this.shutdown();
        }
        if (this.handshakeState == HandshakeState.NOT_STARTED) return;
        if (this.receivedShutdown) return;
        throw new SSLException((String)"Inbound closed before receiving peer's close_notify: possible truncation attack?");
    }

    @Override
    public final synchronized boolean isInboundDone() {
        return this.isInboundDone;
    }

    @Override
    public final synchronized void closeOutbound() {
        if (this.outboundClosed) {
            return;
        }
        this.outboundClosed = true;
        if (this.handshakeState != HandshakeState.NOT_STARTED && !this.isDestroyed()) {
            int mode = SSL.getShutdown((long)this.ssl);
            if ((mode & SSL.SSL_SENT_SHUTDOWN) == SSL.SSL_SENT_SHUTDOWN) return;
            this.doSSLShutdown();
            return;
        }
        this.shutdown();
    }

    private boolean doSSLShutdown() {
        if (SSL.isInInit((long)this.ssl) != 0) {
            return false;
        }
        int err = SSL.shutdownSSL((long)this.ssl);
        if (err >= 0) return true;
        int sslErr = SSL.getError((long)this.ssl, (int)err);
        if (sslErr != SSL.SSL_ERROR_SYSCALL && sslErr != SSL.SSL_ERROR_SSL) {
            SSL.clearError();
            return true;
        }
        if (logger.isDebugEnabled()) {
            int error = SSL.getLastErrorNumber();
            logger.debug((String)"SSL_shutdown failed: OpenSSL error: {} {}", (Object)Integer.valueOf((int)error), (Object)SSL.getErrorString((long)((long)error)));
        }
        this.shutdown();
        return false;
    }

    @Override
    public final synchronized boolean isOutboundDone() {
        if (!this.outboundClosed) return false;
        if (this.networkBIO == 0L) return true;
        if (SSL.bioLengthNonApplication((long)this.networkBIO) != 0) return false;
        return true;
    }

    @Override
    public final String[] getSupportedCipherSuites() {
        return OpenSsl.AVAILABLE_CIPHER_SUITES.toArray(new String[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public final String[] getEnabledCipherSuites() {
        ReferenceCountedOpenSslEngine referenceCountedOpenSslEngine = this;
        // MONITORENTER : referenceCountedOpenSslEngine
        if (this.isDestroyed()) {
            // MONITOREXIT : referenceCountedOpenSslEngine
            return EmptyArrays.EMPTY_STRINGS;
        }
        String[] enabled = SSL.getCiphers((long)this.ssl);
        // MONITOREXIT : referenceCountedOpenSslEngine
        if (enabled == null) {
            return EmptyArrays.EMPTY_STRINGS;
        }
        ArrayList<String> enabledList = new ArrayList<String>();
        ReferenceCountedOpenSslEngine referenceCountedOpenSslEngine2 = this;
        // MONITORENTER : referenceCountedOpenSslEngine2
        int i = 0;
        do {
            String cipher;
            if (i >= enabled.length) {
                // MONITOREXIT : referenceCountedOpenSslEngine2
                return enabledList.toArray(new String[0]);
            }
            String mapped = this.toJavaCipherSuite((String)enabled[i]);
            String string = cipher = mapped == null ? enabled[i] : mapped;
            if (OpenSsl.isTlsv13Supported() || !SslUtils.isTLSv13Cipher((String)cipher)) {
                enabledList.add(cipher);
            }
            ++i;
        } while (true);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public final void setEnabledCipherSuites(String[] cipherSuites) {
        ObjectUtil.checkNotNull(cipherSuites, (String)"cipherSuites");
        StringBuilder buf = new StringBuilder();
        StringBuilder bufTLSv13 = new StringBuilder();
        CipherSuiteConverter.convertToCipherStrings(Arrays.asList(cipherSuites), (StringBuilder)buf, (StringBuilder)bufTLSv13, (boolean)OpenSsl.isBoringSSL());
        String cipherSuiteSpec = buf.toString();
        String cipherSuiteSpecTLSv13 = bufTLSv13.toString();
        if (!OpenSsl.isTlsv13Supported() && !cipherSuiteSpecTLSv13.isEmpty()) {
            throw new IllegalArgumentException((String)"TLSv1.3 is not supported by this java version.");
        }
        ReferenceCountedOpenSslEngine referenceCountedOpenSslEngine = this;
        // MONITORENTER : referenceCountedOpenSslEngine
        if (this.isDestroyed()) throw new IllegalStateException((String)("failed to enable cipher suites: " + cipherSuiteSpec));
        try {
            SSL.setCipherSuites((long)this.ssl, (String)cipherSuiteSpec, (boolean)false);
            if (!OpenSsl.isTlsv13Supported()) return;
            {
                SSL.setCipherSuites((long)this.ssl, (String)cipherSuiteSpecTLSv13, (boolean)true);
                return;
            }
        }
        catch (Exception e) {
            throw new IllegalStateException((String)("failed to enable cipher suites: " + cipherSuiteSpec), (Throwable)e);
        }
    }

    @Override
    public final String[] getSupportedProtocols() {
        return OpenSsl.SUPPORTED_PROTOCOLS_SET.toArray(new String[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public final String[] getEnabledProtocols() {
        ArrayList<String> enabled = new ArrayList<String>((int)6);
        enabled.add("SSLv2Hello");
        ReferenceCountedOpenSslEngine referenceCountedOpenSslEngine = this;
        // MONITORENTER : referenceCountedOpenSslEngine
        if (this.isDestroyed()) {
            // MONITOREXIT : referenceCountedOpenSslEngine
            return enabled.toArray(new String[0]);
        }
        int opts = SSL.getOptions((long)this.ssl);
        // MONITOREXIT : referenceCountedOpenSslEngine
        if (ReferenceCountedOpenSslEngine.isProtocolEnabled((int)opts, (int)SSL.SSL_OP_NO_TLSv1, (String)"TLSv1")) {
            enabled.add("TLSv1");
        }
        if (ReferenceCountedOpenSslEngine.isProtocolEnabled((int)opts, (int)SSL.SSL_OP_NO_TLSv1_1, (String)"TLSv1.1")) {
            enabled.add("TLSv1.1");
        }
        if (ReferenceCountedOpenSslEngine.isProtocolEnabled((int)opts, (int)SSL.SSL_OP_NO_TLSv1_2, (String)"TLSv1.2")) {
            enabled.add("TLSv1.2");
        }
        if (ReferenceCountedOpenSslEngine.isProtocolEnabled((int)opts, (int)SSL.SSL_OP_NO_TLSv1_3, (String)"TLSv1.3")) {
            enabled.add("TLSv1.3");
        }
        if (ReferenceCountedOpenSslEngine.isProtocolEnabled((int)opts, (int)SSL.SSL_OP_NO_SSLv2, (String)"SSLv2")) {
            enabled.add("SSLv2");
        }
        if (!ReferenceCountedOpenSslEngine.isProtocolEnabled((int)opts, (int)SSL.SSL_OP_NO_SSLv3, (String)"SSLv3")) return enabled.toArray(new String[0]);
        enabled.add("SSLv3");
        return enabled.toArray(new String[0]);
    }

    private static boolean isProtocolEnabled(int opts, int disableMask, String protocolString) {
        if ((opts & disableMask) != 0) return false;
        if (!OpenSsl.SUPPORTED_PROTOCOLS_SET.contains((Object)protocolString)) return false;
        return true;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public final void setEnabledProtocols(String[] protocols) {
        int i;
        if (protocols == null) {
            throw new IllegalArgumentException();
        }
        int minProtocolIndex = OPENSSL_OP_NO_PROTOCOLS.length;
        int maxProtocolIndex = 0;
        for (String p : protocols) {
            if (!OpenSsl.SUPPORTED_PROTOCOLS_SET.contains((Object)p)) {
                throw new IllegalArgumentException((String)("Protocol " + p + " is not supported."));
            }
            if (p.equals((Object)"SSLv2")) {
                if (minProtocolIndex > 0) {
                    minProtocolIndex = 0;
                }
                if (maxProtocolIndex >= 0) continue;
                maxProtocolIndex = 0;
                continue;
            }
            if (p.equals((Object)"SSLv3")) {
                if (minProtocolIndex > 1) {
                    minProtocolIndex = 1;
                }
                if (maxProtocolIndex >= 1) continue;
                maxProtocolIndex = 1;
                continue;
            }
            if (p.equals((Object)"TLSv1")) {
                if (minProtocolIndex > 2) {
                    minProtocolIndex = 2;
                }
                if (maxProtocolIndex >= 2) continue;
                maxProtocolIndex = 2;
                continue;
            }
            if (p.equals((Object)"TLSv1.1")) {
                if (minProtocolIndex > 3) {
                    minProtocolIndex = 3;
                }
                if (maxProtocolIndex >= 3) continue;
                maxProtocolIndex = 3;
                continue;
            }
            if (p.equals((Object)"TLSv1.2")) {
                if (minProtocolIndex > 4) {
                    minProtocolIndex = 4;
                }
                if (maxProtocolIndex >= 4) continue;
                maxProtocolIndex = 4;
                continue;
            }
            if (!p.equals((Object)"TLSv1.3")) continue;
            if (minProtocolIndex > 5) {
                minProtocolIndex = 5;
            }
            if (maxProtocolIndex >= 5) continue;
            maxProtocolIndex = 5;
        }
        ReferenceCountedOpenSslEngine referenceCountedOpenSslEngine = this;
        // MONITORENTER : referenceCountedOpenSslEngine
        if (this.isDestroyed()) throw new IllegalStateException((String)("failed to enable protocols: " + Arrays.asList(protocols)));
        SSL.clearOptions((long)this.ssl, (int)(SSL.SSL_OP_NO_SSLv2 | SSL.SSL_OP_NO_SSLv3 | SSL.SSL_OP_NO_TLSv1 | SSL.SSL_OP_NO_TLSv1_1 | SSL.SSL_OP_NO_TLSv1_2 | SSL.SSL_OP_NO_TLSv1_3));
        int opts = 0;
        for (i = 0; i < minProtocolIndex; opts |= ReferenceCountedOpenSslEngine.OPENSSL_OP_NO_PROTOCOLS[i], ++i) {
        }
        assert (maxProtocolIndex != Integer.MAX_VALUE);
        i = maxProtocolIndex + 1;
        do {
            if (i >= OPENSSL_OP_NO_PROTOCOLS.length) {
                SSL.setOptions((long)this.ssl, (int)opts);
                return;
            }
            opts |= OPENSSL_OP_NO_PROTOCOLS[i];
            ++i;
        } while (true);
    }

    @Override
    public final SSLSession getSession() {
        return this.session;
    }

    @Override
    public final synchronized void beginHandshake() throws SSLException {
        switch (4.$SwitchMap$io$netty$handler$ssl$ReferenceCountedOpenSslEngine$HandshakeState[this.handshakeState.ordinal()]) {
            case 3: {
                this.checkEngineClosed();
                this.handshakeState = HandshakeState.STARTED_EXPLICITLY;
                this.calculateMaxWrapOverhead();
                return;
            }
            case 4: {
                return;
            }
            case 2: {
                throw new SSLException((String)"renegotiation unsupported");
            }
            case 1: {
                this.handshakeState = HandshakeState.STARTED_EXPLICITLY;
                if (this.handshake() == SSLEngineResult.HandshakeStatus.NEED_TASK) {
                    this.needTask = true;
                }
                this.calculateMaxWrapOverhead();
                return;
            }
        }
        throw new Error();
    }

    private void checkEngineClosed() throws SSLException {
        if (!this.isDestroyed()) return;
        throw new SSLException((String)"engine closed");
    }

    private static SSLEngineResult.HandshakeStatus pendingStatus(int pendingStatus) {
        SSLEngineResult.HandshakeStatus handshakeStatus;
        if (pendingStatus > 0) {
            handshakeStatus = SSLEngineResult.HandshakeStatus.NEED_WRAP;
            return handshakeStatus;
        }
        handshakeStatus = SSLEngineResult.HandshakeStatus.NEED_UNWRAP;
        return handshakeStatus;
    }

    private static boolean isEmpty(Object[] arr) {
        if (arr == null) return true;
        if (arr.length == 0) return true;
        return false;
    }

    private static boolean isEmpty(byte[] cert) {
        if (cert == null) return true;
        if (cert.length == 0) return true;
        return false;
    }

    private SSLEngineResult.HandshakeStatus handshakeException() throws SSLException {
        if (SSL.bioLengthNonApplication((long)this.networkBIO) > 0) {
            return SSLEngineResult.HandshakeStatus.NEED_WRAP;
        }
        Throwable exception = this.handshakeException;
        assert (exception != null);
        this.handshakeException = null;
        this.shutdown();
        if (exception instanceof SSLHandshakeException) {
            throw (SSLHandshakeException)exception;
        }
        SSLHandshakeException e = new SSLHandshakeException((String)"General OpenSslEngine problem");
        e.initCause((Throwable)exception);
        throw e;
    }

    final void initHandshakeException(Throwable cause) {
        assert (this.handshakeException == null);
        this.handshakeException = cause;
    }

    private SSLEngineResult.HandshakeStatus handshake() throws SSLException {
        int code;
        if (this.needTask) {
            return SSLEngineResult.HandshakeStatus.NEED_TASK;
        }
        if (this.handshakeState == HandshakeState.FINISHED) {
            return SSLEngineResult.HandshakeStatus.FINISHED;
        }
        this.checkEngineClosed();
        if (this.handshakeException != null) {
            if (SSL.doHandshake((long)this.ssl) > 0) return this.handshakeException();
            SSL.clearError();
            return this.handshakeException();
        }
        this.engineMap.add((ReferenceCountedOpenSslEngine)this);
        if (this.lastAccessed == -1L) {
            this.lastAccessed = System.currentTimeMillis();
        }
        if ((code = SSL.doHandshake((long)this.ssl)) <= 0) {
            int sslError = SSL.getError((long)this.ssl, (int)code);
            if (sslError == SSL.SSL_ERROR_WANT_READ) return ReferenceCountedOpenSslEngine.pendingStatus((int)SSL.bioLengthNonApplication((long)this.networkBIO));
            if (sslError == SSL.SSL_ERROR_WANT_WRITE) {
                return ReferenceCountedOpenSslEngine.pendingStatus((int)SSL.bioLengthNonApplication((long)this.networkBIO));
            }
            if (sslError == SSL.SSL_ERROR_WANT_X509_LOOKUP) return SSLEngineResult.HandshakeStatus.NEED_TASK;
            if (sslError == SSL.SSL_ERROR_WANT_CERTIFICATE_VERIFY) return SSLEngineResult.HandshakeStatus.NEED_TASK;
            if (sslError == SSL.SSL_ERROR_WANT_PRIVATE_KEY_OPERATION) {
                return SSLEngineResult.HandshakeStatus.NEED_TASK;
            }
            if (this.handshakeException == null) throw this.shutdownWithError((String)"SSL_do_handshake", (int)sslError);
            return this.handshakeException();
        }
        if (SSL.bioLengthNonApplication((long)this.networkBIO) > 0) {
            return SSLEngineResult.HandshakeStatus.NEED_WRAP;
        }
        this.session.handshakeFinished();
        return SSLEngineResult.HandshakeStatus.FINISHED;
    }

    private SSLEngineResult.HandshakeStatus mayFinishHandshake(SSLEngineResult.HandshakeStatus status) throws SSLException {
        if (status != SSLEngineResult.HandshakeStatus.NOT_HANDSHAKING) return status;
        if (this.handshakeState == HandshakeState.FINISHED) return status;
        return this.handshake();
    }

    @Override
    public final synchronized SSLEngineResult.HandshakeStatus getHandshakeStatus() {
        if (!this.needPendingStatus()) return SSLEngineResult.HandshakeStatus.NOT_HANDSHAKING;
        if (!this.needTask) return ReferenceCountedOpenSslEngine.pendingStatus((int)SSL.bioLengthNonApplication((long)this.networkBIO));
        return SSLEngineResult.HandshakeStatus.NEED_TASK;
    }

    private SSLEngineResult.HandshakeStatus getHandshakeStatus(int pending) {
        if (!this.needPendingStatus()) return SSLEngineResult.HandshakeStatus.NOT_HANDSHAKING;
        if (!this.needTask) return ReferenceCountedOpenSslEngine.pendingStatus((int)pending);
        return SSLEngineResult.HandshakeStatus.NEED_TASK;
    }

    private boolean needPendingStatus() {
        if (this.handshakeState == HandshakeState.NOT_STARTED) return false;
        if (this.isDestroyed()) return false;
        if (this.handshakeState != HandshakeState.FINISHED) return true;
        if (this.isInboundDone()) return true;
        if (!this.isOutboundDone()) return false;
        return true;
    }

    private String toJavaCipherSuite(String openSslCipherSuite) {
        if (openSslCipherSuite == null) {
            return null;
        }
        String version = SSL.getVersion((long)this.ssl);
        String prefix = ReferenceCountedOpenSslEngine.toJavaCipherSuitePrefix((String)version);
        return CipherSuiteConverter.toJava((String)openSslCipherSuite, (String)prefix);
    }

    private static String toJavaCipherSuitePrefix(String protocolVersion) {
        int c = protocolVersion == null || protocolVersion.isEmpty() ? 0 : (int)protocolVersion.charAt((int)0);
        switch (c) {
            case 84: {
                return "TLS";
            }
            case 83: {
                return "SSL";
            }
        }
        return "UNKNOWN";
    }

    @Override
    public final void setUseClientMode(boolean clientMode) {
        if (clientMode == this.clientMode) return;
        throw new UnsupportedOperationException();
    }

    @Override
    public final boolean getUseClientMode() {
        return this.clientMode;
    }

    @Override
    public final void setNeedClientAuth(boolean b) {
        this.setClientAuth((ClientAuth)(b ? ClientAuth.REQUIRE : ClientAuth.NONE));
    }

    @Override
    public final boolean getNeedClientAuth() {
        if (this.clientAuth != ClientAuth.REQUIRE) return false;
        return true;
    }

    @Override
    public final void setWantClientAuth(boolean b) {
        this.setClientAuth((ClientAuth)(b ? ClientAuth.OPTIONAL : ClientAuth.NONE));
    }

    @Override
    public final boolean getWantClientAuth() {
        if (this.clientAuth != ClientAuth.OPTIONAL) return false;
        return true;
    }

    public final synchronized void setVerify(int verifyMode, int depth) {
        SSL.setVerify((long)this.ssl, (int)verifyMode, (int)depth);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Unable to fully structure code
     * Enabled unnecessary exception pruning
     */
    private void setClientAuth(ClientAuth mode) {
        if (this.clientMode) {
            return;
        }
        var2_2 = this;
        // MONITORENTER : var2_2
        if (this.clientAuth == mode) {
            // MONITOREXIT : var2_2
            return;
        }
        switch (4.$SwitchMap$io$netty$handler$ssl$ClientAuth[mode.ordinal()]) {
            case 1: {
                SSL.setVerify((long)this.ssl, (int)0, (int)10);
                ** break;
            }
            case 2: {
                SSL.setVerify((long)this.ssl, (int)2, (int)10);
                ** break;
            }
            case 3: {
                SSL.setVerify((long)this.ssl, (int)1, (int)10);
                ** break;
            }
        }
        throw new Error((String)mode.toString());
lbl19: // 3 sources:
        this.clientAuth = mode;
        // MONITOREXIT : var2_2
    }

    @Override
    public final void setEnableSessionCreation(boolean b) {
        if (!b) return;
        throw new UnsupportedOperationException();
    }

    @Override
    public final boolean getEnableSessionCreation() {
        return false;
    }

    @SuppressJava6Requirement(reason="Usage guarded by java version check")
    @Override
    public final synchronized SSLParameters getSSLParameters() {
        SSLParameters sslParameters = super.getSSLParameters();
        int version = PlatformDependent.javaVersion();
        if (version < 7) return sslParameters;
        sslParameters.setEndpointIdentificationAlgorithm((String)this.endPointIdentificationAlgorithm);
        Java7SslParametersUtils.setAlgorithmConstraints((SSLParameters)sslParameters, (Object)this.algorithmConstraints);
        if (version < 8) return sslParameters;
        if (this.sniHostNames != null) {
            Java8SslUtils.setSniHostNames((SSLParameters)sslParameters, this.sniHostNames);
        }
        if (!this.isDestroyed()) {
            Java8SslUtils.setUseCipherSuitesOrder((SSLParameters)sslParameters, (boolean)((SSL.getOptions((long)this.ssl) & SSL.SSL_OP_CIPHER_SERVER_PREFERENCE) != 0));
        }
        Java8SslUtils.setSNIMatchers((SSLParameters)sslParameters, this.matchers);
        return sslParameters;
    }

    @SuppressJava6Requirement(reason="Usage guarded by java version check")
    @Override
    public final synchronized void setSSLParameters(SSLParameters sslParameters) {
        int version = PlatformDependent.javaVersion();
        if (version >= 7) {
            if (sslParameters.getAlgorithmConstraints() != null) {
                throw new IllegalArgumentException((String)"AlgorithmConstraints are not supported.");
            }
            if (version >= 8) {
                if (!this.isDestroyed()) {
                    if (this.clientMode) {
                        List<String> sniHostNames = Java8SslUtils.getSniHostNames((SSLParameters)sslParameters);
                        for (String name : sniHostNames) {
                            SSL.setTlsExtHostName((long)this.ssl, (String)name);
                        }
                        this.sniHostNames = sniHostNames;
                    }
                    if (Java8SslUtils.getUseCipherSuitesOrder((SSLParameters)sslParameters)) {
                        SSL.setOptions((long)this.ssl, (int)SSL.SSL_OP_CIPHER_SERVER_PREFERENCE);
                    } else {
                        SSL.clearOptions((long)this.ssl, (int)SSL.SSL_OP_CIPHER_SERVER_PREFERENCE);
                    }
                }
                this.matchers = sslParameters.getSNIMatchers();
            }
            String endPointIdentificationAlgorithm = sslParameters.getEndpointIdentificationAlgorithm();
            boolean endPointVerificationEnabled = ReferenceCountedOpenSslEngine.isEndPointVerificationEnabled((String)endPointIdentificationAlgorithm);
            if (this.clientMode && endPointVerificationEnabled) {
                SSL.setVerify((long)this.ssl, (int)2, (int)-1);
            }
            this.endPointIdentificationAlgorithm = endPointIdentificationAlgorithm;
            this.algorithmConstraints = sslParameters.getAlgorithmConstraints();
        }
        super.setSSLParameters((SSLParameters)sslParameters);
    }

    private static boolean isEndPointVerificationEnabled(String endPointIdentificationAlgorithm) {
        if (endPointIdentificationAlgorithm == null) return false;
        if (endPointIdentificationAlgorithm.isEmpty()) return false;
        return true;
    }

    private boolean isDestroyed() {
        return this.destroyed;
    }

    final boolean checkSniHostnameMatch(byte[] hostname) {
        return Java8SslUtils.checkSniHostnameMatch(this.matchers, (byte[])hostname);
    }

    @Override
    public String getNegotiatedApplicationProtocol() {
        return this.applicationProtocol;
    }

    private static long bufferAddress(ByteBuffer b) {
        assert (b.isDirect());
        if (!PlatformDependent.hasUnsafe()) return Buffer.address((ByteBuffer)b);
        return PlatformDependent.directBufferAddress((ByteBuffer)b);
    }

    static /* synthetic */ ResourceLeakTracker access$000(ReferenceCountedOpenSslEngine x0) {
        return x0.leak;
    }

    static /* synthetic */ ReferenceCountedOpenSslContext access$100(ReferenceCountedOpenSslEngine x0) {
        return x0.parentContext;
    }

    static /* synthetic */ boolean access$200(ReferenceCountedOpenSslEngine x0) {
        return x0.clientMode;
    }

    static /* synthetic */ List access$300(ReferenceCountedOpenSslEngine x0) {
        return x0.sniHostNames;
    }

    static /* synthetic */ boolean access$400(ReferenceCountedOpenSslEngine x0) {
        return x0.isDestroyed();
    }

    static /* synthetic */ long access$500(ReferenceCountedOpenSslEngine x0) {
        return x0.ssl;
    }

    static /* synthetic */ boolean access$600(ReferenceCountedOpenSslEngine x0) {
        return x0.enableOcsp;
    }

    static /* synthetic */ boolean access$702(ReferenceCountedOpenSslEngine x0, boolean x1) {
        x0.needTask = x1;
        return x0.needTask;
    }

    static /* synthetic */ OpenSslSession access$800(ReferenceCountedOpenSslEngine x0) {
        return x0.session;
    }

    static /* synthetic */ long access$900(ReferenceCountedOpenSslEngine x0) {
        return x0.lastAccessed;
    }

    static /* synthetic */ String access$1000(ReferenceCountedOpenSslEngine x0, String x1) {
        return x0.toJavaCipherSuite((String)x1);
    }

    static /* synthetic */ void access$1100(ReferenceCountedOpenSslEngine x0) {
        x0.calculateMaxWrapOverhead();
    }

    static /* synthetic */ HandshakeState access$1202(ReferenceCountedOpenSslEngine x0, HandshakeState x1) {
        x0.handshakeState = x1;
        return x0.handshakeState;
    }

    static /* synthetic */ boolean access$1300(Object[] x0) {
        return ReferenceCountedOpenSslEngine.isEmpty((Object[])x0);
    }

    static /* synthetic */ boolean access$1400(byte[] x0) {
        return ReferenceCountedOpenSslEngine.isEmpty((byte[])x0);
    }

    static /* synthetic */ OpenSslApplicationProtocolNegotiator access$1500(ReferenceCountedOpenSslEngine x0) {
        return x0.apn;
    }

    static /* synthetic */ String access$1602(ReferenceCountedOpenSslEngine x0, String x1) {
        x0.applicationProtocol = x1;
        return x0.applicationProtocol;
    }

    static /* synthetic */ Certificate[] access$1700(ReferenceCountedOpenSslEngine x0) {
        return x0.localCertificateChain;
    }

    static /* synthetic */ int access$1800() {
        return MAX_RECORD_SIZE;
    }
}

