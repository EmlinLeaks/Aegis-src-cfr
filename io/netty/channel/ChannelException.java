/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.channel;

import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.SuppressJava6Requirement;

public class ChannelException
extends RuntimeException {
    private static final long serialVersionUID = 2908618315971075004L;

    public ChannelException() {
    }

    public ChannelException(String message, Throwable cause) {
        super((String)message, (Throwable)cause);
    }

    public ChannelException(String message) {
        super((String)message);
    }

    public ChannelException(Throwable cause) {
        super((Throwable)cause);
    }

    @SuppressJava6Requirement(reason="uses Java 7+ RuntimeException.<init>(String, Throwable, boolean, boolean) but is guarded by version checks")
    protected ChannelException(String message, Throwable cause, boolean shared) {
        super((String)message, (Throwable)cause, (boolean)false, (boolean)true);
        if ($assertionsDisabled) return;
        if (shared) return;
        throw new AssertionError();
    }

    static ChannelException newStatic(String message, Throwable cause) {
        if (PlatformDependent.javaVersion() < 7) return new ChannelException((String)message, (Throwable)cause);
        return new ChannelException((String)message, (Throwable)cause, (boolean)true);
    }
}

