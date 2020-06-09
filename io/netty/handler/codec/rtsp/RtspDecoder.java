/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec.rtsp;

import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.DefaultHttpRequest;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMessage;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpObjectDecoder;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.rtsp.RtspHeaderNames;
import io.netty.handler.codec.rtsp.RtspMethods;
import io.netty.handler.codec.rtsp.RtspVersions;
import io.netty.util.AsciiString;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RtspDecoder
extends HttpObjectDecoder {
    private static final HttpResponseStatus UNKNOWN_STATUS = new HttpResponseStatus((int)999, (String)"Unknown");
    private boolean isDecodingRequest;
    private static final Pattern versionPattern = Pattern.compile((String)"RTSP/\\d\\.\\d");
    public static final int DEFAULT_MAX_INITIAL_LINE_LENGTH = 4096;
    public static final int DEFAULT_MAX_HEADER_SIZE = 8192;
    public static final int DEFAULT_MAX_CONTENT_LENGTH = 8192;

    public RtspDecoder() {
        this((int)4096, (int)8192, (int)8192);
    }

    public RtspDecoder(int maxInitialLineLength, int maxHeaderSize, int maxContentLength) {
        super((int)maxInitialLineLength, (int)maxHeaderSize, (int)(maxContentLength * 2), (boolean)false);
    }

    public RtspDecoder(int maxInitialLineLength, int maxHeaderSize, int maxContentLength, boolean validateHeaders) {
        super((int)maxInitialLineLength, (int)maxHeaderSize, (int)(maxContentLength * 2), (boolean)false, (boolean)validateHeaders);
    }

    @Override
    protected HttpMessage createMessage(String[] initialLine) throws Exception {
        if (versionPattern.matcher((CharSequence)initialLine[0]).matches()) {
            this.isDecodingRequest = false;
            return new DefaultHttpResponse((HttpVersion)RtspVersions.valueOf((String)initialLine[0]), (HttpResponseStatus)new HttpResponseStatus((int)Integer.parseInt((String)initialLine[1]), (String)initialLine[2]), (boolean)this.validateHeaders);
        }
        this.isDecodingRequest = true;
        return new DefaultHttpRequest((HttpVersion)RtspVersions.valueOf((String)initialLine[2]), (HttpMethod)RtspMethods.valueOf((String)initialLine[0]), (String)initialLine[1], (boolean)this.validateHeaders);
    }

    @Override
    protected boolean isContentAlwaysEmpty(HttpMessage msg) {
        if (super.isContentAlwaysEmpty((HttpMessage)msg)) return true;
        if (!msg.headers().contains((CharSequence)RtspHeaderNames.CONTENT_LENGTH)) return true;
        return false;
    }

    @Override
    protected HttpMessage createInvalidMessage() {
        if (!this.isDecodingRequest) return new DefaultFullHttpResponse((HttpVersion)RtspVersions.RTSP_1_0, (HttpResponseStatus)UNKNOWN_STATUS, (boolean)this.validateHeaders);
        return new DefaultFullHttpRequest((HttpVersion)RtspVersions.RTSP_1_0, (HttpMethod)RtspMethods.OPTIONS, (String)"/bad-request", (boolean)this.validateHeaders);
    }

    @Override
    protected boolean isDecodingRequest() {
        return this.isDecodingRequest;
    }
}

