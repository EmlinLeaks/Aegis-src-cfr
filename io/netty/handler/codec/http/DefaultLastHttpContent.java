/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultHttpContent;
import io.netty.handler.codec.http.DefaultLastHttpContent;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.util.ReferenceCounted;
import io.netty.util.internal.StringUtil;
import java.util.Iterator;
import java.util.Map;

public class DefaultLastHttpContent
extends DefaultHttpContent
implements LastHttpContent {
    private final HttpHeaders trailingHeaders;
    private final boolean validateHeaders;

    public DefaultLastHttpContent() {
        this((ByteBuf)Unpooled.buffer((int)0));
    }

    public DefaultLastHttpContent(ByteBuf content) {
        this((ByteBuf)content, (boolean)true);
    }

    public DefaultLastHttpContent(ByteBuf content, boolean validateHeaders) {
        super((ByteBuf)content);
        this.trailingHeaders = new TrailingHttpHeaders((boolean)validateHeaders);
        this.validateHeaders = validateHeaders;
    }

    @Override
    public LastHttpContent copy() {
        return this.replace((ByteBuf)this.content().copy());
    }

    @Override
    public LastHttpContent duplicate() {
        return this.replace((ByteBuf)this.content().duplicate());
    }

    @Override
    public LastHttpContent retainedDuplicate() {
        return this.replace((ByteBuf)this.content().retainedDuplicate());
    }

    @Override
    public LastHttpContent replace(ByteBuf content) {
        DefaultLastHttpContent dup = new DefaultLastHttpContent((ByteBuf)content, (boolean)this.validateHeaders);
        dup.trailingHeaders().set((HttpHeaders)this.trailingHeaders());
        return dup;
    }

    @Override
    public LastHttpContent retain(int increment) {
        super.retain((int)increment);
        return this;
    }

    @Override
    public LastHttpContent retain() {
        super.retain();
        return this;
    }

    @Override
    public LastHttpContent touch() {
        super.touch();
        return this;
    }

    @Override
    public LastHttpContent touch(Object hint) {
        super.touch((Object)hint);
        return this;
    }

    @Override
    public HttpHeaders trailingHeaders() {
        return this.trailingHeaders;
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder((String)super.toString());
        buf.append((String)StringUtil.NEWLINE);
        this.appendHeaders((StringBuilder)buf);
        buf.setLength((int)(buf.length() - StringUtil.NEWLINE.length()));
        return buf.toString();
    }

    private void appendHeaders(StringBuilder buf) {
        Iterator<Map.Entry<String, String>> iterator = this.trailingHeaders().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, String> e = iterator.next();
            buf.append((String)e.getKey());
            buf.append((String)": ");
            buf.append((String)e.getValue());
            buf.append((String)StringUtil.NEWLINE);
        }
    }
}

