/*
 * Decompiled with CFR <Could not determine version>.
 */
package io.netty.handler.codec.spdy;

import io.netty.handler.codec.spdy.DefaultSpdyHeaders;
import io.netty.handler.codec.spdy.DefaultSpdyStreamFrame;
import io.netty.handler.codec.spdy.SpdyHeaders;
import io.netty.handler.codec.spdy.SpdyHeadersFrame;
import io.netty.handler.codec.spdy.SpdyStreamFrame;
import io.netty.util.internal.StringUtil;
import java.util.Iterator;
import java.util.Map;

public class DefaultSpdyHeadersFrame
extends DefaultSpdyStreamFrame
implements SpdyHeadersFrame {
    private boolean invalid;
    private boolean truncated;
    private final SpdyHeaders headers;

    public DefaultSpdyHeadersFrame(int streamId) {
        this((int)streamId, (boolean)true);
    }

    public DefaultSpdyHeadersFrame(int streamId, boolean validate) {
        super((int)streamId);
        this.headers = new DefaultSpdyHeaders((boolean)validate);
    }

    @Override
    public SpdyHeadersFrame setStreamId(int streamId) {
        super.setStreamId((int)streamId);
        return this;
    }

    @Override
    public SpdyHeadersFrame setLast(boolean last) {
        super.setLast((boolean)last);
        return this;
    }

    @Override
    public boolean isInvalid() {
        return this.invalid;
    }

    @Override
    public SpdyHeadersFrame setInvalid() {
        this.invalid = true;
        return this;
    }

    @Override
    public boolean isTruncated() {
        return this.truncated;
    }

    @Override
    public SpdyHeadersFrame setTruncated() {
        this.truncated = true;
        return this;
    }

    @Override
    public SpdyHeaders headers() {
        return this.headers;
    }

    public String toString() {
        StringBuilder buf = new StringBuilder().append((String)StringUtil.simpleClassName((Object)this)).append((String)"(last: ").append((boolean)this.isLast()).append((char)')').append((String)StringUtil.NEWLINE).append((String)"--> Stream-ID = ").append((int)this.streamId()).append((String)StringUtil.NEWLINE).append((String)"--> Headers:").append((String)StringUtil.NEWLINE);
        this.appendHeaders((StringBuilder)buf);
        buf.setLength((int)(buf.length() - StringUtil.NEWLINE.length()));
        return buf.toString();
    }

    protected void appendHeaders(StringBuilder buf) {
        Iterator<Map.Entry<K, V>> iterator = this.headers().iterator();
        while (iterator.hasNext()) {
            Map.Entry<K, V> e = iterator.next();
            buf.append((String)"    ");
            buf.append((CharSequence)((CharSequence)e.getKey()));
            buf.append((String)": ");
            buf.append((CharSequence)((CharSequence)e.getValue()));
            buf.append((String)StringUtil.NEWLINE);
        }
    }
}

