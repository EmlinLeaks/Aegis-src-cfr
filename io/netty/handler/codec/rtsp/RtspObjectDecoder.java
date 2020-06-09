/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec.rtsp;

import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMessage;
import io.netty.handler.codec.http.HttpObjectDecoder;
import io.netty.handler.codec.rtsp.RtspHeaderNames;
import io.netty.util.AsciiString;

@Deprecated
public abstract class RtspObjectDecoder
extends HttpObjectDecoder {
    protected RtspObjectDecoder() {
        this((int)4096, (int)8192, (int)8192);
    }

    protected RtspObjectDecoder(int maxInitialLineLength, int maxHeaderSize, int maxContentLength) {
        super((int)maxInitialLineLength, (int)maxHeaderSize, (int)(maxContentLength * 2), (boolean)false);
    }

    protected RtspObjectDecoder(int maxInitialLineLength, int maxHeaderSize, int maxContentLength, boolean validateHeaders) {
        super((int)maxInitialLineLength, (int)maxHeaderSize, (int)(maxContentLength * 2), (boolean)false, (boolean)validateHeaders);
    }

    @Override
    protected boolean isContentAlwaysEmpty(HttpMessage msg) {
        boolean empty = super.isContentAlwaysEmpty((HttpMessage)msg);
        if (empty) {
            return true;
        }
        if (msg.headers().contains((CharSequence)RtspHeaderNames.CONTENT_LENGTH)) return empty;
        return true;
    }
}

