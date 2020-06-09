/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.ssl;

import io.netty.handler.ssl.ApplicationProtocolNegotiator;
import io.netty.handler.ssl.JdkApplicationProtocolNegotiator;

@Deprecated
public interface JdkApplicationProtocolNegotiator
extends ApplicationProtocolNegotiator {
    public SslEngineWrapperFactory wrapperFactory();

    public ProtocolSelectorFactory protocolSelectorFactory();

    public ProtocolSelectionListenerFactory protocolListenerFactory();
}

