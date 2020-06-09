/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec.haproxy;

import io.netty.handler.codec.DecoderException;

public class HAProxyProtocolException
extends DecoderException {
    private static final long serialVersionUID = 713710864325167351L;

    public HAProxyProtocolException() {
    }

    public HAProxyProtocolException(String message, Throwable cause) {
        super((String)message, (Throwable)cause);
    }

    public HAProxyProtocolException(String message) {
        super((String)message);
    }

    public HAProxyProtocolException(Throwable cause) {
        super((Throwable)cause);
    }
}

