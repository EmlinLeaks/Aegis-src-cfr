/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec.http.websocketx;

import io.netty.handler.codec.CorruptedFrameException;
import io.netty.handler.codec.http.websocketx.WebSocketCloseStatus;

public final class CorruptedWebSocketFrameException
extends CorruptedFrameException {
    private static final long serialVersionUID = 3918055132492988338L;
    private final WebSocketCloseStatus closeStatus;

    public CorruptedWebSocketFrameException() {
        this((WebSocketCloseStatus)WebSocketCloseStatus.PROTOCOL_ERROR, null, null);
    }

    public CorruptedWebSocketFrameException(WebSocketCloseStatus status, String message, Throwable cause) {
        super((String)(message == null ? status.reasonText() : message), (Throwable)cause);
        this.closeStatus = status;
    }

    public CorruptedWebSocketFrameException(WebSocketCloseStatus status, String message) {
        this((WebSocketCloseStatus)status, (String)message, null);
    }

    public CorruptedWebSocketFrameException(WebSocketCloseStatus status, Throwable cause) {
        this((WebSocketCloseStatus)status, null, (Throwable)cause);
    }

    public WebSocketCloseStatus closeStatus() {
        return this.closeStatus;
    }
}

