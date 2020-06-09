/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.ssl;

import io.netty.buffer.ByteBufAllocator;
import io.netty.handler.ssl.OpenSsl;
import io.netty.handler.ssl.OpenSslContext;
import io.netty.handler.ssl.ReferenceCountedOpenSslContext;
import io.netty.handler.ssl.ReferenceCountedOpenSslEngine;
import io.netty.util.ReferenceCounted;

public final class OpenSslEngine
extends ReferenceCountedOpenSslEngine {
    OpenSslEngine(OpenSslContext context, ByteBufAllocator alloc, String peerHost, int peerPort, boolean jdkCompatibilityMode) {
        super((ReferenceCountedOpenSslContext)context, (ByteBufAllocator)alloc, (String)peerHost, (int)peerPort, (boolean)jdkCompatibilityMode, (boolean)false);
    }

    protected void finalize() throws Throwable {
        Object.super.finalize();
        OpenSsl.releaseIfNeeded((ReferenceCounted)this);
    }
}

