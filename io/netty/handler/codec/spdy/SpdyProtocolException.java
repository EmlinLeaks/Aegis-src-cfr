/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec.spdy;

import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.SuppressJava6Requirement;

public class SpdyProtocolException
extends Exception {
    private static final long serialVersionUID = 7870000537743847264L;

    public SpdyProtocolException() {
    }

    public SpdyProtocolException(String message, Throwable cause) {
        super((String)message, (Throwable)cause);
    }

    public SpdyProtocolException(String message) {
        super((String)message);
    }

    public SpdyProtocolException(Throwable cause) {
        super((Throwable)cause);
    }

    static SpdyProtocolException newStatic(String message) {
        if (PlatformDependent.javaVersion() < 7) return new SpdyProtocolException((String)message);
        return new SpdyProtocolException((String)message, (boolean)true);
    }

    @SuppressJava6Requirement(reason="uses Java 7+ Exception.<init>(String, Throwable, boolean, boolean) but is guarded by version checks")
    private SpdyProtocolException(String message, boolean shared) {
        super((String)message, null, (boolean)false, (boolean)true);
        if ($assertionsDisabled) return;
        if (shared) return;
        throw new AssertionError();
    }
}

