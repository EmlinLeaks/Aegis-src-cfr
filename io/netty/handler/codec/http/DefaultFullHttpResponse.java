/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.DecoderResult;
import io.netty.handler.codec.http.CombinedHttpHeaders;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.FullHttpMessage;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMessage;
import io.netty.handler.codec.http.HttpMessageUtil;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.util.IllegalReferenceCountException;
import io.netty.util.ReferenceCounted;
import io.netty.util.internal.ObjectUtil;

public class DefaultFullHttpResponse
extends DefaultHttpResponse
implements FullHttpResponse {
    private final ByteBuf content;
    private final HttpHeaders trailingHeaders;
    private int hash;

    public DefaultFullHttpResponse(HttpVersion version, HttpResponseStatus status) {
        this((HttpVersion)version, (HttpResponseStatus)status, (ByteBuf)Unpooled.buffer((int)0));
    }

    public DefaultFullHttpResponse(HttpVersion version, HttpResponseStatus status, ByteBuf content) {
        this((HttpVersion)version, (HttpResponseStatus)status, (ByteBuf)content, (boolean)true);
    }

    public DefaultFullHttpResponse(HttpVersion version, HttpResponseStatus status, boolean validateHeaders) {
        this((HttpVersion)version, (HttpResponseStatus)status, (ByteBuf)Unpooled.buffer((int)0), (boolean)validateHeaders, (boolean)false);
    }

    public DefaultFullHttpResponse(HttpVersion version, HttpResponseStatus status, boolean validateHeaders, boolean singleFieldHeaders) {
        this((HttpVersion)version, (HttpResponseStatus)status, (ByteBuf)Unpooled.buffer((int)0), (boolean)validateHeaders, (boolean)singleFieldHeaders);
    }

    public DefaultFullHttpResponse(HttpVersion version, HttpResponseStatus status, ByteBuf content, boolean validateHeaders) {
        this((HttpVersion)version, (HttpResponseStatus)status, (ByteBuf)content, (boolean)validateHeaders, (boolean)false);
    }

    public DefaultFullHttpResponse(HttpVersion version, HttpResponseStatus status, ByteBuf content, boolean validateHeaders, boolean singleFieldHeaders) {
        super((HttpVersion)version, (HttpResponseStatus)status, (boolean)validateHeaders, (boolean)singleFieldHeaders);
        this.content = ObjectUtil.checkNotNull(content, (String)"content");
        this.trailingHeaders = singleFieldHeaders ? new CombinedHttpHeaders((boolean)validateHeaders) : new DefaultHttpHeaders((boolean)validateHeaders);
    }

    public DefaultFullHttpResponse(HttpVersion version, HttpResponseStatus status, ByteBuf content, HttpHeaders headers, HttpHeaders trailingHeaders) {
        super((HttpVersion)version, (HttpResponseStatus)status, (HttpHeaders)headers);
        this.content = ObjectUtil.checkNotNull(content, (String)"content");
        this.trailingHeaders = ObjectUtil.checkNotNull(trailingHeaders, (String)"trailingHeaders");
    }

    @Override
    public HttpHeaders trailingHeaders() {
        return this.trailingHeaders;
    }

    @Override
    public ByteBuf content() {
        return this.content;
    }

    @Override
    public int refCnt() {
        return this.content.refCnt();
    }

    @Override
    public FullHttpResponse retain() {
        this.content.retain();
        return this;
    }

    @Override
    public FullHttpResponse retain(int increment) {
        this.content.retain((int)increment);
        return this;
    }

    @Override
    public FullHttpResponse touch() {
        this.content.touch();
        return this;
    }

    @Override
    public FullHttpResponse touch(Object hint) {
        this.content.touch((Object)hint);
        return this;
    }

    @Override
    public boolean release() {
        return this.content.release();
    }

    @Override
    public boolean release(int decrement) {
        return this.content.release((int)decrement);
    }

    @Override
    public FullHttpResponse setProtocolVersion(HttpVersion version) {
        super.setProtocolVersion((HttpVersion)version);
        return this;
    }

    @Override
    public FullHttpResponse setStatus(HttpResponseStatus status) {
        super.setStatus((HttpResponseStatus)status);
        return this;
    }

    @Override
    public FullHttpResponse copy() {
        return this.replace((ByteBuf)this.content().copy());
    }

    @Override
    public FullHttpResponse duplicate() {
        return this.replace((ByteBuf)this.content().duplicate());
    }

    @Override
    public FullHttpResponse retainedDuplicate() {
        return this.replace((ByteBuf)this.content().retainedDuplicate());
    }

    @Override
    public FullHttpResponse replace(ByteBuf content) {
        DefaultFullHttpResponse response = new DefaultFullHttpResponse((HttpVersion)this.protocolVersion(), (HttpResponseStatus)this.status(), (ByteBuf)content, (HttpHeaders)this.headers().copy(), (HttpHeaders)this.trailingHeaders().copy());
        response.setDecoderResult((DecoderResult)this.decoderResult());
        return response;
    }

    @Override
    public int hashCode() {
        int hash = this.hash;
        if (hash != 0) return hash;
        if (this.content().refCnt() != 0) {
            try {
                hash = 31 + this.content().hashCode();
            }
            catch (IllegalReferenceCountException ignored) {
                hash = 31;
            }
        } else {
            hash = 31;
        }
        hash = 31 * hash + this.trailingHeaders().hashCode();
        this.hash = hash = 31 * hash + super.hashCode();
        return hash;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof DefaultFullHttpResponse)) {
            return false;
        }
        DefaultFullHttpResponse other = (DefaultFullHttpResponse)o;
        if (!super.equals((Object)other)) return false;
        if (!this.content().equals((Object)other.content())) return false;
        if (!this.trailingHeaders().equals((Object)other.trailingHeaders())) return false;
        return true;
    }

    @Override
    public String toString() {
        return HttpMessageUtil.appendFullResponse((StringBuilder)new StringBuilder((int)256), (FullHttpResponse)this).toString();
    }
}

