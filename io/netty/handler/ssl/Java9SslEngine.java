/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.ssl;

import io.netty.handler.ssl.Java9SslEngine;
import io.netty.handler.ssl.Java9SslUtils;
import io.netty.handler.ssl.JdkApplicationProtocolNegotiator;
import io.netty.handler.ssl.JdkSslEngine;
import io.netty.handler.ssl.SslUtils;
import io.netty.util.internal.SuppressJava6Requirement;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLEngineResult;
import javax.net.ssl.SSLException;

@SuppressJava6Requirement(reason="Usage guarded by java version check")
final class Java9SslEngine
extends JdkSslEngine {
    private final JdkApplicationProtocolNegotiator.ProtocolSelectionListener selectionListener;
    private final AlpnSelector alpnSelector;

    Java9SslEngine(SSLEngine engine, JdkApplicationProtocolNegotiator applicationNegotiator, boolean isServer) {
        super((SSLEngine)engine);
        if (isServer) {
            this.selectionListener = null;
            this.alpnSelector = new AlpnSelector((Java9SslEngine)this, (JdkApplicationProtocolNegotiator.ProtocolSelector)applicationNegotiator.protocolSelectorFactory().newSelector((SSLEngine)this, new LinkedHashSet<String>(applicationNegotiator.protocols())));
            Java9SslUtils.setHandshakeApplicationProtocolSelector((SSLEngine)engine, (BiFunction<SSLEngine, List<String>, String>)this.alpnSelector);
            return;
        }
        this.selectionListener = applicationNegotiator.protocolListenerFactory().newListener((SSLEngine)this, applicationNegotiator.protocols());
        this.alpnSelector = null;
        Java9SslUtils.setApplicationProtocols((SSLEngine)engine, applicationNegotiator.protocols());
    }

    private SSLEngineResult verifyProtocolSelection(SSLEngineResult result) throws SSLException {
        if (result.getHandshakeStatus() != SSLEngineResult.HandshakeStatus.FINISHED) return result;
        if (this.alpnSelector == null) {
            try {
                String protocol = this.getApplicationProtocol();
                assert (protocol != null);
                if (protocol.isEmpty()) {
                    this.selectionListener.unsupported();
                    return result;
                }
                this.selectionListener.selected((String)protocol);
                return result;
            }
            catch (Throwable e) {
                throw SslUtils.toSSLHandshakeException((Throwable)e);
            }
        }
        assert (this.selectionListener == null);
        this.alpnSelector.checkUnsupported();
        return result;
    }

    @Override
    public SSLEngineResult wrap(ByteBuffer src, ByteBuffer dst) throws SSLException {
        return this.verifyProtocolSelection((SSLEngineResult)super.wrap((ByteBuffer)src, (ByteBuffer)dst));
    }

    @Override
    public SSLEngineResult wrap(ByteBuffer[] srcs, ByteBuffer dst) throws SSLException {
        return this.verifyProtocolSelection((SSLEngineResult)super.wrap((ByteBuffer[])srcs, (ByteBuffer)dst));
    }

    @Override
    public SSLEngineResult wrap(ByteBuffer[] srcs, int offset, int len, ByteBuffer dst) throws SSLException {
        return this.verifyProtocolSelection((SSLEngineResult)super.wrap((ByteBuffer[])srcs, (int)offset, (int)len, (ByteBuffer)dst));
    }

    @Override
    public SSLEngineResult unwrap(ByteBuffer src, ByteBuffer dst) throws SSLException {
        return this.verifyProtocolSelection((SSLEngineResult)super.unwrap((ByteBuffer)src, (ByteBuffer)dst));
    }

    @Override
    public SSLEngineResult unwrap(ByteBuffer src, ByteBuffer[] dsts) throws SSLException {
        return this.verifyProtocolSelection((SSLEngineResult)super.unwrap((ByteBuffer)src, (ByteBuffer[])dsts));
    }

    @Override
    public SSLEngineResult unwrap(ByteBuffer src, ByteBuffer[] dst, int offset, int len) throws SSLException {
        return this.verifyProtocolSelection((SSLEngineResult)super.unwrap((ByteBuffer)src, (ByteBuffer[])dst, (int)offset, (int)len));
    }

    @Override
    void setNegotiatedApplicationProtocol(String applicationProtocol) {
    }

    @Override
    public String getNegotiatedApplicationProtocol() {
        String protocol = this.getApplicationProtocol();
        if (protocol == null) return protocol;
        if (protocol.isEmpty()) {
            return null;
        }
        String string = protocol;
        return string;
    }

    public String getApplicationProtocol() {
        return Java9SslUtils.getApplicationProtocol((SSLEngine)this.getWrappedEngine());
    }

    public String getHandshakeApplicationProtocol() {
        return Java9SslUtils.getHandshakeApplicationProtocol((SSLEngine)this.getWrappedEngine());
    }

    public void setHandshakeApplicationProtocolSelector(BiFunction<SSLEngine, List<String>, String> selector) {
        Java9SslUtils.setHandshakeApplicationProtocolSelector((SSLEngine)this.getWrappedEngine(), selector);
    }

    public BiFunction<SSLEngine, List<String>, String> getHandshakeApplicationProtocolSelector() {
        return Java9SslUtils.getHandshakeApplicationProtocolSelector((SSLEngine)this.getWrappedEngine());
    }
}

