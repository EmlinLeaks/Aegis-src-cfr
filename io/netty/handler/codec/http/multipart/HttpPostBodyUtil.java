/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec.http.multipart;

final class HttpPostBodyUtil {
    public static final int chunkSize = 8096;
    public static final String DEFAULT_BINARY_CONTENT_TYPE = "application/octet-stream";
    public static final String DEFAULT_TEXT_CONTENT_TYPE = "text/plain";

    private HttpPostBodyUtil() {
    }

    static int findNonWhitespace(String sb, int offset) {
        int result = offset;
        while (result < sb.length()) {
            if (!Character.isWhitespace((char)sb.charAt((int)result))) {
                return result;
            }
            ++result;
        }
        return result;
    }

    static int findEndOfString(String sb) {
        int result = sb.length();
        while (result > 0) {
            if (!Character.isWhitespace((char)sb.charAt((int)(result - 1)))) {
                return result;
            }
            --result;
        }
        return result;
    }
}

