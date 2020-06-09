/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec.http.websocketx;

import io.netty.util.internal.ObjectUtil;

public final class WebSocketCloseStatus
implements Comparable<WebSocketCloseStatus> {
    public static final WebSocketCloseStatus NORMAL_CLOSURE = new WebSocketCloseStatus((int)1000, (String)"Bye");
    public static final WebSocketCloseStatus ENDPOINT_UNAVAILABLE = new WebSocketCloseStatus((int)1001, (String)"Endpoint unavailable");
    public static final WebSocketCloseStatus PROTOCOL_ERROR = new WebSocketCloseStatus((int)1002, (String)"Protocol error");
    public static final WebSocketCloseStatus INVALID_MESSAGE_TYPE = new WebSocketCloseStatus((int)1003, (String)"Invalid message type");
    public static final WebSocketCloseStatus INVALID_PAYLOAD_DATA = new WebSocketCloseStatus((int)1007, (String)"Invalid payload data");
    public static final WebSocketCloseStatus POLICY_VIOLATION = new WebSocketCloseStatus((int)1008, (String)"Policy violation");
    public static final WebSocketCloseStatus MESSAGE_TOO_BIG = new WebSocketCloseStatus((int)1009, (String)"Message too big");
    public static final WebSocketCloseStatus MANDATORY_EXTENSION = new WebSocketCloseStatus((int)1010, (String)"Mandatory extension");
    public static final WebSocketCloseStatus INTERNAL_SERVER_ERROR = new WebSocketCloseStatus((int)1011, (String)"Internal server error");
    public static final WebSocketCloseStatus SERVICE_RESTART = new WebSocketCloseStatus((int)1012, (String)"Service Restart");
    public static final WebSocketCloseStatus TRY_AGAIN_LATER = new WebSocketCloseStatus((int)1013, (String)"Try Again Later");
    public static final WebSocketCloseStatus BAD_GATEWAY = new WebSocketCloseStatus((int)1014, (String)"Bad Gateway");
    private final int statusCode;
    private final String reasonText;
    private String text;

    public WebSocketCloseStatus(int statusCode, String reasonText) {
        if (!WebSocketCloseStatus.isValidStatusCode((int)statusCode)) {
            throw new IllegalArgumentException((String)("WebSocket close status code does NOT comply with RFC-6455: " + statusCode));
        }
        this.statusCode = statusCode;
        this.reasonText = ObjectUtil.checkNotNull(reasonText, (String)"reasonText");
    }

    public int code() {
        return this.statusCode;
    }

    public String reasonText() {
        return this.reasonText;
    }

    @Override
    public int compareTo(WebSocketCloseStatus o) {
        return this.code() - o.code();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (null == o) return false;
        if (this.getClass() != o.getClass()) {
            return false;
        }
        WebSocketCloseStatus that = (WebSocketCloseStatus)o;
        if (this.statusCode != that.statusCode) return false;
        return true;
    }

    public int hashCode() {
        return this.statusCode;
    }

    public String toString() {
        String text = this.text;
        if (text != null) return text;
        this.text = text = this.code() + " " + this.reasonText();
        return text;
    }

    public static boolean isValidStatusCode(int code) {
        if (code < 0) return true;
        if (1000 <= code) {
            if (code <= 1003) return true;
        }
        if (1007 <= code) {
            if (code <= 1014) return true;
        }
        if (3000 <= code) return true;
        return false;
    }

    public static WebSocketCloseStatus valueOf(int code) {
        switch (code) {
            case 1000: {
                return NORMAL_CLOSURE;
            }
            case 1001: {
                return ENDPOINT_UNAVAILABLE;
            }
            case 1002: {
                return PROTOCOL_ERROR;
            }
            case 1003: {
                return INVALID_MESSAGE_TYPE;
            }
            case 1007: {
                return INVALID_PAYLOAD_DATA;
            }
            case 1008: {
                return POLICY_VIOLATION;
            }
            case 1009: {
                return MESSAGE_TOO_BIG;
            }
            case 1010: {
                return MANDATORY_EXTENSION;
            }
            case 1011: {
                return INTERNAL_SERVER_ERROR;
            }
            case 1012: {
                return SERVICE_RESTART;
            }
            case 1013: {
                return TRY_AGAIN_LATER;
            }
            case 1014: {
                return BAD_GATEWAY;
            }
        }
        return new WebSocketCloseStatus((int)code, (String)("Close status #" + code));
    }
}

