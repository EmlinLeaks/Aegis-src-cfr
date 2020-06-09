/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec.http;

import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.DefaultHttpRequest;
import io.netty.handler.codec.http.HttpMessage;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpObjectDecoder;
import io.netty.handler.codec.http.HttpVersion;

public class HttpRequestDecoder
extends HttpObjectDecoder {
    public HttpRequestDecoder() {
    }

    public HttpRequestDecoder(int maxInitialLineLength, int maxHeaderSize, int maxChunkSize) {
        super((int)maxInitialLineLength, (int)maxHeaderSize, (int)maxChunkSize, (boolean)true);
    }

    public HttpRequestDecoder(int maxInitialLineLength, int maxHeaderSize, int maxChunkSize, boolean validateHeaders) {
        super((int)maxInitialLineLength, (int)maxHeaderSize, (int)maxChunkSize, (boolean)true, (boolean)validateHeaders);
    }

    public HttpRequestDecoder(int maxInitialLineLength, int maxHeaderSize, int maxChunkSize, boolean validateHeaders, int initialBufferSize) {
        super((int)maxInitialLineLength, (int)maxHeaderSize, (int)maxChunkSize, (boolean)true, (boolean)validateHeaders, (int)initialBufferSize);
    }

    @Override
    protected HttpMessage createMessage(String[] initialLine) throws Exception {
        return new DefaultHttpRequest((HttpVersion)HttpVersion.valueOf((String)initialLine[2]), (HttpMethod)HttpMethod.valueOf((String)initialLine[0]), (String)initialLine[1], (boolean)this.validateHeaders);
    }

    @Override
    protected HttpMessage createInvalidMessage() {
        return new DefaultFullHttpRequest((HttpVersion)HttpVersion.HTTP_1_0, (HttpMethod)HttpMethod.GET, (String)"/bad-request", (boolean)this.validateHeaders);
    }

    @Override
    protected boolean isDecodingRequest() {
        return true;
    }
}

