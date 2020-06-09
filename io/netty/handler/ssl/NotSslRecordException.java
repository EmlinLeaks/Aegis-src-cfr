/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.ssl;

import javax.net.ssl.SSLException;

public class NotSslRecordException
extends SSLException {
    private static final long serialVersionUID = -4316784434770656841L;

    public NotSslRecordException() {
        super((String)"");
    }

    public NotSslRecordException(String message) {
        super((String)message);
    }

    public NotSslRecordException(Throwable cause) {
        super((Throwable)cause);
    }

    public NotSslRecordException(String message, Throwable cause) {
        super((String)message, (Throwable)cause);
    }
}

