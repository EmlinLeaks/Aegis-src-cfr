/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  org.conscrypt.BufferAllocator
 *  org.conscrypt.Conscrypt
 */
package io.netty.handler.ssl;

import io.netty.buffer.ByteBufAllocator;
import io.netty.handler.ssl.ConscryptAlpnSslEngine;
import io.netty.handler.ssl.JdkApplicationProtocolNegotiator;
import io.netty.handler.ssl.JdkSslEngine;
import io.netty.util.internal.SystemPropertyUtil;
import java.nio.ByteBuffer;
import java.util.List;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLEngineResult;
import javax.net.ssl.SSLException;
import org.conscrypt.BufferAllocator;
import org.conscrypt.Conscrypt;

abstract class ConscryptAlpnSslEngine
extends JdkSslEngine {
    private static final boolean USE_BUFFER_ALLOCATOR = SystemPropertyUtil.getBoolean((String)"io.netty.handler.ssl.conscrypt.useBufferAllocator", (boolean)true);

    static ConscryptAlpnSslEngine newClientEngine(SSLEngine engine, ByteBufAllocator alloc, JdkApplicationProtocolNegotiator applicationNegotiator) {
        return new ClientEngine((SSLEngine)engine, (ByteBufAllocator)alloc, (JdkApplicationProtocolNegotiator)applicationNegotiator);
    }

    static ConscryptAlpnSslEngine newServerEngine(SSLEngine engine, ByteBufAllocator alloc, JdkApplicationProtocolNegotiator applicationNegotiator) {
        return new ServerEngine((SSLEngine)engine, (ByteBufAllocator)alloc, (JdkApplicationProtocolNegotiator)applicationNegotiator);
    }

    private ConscryptAlpnSslEngine(SSLEngine engine, ByteBufAllocator alloc, List<String> protocols) {
        super((SSLEngine)engine);
        if (USE_BUFFER_ALLOCATOR) {
            Conscrypt.setBufferAllocator((SSLEngine)engine, (BufferAllocator)new BufferAllocatorAdapter((ByteBufAllocator)alloc));
        }
        Conscrypt.setApplicationProtocols((SSLEngine)engine, (String[])protocols.toArray(new String[0]));
    }

    final int calculateOutNetBufSize(int plaintextBytes, int numBuffers) {
        long maxOverhead = (long)Conscrypt.maxSealOverhead((SSLEngine)this.getWrappedEngine()) * (long)numBuffers;
        return (int)Math.min((long)Integer.MAX_VALUE, (long)((long)plaintextBytes + maxOverhead));
    }

    final SSLEngineResult unwrap(ByteBuffer[] srcs, ByteBuffer[] dests) throws SSLException {
        return Conscrypt.unwrap((SSLEngine)this.getWrappedEngine(), (ByteBuffer[])srcs, (ByteBuffer[])dests);
    }
}

