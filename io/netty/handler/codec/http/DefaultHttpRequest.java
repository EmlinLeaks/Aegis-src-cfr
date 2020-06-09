/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec.http;

import io.netty.handler.codec.http.DefaultHttpMessage;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMessage;
import io.netty.handler.codec.http.HttpMessageUtil;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.internal.ObjectUtil;

public class DefaultHttpRequest
extends DefaultHttpMessage
implements HttpRequest {
    private static final int HASH_CODE_PRIME = 31;
    private HttpMethod method;
    private String uri;

    public DefaultHttpRequest(HttpVersion httpVersion, HttpMethod method, String uri) {
        this((HttpVersion)httpVersion, (HttpMethod)method, (String)uri, (boolean)true);
    }

    public DefaultHttpRequest(HttpVersion httpVersion, HttpMethod method, String uri, boolean validateHeaders) {
        super((HttpVersion)httpVersion, (boolean)validateHeaders, (boolean)false);
        this.method = ObjectUtil.checkNotNull(method, (String)"method");
        this.uri = ObjectUtil.checkNotNull(uri, (String)"uri");
    }

    public DefaultHttpRequest(HttpVersion httpVersion, HttpMethod method, String uri, HttpHeaders headers) {
        super((HttpVersion)httpVersion, (HttpHeaders)headers);
        this.method = ObjectUtil.checkNotNull(method, (String)"method");
        this.uri = ObjectUtil.checkNotNull(uri, (String)"uri");
    }

    @Deprecated
    @Override
    public HttpMethod getMethod() {
        return this.method();
    }

    @Override
    public HttpMethod method() {
        return this.method;
    }

    @Deprecated
    @Override
    public String getUri() {
        return this.uri();
    }

    @Override
    public String uri() {
        return this.uri;
    }

    @Override
    public HttpRequest setMethod(HttpMethod method) {
        if (method == null) {
            throw new NullPointerException((String)"method");
        }
        this.method = method;
        return this;
    }

    @Override
    public HttpRequest setUri(String uri) {
        if (uri == null) {
            throw new NullPointerException((String)"uri");
        }
        this.uri = uri;
        return this;
    }

    @Override
    public HttpRequest setProtocolVersion(HttpVersion version) {
        super.setProtocolVersion((HttpVersion)version);
        return this;
    }

    @Override
    public int hashCode() {
        int result = 1;
        result = 31 * result + this.method.hashCode();
        result = 31 * result + this.uri.hashCode();
        return 31 * result + super.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof DefaultHttpRequest)) {
            return false;
        }
        DefaultHttpRequest other = (DefaultHttpRequest)o;
        if (!this.method().equals((Object)other.method())) return false;
        if (!this.uri().equalsIgnoreCase((String)other.uri())) return false;
        if (!super.equals((Object)o)) return false;
        return true;
    }

    public String toString() {
        return HttpMessageUtil.appendRequest((StringBuilder)new StringBuilder((int)256), (HttpRequest)this).toString();
    }
}

