/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec.http;

import io.netty.handler.codec.http.HttpStatusClass;
import io.netty.util.AsciiString;

public enum HttpStatusClass {
    INFORMATIONAL((int)100, (int)200, (String)"Informational"),
    SUCCESS((int)200, (int)300, (String)"Success"),
    REDIRECTION((int)300, (int)400, (String)"Redirection"),
    CLIENT_ERROR((int)400, (int)500, (String)"Client Error"),
    SERVER_ERROR((int)500, (int)600, (String)"Server Error"),
    UNKNOWN((int)0, (int)0, (String)"Unknown Status"){

        public boolean contains(int code) {
            if (code < 100) return true;
            if (code >= 600) return true;
            return false;
        }
    };
    
    private final int min;
    private final int max;
    private final AsciiString defaultReasonPhrase;

    public static HttpStatusClass valueOf(int code) {
        if (INFORMATIONAL.contains((int)code)) {
            return INFORMATIONAL;
        }
        if (SUCCESS.contains((int)code)) {
            return SUCCESS;
        }
        if (REDIRECTION.contains((int)code)) {
            return REDIRECTION;
        }
        if (CLIENT_ERROR.contains((int)code)) {
            return CLIENT_ERROR;
        }
        if (!SERVER_ERROR.contains((int)code)) return UNKNOWN;
        return SERVER_ERROR;
    }

    public static HttpStatusClass valueOf(CharSequence code) {
        HttpStatusClass httpStatusClass;
        if (code == null) return UNKNOWN;
        if (code.length() != 3) return UNKNOWN;
        char c0 = code.charAt((int)0);
        if (HttpStatusClass.isDigit((char)c0) && HttpStatusClass.isDigit((char)code.charAt((int)1)) && HttpStatusClass.isDigit((char)code.charAt((int)2))) {
            httpStatusClass = HttpStatusClass.valueOf((int)(HttpStatusClass.digit((char)c0) * 100));
            return httpStatusClass;
        }
        httpStatusClass = UNKNOWN;
        return httpStatusClass;
    }

    private static int digit(char c) {
        return c - 48;
    }

    private static boolean isDigit(char c) {
        if (c < '0') return false;
        if (c > '9') return false;
        return true;
    }

    private HttpStatusClass(int min, int max, String defaultReasonPhrase) {
        this.min = min;
        this.max = max;
        this.defaultReasonPhrase = AsciiString.cached((String)defaultReasonPhrase);
    }

    public boolean contains(int code) {
        if (code < this.min) return false;
        if (code >= this.max) return false;
        return true;
    }

    AsciiString defaultReasonPhrase() {
        return this.defaultReasonPhrase;
    }
}

