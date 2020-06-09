/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec.http;

import io.netty.handler.codec.http.DefaultHttpMessage;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMessage;
import io.netty.handler.codec.http.HttpMessageUtil;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.internal.ObjectUtil;

public class DefaultHttpResponse
extends DefaultHttpMessage
implements HttpResponse {
    private HttpResponseStatus status;

    public DefaultHttpResponse(HttpVersion version, HttpResponseStatus status) {
        this((HttpVersion)version, (HttpResponseStatus)status, (boolean)true, (boolean)false);
    }

    public DefaultHttpResponse(HttpVersion version, HttpResponseStatus status, boolean validateHeaders) {
        this((HttpVersion)version, (HttpResponseStatus)status, (boolean)validateHeaders, (boolean)false);
    }

    public DefaultHttpResponse(HttpVersion version, HttpResponseStatus status, boolean validateHeaders, boolean singleFieldHeaders) {
        super((HttpVersion)version, (boolean)validateHeaders, (boolean)singleFieldHeaders);
        this.status = ObjectUtil.checkNotNull(status, (String)"status");
    }

    public DefaultHttpResponse(HttpVersion version, HttpResponseStatus status, HttpHeaders headers) {
        super((HttpVersion)version, (HttpHeaders)headers);
        this.status = ObjectUtil.checkNotNull(status, (String)"status");
    }

    @Deprecated
    @Override
    public HttpResponseStatus getStatus() {
        return this.status();
    }

    @Override
    public HttpResponseStatus status() {
        return this.status;
    }

    @Override
    public HttpResponse setStatus(HttpResponseStatus status) {
        if (status == null) {
            throw new NullPointerException((String)"status");
        }
        this.status = status;
        return this;
    }

    @Override
    public HttpResponse setProtocolVersion(HttpVersion version) {
        super.setProtocolVersion((HttpVersion)version);
        return this;
    }

    public String toString() {
        return HttpMessageUtil.appendResponse((StringBuilder)new StringBuilder((int)256), (HttpResponse)this).toString();
    }

    @Override
    public int hashCode() {
        int result = 1;
        result = 31 * result + this.status.hashCode();
        return 31 * result + super.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof DefaultHttpResponse)) {
            return false;
        }
        DefaultHttpResponse other = (DefaultHttpResponse)o;
        if (!this.status.equals((Object)other.status())) return false;
        if (!super.equals((Object)o)) return false;
        return true;
    }
}

