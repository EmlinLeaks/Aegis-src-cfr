/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec.rtsp;

import io.netty.handler.codec.http.HttpVersion;

public final class RtspVersions {
    public static final HttpVersion RTSP_1_0 = new HttpVersion((String)"RTSP", (int)1, (int)0, (boolean)true);

    public static HttpVersion valueOf(String text) {
        if (text == null) {
            throw new NullPointerException((String)"text");
        }
        if (!"RTSP/1.0".equals((Object)(text = text.trim().toUpperCase()))) return new HttpVersion((String)text, (boolean)true);
        return RTSP_1_0;
    }

    private RtspVersions() {
    }
}

