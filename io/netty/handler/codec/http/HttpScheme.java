/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec.http;

import io.netty.util.AsciiString;

public final class HttpScheme {
    public static final HttpScheme HTTP = new HttpScheme((int)80, (String)"http");
    public static final HttpScheme HTTPS = new HttpScheme((int)443, (String)"https");
    private final int port;
    private final AsciiString name;

    private HttpScheme(int port, String name) {
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
        if (!(o instanceof HttpScheme)) {
            return false;
        }
        HttpScheme other = (HttpScheme)o;
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

