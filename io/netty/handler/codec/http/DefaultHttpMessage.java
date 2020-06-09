/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec.http;

import io.netty.handler.codec.http.CombinedHttpHeaders;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.DefaultHttpObject;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMessage;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.internal.ObjectUtil;

public abstract class DefaultHttpMessage
extends DefaultHttpObject
implements HttpMessage {
    private static final int HASH_CODE_PRIME = 31;
    private HttpVersion version;
    private final HttpHeaders headers;

    protected DefaultHttpMessage(HttpVersion version) {
        this((HttpVersion)version, (boolean)true, (boolean)false);
    }

    protected DefaultHttpMessage(HttpVersion version, boolean validateHeaders, boolean singleFieldHeaders) {
        this((HttpVersion)version, (HttpHeaders)(singleFieldHeaders ? new CombinedHttpHeaders((boolean)validateHeaders) : new DefaultHttpHeaders((boolean)validateHeaders)));
    }

    protected DefaultHttpMessage(HttpVersion version, HttpHeaders headers) {
        this.version = ObjectUtil.checkNotNull(version, (String)"version");
        this.headers = ObjectUtil.checkNotNull(headers, (String)"headers");
    }

    @Override
    public HttpHeaders headers() {
        return this.headers;
    }

    @Deprecated
    @Override
    public HttpVersion getProtocolVersion() {
        return this.protocolVersion();
    }

    @Override
    public HttpVersion protocolVersion() {
        return this.version;
    }

    @Override
    public int hashCode() {
        int result = 1;
        result = 31 * result + this.headers.hashCode();
        result = 31 * result + this.version.hashCode();
        return 31 * result + super.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof DefaultHttpMessage)) {
            return false;
        }
        DefaultHttpMessage other = (DefaultHttpMessage)o;
        if (!this.headers().equals((Object)other.headers())) return false;
        if (!this.protocolVersion().equals((Object)other.protocolVersion())) return false;
        if (!super.equals((Object)o)) return false;
        return true;
    }

    @Override
    public HttpMessage setProtocolVersion(HttpVersion version) {
        if (version == null) {
            throw new NullPointerException((String)"version");
        }
        this.version = version;
        return this;
    }
}

