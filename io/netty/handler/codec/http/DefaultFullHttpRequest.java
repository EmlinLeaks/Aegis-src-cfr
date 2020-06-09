/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.DecoderResult;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.DefaultHttpRequest;
import io.netty.handler.codec.http.FullHttpMessage;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMessage;
import io.netty.handler.codec.http.HttpMessageUtil;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.util.IllegalReferenceCountException;
import io.netty.util.ReferenceCounted;
import io.netty.util.internal.ObjectUtil;

public class DefaultFullHttpRequest
extends DefaultHttpRequest
implements FullHttpRequest {
    private final ByteBuf content;
    private final HttpHeaders trailingHeader;
    private int hash;

    public DefaultFullHttpRequest(HttpVersion httpVersion, HttpMethod method, String uri) {
        this((HttpVersion)httpVersion, (HttpMethod)method, (String)uri, (ByteBuf)Unpooled.buffer((int)0));
    }

    public DefaultFullHttpRequest(HttpVersion httpVersion, HttpMethod method, String uri, ByteBuf content) {
        this((HttpVersion)httpVersion, (HttpMethod)method, (String)uri, (ByteBuf)content, (boolean)true);
    }

    public DefaultFullHttpRequest(HttpVersion httpVersion, HttpMethod method, String uri, boolean validateHeaders) {
        this((HttpVersion)httpVersion, (HttpMethod)method, (String)uri, (ByteBuf)Unpooled.buffer((int)0), (boolean)validateHeaders);
    }

    public DefaultFullHttpRequest(HttpVersion httpVersion, HttpMethod method, String uri, ByteBuf content, boolean validateHeaders) {
        super((HttpVersion)httpVersion, (HttpMethod)method, (String)uri, (boolean)validateHeaders);
        this.content = ObjectUtil.checkNotNull(content, (String)"content");
        this.trailingHeader = new DefaultHttpHeaders((boolean)validateHeaders);
    }

    public DefaultFullHttpRequest(HttpVersion httpVersion, HttpMethod method, String uri, ByteBuf content, HttpHeaders headers, HttpHeaders trailingHeader) {
        super((HttpVersion)httpVersion, (HttpMethod)method, (String)uri, (HttpHeaders)headers);
        this.content = ObjectUtil.checkNotNull(content, (String)"content");
        this.trailingHeader = ObjectUtil.checkNotNull(trailingHeader, (String)"trailingHeader");
    }

    @Override
    public HttpHeaders trailingHeaders() {
        return this.trailingHeader;
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
    public FullHttpRequest retain() {
        this.content.retain();
        return this;
    }

    @Override
    public FullHttpRequest retain(int increment) {
        this.content.retain((int)increment);
        return this;
    }

    @Override
    public FullHttpRequest touch() {
        this.content.touch();
        return this;
    }

    @Override
    public FullHttpRequest touch(Object hint) {
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
    public FullHttpRequest setProtocolVersion(HttpVersion version) {
        super.setProtocolVersion((HttpVersion)version);
        return this;
    }

    @Override
    public FullHttpRequest setMethod(HttpMethod method) {
        super.setMethod((HttpMethod)method);
        return this;
    }

    @Override
    public FullHttpRequest setUri(String uri) {
        super.setUri((String)uri);
        return this;
    }

    @Override
    public FullHttpRequest copy() {
        return this.replace((ByteBuf)this.content().copy());
    }

    @Override
    public FullHttpRequest duplicate() {
        return this.replace((ByteBuf)this.content().duplicate());
    }

    @Override
    public FullHttpRequest retainedDuplicate() {
        return this.replace((ByteBuf)this.content().retainedDuplicate());
    }

    @Override
    public FullHttpRequest replace(ByteBuf content) {
        DefaultFullHttpRequest request = new DefaultFullHttpRequest((HttpVersion)this.protocolVersion(), (HttpMethod)this.method(), (String)this.uri(), (ByteBuf)content, (HttpHeaders)this.headers().copy(), (HttpHeaders)this.trailingHeaders().copy());
        request.setDecoderResult((DecoderResult)this.decoderResult());
        return request;
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
        if (!(o instanceof DefaultFullHttpRequest)) {
            return false;
        }
        DefaultFullHttpRequest other = (DefaultFullHttpRequest)o;
        if (!super.equals((Object)other)) return false;
        if (!this.content().equals((Object)other.content())) return false;
        if (!this.trailingHeaders().equals((Object)other.trailingHeaders())) return false;
        return true;
    }

    @Override
    public String toString() {
        return HttpMessageUtil.appendFullRequest((StringBuilder)new StringBuilder((int)256), (FullHttpRequest)this).toString();
    }
}

