/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec.http.websocketx;

import io.netty.util.AsciiString;

public final class WebSocketScheme {
    public static final WebSocketScheme WS = new WebSocketScheme((int)80, (String)"ws");
    public static final WebSocketScheme WSS = new WebSocketScheme((int)443, (String)"wss");
    private final int port;
    private final AsciiString name;

    private WebSocketScheme(int port, String name) {
        this.port = port;
        this.name = AsciiString.cached((String)name);
    }

    public AsciiString name() {
        return this.name;
    }

    public int port() {
        return this.port;
    }

    public boolean equals(Object o) {
        if (!(o instanceof WebSocketScheme)) {
            return false;
        }
        WebSocketScheme other = (WebSocketScheme)o;
        if (other.port() != this.port) return false;
        if (!other.name().equals((Object)this.name)) return false;
        return true;
    }

    public int hashCode() {
        return this.port * 31 + this.name.hashCode();
    }

    public String toString() {
        return this.name.toString();
    }
}

