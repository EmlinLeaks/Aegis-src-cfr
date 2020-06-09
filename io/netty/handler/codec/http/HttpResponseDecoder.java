/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec.http;

import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.HttpMessage;
import io.netty.handler.codec.http.HttpObjectDecoder;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;

public class HttpResponseDecoder
extends HttpObjectDecoder {
    private static final HttpResponseStatus UNKNOWN_STATUS = new HttpResponseStatus((int)999, (String)"Unknown");

    public HttpResponseDecoder() {
    }

    public HttpResponseDecoder(int maxInitialLineLength, int maxHeaderSize, int maxChunkSize) {
        super((int)maxInitialLineLength, (int)maxHeaderSize, (int)maxChunkSize, (boolean)true);
    }

    public HttpResponseDecoder(int maxInitialLineLength, int maxHeaderSize, int maxChunkSize, boolean validateHeaders) {
        super((int)maxInitialLineLength, (int)maxHeaderSize, (int)maxChunkSize, (boolean)true, (boolean)validateHeaders);
    }

    public HttpResponseDecoder(int maxInitialLineLength, int maxHeaderSize, int maxChunkSize, boolean validateHeaders, int initialBufferSize) {
        super((int)maxInitialLineLength, (int)maxHeaderSize, (int)maxChunkSize, (boolean)true, (boolean)validateHeaders, (int)initialBufferSize);
    }

    @Override
    protected HttpMessage createMessage(String[] initialLine) {
        return new DefaultHttpResponse((HttpVersion)HttpVersion.valueOf((String)initialLine[0]), (HttpResponseStatus)HttpResponseStatus.valueOf((int)Integer.parseInt((String)initialLine[1]), (String)initialLine[2]), (boolean)this.validateHeaders);
    }

    @Override
    protected HttpMessage createInvalidMessage() {
        return new DefaultFullHttpResponse((HttpVersion)HttpVersion.HTTP_1_0, (HttpResponseStatus)UNKNOWN_STATUS, (boolean)this.validateHeaders);
    }

    @Override
    protected boolean isDecodingRequest() {
        return false;
    }
}

