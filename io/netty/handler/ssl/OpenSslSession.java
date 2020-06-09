/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.ssl;

import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSession;

interface OpenSslSession
extends SSLSession {
    public void handshakeFinished() throws SSLException;

    public void tryExpandApplicationBufferSize(int var1);
}

