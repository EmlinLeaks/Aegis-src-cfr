/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec.http.websocketx;

public class WebSocketHandshakeException
extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public WebSocketHandshakeException(String s) {
        super((String)s);
    }

    public WebSocketHandshakeException(String s, Throwable throwable) {
        super((String)s, (Throwable)throwable);
    }
}

